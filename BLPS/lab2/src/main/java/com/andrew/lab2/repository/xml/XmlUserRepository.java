package com.andrew.lab2.repository.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.andrew.lab2.entity.xml.XmlUser;
import com.andrew.lab2.entity.xml.XmlUsers;

import io.jsonwebtoken.lang.Collections;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

@Repository
public class XmlUserRepository {
    private List<XmlUser> users;
    private final AtomicLong idSequence = new AtomicLong(1);

    private File xmlFile;

    @PostConstruct
    public void init() {
        try {
            JAXBContext context = JAXBContext.newInstance(XmlUsers.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            xmlFile = new File("/opt/app/config/users.xml");

            XmlUsers xmlUsers = (XmlUsers) unmarshaller.unmarshal(xmlFile);

            if (xmlUsers.getUsers() != null) {
                users = new ArrayList<>(xmlUsers.getUsers());

                users.stream()
                    .map(XmlUser::getId)
                    .filter(Objects::nonNull)
                    .max(Long::compareTo)
                    .ifPresent(idSequence::set);
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeToFile() {
        try {
            JAXBContext context = JAXBContext.newInstance(XmlUsers.class);
            Marshaller marshaller = context.createMarshaller();

            XmlUsers wrapper = new XmlUsers();
            wrapper.setUsers(users);

            marshaller.marshal(wrapper, xmlFile);
        } catch (JAXBException e) {
            throw new RuntimeException("failed to write xml", e);
        }
    }

    public XmlUser save(XmlUser user) {
        if (user.getId() == null) {
            user.setId(idSequence.incrementAndGet());
            users.add(user);
        } else {
            boolean updated = false;

            for (int i = 0; i < users.size(); i++) {
                if (Objects.equals(users.get(i).getId(), user.getId())) {
                    users.set(i, user);
                    updated = true;
                    break;
                }
            }

            if (!updated)
                users.add(user);
        }

        writeToFile();
        return user;
    }

    public void deleteById(Long id) {
        users.removeIf(u -> Objects.equals(u.getId(), id));
        writeToFile();
    }

    public void delete(XmlUser user) {
        deleteById(user.getId());
    }

    public Page<XmlUser> findAll(Pageable pageable) {
        if (users == null || users.isEmpty())
            return Page.empty();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), users.size());

        if (start > users.size())
            return new PageImpl<>(Collections.emptyList(), pageable, users.size());

        List<XmlUser> content = users.subList(start, end);

        return new PageImpl<>(content, pageable, users.size());
    }

    public Optional<XmlUser> findByUsername(String username) {
        return users.stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst();
    }

    public Optional<XmlUser> findById(Long id) {
        return users.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst();
    }
}

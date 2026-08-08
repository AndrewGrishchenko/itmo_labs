package com.andrew.lab1.entity;

import com.andrew.lab1.entity.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XmlUser {
    private Long id;
    private String username;
    private String password;
    private Role role;
}

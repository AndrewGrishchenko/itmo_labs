package com.andrew.model;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(unique = true, nullable = false)
    private String name; //Поле не может быть null, Строка не может быть пустой
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Color eyeColor; //Поле может быть null
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Color hairColor; //Поле может быть null
    
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = true)
    private Location location; //Поле может быть null
    
    @Column(nullable = false)
    private float weight; //Значение поля должно быть больше 0
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Country nationality; //Поле может быть null

    public Person() {
    }

    public Person(String name, Color eyeColor, Color hairColor, Location location, float weight, Country nationality) {
        this.name = name;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.location = location;
        this.weight = weight;
        this.nationality = nationality;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Country getNationality() {
        return nationality;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }
}

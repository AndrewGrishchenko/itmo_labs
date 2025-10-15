package com.andrew.dto.person;

import com.andrew.dto.OwnerResponse;
import com.andrew.dto.location.LocationResponse;
import com.andrew.model.Color;
import com.andrew.model.Country;

public record PersonResponse(
    int id,
    OwnerResponse owner,
    String name,
    Color eyeColor,
    Color hairColor,
    LocationResponse location,
    float weight,
    Country nationality
) {}

// public class PersonResponse {    
//     private int id;
//     private OwnerResponse owner;

//     private String name;
//     private Color eyeColor;
//     private Color hairColor;
//     private LocationResponse location;
//     private float weight;
//     private Country nationality;
    
//     public PersonResponse() {}

//     public PersonResponse(int id, OwnerResponse owner, String name, Color eyeColor, Color hairColor, LocationResponse location, float weight, Country nationality) {
//         this.id = id;
//         this.owner = owner;
//         this.name = name;
//         this.eyeColor = eyeColor;
//         this.hairColor = hairColor;
//         this.location = location;
//         this.weight = weight;
//         this.nationality = nationality;
//     }

//     public int getId() { return id; }
//     public void setId(int id) { this.id = id; }

//     public String getName() { return name; }
//     public void setName(String name) { this.name = name; }
    
//     public Color getEyeColor() { return eyeColor; }
//     public void setEyeColor(Color eyeColor) { this.eyeColor = eyeColor; }

//     public Color getHairColor() { return hairColor; }
//     public void setHairColor(Color hairColor) { this.hairColor = hairColor; }

//     public LocationResponse getLocation() { return location; }
//     public void setLocation(LocationResponse location) { this.location = location; }

//     public float getWeight() { return weight; }
//     public void setWeight(float weight) { this.weight = weight; }

//     public Country getNationality() { return nationality; }
//     public void setNationality(Country nationality) { this.nationality = nationality; }

//     public OwnerResponse getOwner() { return owner; }
//     public void setOwner(OwnerResponse owner) { this.owner = owner; }
// }

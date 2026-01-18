package com.andrew.model;

import java.time.LocalDateTime;

import com.andrew.interfaces.Identifiable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "movies")
public class Movie implements Identifiable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name; //Поле не может быть null, Строка не может быть пустой
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates; //Поле не может быть null
    
    @Column(nullable = false)
    private LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    
    @Column(nullable = false)
    private long oscarsCount; //Значение поля должно быть больше 0
    
    @Column(nullable = false)
    private Float budget; //Значение поля должно быть больше 0, Поле не может быть null
    
    @Column(nullable = false)
    private double totalBoxOffice; //Значение поля должно быть больше 0
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private MpaaRating mpaaRating; //Поле может быть null
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "director_id", nullable = true)
    private Person director; //Поле может быть null
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "screenwriter_id", nullable = true)
    private Person screenwriter;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "operator_id", nullable = false)
    private Person operator; //Поле не может быть null
    
    @Column(nullable = false)
    private Long length; //Поле не может быть null, Значение поля должно быть больше 0
    
    @Column(nullable = false)
    private int goldenPalmCount; //Значение поля должно быть больше 0
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private MovieGenre genre; //Поле может быть null

    public Movie() {
    }

    public Movie(String name, Coordinates coordinates, long oscarsCount, Float budget, double totalBoxOffice, MpaaRating mpaaRating, Person director, Person screenwriter, Person operator, Long length, int goldenPalmCount, MovieGenre genre) {
        this.name = name;
        this.coordinates = coordinates;
        this.oscarsCount = oscarsCount;
        this.budget = budget;
        this.totalBoxOffice = totalBoxOffice;
        this.mpaaRating = mpaaRating;
        this.director = director;
        this.screenwriter = screenwriter;
        this.operator = operator;
        this.length = length;
        this.goldenPalmCount = goldenPalmCount;
        this.genre = genre;
    }

    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public long getOscarsCount() {
        return oscarsCount;
    }

    public void setOscarsCount(long oscarsCount) {
        this.oscarsCount = oscarsCount;
    }

    public Float getBudget() {
        return budget;
    }

    public void setBudget(Float budget) {
        this.budget = budget;
    }

    public double getTotalBoxOffice() {
        return totalBoxOffice;
    }

    public void setTotalBoxOffice(double totalBoxOffice) {
        this.totalBoxOffice = totalBoxOffice;
    }

    public MpaaRating getMpaaRating() {
        return mpaaRating;
    }

    public void setMpaaRating(MpaaRating mpaaRating) {
        this.mpaaRating = mpaaRating;
    }

    public Person getDirector() {
        return director;
    }

    public void setDirector(Person director) {
        this.director = director;
    }

    public Person getScreenwriter() {
        return screenwriter;
    }

    public void setScreenwriter(Person screenwriter) {
        this.screenwriter = screenwriter;
    }

    public Person getOperator() {
        return operator;
    }

    public void setOperator(Person operator) {
        this.operator = operator;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public int getGoldenPalmCount() {
        return goldenPalmCount;
    }

    public void setGoldenPalmCount(int goldenPalmCount) {
        this.goldenPalmCount = goldenPalmCount;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public void setGenre(MovieGenre genre) {
        this.genre = genre;
    }
}

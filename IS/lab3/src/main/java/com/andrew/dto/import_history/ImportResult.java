package com.andrew.dto.import_history;

import java.util.ArrayList;
import java.util.List;

import com.andrew.dto.coordinates.CoordinatesResponse;
import com.andrew.dto.location.LocationResponse;
import com.andrew.dto.movie.MovieResponse;
import com.andrew.dto.person.PersonResponse;

public class ImportResult {
    private List<CoordinatesResponse> coordinates = new ArrayList<>();
    private List<LocationResponse> locations = new ArrayList<>();
    private List<PersonResponse> persons = new ArrayList<>();
    private List<MovieResponse> movies = new ArrayList<>();

    public List<CoordinatesResponse> getCoordinates() {
        return coordinates;
    }

    public void addCoordinates(CoordinatesResponse coordinate) {
        coordinates.add(coordinate);
    }

    public List<LocationResponse> getLocations() {
        return locations;
    }

    public void addLocation(LocationResponse location) {
        locations.add(location);
    }

    public List<PersonResponse> getPersons() {
        return persons;
    }

    public void addPerson(PersonResponse person) {
        persons.add(person);
    }

    public List<MovieResponse> getMovies() {
        return movies;
    }

    public void addMovie(MovieResponse movie) {
        movies.add(movie);
    }

    public int getTotalCount() {
        return coordinates.size() + locations.size() +
            persons.size() + movies.size();
    }
}

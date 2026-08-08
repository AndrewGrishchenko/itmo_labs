package com.andrew.dto.import_history;

import java.util.ArrayList;
import java.util.List;

import com.andrew.dto.coordinates.CoordinatesRequest;
import com.andrew.dto.location.LocationRequest;
import com.andrew.dto.movie.MovieRaw;
import com.andrew.dto.person.PersonRaw;

public class BulkImportRequest {
    private List<CoordinatesRequest> coordinates = new ArrayList<>();
    private List<LocationRequest> locations = new ArrayList<>();
    private List<PersonRaw> persons = new ArrayList<>();
    private List<MovieRaw> movies = new ArrayList<>();
    
    public List<CoordinatesRequest> getCoordinates() { return coordinates; }
    public void setCoordinates(List<CoordinatesRequest> coordinates) { this.coordinates = coordinates; }

    public List<LocationRequest> getLocations() { return locations; }
    public void setLocations(List<LocationRequest> locations) { this.locations = locations; }

    public List<PersonRaw> getPersons() { return persons; }
    public void setPersons(List<PersonRaw> persons) { this.persons = persons; }

    public List<MovieRaw> getMovies() { return movies; }
    public void setMovies(List<MovieRaw> movies) { this.movies = movies; }
}

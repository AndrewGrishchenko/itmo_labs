import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Movie, Person } from '../../core/models'; 
@Injectable({
  providedIn: 'root'
})
export class OperationService {
  private apiUrl = '/api/operation';

  constructor(private http: HttpClient) { }

  getMovieWithMinGenre(): Observable<Movie> {
    return this.http.get<Movie>(`${this.apiUrl}/movie-min-genre`);
  }

  getCountByGoldenPalm(count: number): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.apiUrl}/count-by-golden-palm/${count}`);
  }

  getCountByGenreLessThan(genre: string): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.apiUrl}/count-genre-less-than/${genre}`);
  }

  getScreenwritersWithNoOscars(): Observable<Person[]> {
    return this.http.get<Person[]>(`${this.apiUrl}/screenwriters-no-oscars`);
  }

  redistributeOscars(sourceGenre: string, destGenre: string): Observable<{ movedOscars: number }> {
    const payload = { sourceGenre, destGenre };
    return this.http.post<{ movedOscars: number }>(`${this.apiUrl}/redistribute-oscars`, payload);
  }
}
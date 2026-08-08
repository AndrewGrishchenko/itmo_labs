import { Injectable } from '@angular/core';
import { Color, Country, MovieGenre, MpaaRating, OperationStatus } from './models';

@Injectable({
  providedIn: 'root'
})
export class EnumService {
  getColors(): Color[] {
    return Object.values(Color);
  }

  getCountries(): Country[] {
    return Object.values(Country);
  }

  getMpaaRatings(): MpaaRating[] {
    return Object.values(MpaaRating);
  }

  getMovieGenres(): MovieGenre[] {
    return Object.values(MovieGenre);
  }

  getOperationStatuses(): OperationStatus[] {
    return Object.values(OperationStatus);
  }
}
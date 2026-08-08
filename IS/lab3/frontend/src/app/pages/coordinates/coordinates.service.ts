import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseCrudService } from '../../core/base-crud.service';
import { Coordinate } from '../../core/models';

@Injectable({ providedIn: 'root' })
export class CoordinatesService extends BaseCrudService<Coordinate, number> {
  protected override apiUrl = '/api/coordinates';
  constructor(http: HttpClient) { super(http); }
}
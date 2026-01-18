import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseCrudService } from '../../core/base-crud.service';
import { Location } from '../../core/models';

@Injectable({ providedIn: 'root' })
export class LocationService extends BaseCrudService<Location, number> {
  protected override apiUrl = '/api/location';
  constructor(http: HttpClient) { super(http); }
}
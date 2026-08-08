import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseCrudService } from '../../core/base-crud.service';
import { Person } from '../../core/models';

@Injectable({ providedIn: 'root' })
export class MovieService extends BaseCrudService<Person, number> {
  protected override apiUrl = '/api/movie';
  constructor(http: HttpClient) { super(http); }
}
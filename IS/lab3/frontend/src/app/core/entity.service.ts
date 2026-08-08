import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseCrudService } from './base-crud.service';
import { EntityName, ENTITY_CONFIGS } from './entity-config';
import { BaseEntity } from './models';

class GenericCrudService<T extends BaseEntity, ID> extends BaseCrudService<T, ID> {
  constructor(http: HttpClient, protected override apiUrl: string) {
    super(http);
  }
}

@Injectable({ providedIn: 'root' })
export class EntityService {
  constructor(private http: HttpClient) {}

  getCrudService<T extends BaseEntity, ID>(entityName: EntityName): BaseCrudService<T, ID> {
    const endpoint = ENTITY_CONFIGS[entityName].httpEndpoint;
    return new GenericCrudService<T, ID>(this.http, endpoint);
  }
}
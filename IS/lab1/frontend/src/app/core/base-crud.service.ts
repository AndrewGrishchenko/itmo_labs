import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SortDirection } from '@angular/material/sort';
import { BaseEntity } from './models';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
}

export interface PageRequest {
  page: number;
  size: number;
  sort: string;
  order: SortDirection;
}

export abstract class BaseCrudService<T extends BaseEntity, ID> {
  
  protected abstract apiUrl: string;

  constructor(protected http: HttpClient) {}

  getPaged(request: PageRequest, extraParams: HttpParams = new HttpParams()): Observable<PageResponse<T>> {
    let params = new HttpParams()
      .set('page', request.page.toString())
      .set('size', request.size.toString())
      .set('sort', request.sort || 'id')
      .set('order', request.order || 'asc');

    extraParams.keys().forEach(key => {
      params = params.set(key, extraParams.get(key)!);
    });

    return this.http.get<PageResponse<T>>(this.apiUrl, { params });
  }

  create(entity: Partial<T>): Observable<T> {
    return this.http.post<T>(this.apiUrl, entity);
  }

  update(id: ID, entity: Partial<T>): Observable<T> {
    return this.http.put<T>(`${this.apiUrl}/${id}`, entity);
  }

  delete(id: ID): Observable<object> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
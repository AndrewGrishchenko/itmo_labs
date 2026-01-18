import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseCrudService } from '../../core/base-crud.service';
import { History } from '../../core/models';

@Injectable({ providedIn: 'root' })
export class HistoryService extends BaseCrudService<History, number> {
  protected override apiUrl = '/api/history';
  constructor(http: HttpClient) { super(http); }
}
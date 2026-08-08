import { Type } from '@angular/core';
import { BaseCrudService } from '../base-crud.service';

export interface SelectorColumn {
  def: string;
  header: string;
  cell: (element: any) => string;
}

export interface EntitySelectorConfig {
  entityName: string;
  crudService: BaseCrudService<any, number>;
  columns: SelectorColumn[];
  dialogComponent: Type<any>;
  webSocketEndpoint: string;
  allowCreation?: boolean;
}
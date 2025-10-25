import { AfterViewInit, Component, Inject, Injector, OnDestroy, OnInit, Type, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule } from '@angular/material/dialog';

import { SelectorColumn } from './entity-selector.config';
import { BaseEntity } from '../models';
import { catchError, map, merge, of, startWith, Subject, switchMap, takeUntil } from 'rxjs';
import { BaseCrudService, PageRequest } from '../base-crud.service';
import { AuthService } from '../../auth/auth';
import { ENTITY_CONFIGS, EntityName } from '../entity-config';
import { EntityService } from '../entity.service';
import { HttpParams } from '@angular/common/http';

export interface EntitySelectorData {
  entityName: EntityName;
  allowCreation?: boolean;
}

@Component({
  selector: 'app-entity-selector',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatTableModule, MatPaginatorModule, MatSortModule,
    MatCheckboxModule, MatButtonModule, MatProgressSpinnerModule, MatDialogModule
  ],
  templateUrl: './entity-selector.html',
})
export class EntitySelectorComponent implements OnInit, AfterViewInit, OnDestroy {
  entityName: EntityName;
  entityDisplayName: string;
  displayedColumns: string[];
  tableColumns: SelectorColumn[];
  dialogComponent: Type<any>;

  dataSource: BaseEntity[] = [];
  resultsLength = 0;
  isLoading = true;
  showOnlyMine = false;
  currentUserId: number | null = null;

  private destroy$ = new Subject<void>();
  private reload$ = new Subject<void>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  private crudService: BaseCrudService<any, number>;
  private authService: AuthService;
  private dialog: MatDialog;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: EntitySelectorData,
    private injector: Injector,
    public dialogRef: MatDialogRef<EntitySelectorComponent>
  ) {
    this.entityName = data.entityName;
    const config = ENTITY_CONFIGS[this.entityName];
    const entityService = this.injector.get(EntityService);

    this.crudService = entityService.getCrudService(this.entityName);
    this.authService = this.injector.get(AuthService);
    this.dialog = this.injector.get(MatDialog);
    
    this.entityDisplayName = this.entityName.charAt(0).toUpperCase() + this.entityName.slice(1);
    this.tableColumns = config.selectorColumns;
    this.displayedColumns = this.tableColumns.map(c => c.def);
    this.dialogComponent = config.dialogComponent;
  }

  ngOnInit(): void {
    this.currentUserId = this.authService.getCurrentUser()?.id || null;
  }

  ngAfterViewInit(): void {
    this.sort.sortChange.subscribe(() => (this.paginator.pageIndex = 0));
    
    merge(this.sort.sortChange, this.paginator.page, this.reload$)
      .pipe(
        startWith({}),
        switchMap(() => {
          this.isLoading = true;
          const pageRequest: PageRequest = {
            page: this.paginator.pageIndex, size: this.paginator.pageSize,
            sort: this.sort.active || 'id', order: this.sort.direction || 'asc',
          };
          let params = new HttpParams();
          if (this.showOnlyMine) { params = params.set('mine', 'true'); }
          return this.crudService.getPaged(pageRequest, params).pipe(
            catchError(() => of(null))
          );
        }),
        map(data => {
          this.isLoading = false;
          if (data === null) return [];
          this.resultsLength = data.totalElements;
          return data.content;
        }),
        takeUntil(this.destroy$)
      ).subscribe(data => (this.dataSource = data));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  
  onFilterChange(): void {
    this.paginator.pageIndex = 0;
    this.reload$.next();
  }
  
  selectEntity(entity: BaseEntity): void {
    this.dialogRef.close(entity);
  }

  createAndSelect(): void {
    const createDialogRef = this.dialog.open(this.dialogComponent, {
      width: '400px',
      disableClose: true,
      data: { 
        currentUserId: this.currentUserId,
        entityName: this.entityName 
      }
    });

    createDialogRef.afterClosed().subscribe(newlyCreatedEntity => {
      if (newlyCreatedEntity && typeof newlyCreatedEntity === 'object') {
        this.dialogRef.close(newlyCreatedEntity);
      }
    });
  }
}
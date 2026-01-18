import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit, Injector, Type, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpErrorResponse, HttpParams } from '@angular/common/http';
import { merge, Subject, of } from 'rxjs';
import { takeUntil, startWith, switchMap, catchError, map, debounceTime, distinctUntilChanged, finalize } from 'rxjs/operators';

import { AuthService } from '../auth/auth';
import { WebSocketService } from './websocket';
import { BaseCrudService, PageRequest } from './base-crud.service';
import { BaseEntity } from './models';
import { ENTITY_CONFIGS, EntityName } from './entity-config';
import { EntityService } from './entity.service';

@Component({
  template: '',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatTableModule, MatCheckboxModule,
    MatProgressSpinnerModule, MatDialogModule, MatButtonModule,
    MatPaginatorModule, MatSortModule
  ]
})
export abstract class BaseTableComponent<T extends BaseEntity> implements OnInit, AfterViewInit, OnDestroy {
  @Input() initialFilter?: { [key: string]: any };
  @Input() isFilterLocked: boolean = false;

  @Input() conflictingEntityId: number | null = null;

  @Output() dependencyConflict = new EventEmitter<{ entityId: number, error: any }>();

  protected abstract entityName: EntityName;

  displayedColumns!: string[];
  dialogComponent!: Type<any>;
  webSocketEndpoint!: string;
  
  dataSource: T[] = [];
  resultsLength = 0;
  isLoading = true;
  showOnlyMine = false;
  currentUserId: number | null = null;
  
  protected destroy$ = new Subject<void>();
  protected reload$ = new Subject<void>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  protected crudService!: BaseCrudService<T, number>;
  protected webSocketService!: WebSocketService;
  protected authService!: AuthService;
  public dialog!: MatDialog;
  protected snackBar!: MatSnackBar;

  public filterForm!: FormGroup;
  protected fb!: FormBuilder;

  constructor(protected injector: Injector) {}

  ngOnInit(): void {
    const entityService = this.injector.get(EntityService);
    this.crudService = entityService.getCrudService(this.entityName);
    this.webSocketService = this.injector.get(WebSocketService);
    this.authService = this.injector.get(AuthService);
    this.dialog = this.injector.get(MatDialog);
    this.fb = this.injector.get(FormBuilder);
    this.snackBar = this.injector.get(MatSnackBar);

    const config = ENTITY_CONFIGS[this.entityName];
    this.displayedColumns = config.tableColumns;
    this.dialogComponent = config.dialogComponent;
    this.webSocketEndpoint = config.wsEndpoint;

    const filterControls: { [key: string]: any } = {};
    this.displayedColumns.forEach(columnName => {
      if (!columnName.includes('.')) {
        filterControls[columnName] = [null];
      }
    });
    this.filterForm = this.fb.group(filterControls);

    if (this.initialFilter) {
      this.filterForm.patchValue(this.initialFilter);
    }
    if (this.isFilterLocked) {
      Object.keys(this.initialFilter || {}).forEach(key => {
        this.filterForm.get(key)?.disable();
      });
    }

    this.currentUserId = this.authService.getCurrentUser()?.id || null;
    this.setupWebSocketListener();
  }

  ngAfterViewInit(): void {
    this.sort.sortChange.subscribe(() => {
      if (this.paginator) { this.paginator.pageIndex = 0; }
    });


    this.filterForm.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged((prev, curr) => JSON.stringify(prev) === JSON.stringify(curr)),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      if (this.paginator) { this.paginator.pageIndex = 0; }
      this.reload$.next();
    });

    merge(this.sort.sortChange, this.paginator.page, this.filterForm.valueChanges.pipe(debounceTime(400)), this.reload$)
      .pipe(
        startWith({}),
        switchMap(() => {
          this.isLoading = true;
          const pageRequest: PageRequest = {
            page: this.paginator.pageIndex,
            size: this.paginator.pageSize,
            sort: this.sort.active || 'id',
            order: this.sort.direction || 'asc',
          };
          return this.crudService.getPaged(pageRequest, this.getExtraParams()).pipe(
            catchError(() => of(null))
          );
        }),
        map(data => {
          this.isLoading = false;
          if (data === null) {
            this.resultsLength = 0;
            return [];
          }
          this.resultsLength = data.totalElements;
          return data.content;
        }),
        takeUntil(this.destroy$)
      ).subscribe(data => (this.dataSource = data));
  }

  protected getExtraParams(): HttpParams { 
    let params = new HttpParams();
    if (this.showOnlyMine) {
      params = params.set('mine', 'true');
    }

    const formFilters = this.filterForm.getRawValue();
    const allFilters = { ...formFilters, ...this.initialFilter };

    for (const key in allFilters) {
      const value = allFilters[key];

      if (value === null || value === undefined || value === '') {
        continue;
      }

      const filterValue = (typeof value === 'object' && value.id) ? value.id : value;

      params = params.set(key, String(filterValue));
    }

    return params;
  }

  onFilterChange(): void {
    this.paginator.pageIndex = 0;
    this.reload$.next();
  }

  private setupWebSocketListener(): void {
    this.webSocketService.connect(this.webSocketEndpoint);
    this.webSocketService.messages$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.reload$.next());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    
    if (this.webSocketService) {
      this.webSocketService.close();
    }
  }
  
  openDialog(entity?: T): void {
    const dialogRef = this.dialog.open(this.dialogComponent, {
      width: '400px',
      disableClose: true,
      panelClass: 'app-base-dialog',
      data: { 
        entity, 
        currentUserId: this.currentUserId,
        entityName: this.entityName
      }
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result?.action === 'delete' && result.entityId) {
        this.handleDelete(result.entityId);
      }
    });
  }

  public handleDelete(entityId: number): void {
    this.isLoading = true;
    this.crudService.delete(entityId)
      .pipe(
        finalize(() => {
          this.isLoading = false;
          this.webSocketService.connect(this.webSocketEndpoint); 
          console.log(`WebSocket reconnected for ${this.webSocketEndpoint}`);
        })
      )
      .subscribe({
        next: () => {
          this.snackBar.open(`${this.entityName} успешно удален.`, 'OK', { duration: 3000, verticalPosition: 'top' });
          this.reload$.next();
        },
        error: (err: HttpErrorResponse) => {
          if (err.status === 409) {
            const errorBody = err.error;

            if (errorBody && errorBody.error === 'FOREIGN_DEPENDENCY_CONFLICT') {
              this.snackBar.open(
                errorBody.message || 'Удаление невозможно: объект используется другими пользователями.',
                'Закрыть',
                { duration: 7000, verticalPosition: 'top', panelClass: 'error-snackbar' }
              );
            } else {
              this.onDependencyConflict({ entityId: entityId, error: errorBody });
            }
          } else {
            const errorMessage = err.error?.message || 'Произошла ошибка при удалении.';
            this.snackBar.open(errorMessage, 'Закрыть', { duration: 5000, verticalPosition: 'top' });
          }
        }
      });
  }

  protected onDependencyConflict(event: { entityId: number, error: any }): void {
    console.error(
      'Dependency conflict occurred, but no handler is implemented', 
      event
    );
    this.snackBar.open('Произошел конфликт зависимостей, но обработчик не реализован.', 'Закрыть', { duration: 5000 });
  }
}
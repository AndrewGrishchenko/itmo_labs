import { Component, Injector } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { BaseTableComponent } from '../../core/base-table.component';
import { Coordinate, OperationStatus } from '../../core/models';
import { HistoryService } from './history.service';
import { BaseCrudService } from '../../core/base-crud.service';
import { EntityName } from '../../core/entity-config';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialog } from '@angular/material/dialog';
import { EntitySelectorComponent, EntitySelectorData } from '../../core/entity-selector/entity-selector.component';
import { User } from '../../auth/auth';
import { MatIconModule } from '@angular/material/icon';
import { ResolveDependenciesDialogComponent, ResolveDependenciesDialogData } from '../../core/resolve-dependencies-dialog.component';
import { MatSelectModule } from '@angular/material/select';
import { EnumService } from '../../core/enum.service';

@Component({
  selector: 'app-history',
  standalone: true,
  providers: [{ provide: BaseCrudService, useClass: HistoryService }],
  imports: [
    CommonModule, FormsModule, MatCheckboxModule, MatButtonModule,
    MatTableModule, MatSortModule, MatPaginatorModule, MatProgressSpinnerModule,
    ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatIconModule, MatSelectModule
  ],
  templateUrl: './history.html',
  styleUrls: ['./history.scss']
})
export class HistoryComponent extends BaseTableComponent<Coordinate> {
  protected override entityName: EntityName = 'history';

  private matDialog: MatDialog;

  public operationStatuses: OperationStatus[];

  constructor(injector: Injector) {
    super(injector);
    this.matDialog = injector.get(MatDialog);

    const enumService = this.injector.get(EnumService);
    this.operationStatuses = enumService.getOperationStatuses();
  }

  openOwnerFilterSelector(): void {
    const dialogData: EntitySelectorData = {
      entityName: 'user',
      allowCreation: false
    };

    const selectorDialog = this.matDialog.open(EntitySelectorComponent, {
      width: '80vw',
      maxWidth: '800px',
      data: dialogData
    });

    selectorDialog.afterClosed().subscribe((selectedOwner: User | undefined) => {
      if (selectedOwner) {
        this.filterForm.get('user')?.setValue(selectedOwner);
      }
    });
  }

  clearOwnerFilter(event: MouseEvent): void {
    event.stopPropagation();
    this.filterForm.get('user')?.reset();
  }

  override onDependencyConflict(event: { entityId: number, error: any }): void {
    const conflictingCoordinate = this.dataSource.find(mov => mov.id === event.entityId);

    const coordinateName = conflictingCoordinate ? "Координаты #" + conflictingCoordinate.id : 'Неизвестные координаты';
    
    const movieFilterForCoordinate = {
      coordinates: event.entityId
    };

    const dialogData: ResolveDependenciesDialogData = {
      entityName: this.entityName,
      entityId: event.entityId,
      entityDisplayName: coordinateName,
      dependencyCount: event.error.dependencyCount.Movie,
      dependencyEntityName: 'movie',
      initialFilter: movieFilterForCoordinate
    };
    
    const dialogRef = this.dialog.open(ResolveDependenciesDialogComponent, {
      width: '90vw',
      maxWidth: '1200px',
      height: '80vh',
      data: dialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.retry) {
        (this as any).handleDelete(event.entityId);
      }
    });
  }
}
import { Component, Injector } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { BaseTableComponent } from '../../core/base-table.component';
import { Location } from '../../core/models';
import { LocationService } from './location.service';
import { BaseCrudService } from '../../core/base-crud.service';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { EntityName } from '../../core/entity-config';
import { MatDialog } from '@angular/material/dialog';
import { EntitySelectorComponent, EntitySelectorData } from '../../core/entity-selector/entity-selector.component';
import { User } from '../../auth/auth';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ResolveDependenciesDialogComponent, ResolveDependenciesDialogData } from '../../core/resolve-dependencies-dialog.component';
import { finalize } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-location',
  standalone: true,
  providers: [{ provide: BaseCrudService, useClass: LocationService }],
  imports: [
    CommonModule, FormsModule, MatCheckboxModule, MatButtonModule,
    MatTableModule, MatSortModule, MatPaginatorModule, MatProgressSpinnerModule,
    ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatIconModule
  ],
  templateUrl: './location.html',
  styleUrls: ['./location.scss']
})
export class LocationComponent extends BaseTableComponent<Location> {
  protected override entityName: EntityName = 'location';
  
  private matDialog: MatDialog;

  constructor(injector : Injector) {
    super(injector);
    this.matDialog = injector.get(MatDialog);
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
        this.filterForm.get('owner')?.setValue(selectedOwner);
      }
    });
  }

  clearOwnerFilter(event: MouseEvent): void {
    event.stopPropagation();
    this.filterForm.get('owner')?.reset();
  }

  import(): void {
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = '.json';
    fileInput.style.display = 'none';
    
    fileInput.onchange = (event: any) => {
      const file = event.target.files[0];
      if (file) {
        this.handleImport(file);
      }
    };

    document.body.appendChild(fileInput);
    fileInput.click();
    setTimeout(() => document.body.removeChild(fileInput), 100);
  }

  protected handleImport(file: File): void {
    this.isLoading = true;
    this.crudService.importFile(file)
      .pipe(
        finalize(() => {
          this.isLoading = false;
          this.webSocketService.connect(this.webSocketEndpoint); 
        })
      )
      .subscribe({
        next: () => {
          this.snackBar.open('Импорт выполнен успешно', 'OK', { duration: 3000, verticalPosition: 'top' });
          this.reload$.next();
        },
        error: (err: HttpErrorResponse) => {
          const msg = err.error?.message || 'Ошибка при импорте файла';
          this.snackBar.open(msg, 'Закрыть', { duration: 5000, verticalPosition: 'top', panelClass: 'error-snackbar' });
        }
      });
  }

  override onDependencyConflict(event: { entityId: number, error: any }): void {
    const conflictingLocation = this.dataSource.find(loc => loc.id === event.entityId);

    const locationName = conflictingLocation ? conflictingLocation.name : 'Неизвестная локация';
    
    const personFilterForLocation = {
      location: event.entityId
    };

    const dialogData: ResolveDependenciesDialogData = {
      entityName: this.entityName,
      entityId: event.entityId,
      entityDisplayName: locationName,
      dependencyCount: event.error.dependencyCount.Person,
      dependencyEntityName: 'person',
      initialFilter: personFilterForLocation
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
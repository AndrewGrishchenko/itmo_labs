import { Component, Injector } from '@angular/core';
import { BaseTableComponent } from '../../core/base-table.component';
import { Color, Country, Location, Person } from '../../core/models';
import { PersonService } from './person.service';
import { BaseCrudService } from '../../core/base-crud.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { EntityName } from '../../core/entity-config';
import { MatDialog } from '@angular/material/dialog';
import { EntitySelectorComponent, EntitySelectorData } from '../../core/entity-selector/entity-selector.component';
import { EnumService } from '../../core/enum.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { User } from '../../auth/auth';
import { MatSelectModule } from '@angular/material/select';
import { ResolveDependenciesDialogComponent, ResolveDependenciesDialogData } from '../../core/resolve-dependencies-dialog.component';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-person',
  standalone: true,
  providers: [{ provide: BaseCrudService, useClass: PersonService }],
  imports: [
    CommonModule, FormsModule, MatCheckboxModule, MatButtonModule,
    MatTableModule, MatSortModule, MatPaginatorModule, MatProgressSpinnerModule,
    ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatIconModule, MatSelectModule
  ],
  templateUrl: './person.html',
  styleUrls: ['./person.scss']
})
export class PersonComponent extends BaseTableComponent<Person> {
  protected override entityName: EntityName = 'person';
  
  private matDialog: MatDialog;

  public eyeColors: Color[];
  public hairColors: Color[];
  public nationalities: Country[];

  constructor(injector : Injector) {
    super(injector);
    this.matDialog = injector.get(MatDialog);

    const enumService = this.injector.get(EnumService);
    this.eyeColors = enumService.getColors();
    this.hairColors = enumService.getColors();
    this.nationalities = enumService.getCountries();
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

  openLocationFilterSelector(): void {
    const dialogData: EntitySelectorData = {
      entityName: 'location',
      allowCreation: false
    };

    const selectorDialog = this.matDialog.open(EntitySelectorComponent, {
      width: '80vw',
      maxWidth: '800px',
      data: dialogData
    });

    selectorDialog.afterClosed().subscribe((selectedLocation: Location | undefined) => {
      if (selectedLocation) {
        this.filterForm.get('location')?.setValue(selectedLocation);
      }
    });
  }

  clearLocationFilter(event: MouseEvent): void {
    event.stopPropagation();
    this.filterForm.get('location')?.reset();
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
    const conflictingPerson = this.dataSource.find(per => per.id === event.entityId);

    const personName = conflictingPerson ? conflictingPerson.name : 'Неизвестная персона';
    
    const movieFilterForPerson = {
      director: { id: event.entityId },
      screenwriter: { id: event.entityId },
      operator: { id: event.entityId },
      filterLogic: 'OR'
    };

    const dialogData: ResolveDependenciesDialogData = {
      entityName: this.entityName,
      entityId: event.entityId,
      entityDisplayName: personName,
      dependencyCount: event.error.dependencyCount.Movie,
      dependencyEntityName: 'movie',
      initialFilter: movieFilterForPerson
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
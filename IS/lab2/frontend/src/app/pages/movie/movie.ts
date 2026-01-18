import { Component, Injector } from '@angular/core';
import { BaseTableComponent } from '../../core/base-table.component';
import { Coordinate, Movie, MovieGenre, MpaaRating, Person } from '../../core/models';
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
import { MovieService } from './movie.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatDialog } from '@angular/material/dialog';
import { EnumService } from '../../core/enum.service';
import { EntitySelectorComponent, EntitySelectorData } from '../../core/entity-selector/entity-selector.component';
import { User } from '../../auth/auth';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-movie',
  standalone: true,
  providers: [{ provide: BaseCrudService, useClass: MovieService }],
  imports: [
    CommonModule, FormsModule, MatCheckboxModule, MatButtonModule,
    MatTableModule, MatSortModule, MatPaginatorModule, MatProgressSpinnerModule,
    ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatIconModule, MatSelectModule
  ],
  templateUrl: './movie.html',
  styleUrls: ['./movie.scss']
})
export class MovieComponent extends BaseTableComponent<Movie> {
  protected override entityName: EntityName = 'movie';
  
  private matDialog: MatDialog;

  public mpaaRatings: MpaaRating[];
  public genres: MovieGenre[];

  constructor(injector : Injector) {
    super(injector);
    this.matDialog = injector.get(MatDialog);

    const enumService = injector.get(EnumService);
    this.mpaaRatings = enumService.getMpaaRatings();
    this.genres = enumService.getMovieGenres();
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
  

  openCoordinatesFilterSelector(): void {
    const dialogData: EntitySelectorData = {
      entityName: 'coordinate',
      allowCreation: false
    };

    const selectorDialog = this.matDialog.open(EntitySelectorComponent, {
      width: '80vw',
      maxWidth: '800px',
      data: dialogData
    });

    selectorDialog.afterClosed().subscribe((selectedCoordinates: Coordinate | undefined) => {
      if (selectedCoordinates) {
        this.filterForm.get('coordinates')?.setValue(selectedCoordinates);
      }
    });
  }

  clearCoordinatesFilter(event: MouseEvent): void {
    event.stopPropagation();
    this.filterForm.get('coordinates')?.reset();
  }


  openDirectorFilterSelector(): void {
    const dialogData: EntitySelectorData = {
      entityName: 'person',
      allowCreation: false
    };

    const selectorDialog = this.matDialog.open(EntitySelectorComponent, {
      width: '80vw',
      maxWidth: '800px',
      data: dialogData
    });

    selectorDialog.afterClosed().subscribe((selectedDirector: Person | undefined) => {
      if (selectedDirector) {
        this.filterForm.get('director')?.setValue(selectedDirector);
      }
    });
  }

  clearDirectorFilter(event: MouseEvent): void {
    event.stopPropagation();
    this.filterForm.get('director')?.reset();
  }


  openScreenwriterFilterSelector(): void {
    const dialogData: EntitySelectorData = {
      entityName: 'person',
      allowCreation: false
    };

    const selectorDialog = this.matDialog.open(EntitySelectorComponent, {
      width: '80vw',
      maxWidth: '800px',
      data: dialogData
    });

    selectorDialog.afterClosed().subscribe((selectedScreenwriter: Person | undefined) => {
      if (selectedScreenwriter) {
        this.filterForm.get('screenwriter')?.setValue(selectedScreenwriter);
      }
    });
  }

  clearScreenwriterFilter(event: MouseEvent): void {
    event.stopPropagation();
    this.filterForm.get('screenwriter')?.reset();
  }


  openOperatorFilterSelector(): void {
    const dialogData: EntitySelectorData = {
      entityName: 'person',
      allowCreation: false
    };

    const selectorDialog = this.matDialog.open(EntitySelectorComponent, {
      width: '80vw',
      maxWidth: '800px',
      data: dialogData
    });

    selectorDialog.afterClosed().subscribe((selectedOperator: Person | undefined) => {
      if (selectedOperator) {
        this.filterForm.get('operator')?.setValue(selectedOperator);
      }
    });
  }

  clearOperatorFilter(event: MouseEvent): void {
    event.stopPropagation();
    this.filterForm.get('operator')?.reset();
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
          this.snackBar.open(msg, 'Закрыть', { duration: 20000, verticalPosition: 'top', panelClass: 'error-snackbar' });
        }
      });
  }
}
import { Component, Injector } from '@angular/core';
import { Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { BaseDialogComponent } from '../../../core/base-dialog.component';
import { BaseCrudService } from '../../../core/base-crud.service';
import { Coordinate, Location, Person } from '../../../core/models';
import { FormFieldComponent, FormFieldConfig } from '../../../core/form-field/form-field.component';
import { EnumService } from '../../../core/enum.service';
import { MatDialog } from '@angular/material/dialog';
import { EntitySelectorComponent, EntitySelectorData } from '../../../core/entity-selector/entity-selector.component';
import { MovieService } from '../movie.service';
import { AppValidators } from '../../../core/validation.utils';
import { CoordinateDialogComponent } from '../../coordinates/coordinate-dialog/coordinate-dialog';
import { PersonDialogComponent } from '../../person/person-dialog/person-dialog';
import { EntityName } from '../../../core/entity-config';

@Component({
  selector: 'app-movie-dialog',
  standalone: true,
  providers: [{ provide: BaseCrudService, useClass: MovieService }],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressBarModule,
    MatSnackBarModule,
    FormFieldComponent
  ],
  templateUrl: '../../../core/base-dialog.html',
  styleUrls: ['./movie-dialog.scss']
})
export class MovieDialogComponent extends BaseDialogComponent<Location> {
  override formFields: FormFieldConfig[];

  private matDialog: MatDialog;

  constructor(injector: Injector) {
    super(injector);

    const enumService = this.injector.get(EnumService);
    this.matDialog = this.injector.get(MatDialog);

    this.formFields = [
      { name: 'id', label: 'ID', fieldType: 'text', validators: []},
      { name: 'name', label: 'Название', fieldType: 'input', type: 'text', validators: [Validators.required] },
      {
        name: 'coordinates',
        label: 'Координаты',
        fieldType: 'picker',
        onPickerClick: () => this.openCoordinatesSelector(),
        onViewSelectedClick: (coord: Coordinate) => this.openEntityDialog(CoordinateDialogComponent, coord, 'coordinate'),
        validators: [Validators.required],
        pickerDisplayValue: (coord: Coordinate | null) => {
            if (!coord) return '';

            return `Координаты #${coord.id}`;
        }
      },
      { name: 'oscarsCount', label: 'Количество оскаров', fieldType: 'input', type: 'integer', validators: [Validators.required, Validators.min(0)] },
      { name: 'budget', label: 'Бюджет', fieldType: 'input', type: 'float', validators: [Validators.required, AppValidators.positive, AppValidators.float] },
      { name: 'totalBoxOffice', label: 'Касса', fieldType: 'input', type: 'float', validators: [Validators.required, AppValidators.positive, AppValidators.float] },
      { name: 'mpaaRating', label: 'Рейтинг', fieldType: 'select', options: enumService.getMpaaRatings(), validators: [] },
      { name: 'director', label: 'Директор', fieldType: 'picker', onPickerClick: () => this.openDirectorSelector(), onViewSelectedClick: (person: Person) => this.openEntityDialog(PersonDialogComponent, person, 'person'), validators: [] },
      { name: 'screenwriter', label: 'Сценарист', fieldType: 'picker', onPickerClick: () => this.openScreenwriterSelector(), onViewSelectedClick: (person: Person) => this.openEntityDialog(PersonDialogComponent, person, 'person'), validators: [] },
      { name: 'operator', label: 'Оператор', fieldType: 'picker', onPickerClick: () => this.openOperatorSelector(), onViewSelectedClick: (person: Person) => this.openEntityDialog(PersonDialogComponent, person, 'person'), validators: [Validators.required] },
      { name: 'length', label: 'Длина', fieldType: 'input', type: 'integer', validators: [Validators.required, AppValidators.positive] },
      { name: 'goldenPalmCount', label: 'Количество золотых пальм', fieldType: 'input', type: 'integer', validators: [Validators.required, AppValidators.positive] },
      { name: 'genre', label: 'Жанр', fieldType: 'select', options: enumService.getMovieGenres(), validators: [] }
    ];
  }

  private openEntityDialog(dialogComponent: any, entity: { id: number }, entityName: EntityName): void {
    if (!entity || !entity.id) {
      console.error("unable to open entity dialog");
      return;
    }

    this.matDialog.open(dialogComponent, {
      width: '80vw',
      maxWidth: '600px',
      data: {
        entity: entity,
        entityName: entityName,
        currentUserId: this.data.currentUserId,
        isNested: true
      }
    });
  }

  openCoordinatesSelector(): void {
    const dialogData: EntitySelectorData = {
        entityName: 'coordinate'
    };

    const selectorDialog = this.matDialog.open(EntitySelectorComponent, {
        width: '80vw',
        maxWidth: '800px',
        height: '80vw',
        maxHeight: '600px',
        data: dialogData
    });

    selectorDialog.afterClosed().subscribe((selectedCoordinates: Coordinate | undefined) => {
        if (selectedCoordinates) {
            this.form.get('coordinates')?.setValue(selectedCoordinates);
        }
    })
  }

  clearCoordinates(): void {
    this.form.get('coordinates')?.setValue(null);
  }

  openDirectorSelector(): void {
    const dialogData: EntitySelectorData = {
        entityName: 'person'
    };

    const selectorDialog = this.matDialog.open(EntitySelectorComponent, {
        width: '80vw',
        maxWidth: '800px',
        height: '80vw',
        maxHeight: '600px',
        data: dialogData
    });

    selectorDialog.afterClosed().subscribe((selectedDirector: Person | undefined) => {
        if (selectedDirector) {
            this.form.get('director')?.setValue(selectedDirector);
        }
    })
  }

  clearDirector(): void {
    this.form.get('director')?.setValue(null);
  }

  openScreenwriterSelector(): void {
    const dialogData: EntitySelectorData = {
        entityName: 'person'
    };

    const selectorDialog = this.matDialog.open(EntitySelectorComponent, {
        width: '80vw',
        maxWidth: '800px',
        height: '80vw',
        maxHeight: '600px',
        data: dialogData
    });

    selectorDialog.afterClosed().subscribe((selectedScreenwriter: Person | undefined) => {
        if (selectedScreenwriter) {
            this.form.get('screenwriter')?.setValue(selectedScreenwriter);
        }
    })
  }

  clearScreenwriter(): void {
    this.form.get('screenwriter')?.setValue(null);
  }

  openOperatorSelector(): void {
    const dialogData: EntitySelectorData = {
        entityName: 'person'
    };

    const selectorDialog = this.matDialog.open(EntitySelectorComponent, {
        width: '80vw',
        maxWidth: '800px',
        height: '80vw',
        maxHeight: '600px',
        data: dialogData
    });

    selectorDialog.afterClosed().subscribe((selectedOperator: Person | undefined) => {
        if (selectedOperator) {
            this.form.get('operator')?.setValue(selectedOperator);
        }
    })
  }

  clearOperator(): void {
    this.form.get('operator')?.setValue(null);
  }

  override ngOnInit(): void {
    super.ngOnInit();
  }
}
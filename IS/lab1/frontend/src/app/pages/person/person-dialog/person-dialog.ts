import { Component, Injector } from '@angular/core';
import { Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';

import { BaseDialogComponent } from '../../../core/base-dialog.component';
import { Person, Location } from '../../../core/models';
import { PersonService } from '../person.service';
import { BaseCrudService } from '../../../core/base-crud.service';
import { EnumService } from '../../../core/enum.service';

import { EntitySelectorComponent, EntitySelectorData } from '../../../core/entity-selector/entity-selector.component';
import { FormFieldComponent, FormFieldConfig } from '../../../core/form-field/form-field.component';
import { AppValidators } from '../../../core/validation.utils';

@Component({
  selector: 'app-person-dialog',
  standalone: true,
  providers: [{ provide: BaseCrudService, useClass: PersonService }],
  imports: [
    CommonModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatProgressBarModule, MatSelectModule, MatIconModule,
    FormFieldComponent
  ],
  templateUrl: '../../../core/base-dialog.html',
  styleUrls: ['./person-dialog.scss']
})
export class PersonDialogComponent extends BaseDialogComponent<Person> {
  override formFields: FormFieldConfig[];

  private matDialog: MatDialog;
  
  constructor(injector: Injector) {
    super(injector);
    const enumService = this.injector.get(EnumService);
    this.matDialog = this.injector.get(MatDialog);

    this.formFields = [
      { name: 'id', label: 'ID', fieldType: 'text', validators: []},
      { name: 'name', label: 'Имя', fieldType: 'input', type: 'text', validators: [Validators.required] },
      { name: 'eyeColor', label: 'Цвет глаз', fieldType: 'select', options: enumService.getColors(), validators: [] },
      { name: 'hairColor', label: 'Цвет волос', fieldType: 'select', options: enumService.getColors(), validators: [] },
      { 
        name: 'location', 
        label: 'Локация', 
        fieldType: 'picker',
        validators: [],
        onPickerClick: () => this.openLocationSelector()
      },
      { name: 'weight', label: 'Вес', fieldType: 'input', type: 'float', validators: [Validators.required, AppValidators.positiveFloat] },
      { name: 'nationality', label: 'Национальность', fieldType: 'select', options: enumService.getCountries(), validators: [] }
    ];
  }

  openLocationSelector(): void {
    const dialogData: EntitySelectorData = {
      entityName: 'location'
    };

    const selectorDialog = this.matDialog.open(EntitySelectorComponent, {
      width: '80vw',
      maxWidth: '800px',
      height: '80vh',
      maxHeight: '600px',
      data: dialogData
    });

    selectorDialog.afterClosed().subscribe((selectedLocation: Location | undefined) => {
      if (selectedLocation) {
        this.form.get('location')?.setValue(selectedLocation);
      }
    });
  }

  clearLocation(): void {
    this.form.get('location')?.setValue(null);
  }
  
  override ngOnInit(): void {
    super.ngOnInit(); 
  }
}
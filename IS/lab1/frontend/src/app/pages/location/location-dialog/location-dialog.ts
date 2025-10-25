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
import { Location } from '../../../core/models';
import { LocationService } from '../location.service';
import { FormFieldComponent, FormFieldConfig } from '../../../core/form-field/form-field.component';
import { AppValidators } from '../../../core/validation.utils';

@Component({
  selector: 'app-location-dialog',
  standalone: true,
  providers: [{ provide: BaseCrudService, useClass: LocationService }],
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
  styleUrls: ['./location-dialog.scss']
})
export class LocationDialogComponent extends BaseDialogComponent<Location> {
  override formFields: FormFieldConfig[] = [
    { name: 'name', label: 'Название', fieldType: 'input', type: 'text', validators: [Validators.required] },
    { name: 'x', label: 'x', fieldType: 'input', type: 'float', validators: [Validators.required, AppValidators.float] },
    { name: 'y', label: 'y', fieldType: 'input', type: 'float', validators: [Validators.required, AppValidators.float] }
  ];

  constructor(injector: Injector) {
    super(injector);
  }
}
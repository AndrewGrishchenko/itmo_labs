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
import { Coordinate, History } from '../../../core/models';
import { HistoryService } from '../history.service';
import { FormFieldComponent, FormFieldConfig } from '../../../core/form-field/form-field.component';
import { AppValidators } from '../../../core/validation.utils';

@Component({
  selector: 'app-history-dialog',
  standalone: true,
  providers: [{ provide: BaseCrudService, useClass: HistoryService }],
  imports: [
    CommonModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatProgressBarModule, MatSnackBarModule,
    FormFieldComponent
  ],
  templateUrl: '../../../core/base-dialog.html',
  styleUrls: ['./history-dialog.scss']
})
export class HistoryDialogComponent extends BaseDialogComponent<History> {
  override formFields: FormFieldConfig[] = [
    { name: 'id', label: 'ID', fieldType: 'text', validators: []},
    { name: 'x', label: 'X', fieldType: 'input', type: 'integer', validators: [Validators.required, AppValidators.integer] },
    { name: 'y', label: 'Y', fieldType: 'input', type: 'float', validators: [Validators.required, AppValidators.float, Validators.max(450)] }
  ]

  constructor(injector: Injector) {
    super(injector);
  }
}
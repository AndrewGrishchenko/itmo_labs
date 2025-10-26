import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ValidatorFn } from '@angular/forms';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-form-field',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatButtonModule
  ],
  template: `
    <div [formGroup]="form" [ngSwitch]="config.fieldType" class="form-field-wrapper">
      <mat-form-field *ngSwitchCase="'input'" appearance="fill">
        <mat-label>{{ config.label }}</mat-label>
        <input matInput
          [formControlName]="config.name"
          [type]="config.type === 'text' ? 'text' : 'number'"
          [attr.step]="config.type === 'float' ? 'any' : (config.type === 'integer' ? '1' : null)">
      </mat-form-field>

      <mat-form-field *ngSwitchCase="'select'" appearance="fill">
        <mat-label>{{ config.label }}</mat-label>
        <mat-select [formControlName]="config.name">
          <mat-option [value]="null">Не выбрано</mat-option>
          <mat-option *ngFor="let option of config.options" [value]="option">{{ option }}</mat-option>
        </mat-select>
      </mat-form-field>

      <mat-form-field *ngSwitchCase="'picker'"
                      appearance="fill"
                      (click)="form.get(config.name)?.enabled && onFieldClick()"
                      class="picker-field"
                      [class.mat-form-field-disabled]="form.get(config.name)?.disabled">
        <mat-label>{{ config.label }}</mat-label>
        <input matInput readonly 
             [value]="config.pickerDisplayValue ? 
                      config.pickerDisplayValue(form.get(config.name)?.value) : 
                      form.get(config.name)?.value?.name || ''"
             placeholder="Нажмите для выбора"
             [disabled]="form.get(config.name)?.disabled">
        <button *ngIf="form.get(config.name)?.value && form.get(config.name)?.enabled" 
             mat-icon-button matSuffix (click)="onClearClick($event)">
          <mat-icon>close</mat-icon>
        </button>
      </mat-form-field>

      <mat-form-field *ngSwitchCase="'text'">
        <mat-label>{{ config.label }}</mat-label>
        <input matInput
          [formControlName]="config.name"
          [type]="'text'">
      </mat-form-field>

    </div>
  `,
  styles: [`
    .form-field-wrapper, mat-form-field { width: 100%; }
    .picker-field { cursor: pointer; }
    .readonly-field {
      // background-color: #f5f5f5;
      color: #757575;
      cursor: not-allowed;
  
      &:focus {
        outline: none;
      }}
  `]
})
export class FormFieldComponent {
  @Input() form!: FormGroup;
  @Input() config!: FormFieldConfig;

  onFieldClick() {
    if (this.config.fieldType === 'picker' && this.config.onPickerClick) {
      this.config.onPickerClick();
    }
  }
  
  onClearClick(event: MouseEvent) {
    event.stopPropagation();
    this.form.get(this.config.name)?.setValue(null);
  }
}

export interface FormFieldConfig {
  name: string;
  label: string;
  fieldType: 'input' | 'select' | 'picker' | 'text';
  type?: 'text' | 'integer' | 'float';
  options?: any[];
  onPickerClick?: () => void;
  pickerDisplayValue?: (entity: any) => string;
  validators: ValidatorFn[];
}
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
    
        <mat-error *ngIf="form.get(config.name)?.hasError('required')">
          Поле не может быть пустым
        </mat-error>
        <mat-error *ngIf="form.get(config.name)?.hasError('max')">
          Максимальное значение: {{ form.get(config.name)?.errors?.['max'].max }}
        </mat-error>
        <mat-error *ngIf="form.get(config.name)?.hasError('min')">
          Минимальное значение: {{ form.get(config.name)?.errors?.['min'].min }}
        </mat-error>
        <mat-error *ngIf="form.get(config.name)?.hasError('isNotInteger')">
          Значение должно быть целым числом
        </mat-error>
        <mat-error *ngIf="form.get(config.name)?.hasError('isNotFloat')">
          Значение должно быть числом
        </mat-error>
        <mat-error *ngIf="form.get(config.name)?.hasError('isNotPositive')">
          Значение должно быть положительным
        </mat-error>
      </mat-form-field>

      <mat-form-field *ngSwitchCase="'select'" appearance="fill">
        <mat-label>{{ config.label }}</mat-label>
        <mat-select [formControlName]="config.name">
          <mat-option [value]="null">Не выбрано</mat-option>
          <mat-option *ngFor="let option of config.options" [value]="option">{{ option }}</mat-option>
        </mat-select>

        <mat-error *ngIf="form.get(config.name)?.hasError('required')">
          Поле не может быть пустым
        </mat-error>
      </mat-form-field>

      <mat-form-field *ngSwitchCase="'picker'"
                      appearance="fill"
                      (click)="form.get(config.name)?.enabled && onFieldClick()"
                      class="picker-field"
                      [class.mat-form-field-disabled]="form.get(config.name)?.disabled">
        
        <mat-label>{{ config.label }}</mat-label>
        
        <input matInput 
         [formControlName]="config.name" 
         class="hidden-control">
        
        <input matInput readonly 
         class="display-input"
         [value]="config.pickerDisplayValue ? 
                  config.pickerDisplayValue(form.get(config.name)?.value) : 
                  form.get(config.name)?.value?.name || ''"
         placeholder="Нажмите для выбора"
         [disabled]="form.get(config.name)?.disabled">

        <button *ngIf="form.get(config.name)?.value && config.onViewSelectedClick"
                mat-icon-button matSuffix
                (click)="onViewClick($event)">
          <mat-icon>visibility</mat-icon>
        </button>

        <!-- <input matInput readonly 
             [value]="config.pickerDisplayValue ? 
                      config.pickerDisplayValue(form.get(config.name)?.value) : 
                      form.get(config.name)?.value?.name || ''"
             placeholder="Нажмите для выбора"
             [disabled]="form.get(config.name)?.disabled"> -->

        <button *ngIf="form.get(config.name)?.value && form.get(config.name)?.enabled" 
             mat-icon-button matSuffix (click)="onClearClick($event)">
          <mat-icon>close</mat-icon>
        </button>

        <mat-error *ngIf="form.get(config.name)?.hasError('required')">
          Поле не может быть пустым
        </mat-error>
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
      color: #757575;
      cursor: not-allowed;
  
      &:focus {
        outline: none;
      }
    }
    
    .picker-field {
      position: relative;
      cursor: pointer;
    }

    .hidden-control {
      opacity: 0;
      height: 0;
      width: 0;
      padding: 0;
      border: none;
      position: absolute;
    }

    .display-input {
      position: relative;
      z-index: 2;
      background-color: transparent;
    }
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

  onViewClick(event: MouseEvent) {
    event.stopPropagation();
    if (this.config.onViewSelectedClick) {
      this.config.onViewSelectedClick(this.form.get(this.config.name)?.value);
    }
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
  onViewSelectedClick?: (entity: any) => void;
  validators: ValidatorFn[];
}
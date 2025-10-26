import { Component, Inject, Injector, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ValidatorFn } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize, takeUntil } from 'rxjs/operators';

import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

import { BaseCrudService } from './base-crud.service';
import { BaseEntity, Ownable } from './models';
import { EntityName } from './entity-config';
import { EntityService } from './entity.service';
import { FormFieldConfig } from './form-field/form-field.component'; 
import { Subject } from 'rxjs';

export interface FormField {
  name: string;
  label: string;
  type: 'text' | 'number';
  validators: ValidatorFn[];
}

export interface DialogData<T> {
  entity?: T;
  currentUserId: number | null;
  entityName: EntityName;
}

@Component({
  template: './base-dialog.html',
  standalone: true,
  imports: [ ]
})
export abstract class BaseDialogComponent<T extends BaseEntity> implements OnInit {

  abstract formFields: FormFieldConfig[];

  public entityDisplayName: string;
  form!: FormGroup;
  isEditMode!: boolean;
  isOwner!: boolean;
  isSaving = false;

  protected fb: FormBuilder;
  public dialogRef: MatDialogRef<BaseDialogComponent<T>>;
  public data: DialogData<T>;
  protected crudService: BaseCrudService<T, number>;
  protected snackBar: MatSnackBar;

  private destroy$ = new Subject<void>();

  constructor(protected injector: Injector) {
    this.fb = this.injector.get(FormBuilder);
    this.dialogRef = this.injector.get(MatDialogRef);
    this.data = this.injector.get(MAT_DIALOG_DATA);
    this.snackBar = this.injector.get(MatSnackBar);
    
    const entityService = this.injector.get(EntityService);
    this.crudService = entityService.getCrudService(this.data.entityName);
    this.entityDisplayName = this.data.entityName.charAt(0).toUpperCase() + this.data.entityName.slice(1);
  }

  ngOnInit(): void {
    this.fb = this.injector.get(FormBuilder);
    this.dialogRef = this.injector.get(MatDialogRef);
    this.data = this.injector.get(MAT_DIALOG_DATA);
    this.snackBar = this.injector.get(MatSnackBar);
    
    const entityService = this.injector.get(EntityService);
    this.crudService = entityService.getCrudService(this.data.entityName);
    this.entityDisplayName = this.data.entityName.charAt(0).toUpperCase() + this.data.entityName.slice(1);

    const formControls: { [key: string]: any } = {};
    this.formFields.forEach(field => {
        formControls[field.name] = [null, field.validators];
    });
    this.form = this.fb.group(formControls);
    
    this.isEditMode = !!this.data.entity;
    if (this.isEditMode && this.data.entity) {
      this.form.patchValue(this.data.entity);
      const ownableEntity = this.data.entity as Partial<Ownable>;
      this.isOwner = ownableEntity.owner?.id === this.data.currentUserId;
    } else {
      this.isOwner = true;
    }

    if (!this.isOwner) {
      this.form.disable();
    } else {
      this.form.enable();
    }

    const idControl = this.form.get('id');
    if (idControl) {
      idControl.disable();
    }

    this.dialogRef.backdropClick()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.dialogRef.close();
    });

    this.dialogRef.keydownEvents()
      .pipe(takeUntil(this.destroy$))
      .subscribe(event => {
        if (event.key === "Escape") {
          this.dialogRef.close();
        }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  
  onSave(): void {
    if (!this.form.valid || this.isSaving) return;
    this.isSaving = true;

    const request$ = this.isEditMode
      ? this.crudService.update(this.data.entity!.id, this.form.value)
      : this.crudService.create(this.form.value);

    request$.pipe(
      finalize(() => this.isSaving = false)
    ).subscribe({
      next: (resultEntity) => this.dialogRef.close(resultEntity),
      error: (err: HttpErrorResponse) => {
        const errorMessage = err.error?.message || 'Произошла неизвестная ошибка';
        this.snackBar.open(errorMessage, 'Закрыть', { duration: 5000, verticalPosition: 'top' });
      }
    });
  }

  onDelete(): void {
    if (this.data.entity) {
      this.dialogRef.close({ action: 'delete', entityId: this.data.entity.id });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { PersonComponent } from '../pages/person/person';
import { EntityName } from './entity-config';
import { MovieComponent } from '../pages/movie/movie';

export interface ResolveDependenciesDialogData {
  entityName: EntityName;
  entityId: number;
  entityDisplayName: string;
  dependencyCount: number;
  dependencyEntityName: EntityName;
  initialFilter: { [key: string]: any };
}

@Component({
  selector: 'app-resolve-dependencies-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatProgressBarModule,
    PersonComponent,
    MovieComponent
  ],
  templateUrl: './resolve-dependencies-dialog.component.html',
  styleUrls: ['./resolve-dependencies-dialog.component.scss']
})
export class ResolveDependenciesDialogComponent implements OnInit {

  initialFilter: any;

  constructor(
    public dialogRef: MatDialogRef<ResolveDependenciesDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ResolveDependenciesDialogData
  ) {}

  ngOnInit(): void {
    this.initialFilter = this.data.initialFilter;
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onRetryDelete(): void {
    this.dialogRef.close({ retry: true });
  }
  
  onConfirmDelete(): void {}
}
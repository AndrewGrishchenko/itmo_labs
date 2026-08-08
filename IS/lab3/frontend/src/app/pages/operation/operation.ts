import { CommonModule } from "@angular/common";
import { Component, inject } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { OperationService } from "./operation.service";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Movie, MovieGenre, Person } from "../../core/models";
import { HttpErrorResponse } from "@angular/common/http";
import { MatDialog } from "@angular/material/dialog";
import { AuthService } from "../../auth/auth";
import { ENTITY_CONFIGS } from "../../core/entity-config";
import { MatIconModule } from "@angular/material/icon";
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { EnumService } from "../../core/enum.service";

function genresMustBeDifferent(control: AbstractControl): ValidationErrors | null {
  const source = control.get('sourceGenre');
  const dest = control.get('destGenre');
  return source && dest && source.value === dest.value ? { genresAreSame: true } : null;
};

@Component({
    selector: 'app-operation',
    standalone: true,
    imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule
  ],
  templateUrl: './operation.html',
  styleUrls: ['./operation.scss']
})
export class OperationComponent {
  private operationsService = inject(OperationService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private enumService = inject(EnumService);

  isLoadingMinGenre = false;
  minGenreMovieResult: Movie | null = null;
  minGenreError: string | null = null;

  goldenPalmForm!: FormGroup;
  isLoadingGoldenPalm = false;
  goldenPalmResult: number | null = null;
  goldenPalmError: string | null = null;

  genreLessThanForm!: FormGroup;
  movieGenres: MovieGenre[] = [];
  isLoadingGenreLessThan = false;
  genreLessThanResult: number | null = null;
  genreLessThanError: string | null = null;

  isLoadingScreenwriters = false;
  screenwritersResult: Person[] | null = null;
  screenwritersError: string | null = null;

  redistributeForm!: FormGroup;
  isLoadingRedistribute = false;
  redistributeResult: number | null = null;
  redistributeError: string | null = null;

  ngOnInit(): void {
    this.goldenPalmForm = this.fb.group({
      count: [1, [Validators.required, Validators.min(1)]]
    });


    this.genreLessThanForm = this.fb.group({
      genre: [null, [Validators.required]]
    });

    this.movieGenres = this.enumService.getMovieGenres();


    this.redistributeForm = this.fb.group({
      sourceGenre: [null, Validators.required],
      destGenre: [null, Validators.required]
    }, { validators: genresMustBeDifferent });
  }

  onFindMovieWithMinGenre(): void {
    this.isLoadingMinGenre = true;
    this.minGenreMovieResult = null;
    this.minGenreError = null;

    this.operationsService.getMovieWithMinGenre().subscribe({
      next: (movie) => {
        this.minGenreMovieResult = movie;
        this.isLoadingMinGenre = false;
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 404) {
          this.minGenreError = 'Фильм не найден. Возможно, в базе нет фильмов с жанрами.';
        } else {
          this.minGenreError = 'Произошла ошибка при выполнении запроса.';
        }
        this.isLoadingMinGenre = false;
        this.snackBar.open(this.minGenreError, 'Закрыть', { duration: 5000 });
      }
    });
  }

  openMovieDialog(movie: Movie): void {
    const movieDialogComponent = ENTITY_CONFIGS['movie'].dialogComponent;
    
    const dialogData = {
      entity: movie,
      currentUserId: this.authService.getCurrentUser()?.id || null,
      entityName: 'movie'
    };

    this.dialog.open(movieDialogComponent, {
      width: '400px',
      disableClose: true,
      panelClass: 'app-base-dialog',
      data: dialogData
    });
  }

  onCountByGoldenPalm(): void {
    if (this.goldenPalmForm.invalid) {
      return;
    }
    
    this.isLoadingGoldenPalm = true;
    this.goldenPalmResult = null;
    this.goldenPalmError = null;
    
    const count = this.goldenPalmForm.value.count;

    this.operationsService.getCountByGoldenPalm(count).subscribe({
      next: (response) => {
        this.goldenPalmResult = response.count;
        this.isLoadingGoldenPalm = false;
      },
      error: (err) => {
        this.goldenPalmError = 'Произошла ошибка при выполнении запроса.';
        this.isLoadingGoldenPalm = false;
        this.snackBar.open(this.goldenPalmError, 'Закрыть', { duration: 5000 });
      }
    });
  }

  onCountGenreLessThan(): void {
    if (this.genreLessThanForm.invalid) {
      return;
    }

    this.isLoadingGenreLessThan = true;
    this.genreLessThanResult = null;
    this.genreLessThanError = null;
    
    const genre = this.genreLessThanForm.value.genre;

    this.operationsService.getCountByGenreLessThan(genre).subscribe({
      next: (response) => {
        this.genreLessThanResult = response.count;
        this.isLoadingGenreLessThan = false;
      },
      error: (err) => {
        this.genreLessThanError = 'Произошла ошибка при выполнении запроса.';
        this.isLoadingGenreLessThan = false;
        this.snackBar.open(this.genreLessThanError, 'Закрыть', { duration: 5000 });
      }
    });
  }

  openPersonDialog(person: Person): void {
    const personDialogComponent = ENTITY_CONFIGS['person'].dialogComponent;
    
    const dialogData = {
      entity: person,
      currentUserId: this.authService.getCurrentUser()?.id || null,
      entityName: 'person'
    };

    this.dialog.open(personDialogComponent, {
      width: '400px',
      disableClose: true,
      panelClass: 'app-base-dialog',
      data: dialogData
    });
  }

  onFindScreenwritersWithNoOscars(): void {
    this.isLoadingScreenwriters = true;
    this.screenwritersResult = null;
    this.screenwritersError = null;

    this.operationsService.getScreenwritersWithNoOscars().subscribe({
      next: (persons) => {
        this.screenwritersResult = persons;
        this.isLoadingScreenwriters = false;
      },
      error: (err) => {
        this.screenwritersError = 'Произошла ошибка при выполнении запроса.';
        this.isLoadingScreenwriters = false;
        this.snackBar.open(this.screenwritersError, 'Закрыть', { duration: 5000 });
      }
    });
  }

  onRedistributeOscars(): void {
    if (this.redistributeForm.invalid) {
      return;
    }

    this.isLoadingRedistribute = true;
    this.redistributeResult = null;
    this.redistributeError = null;

    const { sourceGenre, destGenre } = this.redistributeForm.value;

    this.operationsService.redistributeOscars(sourceGenre, destGenre).subscribe({
      next: (response) => {
        this.redistributeResult = response.movedOscars;
        this.isLoadingRedistribute = false;
        if (response.movedOscars > 0) {
            this.snackBar.open(`Успешно перераспределено Оскаров: ${response.movedOscars}`, 'OK', { duration: 5000 });
        } else {
            this.snackBar.open(`Нечего перераспределять (возможно, Оскаров слишком мало для равномерного деления).`, 'OK', { duration: 5000 });
        }
      },
      error: (err: HttpErrorResponse) => {
        this.redistributeError = err.error?.error || 'Произошла неизвестная ошибка.';
        this.isLoadingRedistribute = false;
      }
    });
  }
}
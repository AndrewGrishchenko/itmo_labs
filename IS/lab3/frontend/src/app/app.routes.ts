import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login';
import { RegisterComponent } from './auth/register/register';

import { CoordinatesComponent } from './pages/coordinates/coordinates';
import { LocationComponent } from './pages/location/location';
import { MovieComponent } from './pages/movie/movie';
import { PersonComponent } from './pages/person/person';
import { authGuard } from './auth/auth.guard';
import { OperationComponent } from './pages/operation/operation';
import { HistoryComponent } from './pages/history/history';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: 'coordinates', component: CoordinatesComponent, canActivate: [authGuard] },
  { path: 'person', component: PersonComponent, canActivate: [authGuard] },
  { path: 'location', component: LocationComponent, canActivate: [authGuard] },
  { path: 'movie', component: MovieComponent, canActivate: [authGuard] },
  { path: 'operation', component: OperationComponent, canActivate: [authGuard] },
  { path: 'history', component: HistoryComponent, canActivate: [authGuard]},

  { path: '', redirectTo: '/coordinates', pathMatch: 'full' },
];
import { Component, Type } from '@angular/core';

import { Coordinate, History, Location, Movie, Person, User } from './models';
import { SelectorColumn } from './entity-selector/entity-selector.config';

import { CoordinateDialogComponent } from '../pages/coordinates/coordinate-dialog/coordinate-dialog';
import { LocationDialogComponent } from '../pages/location/location-dialog/location-dialog';
import { PersonDialogComponent } from '../pages/person/person-dialog/person-dialog';
import { MovieDialogComponent } from '../pages/movie/movie-dialog/movie-dialog';
import { HistoryDialogComponent } from '../pages/history/history-dialog/history-dialog';

@Component({ template: '' })
class DummyDialogComponent {}

export interface EntityConfig {
  httpEndpoint: string;
  wsEndpoint: string;
  dialogComponent: Type<any>;
  tableColumns: string[];
  selectorColumns: SelectorColumn[];
}

export type EntityName = 'user' | 'coordinate' | 'location' | 'person' | 'movie' | 'history';

export const ENTITY_CONFIGS: Record<EntityName, EntityConfig> = {
  user: {
    httpEndpoint: '/api/auth/user',
    wsEndpoint: '',
    dialogComponent: DummyDialogComponent,
    tableColumns: [],
    selectorColumns: [
      { def: 'id', header: 'ID', cell: (e: User) => e.id.toString() },
      { def: 'username', header: 'username', cell: (e: User) => e.username }
    ]
  },

  coordinate: {
    httpEndpoint: '/api/coordinates',
    wsEndpoint: '/websocket/coordinates',
    dialogComponent: CoordinateDialogComponent,
    tableColumns: ['id', 'x', 'y', 'owner'],
    selectorColumns: [
      { def: 'id', header: 'ID', cell: (e: Coordinate) => e.id.toString() },
      { def: 'x', header: 'X', cell: (e: Coordinate) => e.x.toString() },
      { def: 'y', header: 'Y', cell: (e: Coordinate) => e.y.toString() },
      { def: 'owner', header: 'Owner', cell: (e: Coordinate) => e.owner.username }
    ]
  },

  location: {
    httpEndpoint: '/api/location',
    wsEndpoint: '/websocket/location',
    dialogComponent: LocationDialogComponent,
    tableColumns: ['id', 'name', 'x', 'y', 'owner'],
    selectorColumns: [
      { def: 'id', header: 'ID', cell: (e: Location) => e.id.toString() },
      { def: 'name', header: 'Название', cell: (e: Location) => e.name },
      { def: 'x', header: 'X', cell: (e: Location) => e.x.toString() },
      { def: 'y', header: 'Y', cell: (e: Location) => e.y.toString() },
      { def: 'owner', header: 'Владелец', cell: (e: Location) => e.owner.username },
    ]
  },

  person: {
    httpEndpoint: '/api/person',
    wsEndpoint: '/websocket/person',
    dialogComponent: PersonDialogComponent,
    tableColumns: ['id', 'name', 'eyeColor', 'hairColor', 'location', 'weight', 'nationality', 'owner'],
    selectorColumns: [
      { def: 'id', header: 'ID', cell: (e: Person) => e.id.toString() },
      { def: 'name', header: 'Имя', cell: (e: Person) => e.name },
      { def: 'eyeColor', header: 'Цвет глаз', cell: (e: Person) => e.eyeColor || '-' },
      { def: 'hairColor', header: 'Цвет волос', cell: (e: Person) => e.hairColor || '-' },
      { def: 'location', header: 'Локация', cell: (e: Person) => e.location?.name || '-' },
      { def: 'weight', header: 'Вес', cell: (e: Person) => e.weight.toString() },
      { def: 'nationality', header: 'Национальность', cell: (e: Person) => e.nationality || '—' },
      { def: 'owner', header: 'Владелец', cell: (e: Person) => e.owner.username },
    ]
  },

  movie: {
    httpEndpoint: '/api/movie',
    wsEndpoint: '/websocket/movie',
    dialogComponent: MovieDialogComponent,
    tableColumns: ['id', 'name', 'coordinates', 'creationDate', 'oscarsCount', 'budget', 'totalBoxOffice', 'mpaaRating', 'director', 'screenwriter', 'operator', 'length', 'goldenPalmCount', 'genre', 'owner'],
    selectorColumns: [
      { def: 'id', header: 'ID', cell: (e: Movie) => e.id.toString() },
      { def: 'name', header: 'Название', cell: (e: Movie) => e.name },
      { def: 'coordinates', header: 'Координаты id', cell: (e: Movie) => e.coordinates.id.toString() },
      { def: 'creationDate', header: 'creationDate', cell: (e: Movie) => e.creationDate },
      { def: 'oscarsCount', header: 'Количество оскаров', cell: (e: Movie) => e.oscarsCount.toString() },
      { def: 'budget', header: 'Бюджет', cell: (e: Movie) => e.budget.toString() },
      { def: 'totalBoxOffice', header: 'Касса', cell: (e: Movie) => e.totalBoxOffice.toString() },
      { def: 'mpaaRating', header: 'Рейтинг', cell: (e: Movie) => e.mpaaRating || '-' },
      { def: 'director', header: 'Директор', cell: (e: Movie) => e.director?.name || '-' },
      { def: 'screenwriter', header: 'Сценарист', cell: (e: Movie) => e.screenwriter?.name || '-' },
      { def: 'operator', header: 'Оператор', cell: (e: Movie) => e.operator?.name || '-' },
      { def: 'length', header: 'Длина', cell: (e: Movie) => e.length.toString() },
      { def: 'goldenPalmCount', header: 'Количество золотых пальм', cell: (e: Movie) => e.goldenPalmCount.toString() },
      { def: 'genre', header: 'Жанр', cell: (e: Movie) => e.genre || '-' },
      { def: 'owner', header: 'Владелец', cell: (e: Movie) => e.owner.username }
    ]
  },

  history: {
    httpEndpoint: '/api/history',
    wsEndpoint: '/websocket/history',
    dialogComponent: HistoryDialogComponent,
    tableColumns: ['id', 'creationDate', 'user', 'operationStatus', 'objectCount'],
    selectorColumns: [
      { def: 'id', header: 'ID', cell: (e: History) => e.id.toString() },
      { def: 'creationDate', header: 'creationDate', cell: (e: History) => e.creationDate },
      { def: 'user', header: 'user', cell: (e: History) => e.user.username },
      { def: 'operationStatus', header: 'operationStatus', cell: (e: History) => e.operationStatus },
      { def: 'objectCount', header: 'objectCount', cell: (e: History) => e.objectCount.toString() }
    ]
  }
};
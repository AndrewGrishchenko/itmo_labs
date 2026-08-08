export interface BaseEntity {
  id: number;
}

export interface User extends BaseEntity {
  username: string;
}

export interface Ownable extends BaseEntity {
  owner: User;
}

export interface Coordinate extends Ownable {
  x: number;
  y: number;
}

export interface Location extends Ownable {
  name: string;
  x: number;
  y: number;
}

export interface History extends BaseEntity {
  user: User;
  creationDate: string;
  operationStatus: string;
  objectCount: number;
}

export enum Color {
  GREEN = 'GREEN',
  RED = 'RED',
  BLACK = 'BLACK',
  BLUE = 'BLUE',
  WHITE = 'WHITE'
}

export enum Country {
  FRANCE = 'FRANCE',
  SPAIN = 'SPAIN',
  CHINA = 'CHINA',
  NORTH_KOREA = 'NORTH_KOREA'
}

export interface Person extends Ownable {
  name: string;
  eyeColor?: Color;
  hairColor?: Color;
  location?: Location;
  weight: number;
  nationality?: Country;
}

export enum MpaaRating {
  G = 'G',
  PG_13 = 'PG_13',
  NC_17 = 'NC_17'
}

export enum MovieGenre {
  ACTION = 'ACTION',
  WESTERN = 'WESTERN',
  DRAMA = 'DRAMA',
  HORROR = 'HORROR'
}

export enum OperationStatus {
  OK = 'OK',
  ERROR = 'ERROR'
}

export interface Movie extends Ownable {
  name: string;
  coordinates: Coordinate;
  creationDate: string;
  oscarsCount: number;
  budget: number;
  totalBoxOffice: number;
  mpaaRating?: MpaaRating;
  director?: Person;
  screenwriter?: Person;
  operator: Person;
  length: number;
  goldenPalmCount: number;
  genre?: MovieGenre;
}

export type NewEntity<T> = Omit<T, 'id' | 'owner'>;
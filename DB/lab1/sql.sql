BEGIN;

CREATE TABLE IF NOT EXISTS ships (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    area BOX NOT NULL
);

CREATE TABLE IF NOT EXISTS locations (
    id SERIAL PRIMARY KEY,
    ship_id INT REFERENCES ships(id),
    name TEXT UNIQUE NOT NULL,
    area BOX NOT NULL
);

CREATE TABLE IF NOT EXISTS actions (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS states (
    id SERIAL PRIMARY KEY,
    current_state TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS people (
    id SERIAL PRIMARY KEY,
    ship_id INT REFERENCES ships(id),
    name TEXT NOT NULL,
    location_id INT REFERENCES locations(id) NOT NULL,
    action_id INT REFERENCES actions(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS people_items (
    id SERIAL PRIMARY KEY,
    people_id INT REFERENCES people(id) NOT NULL,
    item TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS smoke_detectors (
    id SERIAL PRIMARY KEY,
    location_id INT REFERENCES locations(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS smoke_detectors_states (
    smoke_detector_id INT REFERENCES smoke_detectors(id),
    state_id INT REFERENCES states(id),
    CONSTRAINT smoke_detectors_states_key PRIMARY KEY (smoke_detector_id, state_id)
);

CREATE TABLE IF NOT EXISTS fire_sirens (
    id SERIAL PRIMARY KEY,
    ship_id INT REFERENCES ships(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS fire_sirens_alarms (
    id SERIAL PRIMARY KEY,
    fire_siren_id INT REFERENCES fire_sirens(id) NOT NULL,
    caused_by_id INT REFERENCES smoke_detectors(id) NOT NULL,
    alarm_ts TIMESTAMP NOT NULL
);


-- inserting

INSERT INTO ships(name, area) VALUES ('Discovery One', BOX(POINT(0.0, 0.0), POINT(100.0, 100.0)));
INSERT INTO locations(ship_id, name, area) VALUES (1, 'палуба', BOX(POINT(0.0, 0.0), POINT(100.0, 50.0))),
(1, 'туалет', BOX(POINT(0.0, 100.0), POINT(50.0, 100.0))),
(1, 'машинное отделение', BOX(POINT(50.0, 50.0), POINT(100.0, 100.0)));

INSERT INTO smoke_detectors(location_id) VALUES (1), (2), (3);
INSERT INTO states(current_state) VALUES ('работает'), ('уловил'), ('сломан');
INSERT INTO smoke_detectors_states(smoke_detector_id, state_id) VALUES (1, 1), (2, 2), (3, 3);
INSERT INTO fire_sirens(ship_id) VALUES (1);
INSERT INTO fire_sirens_alarms(fire_siren_id, caused_by_id, alarm_ts) VALUES (1, 2, TIMESTAMP '2010-06-10 03:23:12');

INSERT INTO actions(name) VALUES ('ничего не делать'), ('чинить двигатель'), ('курить');

INSERT INTO people(ship_id, name, location_ id, action_id) VALUES (1, 'Чандра', 2, 3), (1, 'Флойд', 3, 2);
INSERT INTO people_items(people_id, item) VALUES (1, 'сигары'), (2, 'сканворд'), (2, 'отвертка');


COMMIT;
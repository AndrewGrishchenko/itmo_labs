BEGIN;

CREATE TABLE IF NOT EXISTS ships (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    area BOX NOT NULL
);

CREATE TABLE IF NOT EXISTS locations (
    id SERIAL PRIMARY KEY,
    ship_id INT REFERENCES ships(id),
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
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS people_actions (
    people_id INT REFERENCES people(id) NOT NULL,
    action_id INT REFERENCES actions(id) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    location_id INT REFERENCES locations(id) NOT NULL,
    CONSTRAINT people_actions_key PRIMARY KEY (people_id, action_id)
);

CREATE TABLE IF NOT EXISTS items (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS people_items (
    people_id INT REFERENCES people(id) NOT NULL,
    item_id INT REFERENCES items(id) NOT NULL,
    CONSTRAINT people_items_key PRIMARY KEY (people_id, item_id)
);

CREATE TABLE IF NOT EXISTS smoke_detectors (
    id SERIAL PRIMARY KEY,
    location_id INT REFERENCES locations(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS smoke_detectors_states (
    smoke_detector_id INT REFERENCES smoke_detectors(id),
    state_id INT REFERENCES states(id),
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT smoke_detectors_states_key PRIMARY KEY (smoke_detector_id, state_id)
);

CREATE TABLE IF NOT EXISTS fire_alarms (
    id SERIAL PRIMARY KEY,
    ship_id INT REFERENCES ships(id) NOT NULL,
    caused_by_id INT REFERENCES smoke_detectors(id) NOT NULL,
    alarm_ts TIMESTAMP NOT NULL
);


-- trigger

CREATE OR REPLACE FUNCTION trigger_fire_alarm()
RETURNS TRIGGER AS $$
DECLARE
    ship_id int;
BEGIN
    IF (SELECT current_state FROM states WHERE id = NEW.state_id) = 'triggered' THEN
        SELECT l.ship_id INTO ship_id
        FROM locations l
        JOIN smoke_detectors sd ON l.id = sd.location_id
        WHERE sd.id = NEW.smoke_detector_id;
        
        INSERT INTO fire_alarms (ship_id, caused_by_id, alarm_ts)
        VALUES (ship_id, NEW.smoke_detector_id, NEW.timestamp);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER after_smoke_detector_state_insert
AFTER INSERT ON smoke_detectors_states
FOR EACH ROW
EXECUTE FUNCTION trigger_fire_alarm();


-- inserting

INSERT INTO ships(name, area) VALUES ('Discovery One', BOX(POINT(0.0, 0.0), POINT(100.0, 100.0)));
INSERT INTO locations(ship_id, area) VALUES (1, BOX(POINT(0.0, 0.0), POINT(100.0, 50.0))),
(1, BOX(POINT(0.0, 100.0), POINT(50.0, 100.0))),
(1, BOX(POINT(50.0, 50.0), POINT(100.0, 100.0)));

INSERT INTO smoke_detectors(location_id) VALUES (1), (2), (3);
INSERT INTO actions(name) VALUES ('ничего не делать'), ('чинить двигатель'), ('курить');
INSERT INTO people (name) VALUES ('Флойд'), ('Чандра');
INSERT INTO items (name) VALUES ('отвертка'), ('сигара');
INSERT INTO people_items (people_id, item_id) VALUES (1, 1), (2, 2);
INSERT INTO people_actions (people_id, action_id, timestamp, location_id) VALUES (1, 2, TIMESTAMP '2010-06-09 15:23:12', 3), (2, 3, TIMESTAMP '2010-06-10 03:23:12', 2);

INSERT INTO states(current_state) VALUES ('working'), ('triggered'), ('broken');
INSERT INTO smoke_detectors_states (smoke_detector_id, state_id, timestamp) VALUES (1, 3, TIMESTAMP '2010-06-02 04:23:15'), (2, 2, TIMESTAMP '2010-06-10 03:23:12'), (3, 1, TIMESTAMP '2010-04-10 13:23:12');

COMMIT;
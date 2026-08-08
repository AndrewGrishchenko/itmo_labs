#!/bin/bash

echo "Connecting to RW"

psql -h localhost -p 6432 -U postgres -d app_rw <<'SQL'

\echo '--- START TRANSACTION DEMO ---'

BEGIN;

INSERT INTO users(name) VALUES ('Script_User_1');
INSERT INTO orders(user_id, product) VALUES (1, 'Script_Product_1');

INSERT INTO users(name) VALUES ('Script_User_2');
INSERT INTO orders(user_id, product) VALUES (2, 'Script_Product_2');

COMMIT;

\echo '--- CHECK DATA ---'

SELECT * FROM users;
SELECT * FROM orders;

\echo '--- CHECK PRIMARY STATUS ---'
SELECT pg_is_in_recovery();

\echo '--- DELETE DATA ---'

BEGIN;

DELETE FROM users WHERE name='Script_User_1';
DELETE FROM orders WHERE product='Script_Product_1';

DELETE FROM users WHERE name='Script_User_2';
DELETE FROM orders WHERE product='Script_Product_2';

COMMIT;

SQL

echo "RW demo finished"

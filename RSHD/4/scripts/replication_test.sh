#!/bin/bash

echo "Writing to PRIMARY"

psql -h localhost -p 6432 -U postgres -d app_rw -c "
INSERT INTO users(name) VALUES ('Replication_Test_User');
"

echo "Waiting for replication"
sleep 0.5

echo "Checking on REPLICA"

psql -h localhost -p 6432 -U postgres -d app_ro -c "
SELECT * FROM users WHERE name = 'Replication_Test_User';
"

psql -h localhost -p 6432 -U postgres -d app_rw -c "
DELETE FROM users WHERE name='Replication_Test_User';
"

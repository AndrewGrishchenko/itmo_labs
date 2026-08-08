#!/bin/bash

echo "Connecting to RO"

psql -h localhost -p 6432 -U postgres -d app_ro <<'SQL'

\echo '--- READ DATA FROM REPLICA ---'

SELECT * FROM users;
SELECT * FROM orders;

\echo '--- CHECK REPLICA STATUS ---'
SELECT pg_is_in_recovery();

SQL

echo "RO demo finished"

#!/bin/bash

curl -u r2:r2 \
  -X POST http://localhost:8080/music \
  -H "Content-Type: application/json" \
  -d '{
    "name": "m2"
  }'

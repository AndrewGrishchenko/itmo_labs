#!/bin/bash

curl \
  -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "u1",
    "password": "u1"
  }'

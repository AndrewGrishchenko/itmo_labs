#!/bin/bash

curl -u u1:u1 \
  -X POST http://localhost:8080/video \
  -H "Content-Type: application/json" \
  -d '{
    "title": "title1",
    "music": ["a", "b", "m2"]
  }'

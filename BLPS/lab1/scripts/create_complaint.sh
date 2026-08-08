#!/bin/bash

curl -u r1:r1 \
  -X POST http://localhost:8080/complaint \
  -H "Content-Type: application/json" \
  -d '{
    "videoId": 4,
    "claimDetails": "abc"
  }'

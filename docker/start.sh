#!/bin/bash

start() {
  # run docker-compose
  docker-compose -f docker-compose.yml  --env-file config.env up -d
}

start
#!/bin/zsh

# # DOCKER_BUILDKIT=0 docker build -t adziggy:latest -f ./Dockerfile . --no-cache

# Define a function to execute the docker-compose down command when the script exits
function cleanup {
  docker-compose down
}

trap 'cleanup' INT

# ./gradlew build -x test

./gradlew clean build -x test
docker-compose build 
docker-compose up -d 
docker logs --follow dispatcher
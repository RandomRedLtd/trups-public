# TruPS backend

## About

TruPS backend is a Java based backend application running on Spring framework.

Main functionality of this application is performing training and inference on machine learning models.

The training and inference are done in Python, through `training.py` and `inference.py` scripts.

The main application spawns Python processes for both training and inference.

Other functionalities of the backend application are: 
- user authentication and authorization
- serving machine learning model client data
- machine learning model inference access control

## Building Docker image

Prerequisites: 
- JDK 21
- Docker

A script is provided to build the Docker image for TruPS backend, to build the image run the script: `./build-docker-image.sh`.

The built image is tagged as `trups-backend`.

## Running locally

Prerequisites for running TruPS backend locally is Docker.

A Docker compose file named `compose.yaml` is provided for easy local running of TruPS backend in a Docker container.

The `compose.yaml` comes pre-configured, so no environment variables have to be set.

To run TruPS in Docker run command `docker compose -f compose.yaml up`.

## Development

Prerequisites for the development of TruPS backend are: 

- JDK 21
- Docker
- Python 3.11.9

TruPS backend has one dependency, and that is Postgres, which can be ran using the `compose.yaml`, but with additional flag:
`docker compose -f compose.yaml --scale trups-backend=0`.

The `scale` flag for `trups-backend=0` makes sure that service doesn't start.

Additional settings for `application.yml` can be set by creating `application-local.yml` and setting the Spring profile to `local`.

Setting the Spring profile can be done through the editor (e.g. IntelliJ) or through custom `bootRun` flag `-Dspring.profiles.active=local`

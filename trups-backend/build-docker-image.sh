#!/bin/bash
./gradlew clean build
curl https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz -o jdk.tar.gz
tar -xzf jdk.tar.gz
rm -rf jdk.tar.gz
docker build --tag $TRUPS_BACKEND_IMAGE_TAG . --progress=plain --no-cache

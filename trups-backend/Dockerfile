FROM python:3.11.9-bullseye

WORKDIR /usr/local/app

COPY --chmod=777 ./build/libs/trups-backend-0.1.0-SNAPSHOT.jar ./
COPY --chmod=777 inference.py ./
COPY --chmod=777 training.py ./
COPY --chmod=777 requirements.txt ./
COPY --chmod=777 install_deps.sh ./
RUN mkdir /opt/jdk
COPY --chmod=777 jdk-21.0.2 /opt/jdk
RUN ln -s /opt/jdk/bin/java /usr/bin/java
RUN ./install_deps.sh

EXPOSE 8080

ENTRYPOINT ["java"]
CMD ["-jar", "/usr/local/app/trups-backend-0.1.0-SNAPSHOT.jar"]

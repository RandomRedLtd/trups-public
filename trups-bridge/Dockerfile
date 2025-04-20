FROM ubuntu:24.04

WORKDIR /usr/local/app

COPY --chmod=777 ./dist/trups-bridge ./

EXPOSE 8282

ENTRYPOINT ["./trups-bridge"]

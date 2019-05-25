FROM oracle/graalvm-ce:19.0.0 as graalvm
COPY . /home/app/catalog
WORKDIR /home/app/catalog
RUN gu install native-image
RUN native-image --no-server -cp build/libs/catalog-*.jar

FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/catalog .
ENTRYPOINT ["./catalog"]

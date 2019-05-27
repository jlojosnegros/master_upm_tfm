FROM oracle/graalvm-ce:19.0.0 as graalvm
COPY . /home/app/dynamic-data
WORKDIR /home/app/dynamic-data
RUN gu install native-image
RUN native-image --no-server -cp build/libs/dynamic-data-*.jar

FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/dynamic-data .
ENTRYPOINT ["./dynamic-data"]

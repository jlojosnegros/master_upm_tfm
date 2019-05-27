FROM oracle/graalvm-ce:19.0.0 as graalvm
COPY . /home/app/stream-control
WORKDIR /home/app/stream-control
RUN gu install native-image
RUN native-image --no-server -cp build/libs/stream-control-*.jar

FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/stream-control .
ENTRYPOINT ["./stream-control"]

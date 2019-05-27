FROM oracle/graalvm-ce:19.0.0 as graalvm
COPY . /home/app/api-gw
WORKDIR /home/app/api-gw
RUN gu install native-image
RUN native-image --no-server -cp build/libs/api-gw-*.jar

FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/api-gw .
ENTRYPOINT ["./api-gw"]

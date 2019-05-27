FROM oracle/graalvm-ce:19.0.0 as graalvm
COPY . /home/app/recommendations
WORKDIR /home/app/recommendations
RUN gu install native-image
RUN native-image --no-server -cp build/libs/recommendations-*.jar

FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/recommendations .
ENTRYPOINT ["./recommendations"]

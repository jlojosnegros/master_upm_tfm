FROM oracle/graalvm-ce:19.0.0 as graalvm
COPY . /home/app/user-categories
WORKDIR /home/app/user-categories
RUN gu install native-image
RUN native-image --no-server -cp build/libs/user-categories-*.jar

FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/user-categories .
ENTRYPOINT ["./user-categories"]

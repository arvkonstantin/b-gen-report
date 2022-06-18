FROM hseeberger/scala-sbt:17.0.2_1.6.2_2.13.8

COPY . b-gen-report

WORKDIR b-gen-report

RUN sbt assembly

EXPOSE 8080/tcp

ENTRYPOINT java -jar target/scala-2.13/b-gen-report-assembly-*.jar
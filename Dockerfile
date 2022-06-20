FROM hseeberger/scala-sbt:17.0.2_1.6.2_2.13.8

COPY . b-gen-report

WORKDIR b-gen-report

RUN sbt assembly

RUN apt-get update && apt-get upgrade -y && \
    apt-get install -y nodejs \
    npm

RUN apt-get install -y libnss3 \
    libnspr4 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libcups2 \
    libdrm2 \
    libxkbcommon0 \
    libxcomposite1 \
    libxdamage1 \
    libxfixes3 \
    libxrandr2 \
    libgbm1 \
    libasound2

RUN apt-get install libxss1

RUN npm i -g relaxedjs

EXPOSE 8080/tcp

ENTRYPOINT java -jar /root/b-gen-report/target/scala-2.13/b-gen-report-assembly-*.jar
FROM maven:3-jdk-8

ENV HOME=/home/usr/app

RUN mkdir -p $HOME

WORKDIR $HOME

# 1. add pom.xml only here

ADD pom.xml $HOME

# 2. start downloading dependencies

RUN ["/usr/local/bin/mvn-entrypoint.sh", "mvn", "verify", "clean", "--fail-never"]

# 3. add all source code and start compiling

ADD . $HOME

RUN ["mvn", "package"]

EXPOSE 8005

# 4. add any non-default input/output directories as arguments after the .jar in this format: [... .jar" , "input directory", "output-directory"]

ENTRYPOINT ["java", "-jar", "./target/converter-jar-with-dependencies.jar"]

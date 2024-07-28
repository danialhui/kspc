FROM openjdk:17-jdk-slim

WORKDIR .

COPY src/ObjectsSorter.java .
RUN javac ObjectsSorter.java
CMD ["java", "ObjectsSorter", "/data/in/my_random_objects.txt","/data/out/my_processed_objects.txt"]


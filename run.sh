#!/bin/bash

mkdir -p data/in
mkdir -p data/out
mkdir bin
javac src/RandomObjectGenerator.java -d bin
javac src/ObjectsSorter.java -d bin
# java -classpath bin RandomObjectGenerator data/in/my_random_objects.txt
# java -classpath bin ObjectsSorter data/my_random_objects.txt data/my_processed_objects.txt

docker build . -t sorter-object-app
docker run -v ./data:/data sorter-object-app

java -classpath bin RandomObjectGenerator data/in/my_random_objects.txt
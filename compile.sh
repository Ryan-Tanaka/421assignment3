#!/bin/bash

jdbclib="./mysql-connector-java-5.1.40-bin.jar"
antlrlib="./antlr-4.6-complete.jar"
parserlib="./commons-csv-1.4.jar"

javac -cp $jdbclib:$antlrlib:$parserlib:. *.java

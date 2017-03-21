#!/bin/bash

jdbclib="./mysql-connector-java-5.1.40-bin.jar"
antlrlib="./antlr-4.6-complete.jar"
parserlib="./commons-csv-1.4.jar"

#1 : cluster config
#2 : ddl, sql query, or csv file

java -cp $jdbclib:$antlrlib:$parserlib:. runSQL $1 $2 

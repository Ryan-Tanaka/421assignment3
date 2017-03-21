#!/bin/bash

jdbclib="./mysql-connector-java-5.1.40-bin.jar"
antlrlib="./antlr-4.6-complete.jar"

java -cp $jdbclib:$antlrlib:. runSQL clustercfg sqlfile_single

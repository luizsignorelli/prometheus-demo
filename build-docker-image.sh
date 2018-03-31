#!/bin/bash
PROJECT_ROOT=`dirname "$0"`
PROJECT_ROOT=`( cd "$PROJECT_ROOT" && pwd )`

cd $PROJECT_ROOT

mvn clean install -DskipTests
docker build -t "prometheus-demo" $PROJECT_ROOT
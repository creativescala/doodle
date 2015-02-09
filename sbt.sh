#!/usr/bin/env bash

java -Xmx3g -Xms3g -XX:+TieredCompilation -XX:ReservedCodeCacheSize=256m -XX:MaxPermSize=512m -XX:+UseNUMA -XX:+UseParallelGC -XX:+CMSClassUnloadingEnabled -jar sbt-launch.jar "$@"

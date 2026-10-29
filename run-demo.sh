#!/bin/sh
set -eu
rm -rf build
mkdir -p build
javac -d build $(find src/main/java -name '*.java')
java -cp build com.example.report.ReportApplication

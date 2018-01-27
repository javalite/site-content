#!/bin/sh
mvn clean install
rsync -r --delete target/output/ 192.168.85.10:/opt/javalite/content




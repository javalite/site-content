#!/bin/sh
./build.sh
rsync -r --delete output/ 192.168.85.10:/opt/javalite/content




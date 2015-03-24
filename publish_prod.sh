#!/bin/sh
./build.sh
rsync -r --delete output/ javalite@45.55.133.36:/opt/javalite/content




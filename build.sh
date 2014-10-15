#!/bin/sh
rm -rf output/*
for file in `ls src/activejdbc/*.md | tr '/' ' ' | awk '{print $3}'`; do  pandoc -f markdown -t html --template template.html  src/activejdbc/$file -o output/$file.html; done
for file in `ls src/activeweb/*.md | tr '/' ' ' | awk '{print $3}'`; do  pandoc -f markdown -t html --template template.html src/activeweb/$file -o output/$file.html; done

cp src/activejdbc/*.properties output/
cp src/activeweb/*.properties output/
echo "Rebuilt: `date`"

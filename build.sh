#rm -rf output/*
#for file in `ls src/activejdbc/* | tr '/' ' ' | awk '{print $3}'`; do  pandoc -f markdown -t html src/activejdbc/$file -o output/$file.html; done
for file in `ls src/activeweb/* | tr '/' ' ' | awk '{print $3}'`; do  pandoc -f markdown -t html src/activeweb/$file -o output/$file.html; done
cp src/index.html output
echo "Rebuilt: `date`"

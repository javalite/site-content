for file in $(ls src/**/*.md); do pandoc -f markdown -t html "${file}" -o "${file%md}html"; done
find src/activeweb/ -name '*.html' -exec mv {} output/activeweb/ \;
find src/activejdbc/ -name '*.html' -exec mv {} output/activejdbc/ \;

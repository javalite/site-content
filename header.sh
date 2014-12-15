#!/bin/sh
export NAME="`echo "$1"  | tr '/' ' '| awk '{print $3}' | tr '.' ' ' | awk '{print $1}' | python -c "print raw_input().capitalize()" | tr '_'  ' '`"

echo "<ol class="breadcrumb">"
echo  "   <li><a href="/">Home</a></li>"
echo  "   <li><a href="/activeweb">ActiveWeb</a></li>"
echo  "   <li class="active">"$NAME"</li>"
echo "</ol>"

echo "<div class="page-header">"
echo "   <h1>"$NAME" <small></small></h1>"
echo "</div>"

cat $1

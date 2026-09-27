#!/bin/sh

DIFF2XLS="${HOME}/lib/diff2xls-2.0-shaded.jar"
TEMPLATE="${HOME}/lib/template.xlsx"

java -jar ${DIFF2XLS} ${TEMPLATE} $*

set DIFF2XLS=%USERPROFILE%\lib\diff2xls-2.0-shaded.jar
set TEMPLATE=%USERPROFILE%\lib\template.xlsx

java -jar %DIFF2XLS% %TEMPLATE% %*

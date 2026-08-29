set DIFF2XLS=%USERPROFILE%\lib\diff2xls-1.3-shaded.jar
set TEMPLATE=%USERPROFILE%\lib\template.xlsx

java -jar %DIFF2XLS% %TEMPLATE% %*

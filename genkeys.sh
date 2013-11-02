if [ -z "$1" ]
  then
    echo "Usage: genkeys [certificate alias] [valid period (days)]"
else

if [ -z "$2" ]
  then
    echo "Usage: genkeys [certificate alias] [valid period (days)]"
else

tput bold
echo "Creating keystore..."
tput sgr0
keytool -genkeypair -alias $1 -keyalg RSA -validity $2 -keystore keystore
tput bold
echo "Exporting certificate..."
tput sgr0
keytool -export -alias $1 -keystore keystore -rfc -file temp.cer
tput bold
echo "Creating client truststore..."
tput sgr0
keytool -import -alias rsmcert -file temp.cer -keystore truststore
tput bold
echo "Cleaning up..."
tput sgr0
rm temp.cer
mv keystore serverkeystore
mv truststore clienttruststore
tput bold
echo "Key generation completed!"
tput sgr0

fi
fi

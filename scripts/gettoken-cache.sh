#!/bin/bash
set -e
if [ "$#" -eq 0 ]; then
	echo "Usage: $0 <productCode>"
	exit 1
fi
PRODUCT_CODE=$1
<<<<<<< HEAD
#KEY="TOKEN:PER_0F6169E1-FDD5-4DAF-BEC3-4126C6626752"
=======
>>>>>>> 97b8bf440036ea13a53cdcb4a18f77c7676700a9
KEY="TOKEN:PER_5C1491C4-8AA6-40DC-8CAF-F2806D4737C4"
TOKEN=`./gettoken-prod.sh`
#echo "TOKEN=$TOKEN"
CACHEREAD=`curl -s GET --header 'Content-Type: application/json' --header 'Accept: application/json' --header "Authorization: Bearer $TOKEN"  "http://alyson7.genny.life:10070/cache/${PRODUCT_CODE}/${KEY}"`
echo $CACHEREAD

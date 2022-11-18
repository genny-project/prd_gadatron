#!/bin/bash
set -e
<<<<<<< HEAD
realm=internmatch
realm=`echo $GENNY_KEYCLOAK_REALM`
password=`echo $GENNY_SERVICE_PASSWORD`
clientid=`echo $GENNY_CLIENT_ID`
secret=`echo $GENNY_CLIENT_SECRET`
keycloakurl=`echo $GENNY_KEYCLOAK_URL`
username=`echo $GENNY_SERVICE_USERNAME`
#echo "realm=$realm"
#echo "keycloakurl=$keycloakurl"
#echo "clientid=$clientid"
#echo "secret=$secret"
#echo "username=$username"
#echo "password=$password"
KEYCLOAK_RESPONSE=`curl -s -X POST ${keycloakurl}/realms/${realm}/protocol/openid-connect/token  -H "Content-Type: application/x-www-form-urlencoded" -d 'username='$username'' -d 'password='$password'' -d 'grant_type=password' -d 'client_id='$clientid''  -d 'client_secret='$secret''`
=======
realm=`echo $GENNY_KEYCLOAK_REALM`
username=`echo $TEST_USER_NAME`
password=`echo $TEST_USER_PASSWORD`
clientid=`echo $GENNY_CLIENT_ID`
secret=`echo $GENNY_CLIENT_SECRET`

KEYCLOAK_RESPONSE=`curl -s -X POST https://keycloak-bali.gada.io/realms/${realm}/protocol/openid-connect/token  -H "Content-Type: application/x-www-form-urlencoded" -d 'username='$username'' -d 'password='$password'' -d 'grant_type=password' -d 'client_id='$clientid''  -d 'client_secret='$secret''`
>>>>>>> 97b8bf440036ea13a53cdcb4a18f77c7676700a9

ACCESS_TOKEN=`echo "$KEYCLOAK_RESPONSE" | jq -r '.access_token'`
echo ${ACCESS_TOKEN}  


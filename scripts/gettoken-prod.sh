#!/bin/bash
set -e
realm=`echo $GENNY_KEYCLOAK_REALM`
username=`echo $GENNY_TEST_USERNAME`
password=`echo $GENNY_TEST_PASSWORD`
clientid=`echo $GENNY_CLIENT_ID`
secret=`echo $GENNY_CLIENT_SECRET`
keycloakurl=`echo $GENNY_KEYCLOAK_URL`

#echo "realm=$realm"
##echo "keycloakurl=$keycloakurl"
#echo "clientid=$clientid"
#echo "secret=$secret"
#echo "username=$username"
#echo "password=$password"

KEYCLOAK_RESPONSE=`curl -s -X POST ${keycloakurl}/realms/${realm}/protocol/openid-connect/token  -H "Content-Type: application/x-www-form-urlencoded" -d 'username='$username'' -d 'password='$password'' -d 'grant_type=password' -d 'client_id='$clientid''  -d 'client_secret='$secret''`

ACCESS_TOKEN=`echo "$KEYCLOAK_RESPONSE" | jq -r '.access_token'`
echo ${ACCESS_TOKEN}  


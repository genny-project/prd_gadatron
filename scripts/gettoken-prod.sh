#!/bin/bash
set -e

realm=`echo $GENNY_REALM`
username=`echo $TEST_USER_NAME`
password=`echo $TEST_USER_PASSWORD`
clientid=`echo $GENNY_CLIENT_ID`
secret=`echo $GENNY_CLIENT_SECRET`

KEYCLOAK_RESPONSE=`curl -s -X POST https://keycloak-bali.gada.io/realms/${realm}/protocol/openid-connect/token  -H "Content-Type: application/x-www-form-urlencoded" -d 'username='$username'' -d 'password='$password'' -d 'grant_type=password' -d 'client_id='$clientid''  -d 'client_secret='$secret''`

ACCESS_TOKEN=`echo "$KEYCLOAK_RESPONSE" | jq -r '.access_token'`
echo ${ACCESS_TOKEN}  


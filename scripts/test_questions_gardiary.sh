#!/bin/bash
productCode=${1:-gadatron}
questionCode=${2:-QUE_USER_DETAILS_GRP}
firstName=${3:-Gardiary}
targetCode=${4:-PER_4EBC2BAD-C668-4526-8F03-8192B6BE82F9}
sourceCode=PER_5C1491C4-8AA6-40DC-8CAF-F2806D4737C4
TOKEN=`./gettoken-cache.sh ${productCode}`
echo $TOKEN
payload="{\"data\":{\"sourceCode\":\"${sourceCode}\",\"targetCode\":\"${targetCode}\",\"code\":\"GADA_GAR_CREATE_PER_${questionCode}\",\"content\":\"${firstName}\"},\"token\":\"${TOKEN}\",\"msg_type\":\"EVT_MSG\"}"
echo $payload
echo  $payload > event.json

docker exec -i kafka bash -c " \
printf '%s\n%s\n' 'security.protocol=PLAINTEXT' 'sasl.mechanism=PLAIN' > prop && \
kafka-console-producer --producer.config=prop  --bootstrap-server localhost:9092 --topic events" < event.json

echo "Submit sent"
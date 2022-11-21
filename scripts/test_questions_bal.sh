#!/bin/bash
productCode=${1:-gadatron}
CONTENT=${2:-TAZ}
questionCode=${3:-GADA_TAZ_BPMN_TST}
targetCode=${4:-PER_949F7C8B-7F7F-4505-BDF7-117C3F1231EC}

sourceCode=-PER_5C1491C4-8AA6-40DC-8CAF-F2806D4737C4

TOKEN=`./gettoken-cache.sh ${productCode}`

echo $TOKEN
payload="{\"data\":{\"sourceCode\":\"${sourceCode}\",\"targetCode\":\"${targetCode}\",\"code\":\"${questionCode}\",\"content\":\"${CONTENT}\"},\"token\":\"${TOKEN}\",\"msg_type\":\"EVT_MSG\"}"
echo $payload
echo  $payload > event.json

docker exec -i kafka bash -c " \
printf '%s\n%s\n' 'security.protocol=PLAINTEXT' 'sasl.mechanism=PLAIN' > prop && \
kafka-console-producer --producer.config=prop  --bootstrap-server localhost:9092 --topic events" < event.json

echo "Submit sent"

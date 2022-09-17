#!/bin/bash
productCode=${1}
sourceCode=${2}
targetCode=${3}
questionCode=${4}

TOKEN=`./gettoken-cache.sh ${productCode}`

payload="{\"data\":{\"sourceCode\":\"${sourceCode}\",\"targetCode\":\"${targetCode}\",\"code\":\"${questionCode}\"},\"token\":\"${TOKEN}\",\"msg_type\":\"EVT_MSG\"}"
#echo $payload
echo  $payload > answer.json

docker exec -i kafka bash -c " \
printf '%s\n%s\n' 'security.protocol=PLAINTEXT' 'sasl.mechanism=PLAIN' > prop && \
kafka-console-producer --producer.config=prop  --bootstrap-server localhost:9092 --topic events" < answer.json

echo "Submit sent"

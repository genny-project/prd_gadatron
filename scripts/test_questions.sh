#!/bin/bash
productCode=${1:-gadatron}
questionCode=${2:-QUE_USER_DETAILS_GRP}
#targetCode=${3:-PER_5C1491C4-8AA6-40DC-8CAF-F2806D4737C4}
#sourceCode=PER_0F6169E1-FDD5-4DAF-BEC3-4126C6626752
targetCode=${3:-PER_4EBC2BAD-C668-4526-8F03-8192B6BE82F9}
sourceCode=PER_5C1491C4-8AA6-40DC-8CAF-F2806D4737C4
TOKEN=`./gettoken-cache.sh ${productCode}`
echo $TOKEN
payload="{\"data\":{\"sourceCode\":\"${sourceCode}\",\"targetCode\":\"${targetCode}\",\"code\":\"TESTQ_${questionCode}\"},\"token\":\"${TOKEN}\",\"msg_type\":\"EVT_MSG\"}"
echo $payload
echo  $payload > event.json

docker exec -i kafka bash -c " \
printf '%s\n%s\n' 'security.protocol=PLAINTEXT' 'sasl.mechanism=PLAIN' > prop && \
kafka-console-producer --producer.config=prop  --bootstrap-server localhost:9092 --topic events" < event.json

echo "Submit sent"

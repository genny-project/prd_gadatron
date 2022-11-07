#!/bin/bash

ENV_FILE=genny.env
name=prd_gadatron
echo "logging ${name}"
#ENV_FILE=$ENV_FILE docker-compose logs -f  ${name} 
#ENV_FILE=$ENV_FILE docker-compose logs -f $name 
docker logs -f prd_gadatron

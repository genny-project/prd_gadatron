#!/bin/bash
rm -Rf src/main/proto/*
./mvnw clean install -DskipTests=true -Dcheckstyle.skip -DresolutionFuzziness=life.genny

#!/bin/bash
./mvnw clean install -DskipTests=true -Dcheckstyle.skip -DresolutionFuzziness=life.genny

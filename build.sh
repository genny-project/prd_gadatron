#!/bin/bash
mvn clean install -DskipTests=true -Dcheckstyle.skip -DresolutionFuzziness=life.genny

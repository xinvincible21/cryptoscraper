#!/bin/bash
java -cp ~/cryptoscraper/target/scala-2.12/theorg-assembly-0.1.0-SNAPSHOT.jar com.invincible.FindGeminiPrices
status=$?
echo "exit status is "$status
if [[ "status" -eq 0 ]]; then
   ~/theorg/src/main/resources/run_server.sh restart
   exit;
fi

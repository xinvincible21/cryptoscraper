#!/bin/bash

start() {
    java -cp ~/cryptoscraper/target/scala-2.12/cryptoscraper-assembly-0.1.0-SNAPSHOT.jar com.invincible.CryptoApp > /tmp/server.log &
    PID=$!
    echo "starting $PID"
}

stop() {
   PID=`ps -eaf | grep 'com.invincible.CryptoApp' | grep -v grep | awk '{print $2}'`
   if [[ "" !=  "$PID" ]]; then
	   echo "killing $PID"
	   kill -9 $PID
   fi
}

case "$1" in
    start)
       start
       ;;
    stop)
       stop
       ;;
    restart)
       stop
       start
       ;;
    status)
       ps -eaf | grep 'com.invincible.CryptoApp' | grep -v grep
       ;;
    *)
       echo "Usage: $0 {start|stop|status|restart}"
esac

exit 0

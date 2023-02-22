#!/bin/bash

start() {
    java -cp ~/cryptoscraper/target/scala-2.12/theorg-assembly-0.1.0-SNAPSHOT.jar com.invincible.CryptoApp > /tmp/server.log &
}

stop() {
   kill $(ps aux | grep 'com.invincible.CryptoApp' | awk '{print $2}')
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
       ps aux | grep 'com.invincible.CryptoApp'
       ;;
    *)
       echo "Usage: $0 {start|stop|status|restart}"
esac

exit 0

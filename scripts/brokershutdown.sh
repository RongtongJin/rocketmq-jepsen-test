#!/bin/sh

pid=`ps ax | grep -i 'org.apache.rocketmq.broker.BrokerStartup' |grep java | grep -v grep | awk '{print $1}'`
if [ -z "$pid" ] ; then
        echo "No mqbroker running."
        exit 0;
fi

echo "The mqbroker(${pid}) is running..."

kill ${pid}

echo "Send shutdown request to mqbroker(${pid}) OK"
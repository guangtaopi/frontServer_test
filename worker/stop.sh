#!/bin/sh
ps -ef |grep com.v5.test.worker.Main|awk '{print $2}'|xargs kill -9
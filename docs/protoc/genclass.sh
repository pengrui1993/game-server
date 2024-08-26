#!/bin/bash

SRC_DIR=.
DST_DIR=./javaout

if [ ! -d $DST_DIR ];then
	mkdir $DST_DIR
fi
protoc -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/addressbook.proto

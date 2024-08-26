#!/bin/bash

SRC_DIR=.
DST_DIR=./out

if [ ! -d $DST_DIR ];then
	mkdir $DST_DIR
fi
protoc -I=$SRC_DIR --cpp_out=$DST_DIR $SRC_DIR/addressbook.proto

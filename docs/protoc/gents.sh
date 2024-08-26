#!/bin/bash

SRC_DIR=.
DST_DIR=./tsout
NODE_MODULE=./node_modules/.bin/protoc-gen-ts_proto

if [ ! -d $DST_DIR ];then
        mkdir $DST_DIR
fi
protoc \
    --plugin="${NODE_MODULE}" \
    --ts_proto_out="${DST_DIR}" \
    --ts_proto_opt=esModuleInterop=true \
    $SRC_DIR/addressBook.proto

#!/bin/bash



/*
npm install -g protobufjs
npm install protobufjs-cli -g
*/
pbjs -t static-module -w commonjs -o bundle.js *.proto
pbts -o bundle.d.ts bundle.js



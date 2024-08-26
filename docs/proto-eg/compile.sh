#!/bin/bash

protoc -I=. --cpp_out=. ./addressbook.proto
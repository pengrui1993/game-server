#!/bin/bash


PKG_CONFIG_PATH=$PKG_CONFIG_PATH:/usr/local/lib:/usr/local/lib/pkgconfig
export PKG_CONFIG_PATH

make $@
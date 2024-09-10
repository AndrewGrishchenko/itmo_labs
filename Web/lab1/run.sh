#!/bin/zsh

javac *.java
java -DFCGI_PORT=9884 FCGIServer
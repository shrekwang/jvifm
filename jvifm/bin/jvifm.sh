#!/bin/sh
cd /soft/jvifm/dist

java -cp ../lib/jintellitype-1.3.1.jar:../lib/jxgrabkey.jar:../lib/commons-vfs-20070730.jar:../lib/lucene-core-2.2.0.jar:../lib/commons-io-1.4.jar:../lib/commons-cli-1.1.jar:../lib/commons-logging.jar:../lib/dom4j.jar:../lib/jaxen-1.1-beta-9.jar:../lib/log4j-1.2.8.jar:../lib/swt-linux/swt.jar:../lib/jvifm.jar:../lib/apache-tool.jar  -Djava.library.path=../lib net.sf.jvifm.Main

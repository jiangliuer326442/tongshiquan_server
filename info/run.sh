#!/bin/bash
sudo su
rm -rf /usr/local/src/apache-tomcat-9.0.0.M21/webapps/rubyJSP.war
rm -rf /usr/local/src/apache-tomcat-9.0.0.M21/webapps/rubyJSP/
source /etc/profile
cd /home/ubuntu/web/tongshiquan_server/
ant

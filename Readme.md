# 卡拉布同事圈 后端
## 技术选型
卡拉布同事圈后端使用java开发，框架为自己开发的[rubyjps](https://github.com/jiangliuer326442/rubyJSP)框架
## 编译
使用gradle编译，windows环境下双击build.bat文件即可
## 部署
* docker pull jiangliuer326442/tomcat
* docker run -it -d -p 8080:8080 -v /data/wwwroot/webapps:/home/apache-tomcat-9.0.0.M26/webapps fanghailiang/tomcat
8080 tomcat对外暴露的端口
/home/apache-tomcat-9.0.0.M26/webapps tomcat网站目录存放的路径
# mina-app-server

mina-app-server 是一个 Java 的远程调用框架，基于 
[Apache Mina](http://mina.apache.org/)。它主要用于开发独立的远程调用的服务。

### 3.0.0 重大更新：

Mina-App-Server 将成为一个基于 Spring Boot 的框架！

#### （2018-06-12）:

  * 取消原有的 server.properties 配置文件，改为使用 Spring Boot 的 application.properties 
  进行配置，所有的配置以 `"mina-app-server"` 为前缀。对应的配置类为 
  `com.hyd.appserver.core.ServerConfiguration`
  * 在项目中实现 `com.hyd.appserver.MinaAppServerConfigurator` 接口可以做进一步配置。
  * favicon 图标来自 https://www.flaticon.com ，在 web 
  管理页面的 “关于” 页面上有说明。

#### （2017-07-06）3.0.0:

  * 更新 Apache Mina、fastjson 等依赖关系的版本到最新；
  * 引入 [NanoHTTP](https://github.com/NanoHttpd/nanohttpd) 作为管理界面服务器。
  
开发计划：

  * 下一个大版本将完全依赖 Spring。

详细信息请参考 [wiki](http://git.oschina.net/yidinghe/mina-app-server/wikis/home)。

本项目源代码托管的唯一地址是开源中国 git@osc，如果有什么疑问或 BUG 报告，请尽管在这里的 [Issues](http://git.oschina.net/yidinghe/mina-app-server/issues) 中讨论和提交。
# mina-app-server

mina-app-server 是一个 Java 的远程调用框架，基于 
[Apache Mina](http://mina.apache.org/) 。它主要用于开发独立的远程调用的服务。

## 适用场景：

希望快速搭建分布式服务，自带高可用和负载均衡，但不想像 dubbo 那样发布 jar 包。

## 主要特性：

- 基于 Spring Boot 来打包和启动；
- 基于 zookeeper 实现服务注册、发现、高可用和负载均衡；
- 通过注解来实现在浏览器上展示接口文档，以及对接口进行测试；

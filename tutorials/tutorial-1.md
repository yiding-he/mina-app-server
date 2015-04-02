# mina-app-server 使用说明

_本文档假设读者会使用 Maven。_

## 1. 介绍

mina-app-server 是一个远程服务调用框架，特点是：

1. 服务器可单独运行，也可以嵌入其他工程；
2. 使用 Annotation 定义接口的参数和返回值，快速开发业务逻辑；
3. 可通过浏览器打开管理界面，查看接口的执行效率和线程池状态；
4. 支持负载均衡，当一台服务器无法访问时将请求转发到其他服务器；
5. 客户端使用同样简单。

## 2. 搭建工程

下面介绍如何创建一个独立运行的 mina-app-server 服务器工程。

### 1) 创建 Maven 项目

因为对 POM 没有特殊要求，此处略过

### 2) 编辑 POM

#### a. 加入版本库

mina-app-server 没有加入 central repository，而是上传到了 oschina 搭建的 maven 库。你需要在 POM 的 `<project>` 中加入下面的元素

    <repositories>
        <repository>
            <id>nexus</id>
            <name>local private nexus</name>
            <url>http://maven.oschina.net/content/groups/public/</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>

#### b. 加入依赖关系

    <dependency>
        <groupId>com.hyd</groupId>
        <artifactId>mina-app-server</artifactId>
        <version>2.0.0</version>
    </dependency>

如果你获得的是已经编译好的 jar 文件，则将其放到项目的 local-lib 目录下（没有该目录则创建一个），并加入下面的依赖关系

    <dependency>
        <groupId>com.hyd</groupId>
        <artifactId>mina-app-server</artifactId>
        <version>LATEST</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/local-lib/mina-app-server-2.0.0.jar</systemPath>
    </dependency>

### 3) 
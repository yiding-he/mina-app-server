# 01 开始创建项目

_本文档假设读者会使用 Maven。_

### 一、下载并安装到本地 Maven 库

#### 1. 下载

    $ git clone https://gitee.com/yidinghe/mina-app-server.git

#### 2. 安装

```text
$ cd mina-app-server
$ mvn -Dmaven.test.skip=true install
```

### 二、新建一个空的 Spring Boot 项目

最简单的做法就是

    $ curl https://start.spring.io/starter.zip -o demo.zip
    
或者点击 https://start.spring.io/starter.zip 保存到任意位置，解压即可。

### 三、添加依赖关系

在 \<dependencies> 元素中加入下面的内容：

```xml
<dependency>
    <groupId>com.hyd</groupId>
    <artifactId>mina-app-server</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

### 四、修改配置文件

打开 `application.properties`，写入下面的内容

    mina-app-server.autostart=true

### 五、运行！

运行项目的主类即可。

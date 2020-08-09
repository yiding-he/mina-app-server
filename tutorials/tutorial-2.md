# 02 创建 Action

### 一、创建任意的类

例如：

```java
public class GetTime {
    
}
```

### 二、实现 Action 接口

Action 接口有一个叫做 execute 的方法需要实现。该方法接收一个 Request 参数，返回一个 Response 对象。

```java
public class GetTime implements Action {
    
    @Override
    public Response execute(Request request) throws Exception {
        return Response.success().put("now", new Date());
    }
}
```

### 三、添加注解

此时 GetTime 这个接口还不能被外部调用，需要添加 @Function 注解。

```java
@Function(value = "/base/get-time", description = "获取当前时间")
public class GetTime implements Action {
    
    @Override
    public Response execute(Request request) throws Exception {
        return Response.success().put("now", new Date());
    }
}
```

### 


# simplest-api-spring-boot-starter

基于 Spring Boot 4.x 快速构建 Web JSON API 的全能脚手架，解决重复繁琐工作，高效开发前后端分离接口。

## 特性

- 一键配置自定义 RESTful API 统一格式返回
- RESTful API 错误国际化（i18n）
- 全局异常处理 & 全局参数验证（快速失败模式）
- 业务错误断言工具封装，遵循错误优先返回原则
- Redis key/value 操作工具类，统一 key 管理 & Spring Cache 缓存实现
- RestTemplate 封装 POST/GET 请求工具
- 日志集成：自定义路径、按等级分类、压缩分割、按时间滚动
- 工具库集成：Lombok、Hutool v7、commons-lang3、Guava，无需单独引入
- MyBatis-Plus 一键代码生成
- 日志记录 & 服务监控，支持日志链路查询，自定义数据源
- OpenAPI 3 / Swagger 文档一键配置，零入侵 Javadoc 支持
- JWT 标准 Token 生成 & 权限认证
- 全局自定义 JSON 序列化（空值、浮点、时间等格式处理）
- 接口限流 & IP 城市回显（ip2region，支持 IPv4/IPv6）
- HttpUserAgent 请求设备工具封装
- RequestUtil 参数解析封装工具
- MapStruct-Plus 对象转换集成
- 空参数返回拦截（@EmptyParamReturn）
- 请求耗时统计拦截器

## 环境要求

| 依赖 | 版本 |
|------|------|
| Java | 17+ |
| Spring Boot | 4.1.0 |

## 升级说明

- springBoot升级到4.1.0后，springmvc json序列化升级到jackson3，会对以前ObjectMapper有影响，所以本项目默认使用jackson2，需要使用jackson3的需启用配置 `rest-api.json.useJackson3: true`
- springDoc使用调整为动态生成，原文档配置已废弃。
- hutool工具因v6版本后续会停止维护，已经升级到v7版本，包名发生改变，如果原项目依然使用v6版本，需引入v6版本的包。建议升级到v7版本。
- jjwt升级到0.13.0版本，此版本对密钥长度要求更严苛，如果原项目使用的密钥不符合h256规范，必须重新生成。现在JWT工具也废弃密钥类型定义，会根据密钥长度自动识别密钥长度。
- 

```## 快速开始

### 1. 引入依赖

```xml
<dependency>
    <groupId>cn.soboys</groupId>
    <artifactId>simplest-api-spring-boot-starter</artifactId>
    <version>4.1.0</version>
</dependency>
```

### 2. 启用功能

在 Spring Boot 启动类上添加 `@EnableRestFullApi` 注解即可一键开启：

```java
@EnableRestFullApi
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

### 3. 配置文件

```yaml
rest-api:
  enabled: true
  # 统一返回字段名自定义
  success: success
  code: code
  code-success-value: OK
  msg: msg
  timestamp: timestamp
  data: data
  # 分页字段
  page-wrap: true
  page-data: pageData
```

## 核心功能

### 统一响应格式

所有接口自动包装为统一 JSON 格式：

```json
{
  "success": true,
  "code": "OK",
  "msg": "操作成功",
  "requestId": "V1StGXR8_Z5jdHi6B-myT",
  "timestamp": "2026-08-11 10:30:00",
  "data": {}
}
```

分页响应额外包含分页信息：

```json
{
  "success": true,
  "code": "OK",
  "msg": "操作成功",
  "requestId": "V1StGXR8_Z5jdHi6B-myT",
  "timestamp": "2026-08-11 10:30:00",
  "previousPage": 1,
  "nextPage": 2,
  "pageSize": 10,
  "totalPageSize": 100,
  "hasNext": "true",
  "data": {},
  "pageData": []
}
```

使用 `Result` 工具类构建响应：

```java
// 成功
Result.buildSuccess();
Result.buildSuccess(data);
Result.buildSuccess("自定义消息", data);

// 失败
Result.buildFailure("FAIL", "操作失败");
Result.buildFailure(resultCode);

// 自定义
Result.build(true, "OK", "消息", data);
```

### @NoRestFulApi — 跳过统一包装

不希望被自动包装的接口，添加此注解：

```java
@NoRestFulApi
@GetMapping("/raw")
public String raw() {
    return "原始返回";
}
```

### 业务断言

遵循错误优先返回原则，断言失败抛出 `BusinessException`：

```java
Assert.notNull(user, "用户不存在");
Assert.isTrue(age > 0, "年龄必须大于0");
Assert.notBlank(name, "名称不能为空");
Assert.isFalse(exists, "记录已存在");
```

支持自定义错误码：

```java
Assert.isTrue(balance > amount, "余额不足", "INSUFFICIENT_BALANCE");
Assert.isTrue(order.isValid(), ResultCode.ORDER_INVALID);
```

### 全局异常处理

自动捕获并统一格式化以下异常：

- `BusinessException` — 业务异常
- `MethodArgumentNotValidException` — 参数校验异常
- `HttpRequestMethodNotSupportedException` — 请求方法不支持
- 其他未捕获异常

### 国际化（i18n）

错误消息支持多语言，通过请求头 `Lang` 切换语言：

```yaml
rest-api:
  i18n:
    default-lang: cn
    i18n-header: Lang
    message:
      internal_server_error:
        en: Internal Server Error {}
        cn: 系统错误 {}
      unauthorized:
        en: Unauthorized {}
        cn: 未授权 {}
```

### JWT 认证

```yaml
rest-api:
  jwt:
    expiration: 86400
    remember-me-expiration: 604800
    token-header: Token
    secret: your-secret-key
    authorization:
      has-authorization: true
      includes-url: /api/**
      excludes-url: /api/auth/**
```

权限注解：

```java
@hasRole("admin")
@hasAnyRoles({"admin", "editor"})
@hasPerm("user:delete")
@hasAnyPerm({"user:read", "user:write"})
@Authorities({@Authority(role = "admin"), @Authority(perm = "user:manage")})
```

### 接口限流

```java
@Limit(name = "登录接口", key = "login", period = 60, count = 5, limitType = LimitType.CUSTOMER)
@PostMapping("/login")
public Result login(@RequestBody LoginParam param) {
    // ...
}
```

### 日志记录

```java
@Log(value = "用户登录", apiType = LogApiTypeEnum.USER, CURDType = LogCURDTypeEnum.RETRIEVE, ipCity = true)
@PostMapping("/login")
public Result login(@RequestBody LoginParam param) {
    // ...
}
```

日志配置：

```yaml
rest-api:
  logging:
    path: /var/log/myapp
    max-history: 30
    max-file-size: 100MB
    max-total-size-cap: 1GB
    level-root: INFO
```

### OpenAPI 3 文档

```yaml
springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html

```

集成 `therapi-runtime-javadoc`，零入侵支持 Javadoc 注释自动生成文档。

### JSON 序列化

```yaml
rest-api:
  json:
    use-jackson3: false
    date-form: timestamp
    number-form: ",###.##"
    serializable-type:
      - NULLABLE
      - BIG_DECIMAL
      - DOUBLE
      - DATE
      - LOCAL_DATE_TIME
    null-able:
      has-null-able: true
      number-type: number
      array-type: array
      double-type: double
      object-type: string
```

### 请求耗时统计

```yaml
rest-api:
  invoke-time:
    enabled: true
    include-path:
      - /**
    exclude-path:
      - /error
      - /actuator/**
      - /v3/api-docs
```

### 空参数返回拦截

避免空指针，当方法参数为空时直接返回空结果而不执行方法体：

```java
@EmptyParamReturn
@GetMapping("/detail")
public Result detail(String id) {
    // id 为空时自动返回空结果，不进入方法体
    return Result.buildSuccess(userService.getById(id));
}

@EmptyParamReturn(fullMatch = true)
@GetMapping("/search")
public Result search(String keyword, String category) {
    // 所有参数都为空时才拦截
    return Result.buildSuccess(userService.search(keyword, category));
}
```

提供 `RedisTempUtil` 和 `SpringCacheUtil` 工具类，支持 `@CacheKey` 注解统一管理缓存 key。


### 自定义参数校验

内置扩展校验注解：

| 注解 | 说明 |
|------|------|
| `@IsMobile` | 手机号校验 |
| `@IsMoney` | 金额格式校验 |
| `@IsDateTime` | 日期时间格式校验 |
| `@IsCron` | Cron 表达式校验 |
| `@IsEnum` | 枚举值校验 |

### 工具类一览

| 工具类 | 说明 |
|--------|------|
| `RestFulTemp` | RestTemplate 封装 |
| `RedisTempUtil` | Redis 操作工具 |
| `SpringCacheUtil` | Spring Cache 工具 |
| `JwtUtil` | JWT Token 工具 |
| `Ip2RegionUtil` | IP 归属地查询 |
| `HttpUserAgent` | 请求设备解析 |
| `RequestUtil` | 请求参数解析 |
| `MyBatisPlusGenerator` | 代码生成 |
| `MapstructUtils` | 对象转换 |
| `CollUtil` / `StrUtil` / `NumberUtil` | 通用工具 |

## 相关文章

- [SpringBoot 定义优雅全局统一 Restful API 响应框架](https://mp.weixin.qq.com/s?__biz=Mzg4OTkwNjc2MQ==&mid=2247483741&idx=1&sn=2734d2ef008edcf1369dd7a31a88a142&chksm=cfe5f27bf8927b6d468a411fe2eaeeeb6dbdd5527dec77a77a0e580ea32b2b58f38922836552#rd)
- [SpringBoot 定义优雅全局统一 Restful API 响应框架二](https://mp.weixin.qq.com/s?__biz=Mzg4OTkwNjc2MQ==&mid=2247483752&idx=1&sn=eab94282e3f1e62682d2106cfb2949d1&chksm=cfe5f24ef8927b582e01863881a88452dcbcb102afdb9b50985304b97dfd74cd0c3ed0b8c2c3#rd)
- [SpringBoot 定义优雅全局统一 Restful API 响应框架三](https://mp.weixin.qq.com/s?__biz=Mzg4OTkwNjc2MQ==&mid=2247483761&idx=1&sn=dbc516d0ba14228c1f091dfa39d85209&chksm=cfe5f257f8927b41635fe1508c2829d73e2b788105b145bc51525be4574b83a1a8da23c49ec2#rd)
- [SpringBoot 定义优雅全局统一 Restful API 响应框架四](https://mp.weixin.qq.com/s?__biz=Mzg4OTkwNjc2MQ==&mid=2247483887&idx=1&sn=cb737d573adca7eaea7a59cad2a7bbfe&chksm=cfe5f2c9f8927bdfaf7e46fd38d26f56fffbb1ece8460eaffe2addee592e42ffee6ba49530b7#rd)
- [SpringBoot 定义优雅全局统一 Restful API 响应框架五](https://mp.weixin.qq.com/s?__biz=Mzg4OTkwNjc2MQ==&mid=2247484102&idx=1&sn=e17772a12e6548755c341c2d9300e235&chksm=cfe5f1e0f89278f669c4c89548b3fdeec4dd58d6d7d7580315d07f20c9b52ffdd21ef5a7c232&token=691863430&lang=zh_CN#rd)
- [SpringBoot 定义优雅全局统一 Restful API 响应框架六](https://mp.weixin.qq.com/s?__biz=Mzg4OTkwNjc2MQ==&mid=2247484160&idx=1&sn=37eea0079dd175634437f01dde38bb4c&chksm=cfe5f026f892793045fb242a556ce4e3010f4a4aae867a84fe3542ed18ac3c64b85e87fa536a#rd)

[详细使用文档官网](https://boot.soboys.cn/simplest/)

## 关注公众号

公众号 **程序员三时**，全网同名，用心分享持续输出优质内容。

![](https://images.soboys.cn/202307052317593.jpg)

## 赞赏支持

请作者喝一杯 ☕️

![](https://images.soboys.cn/202307102343241.png)

## License

[Apache License 2.0](https://www.apache.org/licenses/)
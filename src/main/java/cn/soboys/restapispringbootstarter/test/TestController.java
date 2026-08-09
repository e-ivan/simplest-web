package cn.soboys.restapispringbootstarter.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

/**
 * 测试接口控制器
 * <p>
 * 这里的 Javadoc 注释会自动作为 OpenAPI 的 Tag 描述，
 * 无需额外添加 @Tag 注解
 *
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/19 22:25
 * @webSite https://github.com/coder-amiao
 */
@RestController
public class TestController {

    /**
     * 获取测试实体数据
     * <p>
     * 方法上的 Javadoc 摘要会自动作为 @Operation(summary)，
     * 详细描述会作为 @Operation(description)，无需添加 @Operation 注解
     *
     * @return 测试实体对象，包含各种序列化场景的字段
     */
    @GetMapping("/test")
    public Entity test() {
        return new Entity(LocalDateTime.now(), new BigDecimal("99.99"), 20.469, Collections.emptyList(), 18, "编程", new Date());
    }

    /**
     * 根据ID查询实体
     * <p>
     * 通过路径变量查询指定ID的实体信息
     *
     * @param id   实体唯一标识，必填
     * @param name 名称过滤条件，可选
     * @return 匹配的实体对象
     */
    @GetMapping("/test/{id}")
    public Entity getById(@PathVariable Long id, @RequestParam(required = false) String name) {
        return new Entity(LocalDateTime.now(), new BigDecimal("199.99"), 30.5, Collections.emptyList(), 25, name, new Date());
    }

    /**
     * 创建实体
     * <p>
     * 接收请求体中的实体数据，创建新的实体记录
     *
     * @param entity 待创建的实体信息
     * @return 创建成功后的实体（带生成的时间戳）
     */
    @PostMapping("/test")
    public Entity create(@RequestBody Entity entity) {
        entity.setCreateTime(LocalDateTime.now());
        return entity;
    }

    /**
     * 简单文本响应
     * <p>
     * 用于健康检查或连通性测试
     *
     * @return 固定的 hello 字符串
     */
    @GetMapping("/hello")
    public String hello() {
        return "hello, simplest-api";
    }

}
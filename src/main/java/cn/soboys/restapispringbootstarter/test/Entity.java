package cn.soboys.restapispringbootstarter.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


/**
 * 测试实体类
 * <p>
 * 类上的 Javadoc 注释会自动作为 Schema 描述。
 * 每个字段上的 Javadoc 注释会自动作为 @Schema(description)，
 * 无需在字段上添加 @Schema 注解。
 *
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/21 18:06
 * @webSite https://github.com/coder-amiao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Entity {

    /**
     * 创建时间
     * <p>
     * 记录实体首次创建的时间戳，使用 ISO-8601 格式
     */
    private LocalDateTime createTime;

    /**
     * 价格
     * <p>
     * 商品单价，精确到小数点后两位，货币单位：元
     */
    private BigDecimal price;

    /**
     * SKU 数值
     * <p>
     * 库存单位对应的数值标识，使用浮点数以兼容多种业务场景
     */
    private Double sku;

    /**
     * 标签列表
     * <p>
     * 实体关联的多个标签，支持动态扩展
     */
    private List t;

    /**
     * 年龄
     * <p>
     * 用户年龄，单位：岁，取值范围 0-150
     */
    private Integer age;

    /**
     * 爱好
     * <p>
     * 用户个人兴趣爱好的文本描述
     */
    private String hobby;

    /**
     * 更新日期
     * <p>
     * 实体最近一次修改的日期（旧版兼容字段，建议使用 createTime）
     */
    private Date updateDate;
}
package cn.soboys.restapispringbootstarter.exception;

import cn.soboys.restapispringbootstarter.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.soboys.restapispringbootstarter.utils.StrUtil;

import java.io.Serial;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/6/26 16:45
 * @webSite https://github.com/coder-amiao
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5160547564767261240L;
    private Object data;

    /**
     * 错误码
     */
    private String code = "20000";

    /**
     * 错误提示
     */
    private String message;


    public BusinessException(String message) {
        this.message = message;

    }

    public BusinessException(String message, String code) {
        this.message = message;
        this.code = code;

    }

    public BusinessException(ResultCode resultCode) {
        this.message = resultCode.getMessage();
        this.code = resultCode.getCode();
    }


    public BusinessException(ResultCode resultCode, String message) {
        this(message, resultCode.getCode());
    }

    public BusinessException(ResultCode resultCode, String messageTemplate, Object... params) {
        this(StrUtil.format(messageTemplate, params), resultCode.getCode());
    }
}

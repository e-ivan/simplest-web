package cn.soboys.restapispringbootstarter;

import cn.hutool.v7.core.exception.ExceptionUtil;
import cn.hutool.v7.http.server.servlet.ServletUtil;
import cn.soboys.restapispringbootstarter.auth.UserContextSupport;
import cn.soboys.restapispringbootstarter.config.RestApiProperties;
import cn.soboys.restapispringbootstarter.exception.BusinessException;
import cn.soboys.restapispringbootstarter.exception.CacheException;
import cn.soboys.restapispringbootstarter.exception.LimitAccessException;
import cn.soboys.restapispringbootstarter.utils.CollUtil;
import cn.soboys.restapispringbootstarter.utils.RequestUtil;
import cn.soboys.restapispringbootstarter.utils.StrUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.*;

/**
 * @author E_Ivan
 * @date 2024/12/15 19:21
 */
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Slf4j
public class ExceptionHandler {
    @Resource
    private RestApiProperties restApiProperties;

    /**
     * 验证 单个参数类型
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    public Result constraintViolationExceptionHandler(ConstraintViolationException e, HttpServletRequest request) {
        List errorList = new ArrayList<>();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            Path.Node leafNode = violation.getPropertyPath().iterator().next();
            String msg = leafNode.getName() + violation.getMessage();
            errorList.add(msg);
        }
        request.setAttribute("argument_error", CollUtil.join(errorList, ";"));
        return Result.buildFailure(HttpStatus.INVALID_ARGUMENT.getCode(),
                StrUtil.format(HttpStatus.INVALID_ARGUMENT.getMessage(), CollUtil.join(errorList, ";")));
    }


    /**
     * 接口不存在
     *
     * @param e
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(NoHandlerFoundException.class)
    public Result noHandlerFoundException(NoHandlerFoundException e) {
        return Result.buildFailure(HttpStatus.NOT_FOUND, e.getRequestURL());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NoResourceFoundException.class)
    public Result noResourceFoundException(NoResourceFoundException e) {
        return Result.buildFailure(HttpStatus.NOT_FOUND, e.getResourcePath());
    }

    /**
     * 请求方法不被允许
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return Result.buildFailure(HttpStatus.METHOD_NOT_ALLOWED, ExceptionUtil.stacktraceToString(e));
    }

    /**
     * 请求与响应媒体类型不一致 异常
     *
     * @param e
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return Result.buildFailure(HttpStatus.BAD_GATEWAY, ExceptionUtil.stacktraceToString(e));
    }

    /**
     * body json参数解析异常
     *
     * @param e
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(HttpMessageNotReadableException.class)
    public Result httpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        request.setAttribute("argument_error", e.getMessage());
        return Result.buildFailure(HttpStatus.INVALID_ARGUMENT.getCode(),
                StrUtil.format(HttpStatus.INVALID_ARGUMENT.getMessage(), e.getMessage()), ExceptionUtil.stacktraceToString(e));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(LimitAccessException.class)
    public Result limitAccessExceptionException(LimitAccessException e, HttpServletRequest request) {
        request.setAttribute("argument_error", e.getMessage());
        return Result.buildFailure(HttpStatus.REQUEST_TIMEOUT.getCode(),
                StrUtil.format(HttpStatus.REQUEST_TIMEOUT.getMessage(), e.getMessage()), ExceptionUtil.stacktraceToString(e));
    }


    /**
     * 统一业务异常处理
     *
     * @param e
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(CacheException.class)
    public Result cacheException(CacheException e, HttpServletRequest request) {
        request.setAttribute("argument_error", e.getMessage());
        return Result.buildFailure(HttpStatus.CACHE_EXCEPTION.getCode(),
                StrUtil.format(HttpStatus.CACHE_EXCEPTION.getMessage(), e.getMessage()), ExceptionUtil.stacktraceToString(e));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public Result error(Exception e) {
        HttpServletRequest req = RequestUtil.getReq();
        String uri = "空";
        String ip = "未知";
        if (Objects.nonNull(req)) {
            uri = req.getRequestURI();
            ip = ServletUtil.getClientIP(req);
        }
        log.error("uri:{} 未知异常 IP:{} {}", uri, ip, Optional.ofNullable(UserContextSupport.getInstance().userId(false)).map(id -> "userId:" + id).orElse(""), e);
        return Result.buildFailure(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 统一业务异常处理
     *
     * @param e
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    public Result businessError(BusinessException e, HttpServletRequest request) {
        if (StrUtil.equals(restApiProperties.getCodeSuccessValue(), e.getCode())) {
            return Result.buildSuccess(e.getMessage(), e.getData());
        }
        log.error("uri:{} 业务异常:{} {}", request.getRequestURI(), e.getMessage(), Optional.ofNullable(UserContextSupport.getInstance().userId(false)).map(id -> "userId:" + id).orElse(""));
        return Result.buildFailure(e.getCode(), e.getMessage(), e.getData());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public Result jsonParamsException(MethodArgumentNotValidException e, HttpServletRequest request) {
        return buildBindResult(e, request);
    }

    /**
     * 验证  对象类型参数
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(BindException.class)
    public Result bindExceptionHandler(BindException e, HttpServletRequest request) {
        return buildBindResult(e, request);
    }

    private static Result buildBindResult(BindException e, HttpServletRequest request) {
        List<String> errorList = new ArrayList<>();
        List<String> userErrorList = new ArrayList<>();

        for (FieldError fieldError : e.getFieldErrors()) {
            String msg = String.format("%s%s；", fieldError.getField(), fieldError.getDefaultMessage());
            errorList.add(msg);
            userErrorList.add(fieldError.getDefaultMessage());
        }
        request.setAttribute("argument_error", CollUtil.join(errorList, ";"));
        if (log.isDebugEnabled()) {
            log.debug("uri:{} 参数异常：{} {}", request.getRequestURI(), CollUtil.join(errorList, ";"), Optional.ofNullable(UserContextSupport.getInstance().userId(false)).map(id -> "userId:" + id).orElse(""));
        }
        return Result.buildFailure(HttpStatus.INVALID_ARGUMENT.getCode(), CollUtil.join(userErrorList, ";"));
    }


}
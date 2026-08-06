package cn.soboys.restapispringbootstarter.interceptor;

import cn.soboys.restapispringbootstarter.filter.RepeatedlyRequestWrapper;
import cn.soboys.restapispringbootstarter.utils.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.v7.core.date.StopWatch;
import cn.hutool.v7.core.io.IoUtil;
import cn.hutool.v7.core.map.MapUtil;
import cn.hutool.v7.core.util.ObjUtil;
import cn.hutool.v7.json.JSONUtil;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.util.Map;

/**
 * web的调用时间统计拦截器
 *
 * @author Lion Li
 * @since 3.3.0
 */
@Slf4j
public class WebInvokeTimeInterceptor implements HandlerInterceptor {

    private final static ThreadLocal<StopWatch> KEY_CACHE = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getMethod() + " " + request.getRequestURI();

        // 打印请求参数
        if (isJsonRequest(request)) {
            String jsonParam = "";
            if (request instanceof RepeatedlyRequestWrapper) {
                BufferedReader reader = request.getReader();
                jsonParam = IoUtil.read(reader);
            }
            log.info("开始请求 => URL[{}],参数类型[json],参数:[{}]", url, jsonParam);
        } else {
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (MapUtil.isNotEmpty(parameterMap)) {
                String parameters = JSONUtil.toJsonStr(parameterMap);
                log.info("开始请求 => URL[{}],参数类型[param],参数:[{}]", url, parameters);
            } else {
                log.info("开始请求 => URL[{}],无参数", url);
            }
        }

        StopWatch stopWatch = new StopWatch();
        KEY_CACHE.set(stopWatch);
        stopWatch.start();

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        StopWatch stopWatch = KEY_CACHE.get();
        if (ObjUtil.isNotNull(stopWatch)) {
            stopWatch.stop();
            log.info("结束请求 => URL[{}],耗时:[{}]毫秒", request.getMethod() + " " + request.getRequestURI(), stopWatch.getTotalTimeMillis());
            KEY_CACHE.remove();
        }
    }

    /**
     * 判断本次请求的数据类型是否为json
     *
     * @param request request
     * @return boolean
     */
    public static boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType != null) {
            return StrUtil.startWithIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE);
        }
        return false;
    }

}

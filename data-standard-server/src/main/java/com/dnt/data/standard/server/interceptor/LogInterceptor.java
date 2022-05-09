package com.dnt.data.standard.server.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Map;


/**
 * @description: 业务方法拦截器-统计执行耗时 <br>
 * @date: 2021/7/9 下午2:46 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Long> startTimeThreadLocal =
            new NamedThreadLocal<>("ThreadLocal StartTime");
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (log.isInfoEnabled()) {
            // 1、开始时间
            long beginTime = System.currentTimeMillis();
            // 线程绑定变量（该数据只有当前请求的线程可见）
            startTimeThreadLocal.set(beginTime);
            log.info("开始计时: {}  URI: {}",
                    new SimpleDateFormat("hh:mm:ss.SSS").format(beginTime),
                    request.getRequestURI());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            log.info("ViewName: " + modelAndView.getViewName());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        // 保存日志--打印日志
        Map<String,String[]> params = request.getParameterMap();
        log.info("操作日志: title:{}, type:{}, userAgent:{},requestURL:{},params:{},method:{}",
                "日志操作", ex == null ? "ACCESS" : "EXCEPTION",
                request.getHeader("user-agent"), request.getRequestURI(), request.getParameterMap(), request.getMethod());
        // 打印JVM信息。
        if (log.isInfoEnabled()) {
            // 得到线程绑定的局部变量（开始时间）
            long beginTime = startTimeThreadLocal.get();
            // 2、结束时间
            long endTime = System.currentTimeMillis();
            log.info("计时结束：{}  耗时：{}  URI: {}  最大内存: {}m  已分配内存: {}m  已分配内存中的剩余空间: {}m  最大可用内存: {}m",
                    new SimpleDateFormat("hh:mm:ss.SSS").format(endTime),
                    formatDateTime(endTime - beginTime),
                    request.getRequestURI(),
                    Runtime.getRuntime().maxMemory() / 1024 / 1024,
                    Runtime.getRuntime().totalMemory() / 1024 / 1024,
                    Runtime.getRuntime().freeMemory() / 1024 / 1024,
                    (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1024 / 1024);
        }
    }


    /**
     * 转换为时间（天,时:分:秒.毫秒）
     *
     * @param timeMillis 时刻
     * @return 格式化后时间
     */
    public String formatDateTime(long timeMillis) {
        long day = timeMillis / (24 * 60 * 60 * 1000L);
        long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
        long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (timeMillis / 1000 - day * 24 * 60 * 60L - hour * 60 * 60 - min * 60);
        long sss = (timeMillis - day * 24 * 60 * 60 * 1000L - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

}

package com.dnt.data.standard.server.config;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.interceptor.LogInterceptor;
import com.dnt.data.standard.server.web.ResultCode;
import com.google.protobuf.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@Configuration
@EnableWebMvc
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {
    /**tomcat下没有 此设置**/
    @Value("${spring.profiles.active}")
    /**当前激活的配置文件**/
    private String env;
    @Value("${dnt.feishui.notice.url}")
    private String feishuUrl;
    /**
     * 请求参数的转换
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.configureMessageConverters(converters);
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        /**自定义fastjson配置**/
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty,
                /** 将数值类型字段的空值输出为0**/
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteDateUseDateFormat,
                /** 禁用循环引用**/
                SerializerFeature.DisableCircularReferenceDetect,
                /**忽略为空的属性向前台返回**/
                SerializerFeature.IgnoreNonFieldGetter
        );
        fastJsonHttpMessageConverter.setFastJsonConfig(config);
        /**converters.add(fastJsonHttpMessageConverter);**/

        /** 添加 StringHttpMessageConverter，解决中文乱码问题**/
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        converters.add(stringHttpMessageConverter);
        /**Long类型的数据转字符串**/
        FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fjc = new FastJsonConfig();
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(Long.class , ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE , ToStringSerializer.instance);
        fjc.setSerializeConfig(serializeConfig);
        fastJsonConverter.setFastJsonConfig(fjc);
        converters.add(fastJsonConverter);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**跨域**/
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        /**设置允许访问的ip与域名信息**/
        registry.addMapping("/**").allowCredentials(true)
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .maxAge(3600);
    }

    /**统一异常处理**/
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new HandlerExceptionResolver() {
            @Override
            public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
                R r = new R();
                String message="";
                if (e instanceof ServiceException) {/**业务失败的异常，如“账号或密码错误”**/
                    message = e.getMessage();
                    r.setCode(ResultCode.FAIL.code()).setMsg(message);
                    log.info(message);
                } else if (e instanceof NoHandlerFoundException) {
                    message = "接口 [" + request.getRequestURI() + "] 不存在";
                    r.setCode(ResultCode.NOT_FOUND.code()).setMsg(message).setData(message);
                } else if (e instanceof ServletException) {
                    message = e.getMessage();
                    r.setCode(ResultCode.FAIL.code()).setMsg("业务层接口出错，请排查一下操作日志");
                }else if (e instanceof HttpMessageNotReadableException) {
                    message = e.getMessage();
                    r.setCode(ResultCode.FAIL.code()).setMsg("调用接口时入参数信息转成JSON异常，请检查入参信息");
                } else {
                    message = "接口 [" + request.getRequestURI() + "] 内部错误，请排查一下操作日志";
                    r.setCode(ResultCode.INTERNAL_SERVER_ERROR.code()).setMsg(message);
                    if (handler instanceof HandlerMethod) {
                        HandlerMethod handlerMethod = (HandlerMethod) handler;
                        message = String.format("接口 [%s] 出现异常",request.getRequestURI());

                        r.setData(message);
                        message+=String.format("，方法：%s.%s，异常摘要：%s",
                                handlerMethod.getBean().getClass().getName(),
                                handlerMethod.getMethod().getName(),
                                e.getMessage());
                    }else {
                        message = e.getMessage();
                        r.setMsg(message);
                    }

                    log.error(message, e);
                }
                responseResult(response, r);
                /**发送告警通知**/
                sendFeiShu(message);
                return new ModelAndView();
            }



        });
    }

    /**发送告警通知**/
    /**{"msg_type":"text","content":{"text":"测试通知"}}**/
    private void sendFeiShu(String message) {

        JSONObject mes = new JSONObject();
        mes.put("text",message);
        JSONObject mesj = new JSONObject();
        mesj.put("msg_type","text");
        mesj.put("content",mes);
        
        String str1 = HttpRequest.post(feishuUrl)
                .header(Header.CONTENT_TYPE,"application/json")
                .body(mesj.toJSONString())
                .execute().body();
        log.info("消息推送结果:{}",str1);

    }
    /**添加拦截器**/
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**接口签名认证拦截器，该签名认证比较简单，实际项目中可以使用Json Web Token或其他更好的方式替代。**/
        registry.addInterceptor(new HandlerInterceptorAdapter() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                /**验证签名**/
                boolean pass = validateSign(request);
                if (pass) {
                    return true;
                } else {
                    log.warn("签名认证失败，请求接口：{}，请求IP：{}，请求参数：{}",
                            request.getRequestURI(), getIpAddress(request), JSON.toJSONString(request.getParameterMap()));

                    R r = new R();
                    r.setCode(ResultCode.UNAUTHORIZED.code()).setMsg("签名认证失败");
                    responseResult(response, r);
                    return false;
                }
            }
        });

        /** 日志拦截器**/
        registry.addInterceptor(new LogInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/webjars/**");

    }

    private void responseResult(HttpServletResponse response, R result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    /**
     * 一个简单的签名认证，规则：
     */
    private boolean validateSign(HttpServletRequest request) {

        return true;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        /** 如果是多级代理，那么取第一个ip为客户端ip**/
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }

        return ip;
    }

}

package com.dnt.data.standard.server.web;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * @description: 服务端统一错误拦截器 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@RestControllerAdvice
@Slf4j
public class ServiceExceptionHandler {

    @Value("${spring.application.name}")
    private String serverName;

    @ResponseBody
    @ExceptionHandler(ServiceException.class)
    public R handlerServiceException(Throwable t) {
        String msg = serverName + Constant.GLOBAL_EXECP_START_MESSAGE+ t.getMessage();
        log.error(msg, t);

        return Result.fail("拦截到自定义的错误异常");
    }

    /**
     * 服务器端 运行时异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public R notFount(RuntimeException e){

        String msg = serverName + Constant.GLOBAL_EXECP_START_MESSAGE +"运行时异常:" + getStackTrace(e);
        log.error(msg, e);

        return Result.fail(msg);
    }

    /**
     * 自定义输出异常信息
     * @param throwable
     * @return
     */
    private String getStackTrace(Throwable throwable){
        StringWriter stringWriter=new StringWriter();
        PrintWriter printWriter=new PrintWriter(stringWriter);

        try {
            throwable.printStackTrace(printWriter);
            return stringWriter.toString();
        }finally {
            printWriter.close();
        }

    }
    /**
     * 服务器端 系统异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public R notFount(Exception e){
        String msg = serverName + Constant.GLOBAL_EXECP_START_MESSAGE +"服务器错误，请联系管理员" + e.getMessage();
        log.error(msg, e);

        return Result.fail(msg);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleIllegalParamException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String message = "参数不合法";
        if (errors.size() > 0) {
            message = errors.get(0).getDefaultMessage();
        }
        return R.failed(message).setCode(400);
    }

}

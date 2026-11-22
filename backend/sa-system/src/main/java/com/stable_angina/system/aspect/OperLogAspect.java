package com.stable_angina.system.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stable_angina.system.annotation.OperLog;
import com.stable_angina.system.domain.entity.SysOperLog;
import com.stable_angina.system.mapper.SysOperLogMapper;
import com.stable_angina.system.security.LoginUser;
import com.stable_angina.system.security.SecurityUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 操作日志切面：拦截 @OperLog 标注的接口，记录调用入参与执行结果
 */
@Slf4j
@Aspect
@Component
public class OperLogAspect {

    private static final int PARAM_MAX_LENGTH = 2000;
    private static final int RESULT_MAX_LENGTH = 2000;
    private static final int ERROR_MAX_LENGTH = 500;

    /** 敏感字段脱敏，避免密码、令牌明文落库 */
    private static final Pattern SENSITIVE_PATTERN =
            Pattern.compile("(?i)(\"(?:password|oldPassword|newPassword|token|apiKey)\"\\s*:\\s*)\"[^\"]*\"");

    @Autowired
    private SysOperLogMapper operLogMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(operLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperLog operLog) throws Throwable {
        SysOperLog record = buildRecord(joinPoint, operLog);
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            record.setStatus(1);
            record.setJsonResult(truncate(toJson(result), RESULT_MAX_LENGTH));
            return result;
        } catch (Throwable ex) {
            record.setStatus(0);
            record.setErrorMsg(truncate(ex.getMessage(), ERROR_MAX_LENGTH));
            throw ex;
        } finally {
            record.setCostTime(System.currentTimeMillis() - start);
            record.setOperTime(LocalDateTime.now());
            saveQuietly(record);
        }
    }

    private SysOperLog buildRecord(ProceedingJoinPoint joinPoint, OperLog operLog) {
        SysOperLog record = new SysOperLog();
        record.setTitle(operLog.module());
        record.setBusinessType(operLog.action());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        record.setMethod(truncate(signature.getDeclaringTypeName() + "." + signature.getName(), 255));
        record.setOperParam(truncate(buildParamJson(joinPoint, signature), PARAM_MAX_LENGTH));

        HttpServletRequest request = currentRequest();
        if (request != null) {
            record.setRequestMethod(request.getMethod());
            record.setOperUrl(truncate(resolveUrl(request), 255));
            record.setOperIp(truncate(clientIp(request), 64));
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        record.setOperName(loginUser == null ? "anonymous" : loginUser.getUsername());
        return record;
    }

    private String buildParamJson(ProceedingJoinPoint joinPoint, MethodSignature signature) {
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();
        Map<String, Object> params = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (ignorable(arg)) {
                continue;
            }
            String key = parameterNames != null && i < parameterNames.length ? parameterNames[i] : "arg" + i;
            params.put(key, arg);
        }
        return params.isEmpty() ? null : toJson(params);
    }

    private boolean ignorable(Object arg) {
        return arg == null
                || arg instanceof ServletRequest
                || arg instanceof ServletResponse
                || arg instanceof MultipartFile
                || arg instanceof MultipartFile[]
                || arg instanceof BindingResult;
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return SENSITIVE_PATTERN.matcher(objectMapper.writeValueAsString(value)).replaceAll("$1\"******\"");
        } catch (Exception ex) {
            log.warn("操作日志序列化失败: {}", ex.getMessage());
            return null;
        }
    }

    private String resolveUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        return StringUtils.hasText(contextPath) && uri.startsWith(contextPath)
                ? uri.substring(contextPath.length()) : uri;
    }

    private String clientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

    private void saveQuietly(SysOperLog record) {
        try {
            operLogMapper.insert(record);
        } catch (Exception ex) {
            log.warn("操作日志落库失败: {}", ex.getMessage());
        }
    }
}

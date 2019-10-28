package com.yb.cheung.modules.sys.oauth2;

import com.yb.cheung.modules.sys.service.SysCaptchaService;
import io.scalajs.nodejs.http.Http;
import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ValidateCodeFilter extends OncePerRequestFilter {

    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private SysCaptchaService sysCaptchaService;

    public ValidateCodeFilter(SysCaptchaService sysCaptchaService){
        this.sysCaptchaService = sysCaptchaService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String context = request.getContextPath();
        String loginUrl = request.getRequestURI().substring(context.length());

        if (StringUtils.equals("/login",loginUrl) &&  StringUtils.equalsIgnoreCase(request.getMethod(), "post")){
            try {
                // 1. 进行验证码的校验
                String uuid = request.getParameter("uuid");
                String captcha = request.getParameter("captcha");
                boolean isCaptcha = sysCaptchaService.validate(uuid, captcha);
                if (!isCaptcha){
                    throw new BadCredentialsException("验证码错误!");
                }

            } catch (AuthenticationException e) {
                // 2. 如果校验不通过，调用SpringSecurity的校验失败处理器
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return ;
            }
        }
        // 3. 校验通过，就放行
        chain.doFilter(request, response);
    }
}

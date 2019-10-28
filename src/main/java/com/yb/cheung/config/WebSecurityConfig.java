package com.yb.cheung.config;

import com.alibaba.fastjson.JSON;
import com.yb.cheung.common.utils.Constant;
import com.yb.cheung.common.utils.R;
import com.yb.cheung.modules.sys.entity.SysUserEntity;
import com.yb.cheung.modules.sys.oauth2.*;
import com.yb.cheung.modules.sys.service.SysCaptchaService;
import com.yb.cheung.modules.sys.service.SysUserService;
import com.yb.cheung.modules.sys.service.SysUserTokenService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    SecurityUserService userService;
    @Autowired
    MyFilterInvocationSecurityMetadataSource myFilterInvocationSecurityMetadataSource;
    @Autowired
    MyAccessDecisionManager myAccessDecisionManager;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    SysUserTokenService sysUserTokenService;

    @Autowired
    private SysCaptchaService sysCaptchaService;


    /**
     * 自定义的加密算法
     * @return
     */
    @Bean
    public PasswordEncoder myPasswordEncoder() {
        return new MyPasswordEncoder();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(myPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/index.html", "/static/**","/loginPage","/register","/unlogin");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .authorizeRequests()
        .antMatchers("/**").authenticated()
        .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                o.setSecurityMetadataSource(myFilterInvocationSecurityMetadataSource);
                o.setAccessDecisionManager(myAccessDecisionManager);
                return o;
            }
        })
        .and()
        .addFilterBefore(new ValidateCodeFilter(sysCaptchaService), UsernamePasswordAuthenticationFilter.class)
        .formLogin()
        .loginPage("/unlogin")
        .loginProcessingUrl("/login").usernameParameter("username").passwordParameter("password").permitAll()
        //登录失败
        .failureHandler(
            (HttpServletRequest request, HttpServletResponse response, AuthenticationException e)->{
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                R r = null;
                if (e instanceof UsernameNotFoundException || e instanceof BadCredentialsException) {
                    r = R.error("用户名或密码输入错误，登录失败!");
                } else {
                    r = R.error("登录失败!");
                }
                out.write(r.toJSONString());
                out.flush();
                out.close();
                response.setStatus(Constant.UNLOGIN);
            }
        )
        //成功登录
        .successHandler(
            (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication)->{
                httpServletResponse.setContentType("application/json;charset=utf-8");
                PrintWriter out = httpServletResponse.getWriter();
                SysUserEntity user = (SysUserEntity) authentication.getPrincipal();
                out.write(R.ok(user,"登陆成功！").toJSONString());
                out.flush();
                out.close();
            }
        )
        .and()
        .logout()
        .logoutUrl("/logout")
        //退出处理
        .logoutSuccessHandler(
            (HttpServletRequest request, HttpServletResponse response, Authentication authentication)->{
                if(null == authentication){
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    out.write(R.error("用户已经退出").toJSONString());
                    out.flush();
                    out.close();
                } else {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    out.write(R.error("退出成功").toJSONString());
                    out.flush();
                    out.close();
                }
            }
        )
        .permitAll()
        .deleteCookies("JSESSIONID")
        .and()
        .csrf().disable()
        .exceptionHandling()
        //权限不足异常处理
        .accessDeniedHandler(
            (HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)->{
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.write(R.unAccess().toJSONString());
                out.flush();
                out.close();
                response.setStatus(Constant.UNACCESS);
            }
        );
    }


}

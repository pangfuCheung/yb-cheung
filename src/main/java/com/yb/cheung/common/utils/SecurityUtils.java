package com.yb.cheung.common.utils;

import com.yb.cheung.common.exception.RRException;
import com.yb.cheung.modules.sys.entity.SysUserEntity;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static SysUserEntity getUser(){
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(object instanceof SysUserEntity){
            return (SysUserEntity)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        throw new RRException("权限不足");
    }

}

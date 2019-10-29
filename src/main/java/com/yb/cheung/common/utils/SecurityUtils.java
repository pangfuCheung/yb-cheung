package com.yb.cheung.common.utils;

import com.yb.cheung.common.exception.RRException;
import com.yb.cheung.modules.sys.entity.SysUserEntity;
import com.yb.cheung.modules.sys.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * 如果是true获得用户的全部信息
     * @param isAll
     * @return
     */
    public static SysUserEntity getUser(boolean isAll){
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(object instanceof SysUserEntity){
            SysUserEntity userEntity = (SysUserEntity)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!isAll){
                userEntity.setSalt(null);
                userEntity.setPassword(null);
            }
            return userEntity;
        }
        throw new RRException("权限不足");
    }

    public static SysUserEntity getUser(){
        return getUser(false);
    }

}

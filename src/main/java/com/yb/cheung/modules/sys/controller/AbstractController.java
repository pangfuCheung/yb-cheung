/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yb.cheung.modules.sys.controller;

import com.yb.cheung.common.utils.SecurityUtils;
import com.yb.cheung.modules.sys.entity.SysRoleEntity;
import com.yb.cheung.modules.sys.entity.SysUserEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller公共组件
 *
 * @author cheung pangfucheung@163.com
 */
public abstract class AbstractController {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected SysUserEntity getUser() {
		return (SysUserEntity) SecurityUtils.getUser();
	}

	protected String getUserId() {
		return getUser().getUserId();
	}

	protected boolean isAdmin(){
		SysUserEntity user = getUser();
		List<SysRoleEntity> roles = user.getRoles();
		boolean isAdmin = false;
		for (SysRoleEntity role:roles){
			if ("ROLE_ADMIN".equals(role.getRoleCode())){
				isAdmin = true;
			}
		}
		return isAdmin;
	}
}

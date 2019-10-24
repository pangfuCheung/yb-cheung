/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yb.cheung.modules.sys.controller;

import com.yb.cheung.modules.sys.entity.SysUserEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller公共组件
 *
 * @author cheung pangfucheung@163.com
 */
public abstract class AbstractController {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected SysUserEntity getUser() {
		return null; /*(SysUserEntity) SecurityUtils.getSubject().getPrincipal();*/
	}

	protected Long getUserId() {
		return getUser().getUserId();
	}
}

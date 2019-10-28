/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yb.cheung.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yb.cheung.common.utils.PageUtils;
import com.yb.cheung.modules.sys.entity.SysRoleEntity;

import java.util.List;
import java.util.Map;


/**
 * 角色
 *
 * @author cheung pangfucheung@163.com
 */
public interface SysRoleService extends IService<SysRoleEntity> {

	PageUtils queryPage(Map<String, Object> params);

	void saveRole(SysRoleEntity role);

	void update(SysRoleEntity role);

	void deleteBatch(String[] roleIds);

	
	/**
	 * 查询用户创建的角色ID列表
	 */
	List<String> queryRoleIdList(String createUserId);

	/**
	 * 根据用户id和用户公司主键查询用户的角色信息
	 * @param userId
	 * @param companyId
	 * @return
	 */
	List<SysRoleEntity> getUserRolesByUserIdAndCompanyId(String userId,String companyId);
}

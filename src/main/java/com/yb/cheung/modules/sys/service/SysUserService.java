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
import com.yb.cheung.modules.sys.entity.SysMenuEntity;
import com.yb.cheung.modules.sys.entity.SysUserEntity;

import java.util.List;
import java.util.Map;


/**
 * 系统用户
 *
 * @author cheung pangfucheung@163.com
 */
public interface SysUserService extends IService<SysUserEntity> {

	PageUtils queryPage(Map<String, Object> params);

	/**
	 * 查询用户的所有权限
	 * @param userId  用户ID
	 */
	List<String> queryAllPerms(String userId);
	
	/**
	 * 查询用户的所有菜单ID
	 */
	List<String> queryAllMenuId(String userId);

	/**
	 * 根据用户名，查询系统用户
	 */
	SysUserEntity queryByUserName(String username);

	/**
	 * 保存用户
	 */
	void saveUser(SysUserEntity user);
	
	/**
	 * 修改用户
	 */
	void update(SysUserEntity user);
	
	/**
	 * 删除用户
	 */
	void deleteBatch(String[] userIds);

	/**
	 * 修改密码
	 * @param userId       用户ID
	 * @param password     原密码
	 * @param newPassword  新密码
	 */
	boolean updatePassword(String userId, String password, String newPassword);


	/**
	 * 判断是否拥有管理员权限
	 */
	boolean isAdmin(String userId);


	/**
	 *
	 * @param userId
	 * @return
	 */
	SysUserEntity userLoginInit(String userId);

}

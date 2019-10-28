/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yb.cheung.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yb.cheung.modules.sys.entity.SysMenuEntity;

import java.util.List;


/**
 * 菜单管理
 *
 * @author cheung pangfucheung@163.com
 */
public interface SysMenuService extends IService<SysMenuEntity> {

	/**
	 * 根据父菜单，查询子菜单
	 * @param parentId 父菜单ID
	 * @param menuIdList  用户菜单ID
	 */
	List<SysMenuEntity> queryListParentId(String parentId, List<String> menuIdList);

	/**
	 * 根据父菜单，查询子菜单
	 * @param parentId 父菜单ID
	 */
	List<SysMenuEntity> queryListParentId(String parentId);
	
	/**
	 * 获取不包含按钮的菜单列表
	 */
	List<SysMenuEntity> queryNotButtonList();

	/**
	 * 登陆成功之后，根据角色拿到所有菜单信息
	 */
	List<SysMenuEntity> getUserMenuList(String userId);

	/**
	 * 删除
	 */
	void delete(String menuId);

	/**
	 * 根据userid和companyid查询用户的菜单
	 */
	List<SysMenuEntity> getUserMenusByUserIdAndCompanyId(String userId,String companyId);

	/**
	 * 根据用户id和公司id获取用户的权限列表
	 * @param userId
	 * @param companyId
	 * @return
	 */
	List<SysMenuEntity> getUserPermsByUserIdAndCompanyId(String userId,String companyId);

}

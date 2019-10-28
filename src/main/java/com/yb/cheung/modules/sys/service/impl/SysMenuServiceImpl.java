/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yb.cheung.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yb.cheung.common.utils.Constant;
import com.yb.cheung.common.utils.MapUtils;
import com.yb.cheung.modules.sys.dao.SysMenuDao;
import com.yb.cheung.modules.sys.entity.*;
import com.yb.cheung.modules.sys.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("sysMenuService")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuDao, SysMenuEntity> implements SysMenuService {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysRoleMenuService sysRoleMenuService;
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private SysRoleService sysRoleService;
	
	@Override
	public List<SysMenuEntity> queryListParentId(String parentId, List<String> menuIdList) {
		List<SysMenuEntity> menuList = queryListParentId(parentId);
		if(menuIdList == null){
			return menuList;
		}
		
		List<SysMenuEntity> userMenuList = new ArrayList<>();
		for(SysMenuEntity menu : menuList){
			if(menuIdList.contains(menu.getMenuId())){
				userMenuList.add(menu);
			}
		}
		return userMenuList;
	}

	@Override
	public List<SysMenuEntity> queryListParentId(String parentId) {
		return baseMapper.queryListParentId(parentId);
	}

	@Override
	public List<SysMenuEntity> queryNotButtonList() {
		return baseMapper.queryNotButtonList();
	}



	@Override
	public void delete(String menuId){
		//删除菜单
		this.removeById(menuId);
		//删除菜单与角色关联
		sysRoleMenuService.removeByMap(new MapUtils().put("menu_id", menuId));
	}

	/**
	 * 获取所有菜单列表
	 */
	private List<SysMenuEntity> getAllMenuList(List<String> menuIdList){
		//查询根菜单列表
		List<SysMenuEntity> menuList = queryListParentId("0", menuIdList);
		//递归获取子菜单
		getMenuTreeList(menuList, menuIdList);
		
		return menuList;
	}

	/**
	 * 递归
	 */
	private List<SysMenuEntity> getMenuTreeList(List<SysMenuEntity> menuList, List<String> menuIdList){
		List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();
		
		for(SysMenuEntity entity : menuList){
			//目录
			if(entity.getType() == Constant.MenuType.CATALOG.getValue()){
				entity.setList(getMenuTreeList(queryListParentId(entity.getMenuId(), menuIdList), menuIdList));
			}
			subMenuList.add(entity);
		}
		
		return subMenuList;
	}

	@Override
	public List<SysMenuEntity> getUserMenuList(String userId) {
		/*//系统管理员，拥有最高权限
		if(!"admin".equals(userId)){
			return getAllMenuList(null);
		}

		//用户菜单列表
		List<String> menuIdList = sysUserService.queryAllMenuId(userId);
		return getAllMenuList(menuIdList);*/


		//拿到用户的所有角色，根据角色查所有菜单和按钮权限
		SysUserEntity user = sysUserService.getById(userId);
		String companyId = user.getCompanyId();
		sysUserService.queryAllMenuId(userId);
		return null;
	}

	/**
	 * 根据userid和companyid查询用户的菜单
	 * @param userId
	 * @param companyId
	 * @return
	 */
	public List<SysMenuEntity> getUserMenusByUserIdAndCompanyId(String userId,String companyId){
		List<SysMenuEntity> menus = getMenuList(userId,companyId);
		//此处只返回一级菜单
		List<SysMenuEntity> parentMenus = new ArrayList<>();
		for (SysMenuEntity menu:menus){
			//处理菜单
			if ("0".equals(menu.getParentId())){
				genMenuTree(menus,menu);
				parentMenus.add(menu);
			}
		}

		return parentMenus;
	}

	/**
	 * 根据用户id和公司id获取权限列表
	 * @param userId
	 * @param companyId
	 * @return
	 */
	public List<SysMenuEntity> getUserPermsByUserIdAndCompanyId(String userId,String companyId){
		List<SysMenuEntity> menus = getMenuList(userId,companyId);
		//此处只返回一级菜单
		List<SysMenuEntity> permsList = new ArrayList<>();
		for (SysMenuEntity menu:menus){
			//处理权限
			if (!"0".equals(menu.getParentId()) && null == menu.getUrl()){
				genMenuTree(menus,menu);
				permsList.add(menu);
			}
		}

		return permsList;
	}


	private List<SysMenuEntity> getMenuList(String userId,String companyId){
		List<SysMenuEntity> menus = new ArrayList<>();
		QueryWrapper<SysUserRoleEntity> userRoleWrapper = new QueryWrapper<SysUserRoleEntity>();
		userRoleWrapper.eq("user_id",userId);
		List<SysUserRoleEntity> userRoles = sysUserRoleService.list(userRoleWrapper);
		for (SysUserRoleEntity userRole:userRoles){
			SysRoleEntity role = sysRoleService.getById(userRole.getRoleId());
			QueryWrapper<SysRoleMenuEntity> roleMenuWrapper = new QueryWrapper<>();
			roleMenuWrapper.eq("role_id",role.getRoleId());
			List<SysRoleMenuEntity> roleMenus = sysRoleMenuService.list(roleMenuWrapper);
			for (SysRoleMenuEntity roleMenu:roleMenus){
				menus.add(baseMapper.selectById(roleMenu.getMenuId()));
			}
		}

		return menus;
	}



	//通过递归找出所有二级菜单
	public void genMenuTree(List<SysMenuEntity> parentMenus,SysMenuEntity parentMenu){
		List<SysMenuEntity> childs = new ArrayList<>();

		for (SysMenuEntity menu:parentMenus){
			if (parentMenu.getMenuId().equals(menu.getParentId())){
				childs.add(menu);
			}
		}
		parentMenu.setMenus(childs);

		if (childs.size()>0){
			for (SysMenuEntity menu:parentMenus){
				genMenuTree(childs,menu);
			}
		}

	}

}

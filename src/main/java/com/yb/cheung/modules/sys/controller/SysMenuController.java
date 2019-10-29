/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yb.cheung.modules.sys.controller;

import com.yb.cheung.common.annotation.SysLog;
import com.yb.cheung.common.exception.RRException;
import com.yb.cheung.common.utils.Constant;
import com.yb.cheung.common.utils.R;
import com.yb.cheung.modules.sys.entity.SysMenuEntity;
import com.yb.cheung.modules.sys.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 系统菜单
 *
 * @author cheung pangfucheung@163.com
 */
@Api(description = "系统菜单")
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends AbstractController {
	@Autowired
	private SysMenuService sysMenuService;

	/**
	 * 导航菜单
	 */
	@ApiOperation(value = "导航菜单",httpMethod = "GET")
	@GetMapping("/nav")
	public R nav(){
		List<SysMenuEntity> menuList = getUser().getMenus();
		//Set<String> permissions = shiroService.getUserPermissions(getUserId());
		//Set<String> permissions = getUser().getPermList();
		List<SysMenuEntity> permissions = getUser().getPermList();
		return R.ok().put("menuList", menuList).put("permissions", permissions);
	}
	
	/**
	 * 所有菜单列表
	 */
	@ApiOperation(value = "所有菜单列表",httpMethod = "GET")
	@GetMapping("/list")
	public List<SysMenuEntity> list(){
		List<SysMenuEntity> menuList = sysMenuService.list();
		for(SysMenuEntity sysMenuEntity : menuList){
			SysMenuEntity parentMenuEntity = sysMenuService.getById(sysMenuEntity.getParentId());
			if(parentMenuEntity != null){
				sysMenuEntity.setParentName(parentMenuEntity.getName());
			}
		}

		return menuList;
	}
	
	/**
	 * 选择菜单(添加、修改菜单)
	 */
	@ApiOperation(value = "选择菜单(添加、修改菜单)",httpMethod = "GET")
	@GetMapping("/select")
	public R select(){
		//查询列表数据
		List<SysMenuEntity> menuList = sysMenuService.queryNotButtonList();
		
		//添加顶级菜单
		SysMenuEntity root = new SysMenuEntity();
		root.setMenuId("0");
		root.setName("一级菜单");
		root.setParentId("-1");
		root.setOpen(true);
		menuList.add(root);
		
		return R.ok().put("menuList", menuList);
	}
	
	/**
	 * 菜单信息
	 */
	@ApiOperation(value = "根据menuId菜单信息",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "menuId",value = "角色id" ,required = true , dataType = "String" ,paramType = "path")
	})
	@GetMapping("/info/{menuId}")
	public R info(@PathVariable("menuId") String menuId){
		SysMenuEntity menu = sysMenuService.getById(menuId);
		return R.ok().put("menu", menu);
	}
	
	/**
	 * 保存
	 */
	@ApiOperation(value = "保存菜单",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "menu",value = "菜单实体类" ,required = true , dataType = "SysMenuEntity" ,paramType = "body")
	})
	@SysLog("保存菜单")
	@PostMapping("/save")
	public R save(@RequestBody SysMenuEntity menu){
		//数据校验
		verifyForm(menu);
		
		sysMenuService.save(menu);
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@ApiOperation(value = "修改菜单",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "menu",value = "菜单实体类" ,required = true , dataType = "SysMenuEntity" ,paramType = "body")
	})
	@SysLog("修改菜单")
	@PostMapping("/update")
	public R update(@RequestBody SysMenuEntity menu){
		//数据校验
		verifyForm(menu);
				
		sysMenuService.updateById(menu);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@ApiOperation(value = "删除菜单",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "menu",value = "菜单实体类" ,required = true , dataType = "SysMenuEntity" ,paramType = "body")
	})
	@SysLog("删除菜单")
	@PostMapping("/delete/{menuId}")
	public R delete(@PathVariable("menuId") String menuId){
		//判断是否有子菜单或按钮
		List<SysMenuEntity> menuList = sysMenuService.queryListParentId(menuId);
		if(menuList.size() > 0){
			return R.error("请先删除子菜单或按钮");
		}
		sysMenuService.delete(menuId);
		return R.ok();
	}
	
	/**
	 * 验证参数是否正确
	 */
	private void verifyForm(SysMenuEntity menu){
		if(StringUtils.isBlank(menu.getName())){
			throw new RRException("菜单名称不能为空");
		}
		
		if(menu.getParentId() == null){
			throw new RRException("上级菜单不能为空");
		}
		
		//菜单
		if(menu.getType() == Constant.MenuType.MENU.getValue()){
			if(StringUtils.isBlank(menu.getUrl())){
				throw new RRException("菜单URL不能为空");
			}
		}
		
		//上级菜单类型
		int parentType = Constant.MenuType.CATALOG.getValue();
		if("0".equals(menu.getParentId())){
			SysMenuEntity parentMenu = sysMenuService.getById(menu.getParentId());
			parentType = parentMenu.getType();
		}
		
		//目录、菜单
		if(menu.getType() == Constant.MenuType.CATALOG.getValue() ||
				menu.getType() == Constant.MenuType.MENU.getValue()){
			if(parentType != Constant.MenuType.CATALOG.getValue()){
				throw new RRException("上级菜单只能为目录类型");
			}
			return ;
		}
		
		//按钮
		if(menu.getType() == Constant.MenuType.BUTTON.getValue()){
			if(parentType != Constant.MenuType.MENU.getValue()){
				throw new RRException("上级菜单只能为菜单类型");
			}
			return ;
		}
	}
}

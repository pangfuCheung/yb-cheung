/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yb.cheung.modules.sys.controller;

import com.yb.cheung.common.annotation.SysLog;
import com.yb.cheung.common.utils.Constant;
import com.yb.cheung.common.utils.PageUtils;
import com.yb.cheung.common.utils.R;
import com.yb.cheung.common.validator.ValidatorUtils;
import com.yb.cheung.modules.sys.entity.SysRoleEntity;
import com.yb.cheung.modules.sys.service.SysRoleMenuService;
import com.yb.cheung.modules.sys.service.SysRoleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 *
 * @author cheung pangfucheung@163.com
 */
@Api(description = "角色管理")
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends AbstractController {
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysRoleMenuService sysRoleMenuService;

	/**
	 * 角色列表
	 */
	@ApiOperation(value = "所有角色列表",httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "params",value = "查询条件" ,required = true , dataType = "map" ,paramType = "query")
	})
	@GetMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		//如果不是超级管理员，则只查询自己创建的角色列表
		if(!"admin".equals(getUserId())){
			params.put("createUserId", getUserId());
		}
		PageUtils page = sysRoleService.queryPage(params);
		return R.ok().put("page", page);
	}
	
	/**
	 * 角色列表
	 */
	@ApiOperation(value = "如果不是超级管理员，则只查询自己所拥有的角色列表",httpMethod = "GET")
	@GetMapping("/select")
	public R select(){
		Map<String, Object> map = new HashMap<>();
		
		//如果不是超级管理员，则只查询自己所拥有的角色列表
		if(!"admin".equals(getUserId())){
			map.put("create_user_id", getUserId());
		}
		List<SysRoleEntity> list = (List<SysRoleEntity>) sysRoleService.listByMap(map);
		
		return R.ok().put("list", list);
	}
	
	/**
	 * 角色信息
	 */
	@ApiOperation(value = "根据roleId角色信息",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "roleId",value = "角色id" ,required = true , dataType = "String" ,paramType = "path")
	})
	@GetMapping("/info/{roleId}")
	public R info(@PathVariable("roleId") String roleId){
		SysRoleEntity role = sysRoleService.getById(roleId);
		
		//查询角色对应的菜单
		List<String> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
		role.setMenuIdList(menuIdList);
		
		return R.ok().put("role", role);
	}
	
	/**
	 * 保存角色
	 */
	@ApiOperation(value = "保存角色",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "role",value = "角色实体类" ,required = true , dataType = "SysRoleEntity" ,paramType = "body")
	})
	@SysLog("保存角色")
	@PostMapping("/save")
	public R save(@RequestBody SysRoleEntity role){
		ValidatorUtils.validateEntity(role);
		
		role.setCreateUserId(getUserId());
		sysRoleService.saveRole(role);
		
		return R.ok();
	}
	
	/**
	 * 修改角色
	 */
	@ApiOperation(value = "修改角色",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "role",value = "角色实体类" ,required = true , dataType = "SysRoleEntity" ,paramType = "body")
	})
	@SysLog("修改角色")
	@PostMapping("/update")
	public R update(@RequestBody SysRoleEntity role){
		ValidatorUtils.validateEntity(role);
		
		role.setCreateUserId(getUserId());
		sysRoleService.update(role);
		
		return R.ok();
	}
	
	/**
	 * 删除角色
	 */
	@ApiOperation(value = "删除角色",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "roleIds",value = "用户实体类" ,required = true , dataType = "String[]" ,paramType = "body")
	})
	@SysLog("删除角色")
	@PostMapping("/delete")
	public R delete(@RequestBody String[] roleIds){
		sysRoleService.deleteBatch(roleIds);
		
		return R.ok();
	}
}

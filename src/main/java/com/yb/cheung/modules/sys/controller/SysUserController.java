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
import com.yb.cheung.common.utils.Sha256Hash;
import com.yb.cheung.common.validator.Assert;
import com.yb.cheung.common.validator.ValidatorUtils;
import com.yb.cheung.common.validator.group.AddGroup;
import com.yb.cheung.common.validator.group.UpdateGroup;
import com.yb.cheung.modules.sys.entity.SysUserEntity;
import com.yb.cheung.modules.sys.form.PasswordForm;
import com.yb.cheung.modules.sys.service.SysUserRoleService;
import com.yb.cheung.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.ArrayUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统用户
 *
 * @author cheung pangfucheung@163.com
 */
@Api(description = "系统用户")
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends AbstractController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserRoleService sysUserRoleService;


	/**
	 * 所有用户列表
	 */
	@ApiOperation(value = "所有用户列表",httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "params",value = "查询条件" ,required = true , dataType = "map" ,paramType = "query")
	})
	@GetMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		//只有超级管理员，才能查看所有管理员列表
		if(!"admin".equals(getUserId())){
			params.put("createUserId", getUserId());
		}
		PageUtils page = sysUserService.queryPage(params);

		return R.ok().put("page", page);
	}
	
	/**
	 * 获取登录的用户信息
	 */
	@ApiOperation(value = "获取登录的用户信息",httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "companyId",value = "公司id" ,required = true , dataType = "String" ,paramType = "path")
	})
	@GetMapping("/info")
	public R info(){
		return R.ok().put("user", getUser());
	}
	
	/**
	 * 修改登录用户密码
	 */
	@ApiOperation(value = "修改登录用户密码",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "form",value = "保存新旧密码的表单" ,required = true , dataType = "PasswordForm" ,paramType = "body")
	})
	@SysLog("修改密码")
	@PostMapping("/password")
	public R password(@RequestBody PasswordForm form){
		Assert.isBlank(form.getNewPassword(), "新密码不为能空");
		
		//sha256加密
		String password = new Sha256Hash(form.getPassword(), getUser().getSalt()).toHex();
		//sha256加密
		String newPassword = new Sha256Hash(form.getNewPassword(), getUser().getSalt()).toHex();
				
		//更新密码
		boolean flag = sysUserService.updatePassword(getUserId(), password, newPassword);
		if(!flag){
			return R.error("原密码不正确");
		}
		
		return R.ok();
	}
	
	/**
	 * 用户信息
	 */
	@ApiOperation(value = "根据userId获取用户信息",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId",value = "用户id" ,required = true , dataType = "String" ,paramType = "path")
	})
	@GetMapping("/info/{userId}")
	public R info(@PathVariable("userId") String userId){
		SysUserEntity user = sysUserService.getById(userId);

		if (user != null){
			//获取用户所属的角色列表
			List<String> roleIdList = sysUserRoleService.queryRoleIdList(userId);
			user.setRoleIdList(roleIdList);
		}

		return R.ok().put("user", user);
	}
	
	/**
	 * 保存用户
	 */
	@ApiOperation(value = "保存用户",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "user",value = "用户实体类" ,required = true , dataType = "SysUserEntity" ,paramType = "body")
	})
	@SysLog("保存用户")
	@PostMapping("/save")
	public R save(@RequestBody SysUserEntity user){
		ValidatorUtils.validateEntity(user, AddGroup.class);
		
		user.setCreateUserId(getUserId());
		sysUserService.saveUser(user);
		
		return R.ok();
	}
	
	/**
	 * 修改用户
	 */
	@ApiOperation(value = "修改用户",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "user",value = "用户实体类" ,required = true , dataType = "SysUserEntity" ,paramType = "body")
	})
	@SysLog("修改用户")
	@PostMapping("/update")
	public R update(@RequestBody SysUserEntity user){
		ValidatorUtils.validateEntity(user, UpdateGroup.class);

		user.setCreateUserId(getUserId());
		sysUserService.update(user);
		
		return R.ok();
	}
	
	/**
	 * 删除用户
	 */
	@ApiOperation(value = "删除用户",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "user",value = "用户实体类" ,required = true , dataType = "String[]" ,paramType = "body")
	})
	@SysLog("删除用户")
	@PostMapping("/delete")
	public R delete(@RequestBody String[] userIds){
		if(ArrayUtils.contains(userIds, 1L)){
			return R.error("系统管理员不能删除");
		}
		
		if(ArrayUtils.contains(userIds, getUserId())){
			return R.error("当前用户不能删除");
		}
		
		sysUserService.deleteBatch(userIds);
		
		return R.ok();
	}
}

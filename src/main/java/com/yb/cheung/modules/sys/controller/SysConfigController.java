/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yb.cheung.modules.sys.controller;


import com.yb.cheung.common.annotation.SysLog;
import com.yb.cheung.common.utils.PageUtils;
import com.yb.cheung.common.utils.R;
import com.yb.cheung.common.validator.ValidatorUtils;
import com.yb.cheung.modules.sys.entity.SysConfigEntity;
import com.yb.cheung.modules.sys.service.SysConfigService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统配置信息
 *
 * @author cheung pangfucheung@163.com
 */
@RestController
@RequestMapping("/sys/config")
public class SysConfigController extends AbstractController {
	@Autowired
	private SysConfigService sysConfigService;
	
	/**
	 * 所有配置列表
	 */
	@ApiOperation(value = "所有配置列表",httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "params",value = "查询条件" ,required = true , dataType = "map" ,paramType = "query")
	})
	@GetMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = sysConfigService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 配置信息
	 */
	@ApiOperation(value = "根据id配置信息",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "配置信息id" ,required = true , dataType = "String" ,paramType = "path")
	})
	@GetMapping("/info/{id}")
	public R info(@PathVariable("id") String id){
		SysConfigEntity config = sysConfigService.getById(id);
		
		return R.ok().put("config", config);
	}
	
	/**
	 * 保存配置
	 */
	@ApiOperation(value = "保存配置",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "role",value = "配置实体类" ,required = true , dataType = "SysConfigEntity" ,paramType = "body")
	})
	@SysLog("保存配置")
	@PostMapping("/save")
	public R save(@RequestBody SysConfigEntity config){
		ValidatorUtils.validateEntity(config);

		sysConfigService.saveConfig(config);
		
		return R.ok();
	}
	
	/**
	 * 修改配置
	 */
	@ApiOperation(value = "修改配置",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "role",value = "配置实体类" ,required = true , dataType = "SysRoleEntity" ,paramType = "body")
	})
	@SysLog("修改配置")
	@PostMapping("/update")
	public R update(@RequestBody SysConfigEntity config){
		ValidatorUtils.validateEntity(config);
		
		sysConfigService.update(config);
		
		return R.ok();
	}
	
	/**
	 * 删除配置
	 */
	@ApiOperation(value = "删除配置",httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ids",value = "用户实体类" ,required = true , dataType = "String[]" ,paramType = "body")
	})
	@SysLog("删除配置")
	@PostMapping("/delete")
	public R delete(@RequestBody String[] ids){
		sysConfigService.deleteBatch(ids);
		return R.ok();
	}

}

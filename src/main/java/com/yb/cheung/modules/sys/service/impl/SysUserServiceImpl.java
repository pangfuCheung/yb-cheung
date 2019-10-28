/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yb.cheung.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yb.cheung.common.exception.RRException;
import com.yb.cheung.common.utils.Constant;
import com.yb.cheung.common.utils.PageUtils;
import com.yb.cheung.common.utils.Query;
import com.yb.cheung.common.utils.Sha256Hash;
import com.yb.cheung.modules.sys.dao.SysUserDao;
import com.yb.cheung.modules.sys.entity.SysMenuEntity;
import com.yb.cheung.modules.sys.entity.SysRoleEntity;
import com.yb.cheung.modules.sys.entity.SysUserEntity;
import com.yb.cheung.modules.sys.entity.SysUserRoleEntity;
import com.yb.cheung.modules.sys.service.SysMenuService;
import com.yb.cheung.modules.sys.service.SysRoleService;
import com.yb.cheung.modules.sys.service.SysUserRoleService;
import com.yb.cheung.modules.sys.service.SysUserService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 系统用户
 *
 * @author cheung pangfucheung@163.com
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysMenuService sysMenuService;


	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String username = (String)params.get("username");
		String createUserId = (String)params.get("createUserId");

		IPage<SysUserEntity> page = this.page(
			new Query<SysUserEntity>().getPage(params),
			new QueryWrapper<SysUserEntity>()
				.like(StringUtils.isNotBlank(username),"username", username)
				.eq(createUserId != null,"create_user_id", createUserId)
		);

		return new PageUtils(page);
	}

	@Override
	public List<String> queryAllPerms(String userId) {
		return baseMapper.queryAllPerms(userId);
	}

	@Override
	public List<String> queryAllMenuId(String userId) {
		return baseMapper.queryAllMenuId(userId);
	}

	@Override
	public SysUserEntity queryByUserName(String username) {
		return baseMapper.queryByUserName(username);
	}

	@Override
	@Transactional
	public void saveUser(SysUserEntity user) {
		user.setCreateTime(new Date());
		//sha256加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		user.setPassword(new Sha256Hash(user.getPassword(), salt).toHex());
		user.setSalt(salt);
		this.save(user);
		
		//检查角色是否越权
		checkRole(user);
		
		//保存用户与角色关系
		sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
	}

	@Override
	@Transactional
	public void update(SysUserEntity user) {
		if(StringUtils.isBlank(user.getPassword())){
			user.setPassword(null);
		}else{
			user.setPassword(new Sha256Hash(user.getPassword(), user.getSalt()).toHex());
		}
		this.updateById(user);
		
		//检查角色是否越权
		checkRole(user);
		
		//保存用户与角色关系
		sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
	}

	@Override
	public void deleteBatch(String[] userId) {
		this.removeByIds(Arrays.asList(userId));
	}

	@Override
	public boolean updatePassword(String userId, String password, String newPassword) {
		SysUserEntity userEntity = new SysUserEntity();
		userEntity.setPassword(newPassword);
		return this.update(userEntity,
				new QueryWrapper<SysUserEntity>().eq("user_id", userId).eq("password", password));
	}
	
	/**
	 * 检查角色是否越权
	 */
	private void checkRole(SysUserEntity user){
		if(user.getRoleIdList() == null || user.getRoleIdList().size() == 0){
			return;
		}
		//如果不是超级管理员，则需要判断用户的角色是否自己创建
		if(!"admin".equals(user.getCreateUserId())){
			return ;
		}
		
		//查询用户创建的角色列表
		List<String> roleIdList = sysRoleService.queryRoleIdList(user.getCreateUserId());

		//判断是否越权
		if(!roleIdList.containsAll(user.getRoleIdList())){
			throw new RRException("新增用户所选角色，不是本人创建");
		}
	}

	/**
	 * 判断是否拥有管理员权限
	 */
	public boolean isAdmin(String userId){
		QueryWrapper<SysUserRoleEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_id",userId);
		boolean isTrue = false;
		List<SysUserRoleEntity> userRoles =  sysUserRoleService.list(queryWrapper);
		for (SysUserRoleEntity userRole:userRoles){
			SysRoleEntity sysRoleEntity = sysRoleService.getById(userRole.getRoleId());
			if("admin".equals(sysRoleEntity.getRoleCode())){
				isTrue = true;
			}
		}
		return isTrue;
	}

	/**
	 *
	 * @param userId
	 * @return
	 */
	public SysUserEntity userLoginInit(String userId){
		//拿到用户的菜单权限
		SysUserEntity user = baseMapper.selectById(userId);
		String companyId = user.getCompanyId();
		user.setMenus(sysMenuService.getUserMenusByUserIdAndCompanyId(userId,companyId));
		user.setRoles(sysRoleService.getUserRolesByUserIdAndCompanyId(userId,companyId));
		user.setPermList(sysMenuService.getUserPermsByUserIdAndCompanyId(userId,companyId));
		return user;
	}

}
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
import com.yb.cheung.modules.sys.dao.SysRoleDao;
import com.yb.cheung.modules.sys.entity.SysRoleEntity;
import com.yb.cheung.modules.sys.entity.SysUserRoleEntity;
import com.yb.cheung.modules.sys.service.SysRoleMenuService;
import com.yb.cheung.modules.sys.service.SysRoleService;
import com.yb.cheung.modules.sys.service.SysUserRoleService;
import com.yb.cheung.modules.sys.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 角色
 *
 * @author cheung pangfucheung@163.com
 */
@Service("sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleDao, SysRoleEntity> implements SysRoleService {
	@Autowired
	private SysRoleMenuService sysRoleMenuService;
	@Autowired
	private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String roleName = (String)params.get("roleName");
		String createUserId = (String)params.get("createUserId");

		IPage<SysRoleEntity> page = this.page(
			new Query<SysRoleEntity>().getPage(params),
			new QueryWrapper<SysRoleEntity>()
				.like(StringUtils.isNotBlank(roleName),"role_name", roleName)
				.eq(createUserId != null,"create_user_id", createUserId)
		);

		return new PageUtils(page);
	}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(SysRoleEntity role) {
        role.setCreateTime(new Date());
        this.save(role);

        //检查权限是否越权
        checkPrems(role);

        //保存角色与菜单关系
        sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysRoleEntity role) {
        this.updateById(role);

        //检查权限是否越权
        checkPrems(role);

        //更新角色与菜单关系
        sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(String[] roleIds) {
        //删除角色
        this.removeByIds(Arrays.asList(roleIds));

        //删除角色与菜单关联
        sysRoleMenuService.deleteBatch(roleIds);

        //删除角色与用户关联
        sysUserRoleService.deleteBatch(roleIds);
    }


    @Override
	public List<String> queryRoleIdList(String createUserId) {
		return baseMapper.queryRoleIdList(createUserId);
	}

	/**
	 * 检查权限是否越权
	 */
	private void checkPrems(SysRoleEntity role){
		//如果不是超级管理员，则需要判断角色的权限是否超过自己的权限
		if(!"admin".equals(role.getCreateUserId())){
			return ;
		}
		
		//查询用户所拥有的菜单列表
		List<String> menuIdList = sysUserService.queryAllMenuId(role.getCreateUserId());
		
		//判断是否越权
		if(!menuIdList.containsAll(role.getMenuIdList())){
			throw new RRException("新增角色的权限，已超出你的权限范围");
		}
	}

	/**
	 * 根据用户id和用户公司主键查询用户的角色信息
	 * @param userId
	 * @param companyId
	 * @return
	 */
	public List<SysRoleEntity> getUserRolesByUserIdAndCompanyId(String userId,String companyId){
		List<SysRoleEntity> roles = new ArrayList<>();
		QueryWrapper<SysUserRoleEntity> userRoleWrapper = new QueryWrapper<>();
		userRoleWrapper.eq("user_id",userId);
		List<SysUserRoleEntity> userRoles = sysUserRoleService.list(userRoleWrapper);
		for (SysUserRoleEntity userRole:userRoles){
			SysRoleEntity role = baseMapper.selectById(userRole.getRoleId());
			roles.add(role);
		}
		return roles;
	}
}

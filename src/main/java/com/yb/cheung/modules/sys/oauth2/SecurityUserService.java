package com.yb.cheung.modules.sys.oauth2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yb.cheung.modules.app.entity.UserEntity;
import com.yb.cheung.modules.app.form.LoginForm;
import com.yb.cheung.modules.app.service.UserService;
import com.yb.cheung.modules.sys.entity.SysMenuEntity;
import com.yb.cheung.modules.sys.entity.SysRoleEntity;
import com.yb.cheung.modules.sys.entity.SysUserEntity;
import com.yb.cheung.modules.sys.entity.SysUserRoleEntity;
import com.yb.cheung.modules.sys.service.SysMenuService;
import com.yb.cheung.modules.sys.service.SysRoleService;
import com.yb.cheung.modules.sys.service.SysUserRoleService;
import com.yb.cheung.modules.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SecurityUserService implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRoleService sysRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUserEntity sysUserEntity = sysUserService.queryByUserName(username);
        if (sysUserEntity != null) {
            SysUserEntity user = sysUserService.userLoginInit(sysUserEntity.getUserId());
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            QueryWrapper queryWrapper = new QueryWrapper<SysMenuEntity>().eq("user_id",user.getUserId());
            List<SysUserRoleEntity> userRoleEntities = sysUserRoleService.list(queryWrapper);
            for (SysUserRoleEntity userRole:userRoleEntities){
                SysRoleEntity role = sysRoleService.getById(userRole.getRoleId());
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getRoleName());
                grantedAuthorities.add(grantedAuthority);
            }
            user.setGrantedAuthorities(grantedAuthorities);
            user.setPassword(user.getPassword() + "," + user.getSalt());
            return user;
        } else {
            throw new UsernameNotFoundException("username: " + username + " do not exist!");
        }
    }
}

package com.yb.cheung.modules.sys.oauth2;

import com.yb.cheung.modules.app.entity.UserEntity;
import com.yb.cheung.modules.app.form.LoginForm;
import com.yb.cheung.modules.app.service.UserService;
import com.yb.cheung.modules.sys.entity.SysMenuEntity;
import com.yb.cheung.modules.sys.entity.SysUserEntity;
import com.yb.cheung.modules.sys.service.SysMenuService;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUserEntity user = sysUserService.queryByUserName(username);
        if (user != null) {
            List<SysMenuEntity> permissions = sysMenuService.getUserMenuList(user.getUserId());
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            for (SysMenuEntity permission : permissions) {
                if (permission != null && permission.getName()!=null) {

                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission.getName());
                    grantedAuthorities.add(grantedAuthority);
                }
            }
            SysUserEntity sysUser = new SysUserEntity();
            sysUser.setUsername(user.getUsername());
            sysUser.setMenus(permissions);
            sysUser.setGrantedAuthorities(grantedAuthorities);
            return sysUser;
        } else {
            throw new UsernameNotFoundException("username: " + username + " do not exist!");
        }
    }
}

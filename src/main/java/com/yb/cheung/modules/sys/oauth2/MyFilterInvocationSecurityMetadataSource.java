package com.yb.cheung.modules.sys.oauth2;

import com.yb.cheung.modules.sys.entity.SysMenuEntity;
import com.yb.cheung.modules.sys.entity.SysRoleEntity;
import com.yb.cheung.modules.sys.entity.SysRoleMenuEntity;
import com.yb.cheung.modules.sys.service.SysMenuService;
import com.yb.cheung.modules.sys.service.SysRoleMenuService;
import com.yb.cheung.modules.sys.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class MyFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    SysMenuService sysMenuService;

    @Autowired
    SysRoleMenuService sysRoleMenuService;

    @Autowired
    SysRoleService sysRoleService;


    private HashMap<String, Collection<ConfigAttribute>> map = null;

    /**
     * 加载权限表中所有权限
     */
    public void loadResourceDefine() {
        map = new HashMap<String, Collection<ConfigAttribute>>();

        //数据结构是Map；key是url；value是role_code
        List<SysRoleMenuEntity> roleMenuList = sysRoleMenuService.list();
        for (SysRoleMenuEntity s:roleMenuList){
            SysRoleEntity role = sysRoleService.getById(s.getRoleId());
            SysMenuEntity menu = sysMenuService.getById(s.getMenuId());
            if (null != menu.getUrl() && !"".equals(menu.getUrl())){
                List<ConfigAttribute> list = null;
                ConfigAttribute cfg = new SecurityConfig(role.getRoleName());
                List<ConfigAttribute> cs = (List<ConfigAttribute>)map.get(menu.getUrl());
                if (null != cs && cs.size() > 0){
                    list = cs;
                }else {
                    list =  new ArrayList<>();
                }
                list.add(cfg);
                map.put(menu.getUrl(),list);
            }

            if(null != menu.getPerms() && !"".equals(menu.getPerms())){
                List<ConfigAttribute> list = null;
                ConfigAttribute cfg = new SecurityConfig(role.getRoleName());
                List<ConfigAttribute> cs = (List<ConfigAttribute>)map.get(menu.getPerms());
                if (null != cs && cs.size() > 0){
                    list = cs;
                }else {
                    list = new ArrayList<>();
                }
                list.add(cfg);
                String permsStr = menu.getPerms();
                String perms[] = permsStr.split(",");
                for (String perm:perms){
                    map.put(perm,list);
                }
            }
        }
    }


    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if (map == null) {
            loadResourceDefine();
        }
        // object 中包含用户请求的request的信息
        HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        String realUrl = uri.substring(uri.lastIndexOf(context)+context.length()+1);
        for (Map.Entry<String, Collection<ConfigAttribute>> entry : map.entrySet()) {
            String urlStr = entry.getKey();
            String urls[] = urlStr.split(",");
            for (String url:urls){
                if (realUrl.equals(url)) {
                    return map.get(url);
                }
            }
        }

        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}

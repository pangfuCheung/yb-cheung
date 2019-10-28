package com.yb.cheung.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yb.cheung.common.utils.PageUtils;
import com.yb.cheung.modules.sys.entity.SysCompanyEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author pangfucheung
 * @email pangfucheung@163.com
 * @date 2019-10-28 14:25:01
 */
public interface SysCompanyService extends IService<SysCompanyEntity> {

    PageUtils queryPage(Map<String, Object> params);

    SysCompanyEntity getAllCompanyList(String companyId);
}


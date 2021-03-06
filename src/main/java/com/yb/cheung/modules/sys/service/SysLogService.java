/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yb.cheung.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yb.cheung.common.utils.PageUtils;
import com.yb.cheung.modules.sys.entity.SysLogEntity;

import java.util.Map;


/**
 * 系统日志
 *
 * @author cheung pangfucheung@163.com
 */
public interface SysLogService extends IService<SysLogEntity> {

    PageUtils queryPage(Map<String, Object> params);

}

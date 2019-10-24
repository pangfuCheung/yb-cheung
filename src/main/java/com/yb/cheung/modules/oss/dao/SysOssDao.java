/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yb.cheung.modules.oss.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yb.cheung.modules.oss.entity.SysOssEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件上传
 *
 * @author cheung pangfucheung@163.com
 */
@Mapper
public interface SysOssDao extends BaseMapper<SysOssEntity> {
	
}

/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yb.cheung.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yb.cheung.common.utils.R;
import com.yb.cheung.modules.sys.entity.SysUserTokenEntity;

/**
 * 用户Token
 *
 * @author cheung pangfucheung@163.com
 */
public interface SysUserTokenService extends IService<SysUserTokenEntity> {

	/**
	 * 生成token
	 * @param userId  用户ID
	 */
	R createToken(String userId);

	/**
	 * 退出，修改token值
	 * @param userId  用户ID
	 */
	void logout(String userId);

}

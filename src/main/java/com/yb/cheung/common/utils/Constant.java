/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yb.cheung.common.utils;

/**
 * 常量
 *
 * @author cheung pangfucheung@163.com
 */
public class Constant {

    /**
     * 请求成功
     */
    public static final int SECCUSS = 200;
    public static final String MSG_SECCUSS = "请求成功";

    /**
     * 程序异常
     */
    public static final int ERROR = 500;
    public static final String MSG_ERROR = "程序发生异常，请联系管理员";

    /**
     * 未登录/登录超时
     */
    public static final int UNLOGIN = 402;
    public static final String MSG_UNLOGIN = "未登录或者登录超时";

    /**
     * 没有权限
     */
    public  static final int UNACCESS = 201;
    public static final String MSG_UNACCESS = "用户没有权限";

    /**
     * 状态名
     */
    public static final String CODE = "code";

    /**
     * 说明数据
     */
    public static final String MSG = "msg";

    /**
     * 返回数据
     */
    public static final String RESULT = "result";


    /** 超级管理员ID */
	public static final int SUPER_ADMIN = 1;
    /**
     * 当前页码
     */
    public static final String PAGE = "page";
    /**
     * 每页显示记录数
     */
    public static final String LIMIT = "limit";
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     *  升序
     */
    public static final String ASC = "asc";
	/**
	 * 菜单类型
	 * 
	 * @author chenshun
	 * @email sunlightcs@gmail.com
	 * @date 2016年11月15日 下午1:24:29
	 */
    public enum MenuType {
        /**
         * 目录
         */
    	CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    /**
     * 定时任务状态
     * 
     * @author chenshun
     * @email sunlightcs@gmail.com
     * @date 2016年12月3日 上午12:07:22
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
    	NORMAL(0),
        /**
         * 暂停
         */
    	PAUSE(1);

        private int value;

        ScheduleStatus(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }

    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3);

        private int value;

        CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}

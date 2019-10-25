/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.yb.cheung.common.utils;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author cheung pangfucheung@163.com
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public int totalCount;

	public int pageSize;

	public int totalPage;

	public int currPage;

	public Object result;

	public R(PageUtils pageUtils){
		this.totalCount = pageUtils.getTotalCount();
		this.pageSize = pageUtils.getPageSize();
		this.totalPage = pageUtils.getTotalPage();
		this.currPage = pageUtils.getCurrPage();
		this.result = pageUtils.getList();
	}

	public R(int totalCount,int pageSize,int totalPage,int currPage,Object result){
		put(Constant.CODE, Constant.SECCUSS);
		put(Constant.MSG, Constant.MSG_SECCUSS);
	}

	public R() {
		put(Constant.CODE, Constant.SECCUSS);
		put(Constant.MSG, Constant.MSG_SECCUSS);
	}

	public static R unLogin(){
		R r = new R();
		r.put(Constant.CODE,Constant.UNLOGIN);
		r.put(Constant.MSG,Constant.MSG_UNLOGIN);
		return r;
	}

	public static R unAccess(){
		R r = new R();
		r.put(Constant.CODE,Constant.UNACCESS);
		r.put(Constant.MSG,Constant.MSG_UNACCESS);
		return r;
	}

	public static R error() {
		return error(Constant.ERROR, Constant.MSG_ERROR);
	}
	
	public static R error(String msg) {
		return error(Constant.ERROR, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put(Constant.CODE, code);
		r.put(Constant.MSG, msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put(Constant.MSG, msg);
		return r;
	}
	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	public static R ok(Object object) {
		R r = new R();
		if (object instanceof Map){
			r.putAll((Map)object);
		}else {
			r.put(Constant.RESULT,object);
		}
		return r;
	}

	public static R ok(Object object,String msg) {
		R r = R.ok(msg);
		if (object instanceof Map){
			r.putAll((Map)object);
		}else {
			r.put(Constant.RESULT,object);
		}
		return r;
	}

	
	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	public String toJSONString(){
		return JSON.toJSONString(this);
	}
}

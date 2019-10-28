package com.yb.cheung.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 
 * 
 * @author pangfucheung
 * @email pangfucheung@163.com
 * @date 2019-10-28 14:25:01
 */
@Data
@TableName("sys_company")
public class SysCompanyEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 公司id
	 */
	@TableId
	private String companyId;
	/**
	 * 公司名称
	 */
	private String comName;
	/**
	 * 公司编码
	 */
	private String comCode;
	/**
	 * 父id
	 */
	private String parentId;
	/**
	 * 地址
	 */
	private String adress;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建人
	 */
	private String creatorId;

	/**
	 *
	 */
	@TableField(exist = false)
	private List<SysCompanyEntity> companyChilds;

}

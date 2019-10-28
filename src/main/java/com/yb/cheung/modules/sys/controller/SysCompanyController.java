package com.yb.cheung.modules.sys.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yb.cheung.modules.sys.entity.SysRoleEntity;
import com.yb.cheung.modules.sys.entity.SysUserEntity;
import com.yb.cheung.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yb.cheung.modules.sys.entity.SysCompanyEntity;
import com.yb.cheung.modules.sys.service.SysCompanyService;
import com.yb.cheung.common.utils.PageUtils;
import com.yb.cheung.common.utils.R;



/**
 * 
 *
 * @author pangfucheung
 * @email pangfucheung@163.com
 * @date 2019-10-28 14:25:01
 */
@Api(description = "公司查询")
@RestController
@RequestMapping("sys/company")
public class SysCompanyController extends AbstractController {
    @Autowired
    private SysCompanyService sysCompanyService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysCompanyService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @ApiOperation(value = "根据用户的companyId，获取公司的详情",httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyId",value = "公司id" ,required = true , dataType = "String" ,paramType = "path")
    })
    @RequestMapping("/info/{companyId}")
    public R info(@PathVariable("companyId") String companyId){
        SysCompanyEntity sysCompany = null;

        if (isAdmin()){
            sysCompany = sysCompanyService.getAllCompanyList(companyId);
        } else {
            sysCompany = sysCompanyService.getById(companyId);
        }

        return R.ok().put("sysCompany", sysCompany);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SysCompanyEntity sysCompany){
        if(verify(sysCompany)){
            return R.error("父级菜单不能为空");
        }

		sysCompanyService.save(sysCompany);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SysCompanyEntity sysCompany){
		sysCompanyService.updateById(sysCompany);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody String[] companyIds){
		sysCompanyService.removeByIds(Arrays.asList(companyIds));
        return R.ok();
    }

    private boolean verify(SysCompanyEntity company){

        if(company == null){
            return false;
        }

        if(company.getParentId() == null){
            return false;
        }

        if(company.getComCode() != null){
            QueryWrapper<SysCompanyEntity> companyWrapper = new QueryWrapper<>();
            companyWrapper.eq("com_code",company.getComCode());
            SysCompanyEntity companyEntity = sysCompanyService.getOne(companyWrapper);
            if (companyEntity != null){
                return false;
            }
        }

        return false;
    }

}

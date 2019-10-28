package com.yb.cheung.modules.sys.service.impl;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yb.cheung.common.utils.PageUtils;
import com.yb.cheung.common.utils.Query;

import com.yb.cheung.modules.sys.dao.SysCompanyDao;
import com.yb.cheung.modules.sys.entity.SysCompanyEntity;
import com.yb.cheung.modules.sys.service.SysCompanyService;


@Service("sysCompanyService")
public class SysCompanyServiceImpl extends ServiceImpl<SysCompanyDao, SysCompanyEntity> implements SysCompanyService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysCompanyEntity> page = this.page(
                new Query<SysCompanyEntity>().getPage(params),
                new QueryWrapper<SysCompanyEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public SysCompanyEntity getAllCompanyList(String companyId) {
        List<SysCompanyEntity> companys = super.list();
        SysCompanyEntity parentCompany = super.getById(companyId);
        recursion(companys,parentCompany);
        return parentCompany;
    }

    private void recursion(List<SysCompanyEntity> companys,SysCompanyEntity parentCompany){
        List<SysCompanyEntity> childs = new ArrayList<>();

        for (SysCompanyEntity company:companys){
            if (parentCompany.getCompanyId().equals(company.getParentId())){
                childs.add(company);
            }
        }
        if(childs.size() > 0){
            parentCompany.setCompanyChilds(childs);
            for (SysCompanyEntity chlid:childs){
                recursion(companys,chlid);
            }
        }
    }


}
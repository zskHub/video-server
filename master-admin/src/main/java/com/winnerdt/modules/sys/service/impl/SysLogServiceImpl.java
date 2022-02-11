package com.winnerdt.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winnerdt.modules.sys.dao.SysLogDao;
import com.winnerdt.modules.sys.entity.SysLogEntity;
import com.winnerdt.modules.sys.service.SysLogService;
import com.winnerdt.common.utils.PageUtils;
import com.winnerdt.common.utils.Query;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysLogService")
public class SysLogServiceImpl extends ServiceImpl<SysLogDao, SysLogEntity> implements SysLogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String userName = (String)params.get("userName");
        String operation = (String)params.get("operation");
        String method = (String)params.get("method");
        String beginDate = (String)params.get("beginDate");
        String endDate = (String)params.get("endDate");
        boolean createDateFlag = false;
        if(StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)){
            createDateFlag = true;
        }

        Page<SysLogEntity> page = (Page<SysLogEntity>) this.page(
            new Query<SysLogEntity>(params).getPage(),
            new QueryWrapper<SysLogEntity>()
                    .like(StringUtils.isNotBlank(userName),"username", userName)
                    .like(StringUtils.isNotBlank(operation),"operation",operation)
                    .like(StringUtils.isNotBlank(method),"method",method)
                    .between(createDateFlag,"create_date",beginDate,endDate)
                    .orderByDesc("create_date")
        );

        return new PageUtils(page);
    }
}

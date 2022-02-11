package com.winnerdt.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winnerdt.modules.sys.dao.SysDictDao;
import com.winnerdt.modules.sys.entity.SysDictEntity;
import com.winnerdt.modules.sys.service.SysDictService;
import com.winnerdt.common.utils.PageUtils;
import com.winnerdt.common.utils.Query;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysDictService")
public class SysDictServiceImpl extends ServiceImpl<SysDictDao, SysDictEntity> implements SysDictService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String name = (String)params.get("name");
        String type = (String)params.get("type");

        Page<SysDictEntity> page = (Page<SysDictEntity>) this.page(
                new Query<SysDictEntity>(params).getPage(),
                new QueryWrapper<SysDictEntity>()
                    .like(StringUtils.isNotBlank(name),"name", name)
                    .like(StringUtils.isNotBlank(type),"type",type)
        );

        return new PageUtils(page);
    }

}

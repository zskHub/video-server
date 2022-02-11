package com.winnerdt.modules.oss.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winnerdt.modules.oss.service.SysOssService;
import com.winnerdt.common.utils.PageUtils;
import com.winnerdt.common.utils.Query;
import com.winnerdt.modules.oss.dao.SysOssDao;
import com.winnerdt.modules.oss.entity.SysOssEntity;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysOssService")
public class SysOssServiceImpl extends ServiceImpl<SysOssDao, SysOssEntity> implements SysOssService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Page<SysOssEntity> page = (Page<SysOssEntity>) this.page(
				new Query<SysOssEntity>(params).getPage().setDesc("create_date")
		);

		return new PageUtils(page);
	}
	
}

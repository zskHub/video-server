package com.winnerdt.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.winnerdt.modules.sys.dao.SysConfigDao;
import com.winnerdt.modules.sys.redis.SysConfigRedis;
import com.winnerdt.modules.sys.service.SysConfigService;
import com.winnerdt.common.exception.RRException;
import com.winnerdt.common.utils.PageUtils;
import com.winnerdt.common.utils.Query;
import com.winnerdt.modules.sys.entity.SysConfigEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;

@Service("sysConfigService")
public class SysConfigServiceImpl extends ServiceImpl<SysConfigDao, SysConfigEntity> implements SysConfigService {
	@Autowired
	private SysConfigRedis sysConfigRedis;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String paramKey = (String)params.get("paramKey");

		String statusStr = (String)params.get("status");
		Boolean statusTemp1 = false;
		Boolean statusTemp2 = false;
		if(statusStr != null ){
			if(statusStr.equals("0")){
				statusTemp1 = true;
			}else if(statusStr.equals("1")){
				statusTemp2 = true;
			}

		}

		Page<SysConfigEntity> page = (Page<SysConfigEntity>) this.page(
				new Query<SysConfigEntity>(params).getPage(),
				new QueryWrapper<SysConfigEntity>()
					.like(StringUtils.isNotBlank(paramKey),"param_key", paramKey)
						.eq(statusTemp1,"status",0)
						.eq(statusTemp2,"status",1)
		);

		return new PageUtils(page);
	}
	
	@Override
	public boolean save(SysConfigEntity config) {
		super.save(config);
		sysConfigRedis.saveOrUpdate(config);

		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(SysConfigEntity config) {
		this.updateById(config);
		sysConfigRedis.saveOrUpdate(config);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateValueByKey(String key, String value) {
		baseMapper.updateValueByKey(key, value);
		sysConfigRedis.delete(key);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatch(Long[] ids) {
		for(Long id : ids){
			SysConfigEntity config = this.getById(id);
			sysConfigRedis.delete(config.getParamKey());
		}

		this.removeByIds(Arrays.asList(ids));
	}

	@Override
	public String getValue(String key) {
		SysConfigEntity config = sysConfigRedis.get(key);
		if(config == null){
			config = baseMapper.queryByKey(key);
			sysConfigRedis.saveOrUpdate(config);
		}

		return config == null ? null : config.getParamValue();
	}
	
	@Override
	public <T> T getConfigObject(String key, Class<T> clazz) {
		String value = getValue(key);
		if(StringUtils.isNotBlank(value)){
			return new Gson().fromJson(value, clazz);
		}

		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RRException("获取参数失败");
		}
	}
}

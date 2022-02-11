package com.winnerdt.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.winnerdt.common.utils.PageUtils;
import com.winnerdt.modules.sys.entity.SysRoleEntity;

import java.util.Map;


/**
 * 角色
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年9月18日 上午9:42:52
 */
public interface SysRoleService extends IService<SysRoleEntity> {

	PageUtils queryPage(Map<String, Object> params);

	boolean save(SysRoleEntity role);

	void update(SysRoleEntity role);
	
	void deleteBatch(Long[] roleIds);

	/*
	 * 通过角色名查询角色是否存在
	 * */
	String isExistByRoleName(String roleName);

}

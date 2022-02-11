package com.winnerdt.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winnerdt.common.utils.PageUtils;
import com.winnerdt.modules.sys.entity.SysMenuEntity;
import com.winnerdt.modules.sys.entity.SysRoleEntity;
import com.winnerdt.modules.sys.entity.SysUserEntity;

import java.util.List;
import java.util.Map;


/**
 * 系统用户
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年9月18日 上午9:43:39
 */
public interface SysUserService extends IService<SysUserEntity> {

	PageUtils queryPage(Map<String, Object> params);
	
	/**
	 * 查询用户的所有菜单ID
	 */
	List<Long> queryAllMenuId(Long userId);
	
	/**
	 * 保存用户
	 */
	boolean save(SysUserEntity user);
	
	/**
	 * 修改用户
	 */
	void update(SysUserEntity user);

	/**
	 * 修改密码
	 * @param userId       用户ID
	 * @param password     原密码
	 * @param newPassword  新密码
	 */
	boolean updatePassword(Long userId, String password, String newPassword);

	/*查询用户的所有按钮*/
	List<SysMenuEntity> queryAllButton(Map map);

	/*
	* 查询用户所有角色
	* */
	List<SysRoleEntity> queryAllRole(Long userId);

	/*
	* 通过用户名查询用户是否存在
	* */
	String isExistByUserName(String userName);
}

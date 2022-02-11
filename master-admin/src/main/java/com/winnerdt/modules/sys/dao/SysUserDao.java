package com.winnerdt.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
 * @date 2016年9月18日 上午9:34:11
 */
public interface SysUserDao extends BaseMapper<SysUserEntity> {
	
	/**
	 * 查询用户的所有权限
	 * @param userId  用户ID
	 */
	List<String> queryAllPerms(Long userId);
	
	/**
	 * 查询用户的所有菜单ID
	 */
	List<Long> queryAllMenuId(Long userId);

	/*查询用户的所有按钮*/
	List<SysMenuEntity> queryAllButton(Map map);

	/*查询用户所有角色*/
	List<SysRoleEntity> queryAllRole(Long userId);

}

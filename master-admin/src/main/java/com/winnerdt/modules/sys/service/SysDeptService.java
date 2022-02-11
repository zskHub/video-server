package com.winnerdt.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.winnerdt.modules.sys.entity.SysDeptEntity;

import java.util.List;
import java.util.Map;

/**
 * 部门管理
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-06-20 15:23:47
 */
public interface SysDeptService extends IService<SysDeptEntity> {

	List<SysDeptEntity> queryList(Map<String, Object> map);

	/**
	 * 查询子部门ID列表
	 * @param parentId  上级部门ID
	 */
	List<Long> queryDetpIdList(Long parentId);

	/**
	 * 获取子部门ID，用于数据过滤
	 */
	List<Long> getSubDeptIdList(Long deptId);

	/*
	* 查询子部门列表
	* */
	List<SysDeptEntity> queryDetpList(Long parentId);

	/*
	* 部门管理页，treeTable加载
	* */
	List<SysDeptEntity> treeTableShow();

	/*
	 *
	 * 添加时检测部门名称是否已经存在
	 * */
	String isExitDeptNameWhenAdd(String deptName);

	/*
	 * 更新时查询部门名称是否已经存在
	 * */
	String isExitDeptNameWhenUpdate(Map map);

}

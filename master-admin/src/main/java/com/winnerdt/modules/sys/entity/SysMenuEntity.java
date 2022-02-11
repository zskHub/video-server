package com.winnerdt.modules.sys.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 菜单管理
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年9月18日 上午9:26:39
 */
@ToString
@TableName("sys_menu")
public class SysMenuEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 菜单ID
	 */
	@TableId
	private Long menuId;

	/**
	 * 父菜单ID，一级菜单为0
	 */
	private Long parentId;
	
	/**
	 * 父菜单名称
	 */
	@TableField(exist=false)
	private String parentName;

	/**
	 * 菜单名称
	 */
	private String name;

	/**
	 * 菜单URL
	 */
	private String path;

	/**
	 * 授权(多个用逗号分隔，如：user:list,user:create)
	 */
	private String perms;

	/**
	 * 类型     0：目录   1：菜单   2：按钮
	 */
	private Integer type;

	/**
	 * 菜单图标
	 */
	private String icon;

	/**
	 * 排序
	 */
	private Integer orderNum;

	/*
	*
	* 前台面包屑导航使用到的，具体作用只能问前台了
	*
	* */
	private String locale;

	/*
	* 前台使用，应该是默认是否展开，具体内容问前台
	* */
	private Boolean exact;

	/*
	* 用于控制按钮的是否显示，详情逻辑查看SysMenuServiceImpl.java类中的getMenuTreeList方法
	* */
	@TableField(exist=false)
	private Set<String> parmsList;

	
	/**
	 * ztree属性
	 */
	@TableField(exist=false)
	private Boolean open;

	@TableField(exist=false)
	private List<?> children;

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

	@JsonSerialize(using= ToStringSerializer.class)
	public Long getMenuId() {
		return menuId;
	}
	
	/**
	 * 设置：父菜单ID，一级菜单为0
	 * @param parentId 父菜单ID，一级菜单为0
	 */
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	/**
	 * 获取：父菜单ID，一级菜单为0
	 * @return Long
	 */
	public Long getParentId() {
		return parentId;
	}
	
	/**
	 * 设置：菜单名称
	 * @param name 菜单名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取：菜单名称
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 设置：菜单URL
	 * @param path 菜单URL
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * 获取：菜单URL
	 * @return String
	 */
	public String getPath() {
		return path;
	}



	
	public String getPerms() {
		return perms;
	}

	public void setPerms(String perms) {
		this.perms = perms;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * 设置：菜单图标
	 * @param icon 菜单图标
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * 获取：菜单图标
	 * @return String
	 */
	public String getIcon() {
		return icon;
	}
	
	/**
	 * 设置：排序
	 * @param orderNum 排序
	 */
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	/**
	 * 获取：排序
	 * @return Integer
	 */
	public Integer getOrderNum() {
		return orderNum;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Boolean getExact() {
		return exact;
	}

	public void setExact(Boolean exact) {
		this.exact = exact;
	}

	public Set<String> getParmsList() {
		return parmsList;
	}

	public void setParmsList(Set<String> parmsList) {
		this.parmsList = parmsList;
	}

	public List<?> getChildren() {
		return children;
	}

	public void setChildren(List<?> children) {
		this.children = children;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}
}

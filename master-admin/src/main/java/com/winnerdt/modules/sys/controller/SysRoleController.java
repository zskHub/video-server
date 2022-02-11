package com.winnerdt.modules.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.winnerdt.common.annotation.SysLog;
import com.winnerdt.common.utils.PageUtils;
import com.winnerdt.common.utils.R;
import com.winnerdt.common.validator.ValidatorUtils;
import com.winnerdt.modules.sys.entity.SysRoleEntity;
import com.winnerdt.modules.sys.service.SysRoleDeptService;
import com.winnerdt.modules.sys.service.SysRoleMenuService;
import com.winnerdt.modules.sys.service.SysRoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 角色管理
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月8日 下午2:18:33
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends AbstractController {
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysRoleMenuService sysRoleMenuService;
	@Autowired
	private SysRoleDeptService sysRoleDeptService;
	
	/**
	 * 角色列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:role:list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = sysRoleService.queryPage(params);

		return R.ok().put("list", page.getList()).put("pagination",page.getPagination());
	}
	
	/**
	 * 角色列表
	 */
	@RequestMapping("/select")
	@RequiresPermissions("sys:role:select")
	public R select(){
		List<SysRoleEntity> list = sysRoleService.list();
		
		return R.ok().put("list", list);
	}
	
	/**
	 * 角色信息
	 */
	@RequestMapping("/info/{roleId}")
	@RequiresPermissions("sys:role:info")
	public R info(@PathVariable("roleId") Long roleId){
		SysRoleEntity role = sysRoleService.getById(roleId);
		
		//查询角色对应的菜单
		List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
		role.setMenuIdList(menuIdList);

		//查询角色对应的部门
		List<Long> deptIdList = sysRoleDeptService.queryDeptIdList(new Long[]{roleId});
		role.setDeptIdList(deptIdList);
		
		return R.ok().put("role", role);
	}
	
	/**
	 * 保存角色
	 */
	@SysLog("保存角色")
	@RequestMapping("/save")
	@RequiresPermissions("sys:role:save")
	public R save(@RequestBody SysRoleEntity role){
		ValidatorUtils.validateEntity(role);
		

		try{
			sysRoleService.save(role);
			return R.ok();
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("保存角色信息异常，异常时间："+now+":::异常数据："+role.toString()+":::异常原因："+e.toString());
			return R.error("网络错误，角色新增失败！");
		}

	}
	
	/**
	 * 修改角色
	 */
	@SysLog("修改角色")
	@RequestMapping("/update")
	@RequiresPermissions("sys:role:update")
	public R update(@RequestBody SysRoleEntity role){
		ValidatorUtils.validateEntity(role);

		try{
			sysRoleService.update(role);
			return R.ok();
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("更新角色信息异常，异常时间："+now+":::异常数据："+role.toString()+":::异常原因："+e.toString());
			return R.error("网络错误，角色更新失败！");
		}

	}
	
	/**
	 * 删除角色
	 */
	@SysLog("删除角色")
	@RequestMapping("/delete")
	@RequiresPermissions("sys:role:delete")
	public R delete(@RequestBody Long[] roleIds){


		try{
			sysRoleService.deleteBatch(roleIds);
			return R.ok();
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("删除角色信息异常，异常时间："+now+":::异常数据："+ JSONObject.toJSONString(roleIds)+":::异常原因："+e.toString());
			return R.error("网络错误，角色删除失败！");
		}
	}


	/*
	 * 通过角色名查询用户是否已经存在
	 *
	 * */
	@RequestMapping("isExistByRoleName")
	public String isExistByRoleName(@RequestBody Map<String,String> map){
		return JSONObject.toJSONString(sysRoleService.isExistByRoleName(map.get("roleName")));
	}
}

package com.winnerdt.modules.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.winnerdt.modules.sys.service.SysDeptService;
import com.winnerdt.common.utils.Constant;
import com.winnerdt.common.utils.R;
import com.winnerdt.modules.sys.entity.SysDeptEntity;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 部门管理
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-06-20 15:23:47
 */
@RestController
@RequestMapping("/sys/dept")
public class SysDeptController extends AbstractController {

	@Autowired
	private SysDeptService sysDeptService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:dept:list")
	public R list(){
		List<SysDeptEntity> deptList = sysDeptService.treeTableShow();

		return R.ok().put("list",deptList);
	}

	/**
	 * 选择部门(添加、修改菜单)
	 */
	@RequestMapping("/select")
	@RequiresPermissions("sys:dept:select")
	public R select(){
		List<SysDeptEntity> deptList = sysDeptService.queryList(new HashMap<String, Object>());

		//添加一级部门
		if(getUserId() == Constant.SUPER_ADMIN){
			SysDeptEntity root = new SysDeptEntity();
			root.setDeptId(0L);
			root.setName("一级部门");
			root.setParentId(-1L);
			root.setOpen(true);
			root.setCreateTime(new Date());
			deptList.add(root);
		}

		return R.ok().put("deptList", deptList);
	}

	/**
	 * 上级部门Id(管理员则为0)
	 */
	@RequestMapping("/info")
	@RequiresPermissions("sys:dept:list")
	public R info(){
		long deptId = 0;
		if(getUserId() != Constant.SUPER_ADMIN){
			List<SysDeptEntity> deptList = sysDeptService.queryList(new HashMap<String, Object>());
			Long parentId = null;
			for(SysDeptEntity sysDeptEntity : deptList){
				if(parentId == null){
					parentId = sysDeptEntity.getParentId();
					continue;
				}

				if(parentId > sysDeptEntity.getParentId().longValue()){
					parentId = sysDeptEntity.getParentId();
				}
			}
			deptId = parentId;
		}

		return R.ok().put("deptId", deptId);
	}
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{deptId}")
	@RequiresPermissions("sys:dept:info")
	public R info(@PathVariable("deptId") Long deptId){
		SysDeptEntity dept = sysDeptService.getById(deptId);
		
		return R.ok().put("dept", dept);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("sys:dept:save")
	public R save(@RequestBody SysDeptEntity dept){

		try{
			dept.setCreateTime(new Date());
			sysDeptService.save(dept);
			return R.ok();
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("保存部门信息异常，异常时间："+now+":::异常数据："+dept.toString()+":::异常原因："+e.toString());
			return R.error("网络错误，部门保存失败！");
		}
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:dept:update")
	public R update(@RequestBody SysDeptEntity dept){

		try{
			sysDeptService.updateById(dept);
			return R.ok();
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("修改部门信息异常，异常时间："+now+":::异常数据："+dept.toString()+":::异常原因："+e.toString());
			return R.error("网络错误，部门修改失败！");
		}

	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:dept:delete")
	public R delete(@RequestBody long deptId){
		//判断是否有子部门
		List<Long> deptList = sysDeptService.queryDetpIdList(deptId);
		if(deptList.size() > 0){
			return R.error("请先删除子部门");
		}


		try{
			sysDeptService.removeById(deptId);
			return R.ok();
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("删除部门信息异常，异常时间："+now+":::异常数据："+ JSONObject.toJSONString(deptId) +":::异常原因："+e.toString());
			return R.error("网络错误，部门删除失败！");
		}
	}
	/*
	 *  添加时检测部门名称是否已经存在
	 * */
	@RequestMapping("isExitDeptNameWhenAdd")
	public String isExitQrcodeConfig(@RequestBody Map<String,String> map){
		return JSONObject.toJSONString(sysDeptService.isExitDeptNameWhenAdd(map.get("deptName")));
	}

	/*
	 * 更新时检测部门名称是否已经存在
	 * */
	@RequestMapping("isExitDeptNameWhenUpdate")
	public String isExitQrcodeConfigWhenUpdate(@RequestBody Map<String,String> map){
		return JSONObject.toJSONString(sysDeptService.isExitDeptNameWhenUpdate(map));
	}
}

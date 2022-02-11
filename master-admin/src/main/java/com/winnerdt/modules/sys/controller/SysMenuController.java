package com.winnerdt.modules.sys.controller;

import com.winnerdt.common.annotation.SysLog;
import com.winnerdt.common.exception.RRException;
import com.winnerdt.common.utils.Constant;
import com.winnerdt.common.utils.R;
import com.winnerdt.modules.sys.entity.SysMenuEntity;
import com.winnerdt.modules.sys.service.SysMenuService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 系统菜单
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年10月27日 下午9:58:15
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends AbstractController {
	@Autowired
	private SysMenuService sysMenuService;

	/**
	 * 导航菜单
	 */
	@RequestMapping("/nav")
	public R nav(){
		List<SysMenuEntity> menuList = sysMenuService.getUserMenuList(getUserId());
		return R.ok().put("menuList", menuList);
	}

	/**
	 * 所有菜单列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:menu:list")
	public R list(){
//		List<SysMenuEntity> menuList = sysMenuService.selectList(null);
//		for(SysMenuEntity sysMenuEntity : menuList){
//			SysMenuEntity parentMenuEntity = sysMenuService.getById(sysMenuEntity.getParentId());
//			if(parentMenuEntity != null){
//				sysMenuEntity.setParentName(parentMenuEntity.getName());
//			}
//		}
//
//		return menuList;
		List<SysMenuEntity> menuList = sysMenuService.treeTableShow();
		return  R.ok().put("list",menuList);
	}

	/*
	* 根据自己的权限情况，获取菜单信息，可以用于菜单授权
	* */
	@RequestMapping("/menuAuthorization")
	public R menuAuthorization(){

		List<SysMenuEntity> menuList = sysMenuService.menuAuthorization();
		return  R.ok().put("list",menuList);
	}

	/**
	 * 选择菜单(添加、修改菜单)
	 */
	@RequestMapping("/select")
	@RequiresPermissions("sys:menu:select")
	public R select(){
		//查询列表数据
		List<SysMenuEntity> menuList = sysMenuService.queryNotButtonList();

		//添加顶级菜单
		SysMenuEntity root = new SysMenuEntity();
		root.setMenuId(0L);
		root.setName("一级菜单");
		root.setParentId(-1L);
		root.setOpen(true);
		menuList.add(root);

		return R.ok().put("menuList", menuList);
	}

	/**
	 * 菜单信息
	 */
	@RequestMapping("/info/{menuId}")
	@RequiresPermissions("sys:menu:info")
	public R info(@PathVariable("menuId") Long menuId){
		SysMenuEntity menu = sysMenuService.getById(menuId);
		return R.ok().put("menu", menu);
	}

	/**
	 * 保存
	 */
	@SysLog("保存菜单")
	@RequestMapping("/save")
	@RequiresPermissions("sys:menu:save")
	public R save(@RequestBody SysMenuEntity menu){
		//数据校验
		verifyForm(menu);
		/*
		* 处理locale字段
		* */
		try{
			String path = menu.getPath();
			if(null != path){
				String localeTemp = path.replace("/",".");
				while (localeTemp.contains("-")){
					int i = localeTemp.indexOf("-");
					StringBuffer localeSBTemp = new StringBuffer(localeTemp);
					localeSBTemp = localeSBTemp.replace((i+1),(i+2),(String.valueOf(localeTemp.charAt(i+1)).toUpperCase()));
					localeTemp = localeSBTemp.toString();
					localeTemp = localeTemp.replaceFirst("-","");
				}
				menu.setLocale("menu"+localeTemp);
			}
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("添加菜单处理locale字段，异常时间："+now+":::异常数据："+menu.toString()+":::异常原因："+e.toString());
		}

		try{
			sysMenuService.save(menu);
			return R.ok();
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("添加菜单异常，异常时间："+now+":::异常数据："+menu.toString()+":::异常原因："+e.toString());
			return R.error("网络错误，菜单添加失败！");
		}

	}

	/**
	 * 修改
	 */
	@SysLog("修改菜单")
	@RequestMapping("/update")
	@RequiresPermissions("sys:menu:update")
	public R update(@RequestBody SysMenuEntity menu){
		//数据校验
		verifyForm(menu);
		//处理localhost字段
		try{
			String path = menu.getPath();
			if(null != path){
				String localeTemp = path.replace("/",".");
				while (localeTemp.contains("-")){
					int i = localeTemp.indexOf("-");
					StringBuffer localeSBTemp = new StringBuffer(localeTemp);
					localeSBTemp = localeSBTemp.replace((i+1),(i+2),(String.valueOf(localeTemp.charAt(i+1)).toUpperCase()));
					localeTemp = localeSBTemp.toString();
					localeTemp = localeTemp.replaceFirst("-","");
				}
				menu.setLocale("menu"+localeTemp);
			}
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("添加菜单处理locale字段，异常时间："+now+":::异常数据："+menu.toString()+":::异常原因："+e.toString());
		}


		try{
			sysMenuService.updateById(menu);
			return R.ok();
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("更新菜单异常，异常时间："+now+":::异常数据："+menu.toString()+":::异常原因："+e.toString());
			return R.error("网络错误，菜单更新失败！");
		}

	}

	/**
	 * 删除
	 */
	@SysLog("删除菜单")
	@RequestMapping("/delete")
	@RequiresPermissions("sys:menu:delete")
	public R delete(@RequestBody long menuId){
		if(menuId <= 31){
			return R.error("系统菜单，不能删除");
		}

		//判断是否有子菜单或按钮
		List<SysMenuEntity> menuList = sysMenuService.queryListParentId(menuId);
		if(menuList.size() > 0){
			return R.error("请先删除子菜单或按钮");
		}


		try{
			sysMenuService.delete(menuId);
			return R.ok();
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("删除菜单异常，异常时间："+now+":::异常数据："+menuId+":::异常原因："+e.toString());
			return R.error("网络错误，菜单更新失败！");
		}

	}

	/**
	 * 验证参数是否正确
	 */
	private void verifyForm(SysMenuEntity menu){
		if(StringUtils.isBlank(menu.getName())){
			throw new RRException("菜单名称不能为空");
		}

		if(menu.getParentId() == null){
			throw new RRException("上级菜单不能为空");
		}

		//菜单
		if(menu.getType() == Constant.MenuType.MENU.getValue()){
			if(StringUtils.isBlank(menu.getPath())){
				throw new RRException("菜单URL不能为空");
			}
		}

		//上级菜单类型
		int parentType = Constant.MenuType.CATALOG.getValue();
		if(menu.getParentId() != 0){
			SysMenuEntity parentMenu = sysMenuService.getById(menu.getParentId());
			parentType = parentMenu.getType();
		}

		//目录、菜单
		if(menu.getType() == Constant.MenuType.CATALOG.getValue() ||
				menu.getType() == Constant.MenuType.MENU.getValue()){
			if(parentType != Constant.MenuType.CATALOG.getValue()){
				throw new RRException("上级菜单只能为目录类型");
			}
			return ;
		}

		//按钮
		if(menu.getType() == Constant.MenuType.BUTTON.getValue()){
			if(parentType != Constant.MenuType.MENU.getValue()){
				throw new RRException("上级菜单只能为菜单类型");
			}
			return ;
		}
	}
}

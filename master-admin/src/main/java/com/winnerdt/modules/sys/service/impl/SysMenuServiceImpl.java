package com.winnerdt.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winnerdt.common.utils.Constant;
import com.winnerdt.common.utils.MapUtils;
import com.winnerdt.modules.sys.dao.SysMenuDao;
import com.winnerdt.modules.sys.entity.SysMenuEntity;
import com.winnerdt.modules.sys.entity.SysRoleMenuEntity;
import com.winnerdt.modules.sys.service.SysMenuService;
import com.winnerdt.modules.sys.service.SysRoleMenuService;
import com.winnerdt.modules.sys.service.SysUserRoleService;
import com.winnerdt.modules.sys.service.SysUserService;
import com.winnerdt.modules.sys.shiro.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.winnerdt.modules.sys.shiro.ShiroUtils.getUserId;


@Service("sysMenuService")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuDao, SysMenuEntity> implements SysMenuService {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysRoleMenuService sysRoleMenuService;
	@Autowired
	private SysUserRoleService sysUserRoleService;


	@Override
	public List<SysMenuEntity> queryListParentId(Long parentId) {
		return baseMapper.queryListParentId(parentId);
	}

	@Override
	public List<SysMenuEntity> queryNotButtonList() {
		return baseMapper.queryNotButtonList();
	}

	@Override
	public void delete(Long menuId){
		//删除菜单
		this.removeById(menuId);
		//删除菜单与角色关联
		sysRoleMenuService.removeByMap(new MapUtils().put("menu_id", menuId));
	}


	/*开始装填主页左侧菜单*/
	@Override
	public List<SysMenuEntity> getUserMenuList(Long userId) {
		//系统管理员，拥有最高权限
		if(userId == Constant.SUPER_ADMIN){
			return getAllMenuList(null);
		}
		
		//获取用户所有的菜单列表
		List<Long> menuIdList = sysUserService.queryAllMenuId(userId);

		return getAllMenuList(menuIdList);
	}

	/**
	 * 获取所有菜单列表
	 */
	private List<SysMenuEntity> getAllMenuList(List<Long> menuIdList){
		/*
		* 查询根菜单列表
		* queryListParentId方法主要是筛选出该用户拥有哪些根菜单
		*
		* */
		List<SysMenuEntity> menuList = queryListParentId(0L, menuIdList);

		//递归获取子菜单
		getMenuTreeList(menuList, menuIdList);
		
		return menuList;
	}

	@Override
	public List<SysMenuEntity> queryListParentId(Long parentId, List<Long> menuIdList) {
		/*
		 * 通过传入的parentId参数，获取该parentId下的所有的菜单
		 * 例如：
		 * 筛选方法，通过接受的parentId= 0，查询所有的根目录，
		 * 然后通过接受menuIdList，将查询到的所有根目录和该用户拥有的所有的菜单中通过id进行对比
		 * 获取该用户拥有的根菜单并返回
		 * */
		List<SysMenuEntity> menuList = queryListParentId(parentId);
		if(menuIdList == null){
			return menuList;
		}

		List<SysMenuEntity> userMenuList = new ArrayList<>();
		for(SysMenuEntity menu : menuList){
			if(menuIdList.contains(menu.getMenuId())){
				userMenuList.add(menu);
			}
		}
		return userMenuList;
	}

	/**
	 * 用于左边导航栏的装填，所以只需要装填到菜单级别
	 * 递归（主要按钮权限的装填）
	 */
	private List<SysMenuEntity> getMenuTreeList(List<SysMenuEntity> menuList, List<Long> menuIdList){
		/*
		* menuList：根目录
		* menuIdList:查询的该用户所有的菜单
		* */
		List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();
		
		for(SysMenuEntity entity : menuList){
			/*
			 * 当menuList是目录时
			 */
			if(entity.getType() == Constant.MenuType.CATALOG.getValue()){
				entity.setChildren(getMenuTreeList(queryListParentId(entity.getMenuId(), menuIdList), menuIdList));
			}

			/*
			* 使用蚂蚁的页面增加的逻辑（如果可以使用shiro的标签，在页面中直接使用标签就不需要这个操作了）：
			* 前台需要该菜单下的所有的按钮的perms字段信息来控制按钮是否显示。
			* 前台具体逻辑：例如管理员管理页面，管理员管理页面有相应的按钮，在这里将按钮的perms信息都以list的形式放到管理员管理菜单项中
			* 当前台点击管理员管理，进入到管理员管理页面时，通过路由参数的形式，将perms信息传到相应的页面，然后再页面中通过比对各个按钮
			* 需要的perms和实际传过去的perms，来达到是否显示按钮的效果。
			*
			* java具体实现：
			* 当menuList是菜单时，获取该菜单下的用户拥有的按钮权限信息，然后通过按钮的parent_id对比是否是该菜单下的按钮，如果是就加入，
			* 反之就不加入
			* */
			if(entity.getType() == Constant.MenuType.MENU.getValue()){
				List<SysMenuEntity> sysMenuEntityList;
				Map map = new HashMap();
				map.put("superAdmin",Constant.SUPER_ADMIN);
				Long userId = getUserId();
				/*
				* 超级管理员拥有所有的按钮
				* */
				if(userId == Constant.SUPER_ADMIN){
					map.put("userId",Constant.SUPER_ADMIN);
					map.put("menuType",Constant.MenuType.BUTTON.getValue());
					sysMenuEntityList = sysUserService.queryAllButton(map);
				}else {
					map.put("userId",getUserId());
					map.put("menuType",Constant.MenuType.BUTTON.getValue());
					sysMenuEntityList = sysUserService.queryAllButton(map);
				}

				/*使用set去重复*/
				Set<String> buttonSet = new HashSet<>();
				for (SysMenuEntity sysMenuEntity:sysMenuEntityList){
					if(sysMenuEntity.getParentId().equals(entity.getMenuId())){
						/*
						* 说明这个按钮是这个菜单下的
						* */
						if(sysMenuEntity.getPerms().contains(",") ){
							String[] str = sysMenuEntity.getPerms().split(",");
							for(int i = 0 ; i < str.length; i++){
								buttonSet.add(str[i]);
							}
						}else {
							buttonSet.add(sysMenuEntity.getPerms());
						}
					}
				}
				entity.setParmsList(buttonSet);
			}

			subMenuList.add(entity);
		}
		
		return subMenuList;
	}

	/*主页左侧菜单装填结束*/


	/*
	* 开始装填菜单页面的treeTable
	* */
	@Override
	public List<SysMenuEntity> treeTableShow() {
		/*
		 * 获取所有的根节点
		 * */
		List<SysMenuEntity> menuList = queryListParentId((long) Constant.MenuType.CATALOG.getValue());
		return getTreeTableList(menuList);
	}

	@Override
	public List<SysMenuEntity> menuAuthorization() {
		//获取当前的用户id
		Long userId = ShiroUtils.getUserId();
		//如果是超级管理员默认获取所有的菜单授权
		if(userId == Constant.SUPER_ADMIN ){
			List<SysMenuEntity> menuList = queryListParentId((long) Constant.MenuType.CATALOG.getValue());
			return getTreeTableList(menuList);
		}else {
			//普通用户通过角色来判断有哪些菜单授权

			//查询当前用户的角色信息
			List<Long> sysUserRoleIdList = sysUserRoleService.queryRoleIdList(userId);
			//通过角色id，查询拥有的菜单id
			List<SysRoleMenuEntity> sysRoleMenuEntityList = sysRoleMenuService.list(new QueryWrapper<SysRoleMenuEntity>()
					.in("role_id",sysUserRoleIdList)
			);

			List<Long> menuIdList = new ArrayList<>();
			for(SysRoleMenuEntity sysRoleMenuEntity:sysRoleMenuEntityList){
				menuIdList.add(sysRoleMenuEntity.getMenuId());
			}

			//拼装菜单授权
			List<SysMenuEntity> menuList = queryListParentId(0L, menuIdList);
			return getMenuTreeListButton(menuList, menuIdList);
		}

	}
	/**
	 *
	 * 菜单授权的装填，所以需要装填到按钮的级别
	 * 递归(主要完成按钮权限信息的装填和菜单中按钮的装填)
	 */
	private List<SysMenuEntity> getMenuTreeListButton(List<SysMenuEntity> menuList, List<Long> menuIdList){
		/*
		 * menuList：根目录
		 * menuIdList:查询的该用户所有的菜单
		 * */
		List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();

		for(SysMenuEntity entity : menuList){
			/*
			 * 当menuList是目录时
			 */
			if(entity.getType() == Constant.MenuType.CATALOG.getValue()){
				entity.setChildren(getMenuTreeListButton(queryListParentId(entity.getMenuId(), menuIdList), menuIdList));
			}


			if(entity.getType() == Constant.MenuType.MENU.getValue()){
				//用来存放该菜单拥有的按钮信息
				List<SysMenuEntity> buttonEntityList = new ArrayList<>();
				//用来存放所有的按钮信息
				List<SysMenuEntity> sysMenuEntityList;
				Map map = new HashMap();
				map.put("superAdmin",Constant.SUPER_ADMIN);
				Long userId = getUserId();
				/*
				 * 超级管理员拥有所有的按钮
				 * */
				if(userId == Constant.SUPER_ADMIN){
					map.put("userId",Constant.SUPER_ADMIN);
					map.put("menuType",Constant.MenuType.BUTTON.getValue());
					sysMenuEntityList = sysUserService.queryAllButton(map);
				}else {
					map.put("userId",getUserId());
					map.put("menuType",Constant.MenuType.BUTTON.getValue());
					sysMenuEntityList = sysUserService.queryAllButton(map);
				}

				for (SysMenuEntity sysMenuEntity:sysMenuEntityList){
					if(sysMenuEntity.getParentId().equals(entity.getMenuId())){
						//说明这个按钮是这个菜单下的
						buttonEntityList.add(sysMenuEntity);
					}
				}
				entity.setChildren(buttonEntityList);
			}
			subMenuList.add(entity);
		}

		return subMenuList;
	}

	/*
	* 递归装填所有的菜单
	* */
	private List<SysMenuEntity> getTreeTableList(List<SysMenuEntity> menuList){

		for(SysMenuEntity menuEntity:menuList){
			List<SysMenuEntity> sysMenuEntityListTemp = queryListParentId(menuEntity.getMenuId());
			if(sysMenuEntityListTemp != null){
				menuEntity.setChildren(getTreeTableList(sysMenuEntityListTemp));
			}else {
				continue;
			}

		}

		return menuList;
	}


}

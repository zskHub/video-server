package com.winnerdt.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winnerdt.common.annotation.DataFilter;
import com.winnerdt.common.utils.Constant;
import com.winnerdt.common.utils.PageUtils;
import com.winnerdt.common.utils.Query;
import com.winnerdt.modules.sys.dao.SysRoleDao;
import com.winnerdt.modules.sys.entity.SysDeptEntity;
import com.winnerdt.modules.sys.entity.SysRoleEntity;
import com.winnerdt.modules.sys.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 角色
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年9月18日 上午9:45:12
 */
@Service("sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleDao, SysRoleEntity> implements SysRoleService {
	@Autowired
	private SysRoleMenuService sysRoleMenuService;
	@Autowired
	private SysRoleDeptService sysRoleDeptService;
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private SysDeptService sysDeptService;
	@Autowired
	private SysRoleDao sysRoleDao;

	@Override
	@DataFilter(subDept = true, user = false)
	public PageUtils queryPage(Map<String, Object> params) {
		String roleName = null;
		if(null != params.get("roleName")){
			roleName = (String)params.get("roleName");
		}

        Long deptId = null;
		if(null != params.get("deptId")){
            deptId = Long.valueOf((String)params.get("deptId"));
        }

		Page<SysRoleEntity> page = (Page<SysRoleEntity>) this.page(
			new Query<SysRoleEntity>(params).getPage(),
			new QueryWrapper<SysRoleEntity>()
				.like(StringUtils.isNotBlank(roleName),"role_name", roleName)
                .eq(deptId != null,"dept_id",deptId)
				.apply(params.get(Constant.SQL_FILTER) != null, (String)params.get(Constant.SQL_FILTER))

		);

		for(SysRoleEntity sysRoleEntity : page.getRecords()){
			SysDeptEntity sysDeptEntity = sysDeptService.getById(sysRoleEntity.getDeptId());
			if(sysDeptEntity != null){
				sysRoleEntity.setDeptName(sysDeptEntity.getName());
			}
			//查询角色对应的菜单
			List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(sysRoleEntity.getRoleId());
			sysRoleEntity.setMenuIdList(menuIdList);

			//查询角色对应的部门
			List<Long> deptIdList = sysRoleDeptService.queryDeptIdList(new Long[]{sysRoleEntity.getRoleId()});
			sysRoleEntity.setDeptIdList(deptIdList);

		}

		return new PageUtils(page);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean save(SysRoleEntity role) {
		role.setCreateTime(new Date());
		super.save(role);
		//保存角色与菜单关系
		sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());

		//保存角色与部门关系
		sysRoleDeptService.saveOrUpdate(role.getRoleId(), role.getDeptIdList());

		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(SysRoleEntity role) {
		this.updateById(role);

		//更新角色与菜单关系
		sysRoleMenuService.saveOrUpdate(role.getRoleId(), role.getMenuIdList());

		//保存角色与部门关系
		sysRoleDeptService.saveOrUpdate(role.getRoleId(), role.getDeptIdList());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatch(Long[] roleIds) {
		//删除角色
		this.removeByIds(Arrays.asList(roleIds));

		//删除角色与菜单关联
		sysRoleMenuService.deleteBatch(roleIds);

		//删除角色与部门关联
		sysRoleDeptService.deleteBatch(roleIds);

		//删除角色与用户关联
		sysUserRoleService.deleteBatch(roleIds);
	}
	@Override
	public String isExistByRoleName(String roleName) {
		List<SysRoleEntity> list = super.list(new QueryWrapper<SysRoleEntity>().eq("role_name",roleName));
		if(list.size() > 0){
			return "exist";
		}else {
			return "noExist";
		}
	}

}

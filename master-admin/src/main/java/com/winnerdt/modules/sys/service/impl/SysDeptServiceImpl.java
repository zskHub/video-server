package com.winnerdt.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winnerdt.common.annotation.DataFilter;
import com.winnerdt.modules.sys.dao.SysDeptDao;
import com.winnerdt.modules.sys.entity.SysDeptEntity;
import com.winnerdt.modules.sys.service.SysDeptService;
import com.winnerdt.modules.sys.service.SysRoleDeptService;
import com.winnerdt.modules.sys.service.SysUserRoleService;
import com.winnerdt.modules.sys.shiro.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("sysDeptService")
public class SysDeptServiceImpl extends ServiceImpl<SysDeptDao, SysDeptEntity> implements SysDeptService {
	@Autowired
	private SysDeptDao sysDeptDao;
	@Autowired
	private SysRoleDeptService sysRoleDeptService;
	@Autowired
	private SysUserRoleService sysUserRoleService;

	
	@Override
	@DataFilter(subDept = true, user = false, tableAlias = "t1")
	public List<SysDeptEntity> queryList(Map<String, Object> params){
		List<SysDeptEntity> deptList = baseMapper.queryList(params);

		for(SysDeptEntity sysDeptEntity : deptList){
			SysDeptEntity parentDeptEntity =  this.getById(sysDeptEntity.getParentId());
			if(parentDeptEntity != null){
				sysDeptEntity.setParentName(parentDeptEntity.getName());
			}
		}
		return deptList;
	}

	@Override
	public List<Long> queryDetpIdList(Long parentId) {
		return baseMapper.queryDetpIdList(parentId);
	}

	@Override
	public List<Long> getSubDeptIdList(Long deptId){
		//部门及子部门ID列表
		List<Long> deptIdList = new ArrayList<>();

		//获取子部门ID
		List<Long> subIdList = queryDetpIdList(deptId);
		getDeptTreeList(subIdList, deptIdList);

		return deptIdList;
	}

	@Override
	public List<SysDeptEntity> queryDetpList(Long parentId) {
		return sysDeptDao.queryDetpList(parentId);
	}


	@Override
	public List<SysDeptEntity> treeTableShow() {

		/*
		* 查询顶级部门列表
		* */
//		List<SysDeptEntity> sysDeptEntityList = sysDeptDao.queryDetpList((long) Constant.MenuType.CATALOG.getValue());

		/*
		* 现在将逻辑改成：添加用户或者操作部门时，只能操作自己拥有的部门权限。
		* 如果想操作所有的部门权限，将下面的注释了，放开上面的注释就行了
		* */

		/*
		* 查询当前用户能查看的部门信息
		* */
		Long deptId = ShiroUtils.getUserEntity().getDeptId();
		Long userId = ShiroUtils.getUserId();


		//通过管理员的角色数据授权信息查询部门id

		List<Long> finDeptId = new ArrayList<>();

		List<Long> adminDeptIdList = this.getSubDeptIdList(deptId);
		finDeptId.add(deptId);
		//对比角色中的部门id和管理员拥有的部门id
		List<Long> roleDeptIdList = new ArrayList<>();
		List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
		if(roleIdList.size() > 0){
			List<Long> userDeptIdList = sysRoleDeptService.queryDeptIdList(roleIdList.toArray(new Long[roleIdList.size()]));
			roleDeptIdList.addAll(userDeptIdList);
		}
		List<Long> diffDeptIdList  = getDifferent(adminDeptIdList,roleDeptIdList);

		for (Long roleDeptId:roleDeptIdList){
			if(diffDeptIdList.contains(roleDeptId)){
				finDeptId.add(roleDeptId);
			}
		}

		//查询部门信息
		List<SysDeptEntity> sysDeptEntityList = sysDeptDao.selectList(new QueryWrapper<SysDeptEntity>()
				.eq("del_flag",0)
				.eq("status",0)
				.in("dept_id",finDeptId)
				.orderByAsc("order_num")
		);

		return getTreeTableList(sysDeptEntityList);
	}

    @Override
    public String isExitDeptNameWhenAdd(String deptName) {
        List<SysDeptEntity> list = sysDeptDao.selectList(new QueryWrapper<SysDeptEntity>().eq("name",deptName));
        if(list.size() > 0){
            return "exist";
        }else {
            return "noExist";
        }
    }

    @Override
    public String isExitDeptNameWhenUpdate(Map map) {
        String deptName = null;
        String deptId = null;
        if(null != map.get("deptName")){
            deptName = map.get("deptName").toString();
        }
        if(null != map.get("deptId")){
            deptId = map.get("deptId").toString();
        }

        List<SysDeptEntity> list = sysDeptDao.selectList(new QueryWrapper<SysDeptEntity>()
                .eq("name",deptName));

        if(list.size() > 0){
            for(SysDeptEntity sysDeptEntity:list){
                if(!(sysDeptEntity.getDeptId().equals(Long.valueOf(deptId)))){
                    return "exist";
                }
            }
            return "noExist";
        }else {
            return "noExist";
        }
    }

    /*
	 * 递归装填所有的菜单
	 * */
	private List<SysDeptEntity> getTreeTableList(List<SysDeptEntity> sysDeptEntityList){

		for(SysDeptEntity sysDeptEntity:sysDeptEntityList){
			List<SysDeptEntity> sysMenuEntityListTemp = queryDetpList(sysDeptEntity.getDeptId());

			if(sysMenuEntityListTemp != null){
				sysDeptEntity.setChildren(getTreeTableList(sysMenuEntityListTemp));
			}else {
				continue;
			}

		}

		return sysDeptEntityList;
	}

	/**
	 * 递归
	 */
	private void getDeptTreeList(List<Long> subIdList, List<Long> deptIdList){
		for(Long deptId : subIdList){
			List<Long> list = queryDetpIdList(deptId);
			if(list.size() > 0){
				getDeptTreeList(list, deptIdList);
			}

			deptIdList.add(deptId);
		}
	}

	public static List<Long> getDifferent(List<Long> list1, List<Long> list2) {
		Map<Long,Integer> map = new HashMap<Long,Integer>(list1.size()+list2.size());
		List<Long> diff = new ArrayList<Long>();
		List<Long> maxList = list1;
		List<Long> minList = list2;
		if(list2.size()>list1.size()) {
			maxList = list2;
			minList = list1;
		}

		for (Long string : maxList) {
			map.put(string, 1);
		}

		for (Long string : minList) {
			Integer cc = map.get(string);
			if(cc!=null) {
				map.put(string, ++cc);
				continue;
			}
			map.put(string, 1);
		}

		for(Map.Entry<Long, Integer> entry:map.entrySet()) {
			if(entry.getValue()==1) {
				diff.add(entry.getKey());
			}
		}
		return diff;
	}
}

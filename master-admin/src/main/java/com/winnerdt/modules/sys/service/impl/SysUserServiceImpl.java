package com.winnerdt.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winnerdt.common.annotation.DataFilter;
import com.winnerdt.common.utils.Constant;
import com.winnerdt.common.utils.PageUtils;
import com.winnerdt.common.utils.Query;
import com.winnerdt.modules.sys.dao.SysUserDao;
import com.winnerdt.modules.sys.entity.SysDeptEntity;
import com.winnerdt.modules.sys.entity.SysMenuEntity;
import com.winnerdt.modules.sys.entity.SysRoleEntity;
import com.winnerdt.modules.sys.entity.SysUserEntity;
import com.winnerdt.modules.sys.service.SysDeptService;
import com.winnerdt.modules.sys.service.SysUserRoleService;
import com.winnerdt.modules.sys.service.SysUserService;
import com.winnerdt.modules.sys.shiro.ShiroUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 系统用户
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年9月18日 上午9:46:09
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private SysDeptService sysDeptService;
	@Autowired
	private SysUserDao sysUserDao;

	@Override
	public List<Long> queryAllMenuId(Long userId) {
		return baseMapper.queryAllMenuId(userId);
	}

	@Override
	@DataFilter(subDept = true, user = false)
	public PageUtils queryPage(Map<String, Object> params) {
		String username = (String)params.get("username");
		String mobile = (String)params.get("mobile");
        Long deptId = null;
        if(null != params.get("deptId")){
            deptId = Long.valueOf((String)params.get("deptId"));
        }
		String statusStr = (String)params.get("status");
		Boolean statusTemp1 = false;
		Boolean statusTemp2 = false;
		if(statusStr != null ){
			if(statusStr.equals("0")){
				statusTemp1 = true;
			}else if(statusStr.equals("1")){
				statusTemp2 = true;
			}

		}


		Page<SysUserEntity> page = (Page<SysUserEntity>) this.page(
			new Query<SysUserEntity>(params).getPage(),
			new QueryWrapper<SysUserEntity>()
				.like(StringUtils.isNotBlank(username),"username", username)
				.like(StringUtils.isNotBlank(mobile),"mobile",mobile)
				.eq(deptId != null,"dept_id",deptId)
				.eq(statusTemp1,"status",0)
				.eq(statusTemp2,"status",1)
				.apply(params.get(Constant.SQL_FILTER) != null, (String)params.get(Constant.SQL_FILTER))
		);

		for(SysUserEntity sysUserEntity : page.getRecords()){
			SysDeptEntity sysDeptEntity = sysDeptService.getById(sysUserEntity.getDeptId());
			sysUserEntity.setDeptName(sysDeptEntity.getName());

			/*
			* 拼装一下用户角色id，前台修改用户信息回显时需要
			* */
			List<SysRoleEntity> roleList = sysUserDao.queryAllRole(sysUserEntity.getUserId());
			List roleIdList = new ArrayList<>();
			for(SysRoleEntity sysRoleEntity :roleList){
				if(sysRoleEntity.getRoleId() != null){
					roleIdList.add(sysRoleEntity.getRoleId().toString());
				}
			}
			sysUserEntity.setRoleIdList(roleIdList);
		}

		return new PageUtils(page);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean save(SysUserEntity user) {
		user.setCreateTime(new Date());
		//sha256加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		user.setSalt(salt);
		user.setPassword(ShiroUtils.sha256(user.getPassword(), user.getSalt()));
		super.save(user);

		//保存用户与角色关系
		sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(SysUserEntity user) {
		if(StringUtils.isBlank(user.getPassword())){
			user.setPassword(null);
		}else{
			SysUserEntity userEntity = this.getById(user.getUserId());
			user.setPassword(ShiroUtils.sha256(user.getPassword(), userEntity.getSalt()));
		}

		//用户名不让更新
		user.setUsername(null);

		this.updateById(user);
		
		//保存用户与角色关系
		sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
	}


	@Override
	public boolean updatePassword(Long userId, String password, String newPassword) {
        SysUserEntity userEntity = new SysUserEntity();
        userEntity.setPassword(newPassword);
        return this.update(userEntity,
                new QueryWrapper<SysUserEntity>().eq("user_id", userId).eq("password", password));
    }

	@Override
	public List<SysMenuEntity> queryAllButton(Map map) {
		return sysUserDao.queryAllButton(map);
	}

	@Override
	public List<SysRoleEntity> queryAllRole(Long userId) {
		return sysUserDao.queryAllRole(userId);
	}

	@Override
	public String isExistByUserName(String userName) {
		List<SysUserEntity> list = super.list(new QueryWrapper<SysUserEntity>().eq("username",userName));
		if(list.size() > 0){
			return "exist";
		}else {
			return "noExist";
		}
	}

}

package com.winnerdt.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface SysDeptDao extends BaseMapper<SysDeptEntity> {

    List<SysDeptEntity> queryList(Map<String, Object> params);

    /**
     * 查询子部门ID列表
     * @param parentId  上级部门ID
     */
    List<Long> queryDetpIdList(Long parentId);

    /*
    * 查询子部门列表
    * */
    List<SysDeptEntity> queryDetpList(Long parentId);

}

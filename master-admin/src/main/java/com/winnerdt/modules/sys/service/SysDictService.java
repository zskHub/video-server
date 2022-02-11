package com.winnerdt.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.winnerdt.modules.sys.entity.SysDictEntity;
import com.winnerdt.common.utils.PageUtils;

import java.util.Map;

/**
 * 数据字典
 *
 * @author Mark sunlightcs@gmail.com
 * @since 3.1.0 2018-01-27
 */
public interface SysDictService extends IService<SysDictEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


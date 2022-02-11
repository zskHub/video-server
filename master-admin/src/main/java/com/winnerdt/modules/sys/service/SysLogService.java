package com.winnerdt.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.winnerdt.modules.sys.entity.SysLogEntity;
import com.winnerdt.common.utils.PageUtils;

import java.util.Map;


/**
 * 系统日志
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-08 10:40:56
 */
public interface SysLogService extends IService<SysLogEntity> {

    PageUtils queryPage(Map<String, Object> params);

}

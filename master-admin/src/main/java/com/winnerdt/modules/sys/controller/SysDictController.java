package com.winnerdt.modules.sys.controller;

import com.alibaba.fastjson.JSONObject;
import com.winnerdt.modules.sys.entity.SysDictEntity;
import com.winnerdt.modules.sys.service.SysDictService;
import com.winnerdt.common.utils.PageUtils;
import com.winnerdt.common.utils.R;
import com.winnerdt.common.validator.ValidatorUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * 数据字典
 *
 * @author Mark sunlightcs@gmail.com
 * @since 3.1.0 2018-01-27
 */
@RestController
@RequestMapping("sys/dict")
public class SysDictController {
    private static final Logger logger = LoggerFactory.getLogger(SysDictController.class);

    @Autowired
    private SysDictService sysDictService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:dict:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysDictService.queryPage(params);

        return R.ok().put("list", page.getList()).put("pagination",page.getPagination());
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:dict:info")
    public R info(@PathVariable("id") Long id){
        SysDictEntity dict = sysDictService.getById(id);

        return R.ok().put("dict", dict);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:dict:save")
    public R save(@RequestBody SysDictEntity dict){
        //校验类型
        ValidatorUtils.validateEntity(dict);

        try{
            sysDictService.save(dict);
            return R.ok();
        }catch (Exception e){
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = sdf.format(date);
            logger.error("保存字典信息异常，异常时间："+now+":::异常数据："+dict.toString()+":::异常原因："+e.toString());
            return R.error("网络错误，字典保存失败！");
        }

    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:dict:update")
    public R update(@RequestBody SysDictEntity dict){
        //校验类型
        ValidatorUtils.validateEntity(dict);

        try{
            sysDictService.updateById(dict);
            return R.ok();
        }catch (Exception e){
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = sdf.format(date);
            logger.error("修改字典信息异常，异常时间："+now+":::异常数据："+dict.toString()+":::异常原因："+e.toString());
            return R.error("网络错误，字典修改失败！");
        }

    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:dict:delete")
    public R delete(@RequestBody Long[] ids){

        try{
            sysDictService.removeByIds(Arrays.asList(ids));
            return R.ok();
        }catch (Exception e){
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = sdf.format(date);
            logger.error("删除字典信息异常，异常时间："+now+":::异常数据："+ JSONObject.toJSONString(ids)+":::异常原因："+e.toString());
            return R.error("网络错误，字典删除失败！");
        }
    }

}

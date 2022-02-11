package com.winnerdt.modules.oss.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.winnerdt.common.utils.ConfigConstant;
import com.winnerdt.common.utils.Constant;
import com.winnerdt.common.utils.PageUtils;
import com.winnerdt.common.utils.R;
import com.winnerdt.common.validator.ValidatorUtils;
import com.winnerdt.common.validator.group.AliyunGroup;
import com.winnerdt.common.validator.group.QcloudGroup;
import com.winnerdt.common.validator.group.QiniuGroup;
import com.winnerdt.modules.oss.cloud.CloudStorageConfig;
import com.winnerdt.modules.oss.cloud.OSSFactory;
import com.winnerdt.modules.oss.entity.SysOssEntity;
import com.winnerdt.modules.oss.service.SysOssService;
import com.winnerdt.modules.sys.service.SysConfigService;
import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件上传
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-25 12:13:26
 */
@RestController
@RequestMapping("sys/oss")
public class SysOssController {
	private static final Logger logger = LoggerFactory.getLogger(SysOssController.class);

	@Autowired
	private SysOssService sysOssService;
    @Autowired
    private SysConfigService sysConfigService;

    private final static String KEY = ConfigConstant.CLOUD_STORAGE_CONFIG_KEY;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:oss:list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = sysOssService.queryPage(params);

		return R.ok().put("list", page.getList()).put("pagination",page.getPagination());
	}


    /**
     * 云存储配置信息
     */
    @RequestMapping("/config")
    @RequiresPermissions("sys:oss:list")
    public R config(){
        CloudStorageConfig config = sysConfigService.getConfigObject(KEY, CloudStorageConfig.class);

        return R.ok().put("config", config);
    }


	/**
	 * 保存云存储配置信息
	 */
	@RequestMapping("/saveConfig")
	@RequiresPermissions("sys:oss:save")
	public R saveConfig(@RequestBody CloudStorageConfig config){
		//校验类型
		ValidatorUtils.validateEntity(config);

		if(config.getType() == Constant.CloudService.QINIU.getValue()){
			//校验七牛数据
			ValidatorUtils.validateEntity(config, QiniuGroup.class);
		}else if(config.getType() == Constant.CloudService.ALIYUN.getValue()){
			//校验阿里云数据
			ValidatorUtils.validateEntity(config, AliyunGroup.class);
		}else if(config.getType() == Constant.CloudService.QCLOUD.getValue()){
			//校验腾讯云数据
			ValidatorUtils.validateEntity(config, QcloudGroup.class);
		}

        sysConfigService.updateValueByKey(KEY, new Gson().toJson(config));

		return R.ok();
	}
	

	/**
	 * 上传文件
	 */
	@RequestMapping("/upload")
	@RequiresPermissions("sys:oss:upload")
	public R upload(@RequestBody Map<String,Object> fileForm) throws Exception {
		List<String> suffixList = new ArrayList<>();
		suffixList.add(".jpg");
		suffixList.add(".jpeg");
		suffixList.add(".png");

		if(null == fileForm){
			return R.error("上传文件为空");
		}
		try{
			Map fileListMap = (Map) fileForm.get("fileForm");
			List<Map<String,Object>> fileList = (List) fileListMap.get("fileList");
			/*
			* 先判断本次上传格式是否正确
			* */
			for(Map map:fileList){
				String originalFilename = map.get("name").toString();
				String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
				if(!(suffixList.contains(suffix))){
					return R.error("上传类型有误,只支持jpg和png格式");
				}
			}

			/*
			* 开始处理上传
			* */
			for(Map map:fileList){
				String originalFilename = map.get("name").toString();
				String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
				String base64Str = map.get("thumbUrl").toString();
				String baseTemp = base64Str.substring(base64Str.indexOf(","),base64Str.length());
				byte[] data = Base64.decodeBase64(baseTemp);


				/*
				* 上传文件
				* */
				Map<String,String> resultMap = OSSFactory.build().uploadSuffix(data, suffix);

				/*保存数据*/
				SysOssEntity ossEntity = new SysOssEntity();
				try{
					ossEntity.setUrl(resultMap.get("url"));
					ossEntity.setFileName(resultMap.get("fileName"));
					ossEntity.setBucketName(resultMap.get("bucketName"));
					ossEntity.setCreateDate(new Date());
					sysOssService.save(ossEntity);
				}catch (Exception e){
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String now = sdf.format(date);
					logger.error("数据存库异常，异常时间："+now+":::异常数据："+ JSONUtils.toJSONString(ossEntity)+":::异常原因："+e.toString());
				}
			}

			return R.ok();
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("数据解析异常，异常时间："+now+":::异常数据："+ JSONUtils.toJSONString(fileForm)+":::异常原因："+e.toString());
			return R.error("网络错误，上传图片失败！");

		}

	}


	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:oss:delete")
	public R delete(@RequestBody Long[] ids){

		try{

			List<SysOssEntity> sysOssEntityList = new ArrayList<>();
			for(long id:ids){
				SysOssEntity sysOssEntity = sysOssService.getById(id);
				sysOssEntityList.add(sysOssEntity);
			}
			/*
			* 删除数据库
			* */
			sysOssService.removeByIds(Arrays.asList(ids));

			/*
			* 删除云存储数据
			* */
			try{
				for(SysOssEntity sysOssEntity : sysOssEntityList){
					OSSFactory.build().delete(sysOssEntity.getBucketName(),sysOssEntity.getFileName());
				}
			}catch (Exception e){
				e.printStackTrace();
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String now = sdf.format(date);
				logger.error("云存储删除异常，异常时间："+now+":::异常数据："+ JSONObject.toJSONString(ids)+":::异常原因："+e.toString());
				return R.error("网络错误，云储存删除失败！");
			}

			return R.ok();
		}catch (Exception e){
			e.printStackTrace();
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("OSS删除数据库异常，异常时间："+now+":::异常数据："+ JSONObject.toJSONString(ids)+":::异常原因："+e.toString());
			return R.error("网络错误，删除失败！");
		}
	}

}

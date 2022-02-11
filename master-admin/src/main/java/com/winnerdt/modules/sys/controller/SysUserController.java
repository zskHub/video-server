package com.winnerdt.modules.sys.controller;


import com.alibaba.fastjson.JSONObject;
import com.winnerdt.common.annotation.SysLog;
import com.winnerdt.common.utils.DeleteFileUtil;
import com.winnerdt.common.utils.PageUtils;
import com.winnerdt.common.utils.R;
import com.winnerdt.common.utils.UploadUtil;
import com.winnerdt.common.utils.base64ToMultipartFile.Base64MultipartFileUtil;
import com.winnerdt.common.validator.Assert;
import com.winnerdt.common.validator.ValidatorUtils;
import com.winnerdt.common.validator.group.AddGroup;
import com.winnerdt.common.validator.group.UpdateGroup;
import com.winnerdt.modules.sys.entity.SysDeptEntity;
import com.winnerdt.modules.sys.entity.SysUserEntity;
import com.winnerdt.modules.sys.service.SysDeptService;
import com.winnerdt.modules.sys.service.SysUserRoleService;
import com.winnerdt.modules.sys.service.SysUserService;
import com.winnerdt.modules.sys.shiro.ShiroUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 系统用户
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年10月31日 上午10:40:10
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends AbstractController {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private SysDeptService sysDeptService;
    @Autowired
    UploadUtil uploadUtil;
    @Value("${master.imagePath}")
    private String imagePath;

	/**
	 * 所有用户列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:user:list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = sysUserService.queryPage(params);

		return R.ok().put("list", page.getList()).put("pagination",page.getPagination());
	}

	/**
	 * 获取登录的用户信息
	 */
	@RequestMapping("/info")
	public R info(){
		SysUserEntity sysUserEntity = sysUserService.getById(getUser().getUserId());
		SysDeptEntity sysDeptEntity = sysDeptService.getById(sysUserEntity.getDeptId());
		sysUserEntity.setDeptName(sysDeptEntity.getName());
		return R.ok().put("user", sysUserEntity);
	}

	/**
	 * 修改登录用户密码
	 */
	@SysLog("修改密码")
	@RequestMapping("/password")
	public R password(String password, String newPassword){
		Assert.isBlank(newPassword, "新密码不为能空");

		//原密码
		password = ShiroUtils.sha256(password, getUser().getSalt());
		//新密码
		newPassword = ShiroUtils.sha256(newPassword, getUser().getSalt());

		//更新密码
		boolean flag = sysUserService.updatePassword(getUserId(), password, newPassword);
		if(!flag){
			return R.error("原密码不正确");
		}

		return R.ok();
	}

	/**
	 * 用户信息
	 */
	@RequestMapping("/info/{userId}")
	@RequiresPermissions("sys:user:info")
	public R info(@PathVariable("userId") Long userId){
		SysUserEntity user = sysUserService.getById(userId);

		//获取用户所属的角色列表
		List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
		user.setRoleIdList(roleIdList);

		//获取用户部门名称
		SysDeptEntity sysDeptEntity = sysDeptService.getById(user.getDeptId());
		user.setDeptName(sysDeptEntity.getName());

		return R.ok().put("user", user);
	}

	/**
	 * 保存用户
	 */
	@SysLog("保存用户")
	@RequestMapping("/save")
	@RequiresPermissions("sys:user:save")
	public R save(@RequestBody SysUserEntity user){
		ValidatorUtils.validateEntity(user, AddGroup.class);

		try{
			sysUserService.save(user);
			return R.ok();
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("新增用户异常，异常时间："+now+":::异常数据："+user.toString()+":::异常原因："+e.toString());
			return R.error("网络错误，添加失败！");
		}
	}

	/**
	 * 修改用户
	 */
	@SysLog("修改用户")
	@RequestMapping("/update")
	@RequiresPermissions("sys:user:update")
	public R update(@RequestBody SysUserEntity user){
		ValidatorUtils.validateEntity(user, UpdateGroup.class);


		try{
			sysUserService.update(user);
			return R.ok();
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("更新用户异常，异常时间："+now+":::异常数据："+user.toString()+":::异常原因："+e.toString());
			return R.error("网络错误，更新失败！");
		}

	}

	/**
	 * 修改用户基本信息
	 */
	@SysLog("修改用户")
	@RequestMapping("/updateBasic")
	@RequiresPermissions("sys:user:update")
	public R updateBasic(@RequestBody SysUserEntity user){
	    R r = new R();
	    Boolean isUpdatePassword = false;
		if(null == user || user.getUserId() == null){
			return R.error("用户信息丢失");
		}
		if(null != user.getPassword()){
			user.setPassword(ShiroUtils.sha256(user.getPassword(), getUser().getSalt()));
			isUpdatePassword =true;
		}

		try{
			sysUserService.updateById(user);
			if(isUpdatePassword){
			    r.put("isUpdatePassword","true");
			    r.put("msg","信息修改成功，密码已修改请重新登录");
			    r.put("code",0);
			    return r;
            }else {
                r.put("isUpdatePassword","false");
                r.put("msg","信息修改成功");
                r.put("code",0);
                return r;
            }
		}catch (Exception e){
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(date);
			logger.error("更新用户异常，异常时间："+now+":::异常数据："+user.toString()+":::异常原因："+e.toString());
			return R.error("网络错误，更新失败！");
		}

	}

	/**
	 * 删除用户
	 */
	@SysLog("删除用户")
	@RequestMapping("/delete")
	@RequiresPermissions("sys:user:delete")
	public R delete(@RequestBody Long[] userIds){
		if(ArrayUtils.contains(userIds, 1L)){
			return R.error("系统管理员不能删除");
		}

		if(ArrayUtils.contains(userIds, getUserId())){
			return R.error("当前用户不能删除");
		}
        try{
            sysUserService.removeByIds(Arrays.asList(userIds));
			return R.ok();
        }catch (Exception e){
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = sdf.format(date);
            logger.error("用户删除异常，异常时间："+now+":::异常数据："+ JSONObject.toJSONString(userIds)+":::异常原因："+e.toString());
			return R.error("网络错误，删除失败！");
        }
	}


    /**
     * 头像上传
     */
    @PostMapping(value = "upload")
    @ResponseBody
    public R imgUpload(@RequestBody Map model) {
        R r = new R();
        Long userId = null;
        String originalAvatar = null;
        MultipartFile file = null;

        //处理接受的参数
        try{
        	originalAvatar =  model.get("originalAvatar").toString();
            String base64 = model.get("newAvatar").toString();
            file = Base64MultipartFileUtil.base64ToMultipart(base64);

            if(null != model.get("userId")){
                userId = Long.valueOf(model.get("userId").toString());
            }else {
                userId = getUserId();
            }
        }catch (Exception e){
            e.printStackTrace();

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = sdf.format(date);
            logger.error("头像上传，参数处理异常，异常时间："+now+":::异常数据："+ JSONObject.toJSONString(model)+"接受的文件：：："+file+":::异常原因："+e.toString());

            r.put("code",500);
            r.put("msg","网络错误，头像更新失败");
            r.put("avatar",originalAvatar);
            return r;
        }

        /*
        * 上传逻辑：总的逻辑：1.上传新图片》》》2.更新数据库》》》3.删除原来的图片
        * 1.上传新图片：
        *   1.1 成功:执行第2步
        *   1.2 上传失败：返回错误信息和原来的图片信息
        * 2.更新数据库
        *   2.1 更新成功：执行第3步。
        *   2.2 更新失败，删除新上传的图片，返回错误信息和原来的图片信息
        *   2.2.1 删除新上传图片成功：返回2.2中的错误信息
        *   2.2.2 删除新上传图片失败：记录本次错误信息，返回2.2中的错误信息
        * 3. 删除原来的图片
        *   3.1 删除成功：返回修改成功提示，返回新图片信息
        *   3.2 删除失败：返回修改成功提示，返回新图片信息（记录异常信息，因为不影响更新图片，所以前端不报错）
        *
        * */
        try{
            //上传图片
            String fileName=uploadUtil.upload(file);

            //更新数据库
            try{
                SysUserEntity sysUserEntity = new SysUserEntity();
                sysUserEntity.setUserId(userId);
                sysUserEntity.setAvatar(fileName);
                sysUserService.updateById(sysUserEntity);

            }catch (Exception e){

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String now = sdf.format(date);
                logger.error("头像上传，数据库更新异常，异常时间："+now+":::异常数据："+ JSONObject.toJSONString(model)+"接受的文件：：："+file+":::异常原因："+e.toString());

                //数据库更新失败，将已经上传的文件删除了
                try{
                    File fileTemp = new File(imagePath+fileName);
                    String newFilePath = fileTemp.getAbsolutePath();
                    DeleteFileUtil.delete(newFilePath);

                }catch (Exception E){
                    Date date1 = new Date();
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String now1 = sdf1.format(date1);
                    logger.error("头像上传失败，新上传文件删除异常，异常时间："+now1+":::异常数据："+ JSONObject.toJSONString(model)+"接受的文件：：："+file+":::异常原因："+e.toString());

                    r.put("code",500);
                    r.put("msg","头像更新失败");
                    r.put("avatar",originalAvatar);
                    return r;
                }

                r.put("code",500);
                r.put("msg","头像更新失败");
                r.put("avatar",originalAvatar);
                return r;
            }

            /*
            * 上传完成后，删除原来的头像信息，防止服务器数据冗余
            * */
            try{
                if("defaultAvatar".equals(originalAvatar)){
                    //默认头像不做删除
                }else {
                    File fileTemp = new File(imagePath+originalAvatar);
                    String originalFilePath = fileTemp.getAbsolutePath();
                    DeleteFileUtil.delete(originalFilePath);
                }

            }catch (Exception e){
                /*
                * 原头像删除异常时，依旧返回正常信息，不影响新头像的使用
                * */
                Date date1 = new Date();
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String now1 = sdf1.format(date1);
                logger.error("头像上传成功，原文件删除异常，异常时间："+now1+":::异常数据："+ JSONObject.toJSONString(model)+"接受的文件：：："+file+":::异常原因："+e.toString());

                r.put("code",0);
                r.put("avatar",fileName);
                return r;
            }

            r.put("code",0);
            r.put("avatar",fileName);
            return r;
        }catch (Exception e){
        	e.printStackTrace();
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = sdf.format(date);
            logger.error("头像上传，上传异常，异常时间："+now+":::异常数据："+ JSONObject.toJSONString(model)+"接受的文件：：："+file+":::异常原因："+e.toString());


            r.put("code",500);
            r.put("msg","头像更新失败");
            r.put("avatar",originalAvatar);
            return r;
        }

    }


	/*
	* 通过用户名查询用户是否已经存在
	*
	* */
	@RequestMapping("isExistByUserName")
    public String isExistByUserName(@RequestBody Map<String,String> map){
        return JSONObject.toJSONString(sysUserService.isExistByUserName(map.get("userName")));
    }
}

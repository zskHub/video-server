package com.winnerdt.modules.sys.controller;



import com.google.code.kaptcha.Constants;
import com.winnerdt.modules.sys.shiro.ShiroUtils;
import com.winnerdt.common.utils.R;
import com.winnerdt.common.utils.VerifyCodeUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 登录相关
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月10日 下午1:15:31
 */
@Controller
public class SysLoginController {
	@RequestMapping("captcha.jpg")
	public void captcha(HttpServletResponse response)throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
		try {
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType("image/jpg");

			//生成随机字串
			String verifyCode = VerifyCodeUtils.generateVerifyCode(5);
			//存入会话session
			ShiroUtils.setSessionAttribute(Constants.KAPTCHA_SESSION_KEY, verifyCode);
			//生成图片
			int w = 146, h = 33;
			VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 登录
	 */
	@ResponseBody
	@RequestMapping(value = "/sys/login", method = RequestMethod.POST)
	public R login(@RequestBody Map<String, String> map) {
		R r = new R();
		String username = map.get("userName");
		String password = map.get("password");
		String captcha = map.get("captcha");
		String type = map.get("type");

		String kaptcha = ShiroUtils.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
		if(!captcha.equalsIgnoreCase(kaptcha)){
			r.put("code",500);
			r.put("status","error");
			r.put("msg","验证码不正确");
			r.put("type", type);
			return r;
		}

		try{
			Subject subject = ShiroUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			subject.login(token);
		}catch (UnknownAccountException e) {
			r.put("code",500);
			r.put("status","error");
			r.put("msg","账号或密码不存在");
			r.put("type", type);
			return r;
		}catch (IncorrectCredentialsException e) {
			r.put("code",500);
			r.put("status","error");
			r.put("msg","账号或密码不正确");
			r.put("type", type);
			return r;
		}catch (LockedAccountException e) {
			r.put("code",500);
			r.put("status","error");
			r.put("msg","账号已被锁定,请联系管理员");
			r.put("type", type);
			return r;
		}catch (AuthenticationException e) {
			r.put("code",500);
			r.put("status","error");
			r.put("msg","账户验证失败");
			r.put("type", type);
			return r;
		}


		r.put("code",0);
		r.put("msg", "success");
		r.put("status", "ok");
		/*
		* 前台使用
		* */
		r.put("currentAuthority", "admin");
		r.put("type", type);
		return r;
	}

	/**
	 * 退出
	 * 目前将登出操作，放到了过滤器中（ShiroConfig.java中有配置）
	 */
//	@RequestMapping(value = "logout", method = RequestMethod.GET)
//	public void logout() {
//		ShiroUtils.logout();
//	}

}

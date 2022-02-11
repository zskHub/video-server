package com.winnerdt.modules.video.controller;

import com.alibaba.fastjson.JSONObject;
import com.winnerdt.common.utils.R;
import com.winnerdt.modules.video.service.VideoInfoService;
import com.winnerdt.modules.video.vo.ShowVideoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author:zsk
 * @CreateTime:2020-02-19 19:26
 */
@Api(tags = "视频流模块")
@Slf4j
@RestController
@RequestMapping("/videoInfo")
public class VideoInfoController {

    @Autowired
    private VideoInfoService videoInfoService;

    @ApiOperation("查看当前存在的视频流信息")
    @GetMapping("getVideoList")
    public R getVideoList() {
        R r = new R();
        try {
            Map<String, List<String>> videoMap = videoInfoService.getVideoList();
            r.put("videoMap", videoMap);
            r.put("code", 0);
            return r;
        } catch (Exception e) {
            log.error("查询当前视频流异常----->\n【异常原因】：{}\n", e);
            return R.error();
        }
    }

    /**
     * 切换视频流
     */
    @ApiOperation("切换获取视频流信息")
    @PostMapping("/showVideo")
    public R showVideo(@RequestBody ShowVideoVO showVideoVO, HttpServletRequest httpServletRequest) {
        try {
            R r = new R();
            r.put("videoUrl", videoInfoService.showVideo(showVideoVO, httpServletRequest));
            r.put("code", 0);
            return r;
        } catch (Exception e) {
            log.error("切换视频流异常----->\n【异常数据】：showVideoVo:{}\n【异常原因】：{}\n", JSONObject.toJSONString(showVideoVO), e);
            return R.error();
        }
    }

    /**
     * 关闭视频信息
     */
    @ApiOperation("关闭视频流信息")
    @PostMapping("close")
    public R close(HttpServletRequest httpServletRequest) {
        try {
            videoInfoService.closeVideo(httpServletRequest);

            return R.ok();
        } catch (Exception e) {
            log.error("关闭视频流信息----->\n【异常数据】：{}\n【异常原因】：{}", e);
            return R.error("网络错误，视频信息关闭失败！");
        }
    }

    /**
     * 关闭所有视频信息
     */
    @ApiOperation("关闭所有视频流信息")
    @PostMapping("closeAllVideo")
    public R closeAllVideo() {
        try {
            videoInfoService.closeAllVideo();

            return R.ok();
        } catch (Exception e) {
            log.error("关闭所有视频流信息----->\n【异常数据】：{}\n【异常原因】：{}", e);
            return R.error("网络错误，关闭所有视频流信息失败！");
        }
    }

}

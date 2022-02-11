package com.winnerdt.modules.video.service;

import com.winnerdt.modules.video.vo.ShowVideoVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author:zsk
 * @CreateTime:2020-02-19 19:20
 */
public interface VideoInfoService {

    /**
     * 查询视频信息列表
     *
     * @param
     * @return java.util.List<com.winnerdt.modules.video.entity.VideoInfoEntity>
     * @throws
     * @time 2022-02-09 9:30
     * @author zsk
     */
    Map<String, List<String>> getVideoList();


    /**
     * 通过的当前操作人id，切换推流的视频源
     *
     * @param showVideoVO
     * @return java.lang.String
     * @throws Exception
     * @time 2022-02-09 11:07
     * @author zsk
     */
    String showVideo(ShowVideoVO showVideoVO, HttpServletRequest httpServletRequest) throws Exception;

    /**
     * 通过当前操作人Id关闭该操作人对应的视频流
     *
     * @return void
     * @throws Exception
     * @time 2022-02-09 10:06
     * @author zsk
     */
    void closeVideo(HttpServletRequest httpServletRequest) throws Exception;

    /**
     * 关闭所有视频流信息
     * @param
     * @return void
     * @throws
     * @time 2022-02-11 9:19
     * @author zsk
     */
    void closeAllVideo() throws Exception;
}

package com.winnerdt.modules.video.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.winnerdt.common.utils.RedisUtils;
import com.winnerdt.modules.video.ffch4j.CommandManager;
import com.winnerdt.modules.video.service.VideoInfoService;
import com.winnerdt.modules.video.vo.ShowVideoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author:zsk
 * @CreateTime:2020-02-19 19:23
 */
@Slf4j
@Service
public class VideoInfoServiceImpl implements VideoInfoService {
    @Resource
    private CommandManager commandManager;

    /**
     * 全局map，用来保存已经开始推流的信息
     */
    public static Map<String, List<String>> videoMap = new HashMap<>();


    @Override
    public Map<String, List<String>> getVideoList() {
        return videoMap;
    }


    @Override
    public String showVideo(ShowVideoVO showVideoVO, HttpServletRequest httpServletRequest) throws Exception {
        if (null == showVideoVO) {
            log.error("播放视频异常，接受的showVideoVO为空");
            throw new Exception("播放视频异常，接受的showVideoVO为空");
        }

        if (StringUtils.isBlank(showVideoVO.getVideoOriUrl())) {
            log.error("播放视频异常，接受的videoOriUrl为空.showVideoVO:{}", JSONObject.toJSONString(showVideoVO));
            throw new Exception("播放视频异常，接受的showVideoVO为空");
        }

//        String userId = sysUser.getId();
        String ip = getClientIp(httpServletRequest);
        String videoOriUrl = showVideoVO.getVideoOriUrl();
        //判断当前用户下是否有其他的播放流，如果有就关闭该session下的其他流，然后再启动这个流
        this.closeVideo(httpServletRequest);
        //开始拼接视频流推送
        StringBuffer command = new StringBuffer();
        command.append(CommandManager.config.getPushCommand1());
        command.append(" ");
        command.append(videoOriUrl);
        command.append(" ");
        command.append(CommandManager.config.getPushCommand2());
        //获取一个uuid，标识该视频流的任务id
        String uuid = UUID.randomUUID().toString();
        command.append(uuid);

        commandManager.start(uuid, command.toString());

        //拼接下最终的视频链接结果
        String videoUrl = CommandManager.config.getGetCommand1() + uuid;

        //记录当前的视频流信息
        Map<String, List<String>> videoMap = this.getVideoList();
        if (null == videoMap.get(ip)) {
            //为空就新增
            List<String> videoTaskIdList = new ArrayList<>();
            videoTaskIdList.add(uuid);
            videoMap.put(ip, videoTaskIdList);
        } else {
            //不为空就更新
            List<String> videoTaskIdList = videoMap.get(ip);
            videoTaskIdList.add(uuid);
            videoMap.put(ip, videoTaskIdList);
        }

        return videoUrl;

    }

    @Override
    public void closeVideo(HttpServletRequest httpServletRequest) throws Exception {
        String ip = getClientIp(httpServletRequest);

        //查询该userId下在播放的视频流有哪些
        List<String> videoTaskIdList = videoMap.get(ip);
        if (CollectionUtils.isEmpty(videoTaskIdList)) {
            //该session下没有正在播放的视频流，就不做什么操作了
        } else {
            Iterator<String> videoTaskIterator = videoTaskIdList.listIterator();
            while (videoTaskIterator.hasNext()) {
                String videoTaskId = videoTaskIterator.next();
                boolean stopVideoTaskFlag = commandManager.stop(videoTaskId);
                if (!stopVideoTaskFlag) {
                    log.error("视频流信息关闭失败了。userId:{}, videoTaskId:{}", ip, videoTaskId);
                } else {
                    //关闭成功，需要将videoMap中有关的记录删除了
                    videoTaskIterator.remove();
                }
            }

            videoTaskIdList = IteratorUtils.toList(videoTaskIterator);
        }


        if (CollectionUtils.isEmpty(videoTaskIdList)) {
            //该session没有视频了，删了sessionId在map中的记录
            if (null != videoMap.get(ip)) {
                videoMap.remove(ip);
            }
        } else {
            videoMap.put(ip, videoTaskIdList);
        }
    }

    @Override
    public void closeAllVideo() throws Exception {
        try {
            //关闭所有的视频流
            commandManager.stopAll();

            //清空
            VideoInfoServiceImpl.videoMap = new HashMap<>();
        }catch (Exception e){
            log.error("清理全部的视频流信息异常。异常原因：{}", e);
            throw new Exception("清理全部的视频流信息异常异常");
        }
    }

    /**
     * 获取客户端ip
     * */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

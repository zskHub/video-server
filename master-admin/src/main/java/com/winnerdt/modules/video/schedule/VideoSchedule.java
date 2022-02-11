package com.winnerdt.modules.video.schedule;

import com.winnerdt.modules.video.ffch4j.CommandManager;
import com.winnerdt.modules.video.service.VideoInfoService;
import com.winnerdt.modules.video.service.impl.VideoInfoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * 视频流定时任务
 *
 * @author zarl
 * @time 2022-02-09   11:19
 */
@Slf4j
@Component
@EnableScheduling
public class VideoSchedule {
    @Resource
    private VideoInfoService videoInfoService;

    @Scheduled(cron = "0 0 13 * * ?")
    public void cleanAllVideoMap(){
        log.info("测试定时任务。{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        try {
            //关闭所有的视频流
            videoInfoService.closeAllVideo();
        }catch (Exception e){
            log.error("清理全部的视频流信息异常。异常原因：{}", e);
        }

    }
}

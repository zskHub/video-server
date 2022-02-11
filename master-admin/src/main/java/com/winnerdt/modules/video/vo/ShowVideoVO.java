package com.winnerdt.modules.video.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author zarl
 * @time 2022-02-09   09:51
 */
@Data
@ApiModel("视频播放vo对象")
public class ShowVideoVO {
    private String videoOriUrl;
}

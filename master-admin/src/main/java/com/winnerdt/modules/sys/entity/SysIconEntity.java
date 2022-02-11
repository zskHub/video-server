package com.winnerdt.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.ToString;

/**
 * @author:zsk
 * @CreateTime:2019-01-23 11:30
 */
@ToString
@TableName("sys_icon")
public class SysIconEntity {

    @TableId
    private Integer id;

    private String icon;

    private String type;

    private int status;

    @JsonSerialize(using= ToStringSerializer.class)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

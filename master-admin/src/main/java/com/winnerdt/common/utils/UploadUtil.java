package com.winnerdt.common.utils;


import com.alibaba.druid.util.StringUtils;
import com.winnerdt.common.exception.RRException;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by meng on 2018/5/8.
 * 文件上传工具类
 */
@Getter
@Setter
@ConfigurationProperties
@Component
public class UploadUtil {

    /**
     * 按照当日创建文件夹
     */
    @Value("${master.isDayType}")
    private boolean isDayType;
    /**
     * 自定义文件路径
     */
    @Value("${master.uploadPath}")
    private String uploadPath;

    @Value("${master.imagePath}")
    private String imagePath;

    public static final String IMAGE_SUFFIX = "bmp,jpg,png,gif,jpeg";


    public UploadUtil() {
    }

    public String upload(MultipartFile multipartFile) {
        if (isNull(multipartFile)) {
            throw new RRException("上传数据/地址获取异常");
        }

        LoadType loadType = fileNameStyle(multipartFile);
        try {
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), loadType.getCurrentFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadType.getFileName();
    }

    /**
     * 格式化文件名 默认采用UUID
     *
     * @return
     */
    public LoadType fileNameStyle(MultipartFile multipartFile) {
        String curr = multipartFile.getOriginalFilename();
        int suffixLen = curr.lastIndexOf(".");
        if (suffixLen == -1) {
            throw new RRException("文件获取异常");
        }
        String suffix = curr.substring(suffixLen);
        int index = Arrays.binarySearch(IMAGE_SUFFIX.split(","),
                suffix.replace(".", ""));

        curr = UUID.randomUUID() + suffix;
        LoadType loadType = new LoadType();
        loadType.setFileName(curr);
        //image 情况
        curr = StringUtils.isEmpty(imagePath) || index == -1 ?
                uploadPath + File.separator + curr : imagePath + File.separator + curr;
        loadType.setCurrentFile(new File(curr));
        return loadType;
    }

    private boolean isNull(MultipartFile multipartFile) {
        return null == multipartFile;
    }

}

@Data
class LoadType {
    private String fileName;
    private File currentFile;
}

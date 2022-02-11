package com.winnerdt.modules.oss.cloud;

import com.aliyun.oss.OSSClient;
import com.qiniu.common.QiniuException;
import com.winnerdt.common.exception.RRException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云存储
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-26 16:22
 */
public class AliyunCloudStorageService extends CloudStorageService {
    private OSSClient client;

    public AliyunCloudStorageService(CloudStorageConfig config){
        this.config = config;

        //初始化
        init();
    }

    private void init(){
        client = new OSSClient(config.getAliyunEndPoint(), config.getAliyunAccessKeyId(),
                config.getAliyunAccessKeySecret());
    }

    @Override
    public Map<String,String> upload(byte[] data, String path) {
        return upload(new ByteArrayInputStream(data), path);
    }

    @Override
    public Map<String,String> upload(InputStream inputStream, String path) {
        try {
            client.putObject(config.getAliyunBucketName(), path, inputStream);
        } catch (Exception e){
            throw new RRException("上传文件失败，请检查配置信息", e);
        }

        Map<String,String> map = new HashMap<>();
        map.put("url",config.getAliyunDomain() + "/" + path);
        map.put("fileName",path);
        map.put("bucketName",config.getAliyunBucketName());
        return map;
    }

    @Override
    public Map<String,String> uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(config.getAliyunPrefix(), suffix));
    }

    @Override
    public Map<String,String> uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(config.getAliyunPrefix(), suffix));
    }

    @Override
    public void delete(String BucketName, String fileName) throws QiniuException {
        /*
        * 删除未实现
        * */

    }
}

package com.winnerdt.modules.oss.cloud;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.winnerdt.common.exception.RRException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 七牛云存储
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-25 15:41
 */
public class QiniuCloudStorageService extends CloudStorageService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuCloudStorageService.class);

    private BucketManager bucketManager;

    private UploadManager uploadManager;
    private String token;

    public QiniuCloudStorageService(CloudStorageConfig config){
        this.config = config;

        //初始化
        init();
    }

    private void init(){
        uploadManager = new UploadManager(new Configuration(Zone.autoZone()));
        bucketManager = new BucketManager(Auth.create(config.getQiniuAccessKey(),config.getQiniuSecretKey()),new Configuration(Zone.autoZone()));
        token = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey()).
                uploadToken(config.getQiniuBucketName());
    }

    @Override
    public Map<String,String> upload(byte[] data, String path) {
        try {
            Response res = uploadManager.put(data, path, token);
            if (!res.isOK()) {
                throw new RuntimeException("上传七牛出错：" + res.toString());
            }
        } catch (Exception e) {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = sdf.format(date);
            logger.error("数据上传异常，异常时间："+now+":::异常原因："+e.toString());
            throw new RRException("上传文件失败，请核对七牛配置信息", e);
        }
        Map<String,String> map = new HashMap<>();
        map.put("url",config.getQiniuDomain() + "/" + path);
        map.put("fileName",path);
        map.put("bucketName",config.getQiniuBucketName());
        return map;
    }

    @Override
    public Map<String,String> upload(InputStream inputStream, String path) {
        try {
            byte[] data = IOUtils.toByteArray(inputStream);
            return this.upload(data, path);
        } catch (IOException e) {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = sdf.format(date);
            logger.error("数据上传异常，异常时间："+now+":::异常原因："+e.toString());
            throw new RRException("上传文件失败", e);
        }
    }

    @Override
    public Map<String,String> uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(config.getQiniuPrefix(), suffix));
    }

    @Override
    public Map<String,String> uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(config.getQiniuPrefix(), suffix));
    }

    @Override
    public void delete(String BucketName,String fileName) throws QiniuException {
        Response response = bucketManager.delete(config.getQiniuBucketName(), fileName);
        int retry = 0;
        while (response.needRetry() && retry++ < 3) {
            response = bucketManager.delete(config.getQiniuBucketName(),fileName);
        }
    }

}

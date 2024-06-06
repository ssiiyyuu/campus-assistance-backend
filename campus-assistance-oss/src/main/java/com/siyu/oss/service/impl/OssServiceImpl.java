package com.siyu.oss.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.siyu.common.config.GlobalConfig;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.oss.service.OssService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {

    @Override
    public String uploadFile(MultipartFile multipartFile, String prefix) {
        //华南
        Configuration config = new Configuration(Region.region2());
        config.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
        UploadManager uploadManager = new UploadManager(config);

        InputStream inputStream;
        byte[] fileBytes;
        try {
            inputStream = multipartFile.getInputStream();
            fileBytes = inputStream.readAllBytes();
            inputStream.close();
        } catch (IOException e) {
            throw new BusinessException(ErrorStatus.UPLOAD_ERROR);
        }

        String accessKey = GlobalConfig.OSS_ACCESS_KEY;
        String secretKey = GlobalConfig.OSS_SECRET_KEY;
        String bucket = GlobalConfig.OSS_BUCKET_NAME;
        String path = GlobalConfig.OSS_URL;
        String fileName = multipartFile.getOriginalFilename();
        String key = prefix + "/" + UUID.randomUUID() + fileName;

        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(fileBytes, key, upToken);
            DefaultPutRet defaultPutRet =JSONObject.parseObject(response.bodyString(), DefaultPutRet.class);
            return path + "/" + defaultPutRet.key;
        } catch (QiniuException e) {
            throw new BusinessException(ErrorStatus.UPLOAD_ERROR);
        }
    }
    
}

package com.siyu.oss.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {

    String uploadFile(MultipartFile multipartFile, String prefix);
}

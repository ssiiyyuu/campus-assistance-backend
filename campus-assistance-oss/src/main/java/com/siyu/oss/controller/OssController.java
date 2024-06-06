package com.siyu.oss.controller;

import com.siyu.common.domain.R;
import com.siyu.oss.service.OssService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "七牛云——图片上传")
@RestController
@RequestMapping("/oss")
public class OssController {

    @Autowired
    private OssService ossService;

    @PostMapping("/avatar")
    public R<String> uploadAvatar(@RequestPart("file") MultipartFile file) {
        String key = ossService.uploadFile(file, "avatar");
        return R.ok(key);
    }

    @PostMapping("/cover")
    public R<String> uploadCover(@RequestPart("file") MultipartFile file) {
        String key = ossService.uploadFile(file, "cover");
        return R.ok(key);
    }

    @PostMapping("/img")
    public R<String> uploadImg(@RequestPart("file") MultipartFile file) {
        String key = ossService.uploadFile(file, "img");
        return R.ok(key);
    }
}

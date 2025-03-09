package com.example.excelcomparatorpro.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.example.excelcomparatorpro.util.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class UploadController {

    @Value("${base-config.upload-path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
            // 获取原始文件名和扩展名
        String originalFilename = multipartFile.getOriginalFilename();
        String fileExtension = FileUtil.extName(originalFilename);
        // 计算文件的MD5值
        String md5 = MD5.create().digestHex(multipartFile.getInputStream());
        // 生成新的文件名：md5值 + 文件扩展名
        String newFileName = md5 + StrUtil.DOT + fileExtension;
        // 创建文件对象并保存文件
        File destFile = new File(uploadPath, newFileName);
        FileUtil.writeBytes(multipartFile.getBytes(), destFile);
        // 返回文件名
        return Result.ok().data("filename", newFileName);
    }


}

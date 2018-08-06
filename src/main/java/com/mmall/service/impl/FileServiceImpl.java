package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


/**
 * @program: mmall
 * @description: 上传文件
 * @author: BoWei
 * @create: 2018-03-26 10:49
 **/
@Service("iFileServiceImpl")
@Slf4j
public class FileServiceImpl implements IFileService {

    //private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path) {
        String filename = file.getOriginalFilename();
        //扩展名
        //.jpg
        //从文件名最后一个字母读取直道碰到第一个 . 号再 + 1变为想要的扩展名
        String fileExtensionName = filename.substring(filename.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID() + "." + fileExtensionName;
        log.info("开始上传文件,上传文件的文件名:{},上传文件的路径:{},新文件名:{}", filename, path, uploadFileName);
        /*创建目录并判断*/
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        /*创建文件*/
        File targerFile = new File(path, uploadFileName);
        try {
            file.transferTo(targerFile);
            /*将图片上传到Ftp服务器上*/
            FTPUtil.uploadFile(Lists.newArrayList(targerFile));
           /* 上传完之后删除upload 下面的文件*/
            targerFile.delete();
        } catch (IOException e) {
            log.error("文件上传异常", e);
            return null;
        }
        return targerFile.getName();

    }
}

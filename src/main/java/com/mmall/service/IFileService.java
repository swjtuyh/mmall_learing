package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by yinhuan on 2018/4/23.
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}

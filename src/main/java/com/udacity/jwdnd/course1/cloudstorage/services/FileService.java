package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mappers.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.mappers.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.models.File;
import com.udacity.jwdnd.course1.cloudstorage.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@Service
public class FileService {
    private final FileMapper fileMapper;
    private final UserMapper userMapper;

    public FileService(FileMapper fileMapper, UserMapper userMapper){
        this.fileMapper = fileMapper;
        this.userMapper = userMapper;
    }

    public int createFile(MultipartFile multipartFile, String userName) throws IOException {
        User user = userMapper.getUserByName(userName);
        Integer userID = user.getUserid();
        String fileName = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();
        String fileSize = String.valueOf(multipartFile.getSize());
        byte[] fileData = multipartFile.getBytes();
        return fileMapper.addFile(new File(null, fileName,contentType,fileSize,userID, fileData));
    }

    public File getFileByName(int userId,String fileName){
        return fileMapper.getFile(userId,fileName);
    }

    public List<File> getAllFiles(int userId){
        return fileMapper.getAllFiles(userId);
    }

    public int deleteFile(int fileId){
        return fileMapper.deleteFileById(fileId);
    }
}

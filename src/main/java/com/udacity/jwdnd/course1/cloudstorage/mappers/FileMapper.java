package com.udacity.jwdnd.course1.cloudstorage.mappers;

import com.udacity.jwdnd.course1.cloudstorage.models.File;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface FileMapper {
    @Select("SELECT * FROM FILES WHERE userid = #{userid}")
    List<File> getAllFiles(int userid);

    @Select("SELECT * FROM FILES WHERE userid = #{userid} and filename = #{filename}")
    File getFile(int userid, String filename);

    @Insert("INSERT INTO FILES (filename, contenttype, filesize, userid, filedata) " +
            "VALUES(#{filename}, #{contenttype}, #{filesize},  #{userid}, #{filedata})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int addFile(File file);

    @Delete("DELETE FROM FILES WHERE userid = #{userid} and filename = #{fileName}")
    int deleteFile(int userid, String fileName);

    @Delete("DELETE FROM FILES WHERE fileId = #{fileId}")
    int deleteFileById(int fileId);

}

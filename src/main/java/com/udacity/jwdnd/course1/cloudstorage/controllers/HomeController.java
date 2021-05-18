package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.models.File;
import com.udacity.jwdnd.course1.cloudstorage.models.FileForm;
import com.udacity.jwdnd.course1.cloudstorage.models.User;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.apache.commons.io.FileUtils;
import org.apache.juli.logging.LogFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final FileService fileService;
    private final UserService userService;

    public HomeController(FileService fileService, UserService userService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    @GetMapping()
    public String homeView(Authentication authentication, @ModelAttribute("newFile") FileForm newFile, Model model) {
        Integer userId = getUserId(authentication);
        model.addAttribute("files", this.fileService.getAllFiles(userId));
        return "home";
    }

    @PostMapping()
    public String handleUploadFile(Authentication authentication, @ModelAttribute("newFile") FileForm newFile, Model model) throws IOException {
        MultipartFile multipartFile = newFile.getFile();
        if(multipartFile.isEmpty()){
            model.addAttribute("result", "error");
            model.addAttribute("message", "You must choose a file.");
            return "result";
        }
        String fileName = multipartFile.getOriginalFilename();
        String userName = authentication.getName();
        User user = userService.getUser(userName);
        Integer userId = user.getUserid();
        List<File> fileListings = fileService.getAllFiles(userId);
        boolean fileIsDuplicate = false;
        for (File file : fileListings) {
            if (file.getFilename().equals(fileName)) {
                fileIsDuplicate = true;
                break;
            }
        }
        if (!fileIsDuplicate) {
            fileService.createFile(multipartFile, userName);
            model.addAttribute("result", "success");
        } else {
            model.addAttribute("result", "error");
            model.addAttribute("message", "You have tried to add a duplicate file.");
        }
        model.addAttribute("files", fileService.getAllFiles(userId));

        return "result";
    }

    @GetMapping(
            value = "/get-file/{fileName}"
    )
    @ResponseBody
    public String getFile(HttpServletResponse response, @PathVariable String fileName, Authentication authentication) throws IOException {
        String userName = authentication.getName();
        User user = userService.getUser(userName);
        Integer userId = user.getUserid();
        File retrievedFile = fileService.getFileByName(userId, fileName);
        byte[] fileData = retrievedFile.getFiledata();

        java.io.File file = new java.io.File(retrievedFile.getFilename());
        FileUtils.writeByteArrayToFile(file, fileData);

        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName );
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        }catch (IOException e) {
            LogFactory.getLog(this.getClass()).error(e);

            }
        return "home";
    }

    @GetMapping(
            value = "/delete-file/{fileName}"
    )
    public String handleDeleteFile(@PathVariable String fileName,@ModelAttribute("newFile") FileForm newFile, Authentication authentication,Model model) {
        String userName = authentication.getName();
        User user = userService.getUser(userName);
        Integer userId = user.getUserid();
        File file = fileService.getFileByName(userId,fileName);
        if(file!=null){
            fileService.deleteFile(file.getFileId());
            model.addAttribute("files", fileService.getAllFiles(userId));
            model.addAttribute("result", "success");
        }
        return "result";
    }


    private Integer getUserId(Authentication authentication) {
        String userName = authentication.getName();
        User user = userService.getUser(userName);
        return user.getUserid();
    }


}

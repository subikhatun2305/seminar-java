package com.dreamsol.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {

    public String fileSave(MultipartFile file, String path) throws IOException {
        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String fileExtension = StringUtils.getFilenameExtension(fileName);
        if (fileExtension == null) {
            throw new IllegalArgumentException("File name does not contain an extension");
        }

        String id = UUID.randomUUID().toString();
        String newFileName = id + "." + fileExtension;
        String filePath = path + File.separator + newFileName;
        File directory = new File(path);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        return newFileName;
    }

    public byte[] getFile( String uploadDir,String fileName) throws IOException {
        String fullPath = uploadDir + fileName;
        InputStream stream = new FileInputStream(fullPath);
        byte[] bytes = stream.readAllBytes();
        stream.close();
        return bytes;
    }


//    public boolean deleteFile(String path, String fileName) {
//        Path filePath = Paths.get(path, fileName);
//        try {
//            Files.delete(filePath);
//            return true;
//        } catch (IOException exception) {
//            exception.printStackTrace();
//            return false;
//        }
//    }
}

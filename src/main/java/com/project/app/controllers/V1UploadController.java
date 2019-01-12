package com.project.app.controllers;

import com.project.app.models.v1.response.V1Response;
import lombok.val;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class V1UploadController {

    @PostMapping
    private V1Response fileUpload(@RequestParam("file") @NotNull MultipartFile file) {
        if (file.isEmpty()) {
            return V1Response.error("File is empty!");
        }

        try {
            val bytes = file.getBytes();
            val content = new String(bytes, "UTF-8");
            System.out.println(content);
        } catch (IOException e) {
            return V1Response.error("Unable to read file!");
        }

        return V1Response.of("Success!");
    }
}

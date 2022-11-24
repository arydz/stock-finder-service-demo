package com.arydz.stockfinder.domain.housekeeping.api;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileParams {

    private MultipartFile file;
    private String url;
}

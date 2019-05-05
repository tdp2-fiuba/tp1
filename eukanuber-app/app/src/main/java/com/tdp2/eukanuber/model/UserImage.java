package com.tdp2.eukanuber.model;

import java.io.Serializable;
import java.util.List;

public class UserImage implements Serializable {
    private String fileName;
    private String fileContent;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
}


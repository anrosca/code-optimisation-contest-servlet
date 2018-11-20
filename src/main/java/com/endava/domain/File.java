package com.endava.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class File {

    private String fileName;

    private byte[] content;

    public File(final String fileName, final byte[] content) {
        this.fileName = fileName;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public InputStream getContentStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public String toString() {
        return "File{" +
                "fileName='" + fileName + '\'' +
                ", size=" + content.length +
                '}';
    }
}
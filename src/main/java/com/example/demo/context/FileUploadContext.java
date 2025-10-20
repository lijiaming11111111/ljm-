package com.example.demo.context;

import java.util.ArrayList;
import java.util.List;

public class FileUploadContext {

    private static final ThreadLocal<List<Long>> uploadedFileIds = ThreadLocal.withInitial(ArrayList::new);

    public static void addFileId(Long fileId) {
        uploadedFileIds.get().add(fileId);
    }

    public static void setFileIds(List<Long> fileIds) {
        uploadedFileIds.set(fileIds);
    }

    public static List<Long> getFileIds() {
        return uploadedFileIds.get();
    }

    public static void clear() {
        uploadedFileIds.remove();
    }

}

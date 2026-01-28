package com.huobao.drama.infrastructure.storage;

import java.io.InputStream;

public interface StorageService {
    String upload(InputStream inputStream, String filename, String category);
    
    String downloadFromUrl(String url, String category);
    
    DownloadResult downloadFromUrlWithPath(String url, String category);
    
    String getAbsolutePath(String relativePath);
    
    String getUrl(String path);

    class DownloadResult {
        private String url;
        private String relativePath;
        private String absolutePath;

        public DownloadResult(String url, String relativePath, String absolutePath) {
            this.url = url;
            this.relativePath = relativePath;
            this.absolutePath = absolutePath;
        }

        public String getUrl() { return url; }
        public String getRelativePath() { return relativePath; }
        public String getAbsolutePath() { return absolutePath; }
    }
}

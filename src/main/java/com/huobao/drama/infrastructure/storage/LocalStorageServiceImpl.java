package com.huobao.drama.infrastructure.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class LocalStorageServiceImpl implements StorageService {

    @Value("${huobao.storage.local-root:./data/storage}")
    private String localRoot;

    @Value("${server.port:5678}")
    private String serverPort;

    private String baseUrl;

    @PostConstruct
    public void init() {
        File root = new File(localRoot);
        if (!root.exists()) {
            root.mkdirs();
        }
        // Simplified baseUrl, in production this should be a full domain
        this.baseUrl = "http://localhost:" + serverPort + "/static";
    }

    @Override
    public String upload(InputStream inputStream, String filename, String category) {
        try {
            Path categoryPath = Paths.get(localRoot, category);
            if (!Files.exists(categoryPath)) {
                Files.createDirectories(categoryPath);
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String newFilename = timestamp + "_" + filename;
            Path filePath = categoryPath.resolve(newFilename);

            Files.copy(inputStream, filePath);

            return baseUrl + "/" + category + "/" + newFilename;
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @Override
    public String downloadFromUrl(String url, String category) {
        return downloadFromUrlWithPath(url, category).getUrl();
    }

    @Override
    public DownloadResult downloadFromUrlWithPath(String urlStr, String category) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to download file: HTTP " + connection.getResponseCode());
            }

            String contentType = connection.getContentType();
            String ext = getFileExtension(urlStr, contentType);

            Path categoryPath = Paths.get(localRoot, category);
            if (!Files.exists(categoryPath)) {
                Files.createDirectories(categoryPath);
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            String filename = timestamp + "_" + uniqueId + ext;
            Path filePath = categoryPath.resolve(filename);

            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            String relativePath = category + "/" + filename;
            String localUrl = baseUrl + "/" + relativePath;

            return new DownloadResult(localUrl, relativePath, filePath.toString());
        } catch (IOException e) {
            log.error("Failed to download file from URL: {}", urlStr, e);
            throw new RuntimeException("Failed to download file from URL", e);
        }
    }

    @Override
    public String getAbsolutePath(String relativePath) {
        return Paths.get(localRoot, relativePath).toAbsolutePath().toString();
    }

    @Override
    public String getUrl(String path) {
        return baseUrl + "/" + path;
    }

    private String getFileExtension(String url, String contentType) {
        if (url.contains(".")) {
            String ext = url.substring(url.lastIndexOf("."));
            if (ext.contains("?")) {
                ext = ext.substring(0, ext.indexOf("?"));
            }
            if (ext.length() <= 5) {
                return ext;
            }
        }

        if (contentType != null) {
            if (contentType.contains("image/jpeg")) return ".jpg";
            if (contentType.contains("image/png")) return ".png";
            if (contentType.contains("image/gif")) return ".gif";
            if (contentType.contains("image/webp")) return ".webp";
            if (contentType.contains("video/mp4")) return ".mp4";
            if (contentType.contains("video/webm")) return ".webm";
            if (contentType.contains("video/quicktime")) return ".mov";
        }

        return ".bin";
    }
}

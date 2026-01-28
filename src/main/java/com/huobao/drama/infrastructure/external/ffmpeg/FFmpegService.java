package com.huobao.drama.infrastructure.external.ffmpeg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FFmpegService {

    @Value("${huobao.ffmpeg.path:ffmpeg}")
    private String ffmpegPath;

    @Value("${huobao.storage.local-root:./data/storage}")
    private String localRoot;

    private Path tempDir;

    @PostConstruct
    public void init() throws IOException {
        tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "huobao-drama-ffmpeg");
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }
    }

    public String mergeVideos(List<String> videoPaths, String outputRelativePath) throws Exception {
        if (videoPaths == null || videoPaths.isEmpty()) {
            throw new IllegalArgumentException("No videos to merge");
        }

        // 如果只有一个视频，直接复制
        if (videoPaths.size() == 1) {
            Path src = Paths.get(localRoot, videoPaths.get(0));
            Path dest = Paths.get(localRoot, outputRelativePath);
            Files.createDirectories(dest.getParent());
            Files.copy(src, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return outputRelativePath;
        }

        // 创建临时列表文件
        String listFileName = UUID.randomUUID().toString() + ".txt";
        Path listFilePath = tempDir.resolve(listFileName);
        
        StringBuilder sb = new StringBuilder();
        for (String relPath : videoPaths) {
            // FFmpeg concat 协议需要绝对路径或相对于列表文件的路径
            String absPath = Paths.get(localRoot, relPath).toAbsolutePath().toString().replace("\\", "/");
            sb.append("file '").append(absPath).append("'\n");
        }
        Files.write(listFilePath, sb.toString().getBytes());

        // 执行合并命令
        Path outputPath = Paths.get(localRoot, outputRelativePath);
        Files.createDirectories(outputPath.getParent());

        ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath,
                "-f", "concat",
                "-safe", "0",
                "-i", listFilePath.toAbsolutePath().toString(),
                "-c", "copy",
                "-y",
                outputPath.toAbsolutePath().toString()
        );

        log.info("Running FFmpeg: {}", String.join(" ", pb.command()));
        
        Process process = pb.start();
        captureOutput(process);
        int exitCode = process.waitFor();

        // 清理临时文件
        Files.deleteIfExists(listFilePath);

        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg merge failed with exit code " + exitCode);
        }

        return outputRelativePath;
    }

    private void captureOutput(Process process) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("FFmpeg: {}", line);
                }
            } catch (IOException e) {
                log.error("Error reading FFmpeg stderr", e);
            }
        }).start();
    }
}

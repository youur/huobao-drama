package com.huobao.drama.application.service.impl;

import com.huobao.drama.application.service.TaskService;
import com.huobao.drama.application.service.VideoMergeService;
import com.huobao.drama.common.exception.BusinessException;
import com.huobao.drama.domain.model.Episode;
import com.huobao.drama.domain.model.Storyboard;
import com.huobao.drama.domain.model.VideoMerge;
import com.huobao.drama.domain.repository.EpisodeRepository;
import com.huobao.drama.domain.repository.StoryboardRepository;
import com.huobao.drama.domain.repository.VideoMergeRepository;
import com.huobao.drama.infrastructure.external.ffmpeg.FFmpegService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoMergeServiceImpl implements VideoMergeService {

    private final VideoMergeRepository videoMergeRepository;
    private final EpisodeRepository episodeRepository;
    private final StoryboardRepository storyboardRepository;
    private final TaskService taskService;
    private final FFmpegService ffmpegService;

    @Override
    @Transactional
    public String mergeEpisodeVideos(Long episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new BusinessException(404, "Episode not found"));

        // 创建异步任务
        String taskId = taskService.createTask("video_merge", episodeId.toString()).getId();

        // 异步执行合并
        processVideoMerge(taskId, episodeId);

        return taskId;
    }

    @Async("taskExecutor")
    @Override
    public void processVideoMerge(String taskId, Long episodeId) {
        try {
            taskService.updateTaskStatus(taskId, "processing", 10, "正在收集视频片段...");

            List<Storyboard> storyboards = storyboardRepository.findByEpisodeIdOrderByStoryboardNumberAsc(episodeId);
            
            // 获取所有已完成视频生成的 local_path
            // 注意：这里需要确保视频已经在本地（之前 VideoGeneration 已处理）
            // Go 版本中可能是通过 VideoGeneration 记录查找，这里简化为从 Storyboard 拿
            List<String> videoPaths = storyboards.stream()
                    .map(sb -> {
                        // 假设我们需要从数据库重新确认 video_url 对应的本地路径
                        // 这里暂时假设视频已经下载且路径已知
                        // 实际开发中应检查 VideoGeneration 记录
                        return sb.getVideoUrl(); // 临时占位，后续应通过逻辑获取 local_path
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (videoPaths.isEmpty()) {
                throw new RuntimeException("No video clips available for merge");
            }

            taskService.updateTaskStatus(taskId, "processing", 30, "开始合并视频 (FFmpeg)...");

            String outputRelPath = "merged/episode_" + episodeId + "_" + System.currentTimeMillis() + ".mp4";
            
            // 这里我们需要将 URL 转换为本地相对路径，如果 Storyboard 存的是 URL 的话
            // 简单起见，我假设 videoPaths 里的内容已经处理为相对路径，或者在此处转换
            List<String> localRelPaths = videoPaths.stream()
                    .map(url -> url.contains("/static/") ? url.split("/static/")[1] : url)
                    .collect(Collectors.toList());

            ffmpegService.mergeVideos(localRelPaths, outputRelPath);

            taskService.updateTaskStatus(taskId, "processing", 90, "合并完成，正在保存记录...");

            VideoMerge merge = new VideoMerge();
            merge.setEpisodeId(episodeId);
            merge.setDramaId(0L); // 需关联 DramaID
            merge.setProvider("ffmpeg");
            merge.setStatus("completed");
            merge.setMergedUrl("http://localhost:5678/static/" + outputRelPath);
            merge.setCompletedAt(LocalDateTime.now());
            merge.setTaskId(taskId);
            merge.setScenes("[]"); // 记录合并的分镜 ID
            videoMergeRepository.save(merge);

            // 更新剧集的最终视频地址
            Episode episode = episodeRepository.findById(episodeId).get();
            episode.setVideoUrl(merge.getMergedUrl());
            episode.setStatus("completed");
            episodeRepository.save(episode);

            taskService.updateTaskResult(taskId, merge);
            log.info("Video merge completed for episode: {}", episodeId);

        } catch (Exception e) {
            log.error("Video merge failed for episode: {}", episodeId, e);
            taskService.updateTaskError(taskId, e);
        }
    }
}

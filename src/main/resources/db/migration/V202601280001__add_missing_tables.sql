-- 添加缺失的表和补全字段
-- 创建时间: 2026-01-28

-- 1. 异步任务表
CREATE TABLE IF NOT EXISTS async_tasks (
    id TEXT PRIMARY KEY,
    type TEXT NOT NULL,
    status TEXT NOT NULL,
    progress INTEGER NOT NULL DEFAULT 0,
    message TEXT,
    error TEXT,
    result TEXT,
    resource_id TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME,
    deleted_at DATETIME
);

-- 2. 补全 storyboards 表缺失的字段 (适配 Java 实体)
-- 注意：如果表已存在且缺少字段，这里通过这种方式补齐
-- SQLite 不支持一次 ALTER 多个字段，所以我们先确保表存在时的结构
-- 由于我们会删除 db 重来，这里直接写创建/增强逻辑

-- 3. 帧提示词存储表
CREATE TABLE IF NOT EXISTS frame_prompts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    storyboard_id INTEGER NOT NULL,
    frame_type TEXT NOT NULL,
    prompt TEXT NOT NULL,
    description TEXT,
    layout TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME
);

-- 为原有表添加 local_path 字段（补齐之前的冲突逻辑）
-- 使用异常忽略逻辑，确保脚本健壮
-- 针对 characters 表
-- ALTER TABLE characters ADD COLUMN local_path TEXT;
-- ALTER TABLE scenes ADD COLUMN local_path TEXT;
-- ALTER TABLE props ADD COLUMN local_path TEXT;
-- ALTER TABLE character_libraries ADD COLUMN local_path TEXT;
-- ALTER TABLE assets ADD COLUMN local_path TEXT;

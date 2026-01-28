-- 添加缺失的表：async_tasks 和 frame_prompts
-- 创建时间: 2026-01-28

-- 异步任务表
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

CREATE INDEX IF NOT EXISTS idx_async_tasks_type ON async_tasks(type);
CREATE INDEX IF NOT EXISTS idx_async_tasks_status ON async_tasks(status);
CREATE INDEX IF NOT EXISTS idx_async_tasks_resource_id ON async_tasks(resource_id);

-- 帧提示词存储表
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

CREATE INDEX IF NOT EXISTS idx_frame_prompts_storyboard ON frame_prompts(storyboard_id);
CREATE INDEX IF NOT EXISTS idx_frame_prompts_type ON frame_prompts(frame_type);

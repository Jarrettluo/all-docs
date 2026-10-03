# 待补充功能

> 2026-10-03 更新：以下 4 项功能已全部实现（feature/stats-metrics 分支）。

## ✅ 1. 浏览量统计 (viewNum) —— 已实现

**实现方式：** 文档浏览接口 `GET /file/view/{id}` 与 `GET /file/view2/{id}` 中：
- 调用 `redisService.incrementDocScore(docId, 1)` 更新热门文档 ZSet (`doc:hot`)
- 调用 `redisService.increment("stats:view:total")` 累计全站浏览量

`/statistics/all` 的 `viewNum` 读取 `stats:view:total` 计数器。

## ✅ 2. 下载次数统计 (downloadNum) —— 已实现

**实现方式：** 下载接口 `GET /file/downloadFile`（HMAC 核验通过后）与 `GET /file/{id}` 中：
- 调用 `redisService.increment("stats:download:total")` 累计全站下载量

`/statistics/all` 的 `downloadNum` 读取 `stats:download:total` 计数器。

## ✅ 3. 搜索次数统计 (searchNum) —— 已实现

**实现方式：** `/statistics/all` 的 `searchNum` = Redis ZSet `search:hot` 所有成员分数总和
（新增 `RedisService.getZSetTotalScore()` 方法）。

## ✅ 4. 用户活跃度统计 (userActivity) —— 已实现

**实现方式：**
- 用户登录成功时写入月度活跃集合 `active:user:{yyyy-MM}`（Redis Set，保留约 13 个月）
- `/statistics/userActivity` 返回最近 12 个月数据：`{month, activeUsers, totalUsers}`

---

## 附：本次一并修复的问题

- `UserMapper.xml` 的 `insert` / `save` 语句补充 `PermissionEnumTypeHandler`
  （修复：全新部署时初始化管理员写库失败 `Incorrect integer value: 'ADMIN'`）
- 前端 `document.js` 缺失的 URL 常量补全（`DOC_TXT_URL` / `DOC_REBUILD_URL` / `DOC_GENERATE_DOWNLOAD_KEY` / `DOC_DOWNLOAD`）
# 待补充功能

以下功能在 Statistics API 扩展中暂未实现，后续补充：

## 1. 浏览量统计 (viewNum)

**目标：** 在 `/statistics/all` 接口中返回真实的文档浏览次数

**方案：** 在文档浏览接口中增加 Redis ZSet 计数，key 为 `doc:hot`

**待办：**
- [ ] 在文档浏览接口中调用 `redisService.incrementDocScore(docId, 1)`
- [ ] 验证 `doc:hot` ZSet 是否正常更新

## 2. 下载次数统计 (downloadNum)

**目标：** 在 `/statistics/all` 接口中返回真实的文档下载次数

**方案：** 在文档下载接口中增加 Redis 计数

**待办：**
- [ ] 确定下载接口位置
- [ ] 增加下载计数逻辑
- [ ] 在 `all()` 方法中聚合下载次数

## 3. 搜索次数统计 (searchNum)

**目标：** 在 `/statistics/all` 接口中返回真实的搜索次数

**方案：** 使用 Redis ZSet `search:hot` 的总分数作为搜索次数

**待办：**
- [ ] 在 `all()` 方法中聚合 `search:hot` ZSet 的总分

## 4. 用户活跃度统计 (userActivity)

**目标：** 实现 `/statistics/userActivity` 接口

**方案：** 在用户登录/关键操作时记录活跃状态，按月聚合

**待办：**
- [ ] 设计用户活跃度记录机制（Redis 或数据库）
- [ ] 实现按月统计活跃用户数
- [ ] 实现 `userActivity()` 方法

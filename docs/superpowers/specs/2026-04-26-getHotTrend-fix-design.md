# getHotTrend 接口修复设计

## 背景

`GET /api/v1/statistics/getHotTrend` 接口存在三个问题需要修复。

## 问题清单

| # | 问题 | 严重程度 | 位置 |
|---|------|----------|------|
| 1 | `DOC_KEY` (ZSet `doc:hot`) 从未被写入数据，接口始终返回空 | 严重 | 全局 |
| 2 | `deleteKey(s)` 误删整个 Redis key，而非从 ZSet 移除成员 | 高 | StatisticsController:137 |
| 3 | `hit` 排名值从 10 开始递减，逻辑错误 | 中 | StatisticsController:156-163 |

## 设计方案

### 1. 新增 Redis 方法

**文件**: `RedisServiceImpl.java`

```java
public void incrementDocScore(String docId, double delta) {
    redisSearchTemplate.opsForZSet().incrementScore(DOC_KEY, docId, delta);
}
```

**接口**: `RedisService.java`

```java
void incrementDocScore(String docId, double delta);
```

**说明**: 当 `delta > 0` 时点赞加分，`delta < 0` 时取消点赞减分。

---

### 2. 点赞时同步更新 DOC_KEY

**文件**: `LikeServiceImpl.java`

- 点赞成功时: `redisService.incrementDocScore(entityId, 1)`
- 取消点赞时: `redisService.incrementDocScore(entityId, -1)`

---

### 3. Bug 修复

#### 问题2修复

```java
// 错误
redisService.deleteKey(s);

// 正确
redisService.removeByDocId(s);
```

#### 问题3修复

```java
// 错误: count 从 10 开始
int count = 10;

// 正确: 从 2 开始 (top1 是第1名，others 从第2名起)
int count = 2;
```

---

## 数据流

```
用户点赞 → LikeServiceImpl.like() → incrementDocScore(docId, +1)
                                 → Redis ZSet DOC_KEY 分数 +1

用户取消点赞 → LikeServiceImpl.like() → incrementDocScore(docId, -1)
                                    → Redis ZSet DOC_KEY 分数 -1

getHotTrend → Redis ZSet DOC_KEY → 返回热度排名前10文档
```

---

## 测试要点

1. 点赞后 `getHotTrend` 返回的 `likeNum` 应增加
2. 取消点赞后 `likeNum` 应减少
3. `top1` 的 `hit` 值应为 1，`others` 每个的 `hit` 应从 2 开始
4. 无效文档 ID 正确从 ZSet 移除而非删除整个 key

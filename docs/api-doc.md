# Api-doc


## 1 文档信息
|版本|日期|修订内容|修订人|审核人|
|----|----|----|----|----|
|v1.0|2022-06-19|新建|Jarrett|Jarrett|

## 2 api 说明
### 2.1 文档检索
#### 2.1.1 接口原型

#### 2.1.2 接口地址
```http request
GET  /document/search
```
#### 2.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 2.1.3 响应结果

```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": [
    {
      "id": 1,
      "title":"",
      "abstract": "abstract",
      "collectNum": 1,
      "commentNum": 123,
      "category": "category",
      "tags": ["tag1", "tag2", "tag3"]
    }
  ]
}
```

### 2.2 单个文档详情
#### 2.1.1 接口原型

#### 2.1.2 接口地址
```http request
GET  /xxx/xxx
```
#### 2.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 2.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```

### 2.3 文档点赞/收藏
#### 2.3.1 接口原型
用户登录以后对某篇文章进行点赞

#### 2.3.2 接口地址
```http request
POST  /collect/insert
```
#### 2.3.2 请求参数

```json
{
  "param": "none",
  "token": "token",
  "data": {
      "docId": 1
  }
}
```

#### 2.3.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": "SUCCESS"
}
```

### 2.4 文档取消点赞/收藏
#### 2.4.1 接口原型

#### 2.4.2 接口地址
```http request
DELETE /collect/remove
```
#### 2.4.3 请求参数
```json
{
  "param": "none" ,
  "token": "string",
  "data": {
    "docId": 1
  }
}
```

#### 2.4.4 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": "SUCCESS"
}
```

### 2.5 文档增加一条评论
#### 2.5.1 接口原型
对某一篇文档，增加一条评论信息。


#### 2.1.2 接口地址
```http request
POST  /comment/insert
```
#### 2.1.2 请求参数

```json
{
  "param": "key-word",
  "token": "string",
  "data": {
    "content": "content of comment",
    "docId": 123
  }
}
```

#### 2.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": "SUCCESS"
}
```

### 2.5 文档修改一条评论
#### 2.5.1 接口原型

#### 2.5.2 接口地址
```http request
PUT /comment/update
```
#### 2.5.3 请求参数
```json
{
  "param": "key-word",
  "token": "string",
  "data": {
    "content": "content of comment",
    "docId": 123
  }
}
```

#### 2.5.4 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": "SUCCESS"
}
```

### 2.7 文档删除一条评论
#### 2.7.1 接口原型

#### 2.7.2 接口地址
```http request
DELETE  /comment/remove
```
#### 2.7.3 请求参数
```json
{
  "param": "key-word",
  "token": "string",
  "data": {
    "content": "content of comment",
    "docId": 123
  }
}
```

#### 2.7.4 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": "SUCCESS"
}
```

### 2.8 根据文档拉取全部评论信息
#### 2.8.1 接口原型

#### 2.8.2 接口地址
```http request
GET  /comment/queryById
```
#### 2.9.3 请求参数

```json
{
  "param": {
    "docId": 123
  },
  "token": "string",
  "data": "none"
}
```

#### 2.8.4 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": [
    {
      "userName": "userName",
      "content": "content",
      "id": 1,
      "createDate": "createDate",
      "updateDate": "updateDate"
    }
  ]
}
```


## 3 管理系统界面
### 3.1 拉取全部的文档
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
GET  /xxx/xxx
```
#### 3.1.3 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 3.1.4 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```
### 3.2 根据分类拉取全部的文档
#### 3.2.1 接口原型

#### 3.2.2 接口地址
```http request
GET  /xxx/xxx
```
#### 3.2.3 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 3.2.4 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```

### 3.2 根据标签拉取全部的文档
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
GET  /xxx/xxx
```
#### 3.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```

### 3.4 删除一篇文档
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
GET  /xxx/xxx
```
#### 3.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```

### 3.5 读取文档的全部详细信息
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
GET  /xxx/xxx
```
#### 3.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```

### 3.6 增加一个分类信息
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
POST /category/insert
```
#### 3.1.2 请求参数

```json
{
  "param": "key-word",
  "token": "string",
  "data": {
    "name": ""
  }
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": "SUCCESS"
}
```

### 3.7 编辑一个分类信息
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
PUT  /category/update
```
#### 3.1.2 请求参数

```json
{
  "param": "key-word",
  "token": "string",
  "data": {
    "name": "",
    "id":1
  }
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": "SUCCESS"
}
```

### 3.8 删除一个分类信息
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
DELETE /category/remove
```
#### 3.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": {
    "id":1
  }
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": "SUCCESS"
}
```

### 3.9 增加一个标签信息
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
GET  /tag/insert
```
#### 3.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```

### 3.10 编辑一个标签信息
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
PUT /tag/update
```
#### 3.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```

### 3.11 删除一个标签信息
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
DELETE /tag/remove
```
#### 3.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```

### 3.12 某个分类下增加文档
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
DELETE /tag/remove
```
#### 3.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```

### 3.13 某个分类下去除文档
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
DELETE /tag/remove
```
#### 3.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```

### 3.14 某个标签下添加文档
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
DELETE /tag/remove
```
#### 3.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```

### 3.15 某个标签下去除文档
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
DELETE /tag/remove
```
#### 3.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```

### 3.16 查全部的分类信息
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
DELETE /tag/remove
```
#### 3.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```


### 3.17 查全部的标签信息
#### 3.1.1 接口原型

#### 3.1.2 接口地址
```http request
DELETE /tag/remove
```
#### 3.1.2 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": "none"
}
```

#### 3.1.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    
  }
}
```


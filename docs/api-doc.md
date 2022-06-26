# Api-doc
[toc]

## 1 文档信息
|版本|日期|修订内容|修订人|审核人|
|----|----|----|----|----|
|v1.0|2022-06-19|新建|Jarrett|Jarrett|
|v2.0|2022-06-26|修改了统计的内容|Jarrett|Jarrett|


## 2 api 说明
### 2.1 文档检索和文档分页列表
#### 2.1.1 接口原型
用于首页进行文件检索。

#### 2.1.2 接口地址
```http request
GET  /document/list
```
#### 2.1.3 请求参数

```json
{
  "param": {
    "type": "ALL | Filter | Category | tag",
    "filterWord": "xxx",
    "page": 1,
    "rows": 10,
    "categoryId": 1,
    "tagId": 5
  },
  "token": "string",
  "data": "none"
}
```
#### 2.1.4 响应结果

```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": [
    {
      "id": 1,
      "title": "",
      "abstract": "abstract",
      "size": 23334,
      
      "collectNum": 1,
      "commentNum": 123,
      "category": {
        "id": 12,
        "name": "name"
      },
      "tags": [
        {
          "id": 23,
          "name": "name"
        }
      ],
      "userName": "UserName",
      "createTime": "CreateTime",
      "updateTime": "UpdateTime"
    }
  ]
}
```

### 2.2 单个文档详情
#### 2.1.1 接口原型
点击搜索结果的详情，查看到的结果信息

#### 2.1.2 接口地址
```http request
GET  /document/detail
```
#### 2.1.3 请求参数

```json
{
  "param": {
    "docId": 12
  },
  "token": "string",
  "data": "none"
}
```

#### 2.1.4 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    "id": 1,
    "title":"",
    "abstract": "abstract",
    "size": 23334,
    "collectNum": 1,
    "commentNum": 123,
    "category": {
      "id": 12,
      "name": "name"
    },
    "tags": [
      {
        "id": 23,
        "name": "name"
      }
    ],
    "url": "url",
    "userName": "UserName",
    "createTime": "CreateTime",
    "updateTime": "UpdateTime"
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
#### 2.3.3 请求参数

```json
{
  "param": "none",
  "token": "token",
  "data": {
      "docId": 1
  }
}
```

#### 2.3.4 响应结果
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

### 2.6 文档修改一条评论
#### 2.6.1 接口原型
在文章详情页，修改某一条评论信息

#### 2.6.2 接口地址
```http request
PUT /comment/update
```
#### 2.6.3 请求参数
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

#### 2.6.4 响应结果
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
  "param": {
    "commentId": 3232
  },
  "token": "string",
  "data": "none"
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
GET  /comment/list
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
### 3.1 上传文档
#### 3.1.1 接口原型
管理界面第一项，增加文档
#### 3.1.2 接口地址
```http request
GET  /document/upload
```
#### 3.1.3 请求参数

```json
{
  "param": {
    "page": 1,
    "row": 10
  },
  "token": "string",
  "data": "none"
}
```

#### 3.1.4 响应结果

```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": [
    {
      "id": 332,
      "name": "name",
      "size": 23232,
      "category": "category",
      "userName": "ljr",
      "createTime": "2022-08-12 32:00:22"
    }
  ]
}
```

### 3.2 管理界面通过文档id删除一篇文档
#### 3.2.1 接口原型

#### 3.2.2 接口地址
```http request
GET  /document/remove
```
#### 3.2.3 请求参数

```json
{
  "param": "key-word",
  "token": "string",
  "data": {
    "docId": 23
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

### 3.2 增加一个分类/标签信息
#### 3.2.1 接口原型
文档分类页面，文档标签页面
#### 3.2.2 接口地址
```http request
POST /category/insert
```
#### 3.2.3 请求参数

```json
{
  "param": "key-word",
  "token": "string",
  "data": {
    "name": "",
    "type": "CATEGORY | TAG"
  }
}
```

#### 3.2.4 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": "SUCCESS"
}
```

### 3.3 编辑一个分类/标签信息
#### 3.3.1 接口原型

#### 3.3.2 接口地址
```http request
PUT  /category/update
```
#### 3.3.3 请求参数

```json
{
  "param": "key-word",
  "token": "string",
  "data": {
    "name": "",
    "id":1,
    "type": "CATEGORY | TAG"
  }
}
```

#### 3.3.4 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": "SUCCESS"
}
```

### 3.4 删除一个分类/标签信息
#### 3.4.1 接口原型

#### 3.4.2 接口地址
```http request
DELETE /category/remove
```
#### 3.4.2 请求参数

```json
{
  "param": "key-word",
  "token": "string",
  "data": {
    "id": 1,
    "type": "CATEGORY | TAG"
  }
}
```

#### 3.4.3 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": "SUCCESS"
}
```

### 3.5 某个分类/标签下增加文档
#### 3.5.1 接口原型
选中某个分类或者标签的时候，增加全部文档

#### 3.5.2 接口地址
```http request
POST /categroy/addRelationship
```
#### 3.5.3 请求参数

```json
{
  "param": "key-word",
  "token": "string",
  "data": {
    "type": "CATEGORY | TAG",
    "id": 123,
    "docId": 43
  }
}
```

#### 3.5.4 响应结果

```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    "type": "category",
    "result": "success"
  }
}
```

### 3.6 某个分类/标签下去除文档
#### 3.6.1 接口原型

#### 3.6.2 接口地址
```http request
DELETE /category/removeRelationship
```
#### 3.6.3 请求参数
```json
{
  "param": "key-word" ,
  "token": "string",
  "data": {
    "type": "CATEGORY | TAG",
    "id": 123,
    "docId": 43
  }
}
```

#### 3.6.4 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    "type": "category",
    "result": "success"
  }
}
```

### 3.7 查全部的分类信息
#### 3.7.1 接口原型

#### 3.7.2 接口地址
```http request
GET /category/all
```
#### 3.7.3 请求参数

```json
{
  "param": {
    "type": "CATEGORY | TAG"
  },
  "token": "string",
  "data": "none"
}
```

#### 3.7.4 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    "type": "category",
    "result": "success"
  }
}
```

4 其他特殊请求
### 4.1 首页查看分类信息【热度榜】
#### 4.1.1 接口原型
查询最热的三条结果

#### 4.1.2 接口地址
```http request
GET  /statistics/trend
```
#### 4.1.3 请求参数
```json
{
  "param": "none",
  "token": "string",
  "data": "none"
}
```
#### 4.1.4 响应结果
```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": [
    {
      "id": 12,
      "name": 123,
      "docList": [
        {
          "docName": "xxxxx",
          "id": 323
        }
      ]
    }
  ]
}
```

### 4.2 统计总数【热度榜】
#### 4.2.1 接口原型
统计文档总数，统计分类总数，统计标签总数，统计评论量

#### 4.2.2 接口地址
```http request
GET /statistics/all
```
#### 4.2.3 请求参数
```json
{
  "param": "none",
  "token": "string",
  "data": "none"
}
```
#### 4.3.4 响应结果

```json
{
  "code": 200,
  "timestamp": 1632302,
  "data": {
    "docNum": 122,
    "categoryNum": 232,
    "tagNum": 232,
    "commentNum": 332
  }
}
```
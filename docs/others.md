

```json
mongodb 中建立唯一索引
https://mongodb.net.cn/manual/core/index-unique/
```

解决报错的wenti
https://www.cnblogs.com/jxd283465/p/15783187.html


ppt，pptx, doc 暂时没有想到 更好的解决办法
预览的时候采用原始文件转成pdf文件，然后再进行预览操作


// 用户搜索内容进行采集；
// 用户的搜索和评论必须经过防刷；违禁词过滤等。

// redis 引入
// 安装和使用redis的教程：
https://cloud.tencent.com/developer/article/1938468#:~:text=CentOS%20%E5%AE%89%E8%A3%85%20Redis%201%20%E4%B8%8B%E8%BD%BDfedora%E7%9A%84%20epel%20%E4%BB%93%E5%BA%93%202,5%20%E8%BF%9B%E5%85%A5%20redis%20%E6%9C%8D%E5%8A%A1%206%20%E9%98%B2%E7%81%AB%E5%A2%99%E5%BC%80%E6%94%BE%E7%AB%AF%E5%8F%A3%207%20%E4%BF%AE%E6%94%B9redis%E9%BB%98%E8%AE%A4%E7%AB%AF%E5%8F%A3%E5%92%8C%E5%AF%86%E7%A0%81


// redisTemplate的使用方法
https://www.cnblogs.com/caizhaokai/p/11037610.html

// 存储用户经常搜索的内容

// 存储最热点击的文档

// 存储热搜榜单

// https://blog.csdn.net/weixin_41725792/article/details/110928066



启动mongodb

./mongod --dbpath=/data/mongo --logpath=/usr/local/mongodb4/db.log --fork


启动es
```shell
systemctl start elasticsearch
```


在macos上进行安装redis
https://www.jianshu.com/p/3bdfda703552



在macos上安装es
https://cloud.tencent.com/developer/article/2032233
注意版本适配

// 权限自适应设计
1、是否开启普通用户上传功能
2、是否开启管理员强制审核功能
3、是否开启违禁词提醒功能？

4、文档评审，等待，评审完成，评审失败

// 增加用户ip封禁
1、恶意ip检索
2、恶意ip下载
验证码
增加用户封禁功能

// 存储量统计
1、mongodb的统计接口
2、es的统计接口

// 评审页面，增加用户删除文档的提醒信息
// 可以进行删除
// 文档下载记录
// 某某人下载了某某文档

// 关闭管理员评审功能
// 设置日志保留时间，1个月，2个月，3个月，自动删除


备注：

删除文档的时候：

1、删除审核信息

2、删除评论信息

3、删除检索的内容信息

4、


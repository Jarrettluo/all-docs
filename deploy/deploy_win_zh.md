

# all-docs（全文档）在windows环境下部署
本文主要以图文的形式讲解【全文档】项目所需环境在windows下的安装，主要包括IDEA、Redis、Mongodb、Elasticsearch、Kibana。

## IDEA

- 关于IDEA的安装与使用请参考：https://github.com/judasn/IntelliJ-IDEA-Tutorial
- 搜索插件仓库，安装插件Lombok；

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676023579779-a3641af1-f4b1-4b70-a24a-aa6cb3639ff3.png#averageHue=%23363a3f&clientId=u4c71546f-fd35-4&from=paste&id=ufbc36d26&name=image.png&originHeight=714&originWidth=1268&originalType=url&ratio=1&rotation=0&showTitle=false&size=86704&status=done&style=none&taskId=u3bb96b12-8b11-44bc-bc0b-f1f893a0f76&title=)

- 将项目下载到本地，然后直接打开。

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676023600652-c9f640c5-ff38-4c5a-9829-ed62eaa2c6f3.png#averageHue=%233f464e&clientId=u4c71546f-fd35-4&from=paste&id=ue364a655&name=image.png&originHeight=392&originWidth=335&originalType=url&ratio=1&rotation=0&showTitle=false&size=19080&status=done&style=none&taskId=u14892263-1187-423e-91cd-f4d4be2f44e&title=)


![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676023804115-e317853d-c620-4677-a188-2242b8199409.png#averageHue=%23051a2c&clientId=u4c71546f-fd35-4&from=paste&height=541&id=u6cc0f405&name=image.png&originHeight=541&originWidth=470&originalType=binary&ratio=1&rotation=0&showTitle=false&size=25896&status=done&style=none&taskId=u136ef585-77bf-4e44-9a18-c7d5cfe0436&title=&width=470)

## Redis

- 由于Redis官方并没有提供Windows版本，第三方提供的最新版本为5.0，下载地址：https://github.com/tporadowski/redis/releases/

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676023636347-8b85de7c-16ce-47c3-95c8-c8b990edd013.png#averageHue=%23fefefd&clientId=u4c71546f-fd35-4&from=paste&id=ue02c9cac&name=image.png&originHeight=670&originWidth=1060&originalType=url&ratio=1&rotation=0&showTitle=false&size=80523&status=done&style=none&taskId=ucb6ae6c3-1db3-40da-ae20-78ea73ee351&title=)

- 下载完后解压到指定目录；

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676023636331-a9c9a4bf-2f70-4440-b09c-54a2ada49cc6.png#averageHue=%23fbf9f8&clientId=u4c71546f-fd35-4&from=paste&id=udb2634d7&name=image.png&originHeight=512&originWidth=710&originalType=url&ratio=1&rotation=0&showTitle=false&size=55801&status=done&style=none&taskId=ua8c9c160-ea35-454b-a651-e8770e13942&title=)

- 在当前地址栏输入cmd命令后，使用如下命令可以启动Redis服务；
```shell
redis-server.exe redis.windows.conf 
```

- 如果你想把Redis注册为系统服务来使用的话可以试试下面的命令。
```shell
# 安装为服务 
redis-server --service-install redis.windows.conf 

# 启动服务 
redis-server --service-start  

# 停止服务 
redis-server --service-stop 

# 卸载服务 
redis-server --service-uninstall
```

## Elasticsearch
下载Elasticsearch7.17.3版本的zip包，并解压到指定目录，下载地址：[https://www.elastic.co/cn/downloads/past-releases/elasticsearch-7-17-3](https://www.elastic.co/cn/downloads/past-releases/elasticsearch-7-17-3)
![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024050180-92647f97-d3f6-411f-95d0-6ed7fb69d7f8.png#averageHue=%23fcfbf9&clientId=u4c71546f-fd35-4&from=paste&id=u3652b6ba&name=image.png&originHeight=342&originWidth=722&originalType=url&ratio=1&rotation=0&showTitle=false&size=31376&status=done&style=none&taskId=ud15de4ae-fbd0-40cc-8b58-9499b2bcc33&title=)

- 安装中文分词器，注意下载与Elasticsearch对应的版本，下载地址：https://github.com/medcl/elasticsearch-analysis-ik/releases

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024073754-65567ddb-b60c-4182-93a7-1c4448c7545c.png#averageHue=%23fefefe&clientId=u4c71546f-fd35-4&from=paste&id=u4c0a9ae0&name=image.png&originHeight=397&originWidth=995&originalType=url&ratio=1&rotation=0&showTitle=false&size=35485&status=done&style=none&taskId=u606a5cb3-6499-46c4-9db5-518f7246e19&title=)

- 下载完成后解压到Elasticsearch的plugins目录下；

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024086534-a8bc7eee-f253-4d57-8378-90a6e4607ac2.png#averageHue=%23faf8f7&clientId=u4c71546f-fd35-4&from=paste&id=u98e3fd83&name=image.png&originHeight=278&originWidth=676&originalType=url&ratio=1&rotation=0&showTitle=false&size=31889&status=done&style=none&taskId=u5467a632-9189-4f18-88b5-8a0a5be3e50&title=)

- 运行bin目录下的elasticsearch.bat启动Elasticsearch服务。

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024086632-fe6f8fed-0c21-40c1-94c9-4e5672359e43.png#averageHue=%23fbfaf9&clientId=u4c71546f-fd35-4&from=paste&id=u4998578b&name=image.png&originHeight=455&originWidth=921&originalType=url&ratio=1&rotation=0&showTitle=false&size=58090&status=done&style=none&taskId=u015b6843-f6d6-4b09-82d8-3a0c4b65f40&title=)

## Kibana

- 下载Kibana，作为访问Elasticsearch的客户端，请下载7.17.3版本的zip包，并解压到指定目录，下载地址：https://www.elastic.co/cn/downloads/past-releases/kibana-7-17-3

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024105389-b567332b-23e8-438f-b335-eec3b6c553d8.png#averageHue=%23fcfbfa&clientId=u4c71546f-fd35-4&from=paste&id=ud730add8&name=image.png&originHeight=388&originWidth=755&originalType=url&ratio=1&rotation=0&showTitle=false&size=36864&status=done&style=none&taskId=u20207e8a-6904-4f72-a9e3-010524b49d3&title=)

- 运行bin目录下的kibana.bat，启动Kibana服务；

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024105381-108ecf6d-d541-4596-9e2d-2b1f50b86574.png#averageHue=%23faf9f8&clientId=u4c71546f-fd35-4&from=paste&id=u06949bb3&name=image.png&originHeight=196&originWidth=693&originalType=url&ratio=1&rotation=0&showTitle=false&size=18999&status=done&style=none&taskId=u24930e87-41cc-4ea9-b1ae-b216b33c1a5&title=)

- 打开Kibana的用户界面，访问地址：http://localhost:5601

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024105551-8a4fffe2-dd4a-47e7-a43e-11f9925929aa.png#averageHue=%2323c2b6&clientId=u4c71546f-fd35-4&from=paste&id=u4f963afd&name=image.png&originHeight=810&originWidth=1192&originalType=url&ratio=1&rotation=0&showTitle=false&size=142993&status=done&style=none&taskId=u9bd9d9fb-2d1d-427e-a852-10d1a990c85&title=)





-   配置文本抽取管道

`Ingest Attachment Processor Plugin`是一个文本抽取插件，本质上是利用了`Elasticsearch`的`ingest node`功能，提供了关键的预处理器`attachment`。在安装目录下运行以下命令即可安装。

```
./bin/elasticsearch-plugin install ingest-attachment
```



-   在kibana中进行操作

```
PUT /_ingest/pipeline/attachment
{
    "description": "Extract attachment information",
    "processors": [
        {
            "attachment": {
                "field": "content",
                "ignore_missing": true
            }
        },
        {
            "remove": {
                "field": "content"
            }
        }
    ]
}

```

在`attachment`中指定要过滤的字段为`content`，所以写入`Elasticsearch`时需要将文档内容放在`content`字段。

![定义文本抽取管道](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676177096682-deb67850-52ac-4e30-aabf-fce03f85756c.png#averageHue=%23eff1f6&clientId=u58c11836-cadc-4&from=ui&id=uc447b920&name=1240.png&originHeight=288&originWidth=1240&originalType=binary&ratio=2&rotation=0&showTitle=false&size=55838&status=done&style=none&taskId=uff7560f3-693e-401d-89d6-c7f219d7584&title=)





## MongoDB

- 下载MongoDB安装包，选择Windows社区版安装，下载地址：https://www.mongodb.com/download-center/community

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024222985-4398e3d9-1437-46c3-8655-713bb13a2342.png#averageHue=%2336b369&clientId=u4c71546f-fd35-4&from=paste&id=u3c962d33&name=image.png&originHeight=721&originWidth=1170&originalType=url&ratio=1&rotation=0&showTitle=false&size=124570&status=done&style=none&taskId=ub1f614ab-5305-45f2-90c7-e5f2051be5a&title=)

- 运行MongoDB安装包并选择自定义安装，设置好安装路径；

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024222920-d887b02d-1093-4ca4-b4c3-45d9b27284de.png#averageHue=%23ece9e4&clientId=u4c71546f-fd35-4&from=paste&id=u26f23f93&name=image.png&originHeight=387&originWidth=495&originalType=url&ratio=1&rotation=0&showTitle=false&size=26868&status=done&style=none&taskId=uc0762a34-21b2-45bd-aca1-375326b4c5f&title=)

- 配置MongoDB，让MongoDB作为服务运行，并配置好数据目录和日志目录；

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024222929-7c5840e1-80e9-484e-90bb-1447ff7f2d50.png#averageHue=%23ebe6e5&clientId=u4c71546f-fd35-4&from=paste&id=uaaf7b82c&name=image.png&originHeight=387&originWidth=495&originalType=url&ratio=1&rotation=0&showTitle=false&size=23409&status=done&style=none&taskId=uf89790a6-84bf-49fa-adfc-8d27d1ba9a7&title=)

- 取消MongoDB Compass的安装选项（不取消安装极慢），需要可自行安装；

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024222973-7d97b2b5-d8c0-4af5-9db5-8c8b248b7969.png#averageHue=%23edeae6&clientId=u4c71546f-fd35-4&from=paste&id=uc6e21341&name=image.png&originHeight=387&originWidth=495&originalType=url&ratio=1&rotation=0&showTitle=false&size=21821&status=done&style=none&taskId=u2b621943-0110-46b7-b2e1-36570b8eca7&title=)

- 双击mongo.exe可以运行MongoDB自带客户端，操作MongoDB；

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024222989-a2e4b254-d988-48a9-b80b-de51e6f833e7.png#averageHue=%23faf8f7&clientId=u4c71546f-fd35-4&from=paste&id=u842db55c&name=image.png&originHeight=452&originWidth=702&originalType=url&ratio=1&rotation=0&showTitle=false&size=47240&status=done&style=none&taskId=ue7812a89-1fcd-44eb-b1c3-4ae452bb18c&title=)

- 连接成功后会显示如下信息；

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024224157-e8d3c605-9ef5-427b-adab-b0afa4fb930d.png#averageHue=%23262525&clientId=u4c71546f-fd35-4&from=paste&id=uf575537b&name=image.png&originHeight=512&originWidth=979&originalType=url&ratio=1&rotation=0&showTitle=false&size=37844&status=done&style=none&taskId=u2c947d67-0053-4e22-9d88-7a0f550f60b&title=)

- 如果需要移除MongoDB服务，只需使用管理员权限运行cmd工具，并输入如下命令。
```shell

# 需要先stop服务
sc.exe stop mongodb

# 再删除服务
sc.exe delete MongoDB
```

## DocumentSharingSiteApplication 启动

- 启动项目：直接运行com.jiaruiblog.DocumentSharingSiteApplication的main方法即可;

  ![image-20230212113153077](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676177096923-f73ccf79-4161-40d4-96d8-6e46fa08ed29.png#averageHue=%232d2c2b&clientId=u58c11836-cadc-4&from=ui&id=uc4f28230&name=image-20230212113153077.png&originHeight=626&originWidth=1370&originalType=binary&ratio=2&rotation=0&showTitle=false&size=117319&status=done&style=none&taskId=ue9c54ae0-9fe4-4a72-9d7f-069dbd056f5&title=)

- 看到不到更多的日志？

```yaml
logging:
  level:
    root: ${LOGGING_LEVEL:ERROR}
```
默认情况下，`application.yml`文件中设置的日志级别是ERROR，可选的其他配置是：WARNING, INFO。

- 系统的默认配置

以下是`application.yml`文件中提供的默认配置文件。如果有必要可以对参数进行修改。

其中大写的部分是环境变量参数，可以在jar启动的时候进行动态配置。
```yaml
all-docs:
  config:
    # 普通用户可以自行上传文档，默认true，可选为false。
    user-upload: ${AD_USER_UPLOAD:true}
    # 上传的文档必须经过管理员审核，默认false，可选为true。
    admin-review: ${AD_ADMIN_REVIEW:false}
    # 敏感词过滤功能，默认为true，可选为false。
    prohibited-word: ${AD_PROHIBITED_WORD:true}
    # 新用户注册功能，默认为true，可选为false。
    user-registry: ${AD_USER_REGISTRY:true}
    # 系统初始的管理员账号，默认为admin123，可自由设置字符串。
    initial-username: ${AD_INITIAL_USERNAME:admin123}
    # 系统初始的管理员密码，默认为admin123，可自由设置为字符串
    initial-password: ${AD_INITIAL_PASSWORD:admin123}
    # 系统重新启动的时候，是否重置数据库中管理员密码。
    # 例如安装好以后对初始密码进行修改，无法登录了，可以通过此参数进行覆盖。
    cover-admin: ${COVER_ADMIN:true}
  file-path:
    sensitive-file: sensitive.txt
```

- 接口文档地址：http://localhost:8082/api/v1.0/swagger-ui.html
  (注意接口文档并不完善，请见谅)




# 前端项目部署

## Windows下的安装及部署

- 下载nodejs并安装，最好使用v14.16.1版本，版本不对会导致npm install出错，下载地址：https://nodejs.org/dist/v14.16.1/node-v14.16.1-x86.msi
- 下载all-documents-vue的代码；
  - Github：https://github.com/Jarrettluo/all-documents-vue
- 从开发工具中打开all-documents-vue项目；

![image-20230212121442309](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676177096858-fd2252c6-ac5a-484e-b7aa-e33679c1fe14.png#averageHue=%233d444c&clientId=u58c11836-cadc-4&from=ui&id=uf630dbd5&name=image-20230212121442309.png&originHeight=898&originWidth=608&originalType=binary&ratio=2&rotation=0&showTitle=false&size=93596&status=done&style=none&taskId=u212c3540-98cc-4ebb-8d7a-11828e9c5d7&title=)

- 切换至淘宝镜像源加速访问；
```powershell
# 设置为淘宝的镜像源 
npm config set registry https://registry.npm.taobao.org 

# 设置为官方镜像源 
npm config set registry https://registry.npmjs.org 
```

- 打开控制台输入命令安装相关依赖；
```powershell
npm install 
```

![image-20230212121528569](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676177096904-6924685d-42e4-4aec-87e9-0afa01290c8e.png#averageHue=%23303030&clientId=u58c11836-cadc-4&from=ui&id=u516902bc&name=image-20230212121528569.png&originHeight=596&originWidth=1408&originalType=binary&ratio=2&rotation=0&showTitle=false&size=80619&status=done&style=none&taskId=u2f7711a1-ba30-4be9-b2bb-21bcdb5ba4b&title=)


![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024534986-991a2aae-839c-46b2-a5d6-b76acad8c45b.png#averageHue=%232f2d2c&clientId=u4c71546f-fd35-4&from=paste&id=u81c49c3d&name=image.png&originHeight=261&originWidth=1459&originalType=url&ratio=1&rotation=0&showTitle=false&size=28873&status=done&style=none&taskId=ua50a1357-52a9-437c-a504-4122b4dbe3d&title=)

- node-sass无法下载导致构建失败时可使用如下命令下载。
```powershell
# linux 
SASS_BINARY_SITE=https://npm.taobao.org/mirrors/node-sass/ 
npm install node-sass 

# window 
set SASS_BINARY_SITE=https://npm.taobao.org/mirrors/node-sass && npm install node-sass 
```


## 已搭建全文档后台环境的启动

- 可以直接运行全文档后台服务；

![image-20230212122315757](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676177096849-ed195d7e-052a-4941-9818-78629bbdf41e.png#averageHue=%232e2d2b&clientId=u58c11836-cadc-4&from=ui&id=u80d3774f&name=image-20230212122315757.png&originHeight=406&originWidth=1492&originalType=binary&ratio=2&rotation=0&showTitle=false&size=81335&status=done&style=none&taskId=ucb8bf656-2fe9-441b-bc35-a450736c4b2&title=)

- 使用命令启动全文档前端，在控制台中输入如下命令：
```
npm run serve
```


![image-20230212122403431](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676177098183-ddb7747a-5525-4e1f-832e-a038494ea6e4.png#averageHue=%23343434&clientId=u58c11836-cadc-4&from=ui&id=u11665114&name=image-20230212122403431.png&originHeight=736&originWidth=1432&originalType=binary&ratio=2&rotation=0&showTitle=false&size=125061&status=done&style=none&taskId=u897223d2-6dde-45de-a0cf-0477b8e3bd3&title=)

- 访问地址查看效果：http://localhost:8080

![image-20230212122517959](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676177099700-62f20f14-68fa-4188-8ae0-56eb5bb4afdf.png#averageHue=%23e5ebef&clientId=u58c11836-cadc-4&from=ui&id=u490e9b8a&name=image-20230212122517959.png&originHeight=1484&originWidth=2880&originalType=binary&ratio=2&rotation=0&showTitle=false&size=530793&status=done&style=none&taskId=uf7731e9d-29a2-44ff-a252-5b7cb45d2b4&title=)

- 不做任何更改，页面默认是本地接口：

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024535919-2f271e22-1f32-4806-a446-1bf628dc9e01.png#averageHue=%23f7f6f6&clientId=u4c71546f-fd35-4&from=paste&id=u71f1bc67&name=image.png&originHeight=345&originWidth=1170&originalType=url&ratio=1&rotation=0&showTitle=false&size=38516&status=done&style=none&taskId=u2379b84c-795f-4f32-8075-50a41479ece&title=)
## 未搭建全文档后台环境的启动
未搭建全文档后台的需要使用线上api进行访问，线上API地址：http://81.69.247.172:8082/api/v1.0 。

- 修改./src/api/request.js文件中的baseURL为线上地址；

![image-20230212122652194](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676177099454-6a152868-08c9-40e7-b75e-481d7a14b7a0.png#averageHue=%23547442&clientId=u58c11836-cadc-4&from=ui&id=u8c99515b&name=image-20230212122652194.png&originHeight=1564&originWidth=1716&originalType=binary&ratio=2&rotation=0&showTitle=false&size=366255&status=done&style=none&taskId=u311cea3c-5fef-447e-bf67-ac517cd8427&title=)

- 使用命令启动前端项目，在IDEA控制台中输入如下命令：
```
npm run serve
```


![image-20230212122941964](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676177098902-a8a8eae7-194d-46a7-a7cd-ab4af9a06680.png#averageHue=%23313131&clientId=u58c11836-cadc-4&from=ui&id=ufde5fb07&name=image-20230212122941964.png&originHeight=266&originWidth=836&originalType=binary&ratio=2&rotation=0&showTitle=false&size=34032&status=done&style=none&taskId=u39936415-472f-4118-b8b2-b0fa0ba5b07&title=)

- 访问地址http://localhost:8080查看效果：

![image-20230212123112054](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676177100676-30d4373b-d7e2-4982-b680-66f968adbc19.png#averageHue=%23e1e5e4&clientId=u58c11836-cadc-4&from=ui&id=u2ce95cb3&name=image-20230212123112054.png&originHeight=1488&originWidth=2880&originalType=binary&ratio=2&rotation=0&showTitle=false&size=903806&status=done&style=none&taskId=ub9c7f744-5532-4119-8770-7de1e9a2c85&title=)

- 进行登录操作，发现调用的是线上接口：

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024537081-e7b7640d-f997-465c-abb7-6e804baf92fa.png#averageHue=%23f7f6f6&clientId=u4c71546f-fd35-4&from=paste&id=ue536667f&name=image.png&originHeight=343&originWidth=1176&originalType=url&ratio=1&rotation=0&showTitle=false&size=38540&status=done&style=none&taskId=ue2c522e9-36af-4a53-a9a2-96ac2e9345e&title=)
## Linux下的部署

- 修改修改./src/api/request.js文件中的baseURL为线上地址

![image-20230212123617588](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676177100810-426f5848-cc12-434c-b516-42285cae8791.png#averageHue=%23b1d9e0&clientId=u58c11836-cadc-4&from=ui&id=uf5cb5452&name=image-20230212123327158.png&originHeight=1486&originWidth=2880&originalType=binary&ratio=2&rotation=0&showTitle=false&size=653008&status=done&style=none&taskId=u30f09cc1-79ac-4122-9ad0-60ab705e4cd&title=)

- 使用命令进行打包；
```
npm run build 
```


![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024537582-0541c357-03a9-489c-b28e-145612a27be6.png#averageHue=%232d2c2b&clientId=u4c71546f-fd35-4&from=paste&id=u70ff86c8&name=image.png&originHeight=248&originWidth=897&originalType=url&ratio=1&rotation=0&showTitle=false&size=22317&status=done&style=none&taskId=u235d01dd-f700-4b10-a3b7-c1900cb4d87&title=)

- 打包后的代码位置

![image-20230212123427506](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676177100151-e81f8714-127a-4c48-9d28-232d1586b7e8.png#averageHue=%235e8683&clientId=u58c11836-cadc-4&from=ui&id=u6c2e2d6b&name=image-20230212123427506.png&originHeight=1112&originWidth=624&originalType=binary&ratio=2&rotation=0&showTitle=false&size=107683&status=done&style=none&taskId=ucea3fdcf-f15f-47f8-b6f8-514682b4cde&title=)

- 将dist目录打包为dist.tar.gz文件

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024537660-c2705b88-8692-41ab-8590-da8c65f79afc.png#averageHue=%23ebeaea&clientId=u4c71546f-fd35-4&from=paste&id=u01bb05ee&name=image.png&originHeight=442&originWidth=463&originalType=url&ratio=1&rotation=0&showTitle=false&size=52174&status=done&style=none&taskId=u9dd21705-adb7-4cf0-8ffe-c02c9069f3c&title=)

- Linux上Nginx的安装可以参考项目中关于的Nginx部分；
- 将dist.tar.gz上传到linux服务器（nginx相关目录）；

![image.png](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676024538709-fe44bd03-7b80-4790-9320-e63389953f54.png#averageHue=%23090604&clientId=u4c71546f-fd35-4&from=paste&id=u1f378153&name=image.png&originHeight=129&originWidth=664&originalType=url&ratio=1&rotation=0&showTitle=false&size=12788&status=done&style=none&taskId=u4a28a527-b955-4128-ba7a-64e556aedff&title=)

- 使用该命令进行解压操作；
```
tar -zxvf dist.tar.gz 
```


- 删除nginx的html文件夹；
```
rm -rf html 
```

- 移动dist文件夹到html文件夹；
```
mv dist html 
```



- 重启nginx；
```
docker restart nginx 
```


- 访问首页并登录：http://81.69.247.172

![image-20230212123327158](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676177100810-426f5848-cc12-434c-b516-42285cae8791.png#averageHue=%23b1d9e0&clientId=u58c11836-cadc-4&from=ui&id=uf5cb5452&name=image-20230212123327158.png&originHeight=1486&originWidth=2880&originalType=binary&ratio=2&rotation=0&showTitle=false&size=653008&status=done&style=none&taskId=u30f09cc1-79ac-4122-9ad0-60ab705e4cd&title=)

- 发现调用的是Linux服务器地址。

![image-20230212123708108](https://cdn.nlark.com/yuque/0/2023/png/2413981/1676177101601-fb3bfcd9-b9a3-4e5a-8bbe-45053e4eaffb.png#averageHue=%23fbfbfb&clientId=u58c11836-cadc-4&from=ui&id=u2acb4add&name=image-20230212123708108.png&originHeight=524&originWidth=1628&originalType=binary&ratio=2&rotation=0&showTitle=false&size=132762&status=done&style=none&taskId=u7059c76c-29aa-469d-919b-631f2e14331&title=)

------
- 以上内容没有经过验证！欢迎勘误
- 2023年2月19日 Jarrett
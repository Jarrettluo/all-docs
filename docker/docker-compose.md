

在启动之初就必须要设置es的权限

然后注意设置es的插件等

最后注意搞kibana

参考文档：

https://blog.csdn.net/eddielee9217/article/details/113713036?spm=1001.2101.3001.6650.2&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-2-113713036-blog-122365278.pc_relevant_default&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-2-113713036-blog-122365278.pc_relevant_default&utm_relevant_index=3


https://blog.csdn.net/xujingyiss/article/details/120203011


解压到指定路径下
```shell
unzip file.zip -d dest_folder
```

1、前端的配置中，增加对文件的限制

2、redis的启动中，一定要设置配置文件
```shell
bind 0.0.0.0
requirepass 123

```
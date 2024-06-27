db.createCollection('permission')
db.permission.insertMany([
        // CategoryController
        {
            permName: "分类新增",
            permKey: "category.insert",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "分类更新",
            permKey: "category.update",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "分类删除",
            permKey: "category.remove",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "分类查询",
            permKey: "category.query",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "分类与文档添加关系",
            permKey: "category.ship.insert",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "分类与文档删除关系",
            permKey: "category.ship.remove",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "我收藏的文档",
            permKey: "collect.query",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "我上传的文档",
            permKey: "upload.query",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        // CollectController
        {
            permName: "收藏文档",
            permKey: "collect.insert",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "删除收藏文档",
            permKey: "collect.remove",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        // CommentController
        {
            permName: "评论查询",
            permKey: "comment.query",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "评论添加",
            permKey: "comment.insert",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "评论修改",
            permKey: "comment.update",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "评论删除",
            permKey: "comment.remove",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        // DocLogController
        {
            permName: "系统日志查询",
            permKey: "doc.log.query",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "系统日志删除",
            permKey: "doc.log.remove",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        // DocReviewController
        {
            permName: "文档评审查询",
            permKey: "doc.view.query",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "修改文档已读",
            permKey: "doc.view.status",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "文档评审拒绝",
            permKey: "doc.view.refuse",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "文档评审同意",
            permKey: "doc.view.approve",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "文档评审同意",
            permKey: "doc.view.log.remove",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        // DocumentController
        {
            permName: "文档查询",
            permKey: "doc.query",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "文档删除",
            permKey: "doc.remove",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "文档修改",
            permKey: "doc.update",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        // FileController
        {
            permName: "文件查询",
            permKey: "file.query",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "文件预览",
            permKey: "file.preview",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "文件下载",
            permKey: "file.download",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "文件上传",
            permKey: "file.upload",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "附件删除",
            permKey: "file.remove",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        // LikeController
        {
            permName: "点赞收藏添加",
            permKey: "like.insert",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "点赞收藏查询",
            permKey: "like.query",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        // StatisticsController
        // SystemConfigController
        {
            permName: "系统设置查询",
            permKey: "system.config.query",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "系统设置修改",
            permKey: "system.config.update",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "违禁词更新",
            permKey: "prohibited.word.update",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        // UserController
        {
            permName: "用户添加",
            permKey: "user.insert",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "用户查询",
            permKey: "user.query",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "用户信息修改",
            permKey: "user.update",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "用户删除",
            permKey: "user.remove",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "用户分配角色",
            permKey: "user.roles",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "用户角色查询",
            permKey: "user.roles.query",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "用户屏蔽",
            permKey: "user.block",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "用户密码重置",
            permKey: "user.password.reset",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        // RoleController
        {
            permName: "角色添加",
            permKey: "role.insert",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "角色查询",
            permKey: "role.query",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "角色删除",
            permKey: "role.remove",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        {
            permName: "角色分配权限",
            permKey: "role.perms",
            createDate: ISODate(),
            updateDate: ISODate()
        },
        // PermissionController
        {
            permName: "权限查询",
            permKey: "permission.query",
            createDate: ISODate(),
            updateDate: ISODate()
        },

    ]
)
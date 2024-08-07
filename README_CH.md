<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
<!--[![LinkedIn][linkedin-shield]][linkedin-url]-->

- [English version Readme_en.md](https://github.com/Jarrettluo/document-sharing-site/blob/main/README.md)
- [中文版 Readme.md](https://github.com/Jarrettluo/document-sharing-site/blob/main/README_CH.md)


<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/Jarrettluo/document-sharing-site">
    <img src="https://github.com/Jarrettluo/document-sharing-site/blob/main/images/banner.png" alt="Logo" width="300" height="300">
  </a>

<h3 align="center">全文档</h3>

  <p align="center">
    支持全文检索的文档分享、存储系统。
    <br />
  </p>
</p>





<!-- ABOUT THE PROJECT -->
## 关于全文档

在小团队中往往会产生大量的协作文档。例如，我们会将各类文档放在网盘、svn等软件中，但是存在文档内的内容无法快速搜索的问题。因此，专门开发了一个用于存储ppt、word、png等文档的，支持私有部属的知识库的检索。


<p>体验地址：<a href="http://81.69.247.172/#/">http://81.69.247.172/#/</a></p>

> 管理员账号：admin123 管理员密码： admin123

## 快捷部署
> 在ubuntu 18.04 环境下测试通过
> 
> centos 的安装脚本暂时没有

‼️ 受dockerhub拉取镜像影响，此部署方法暂时失效！‼️
```shell
# 克隆项目
git clone https://github.com/Jarrettluo/document-sharing-site.git

# 切换为管理员
su root

# 切换到工作目录
cd document-sharing-site && cd docker

# 修改文件权限
chmod +x auto.sh

# 启动脚本
sudo ./auto.sh

```

## 预览图

<p align="center">
<img src="https://github.com/Jarrettluo/document-sharing-site/blob/main/images/homepage.png" alt="homepage" height="400">
<p align="center">
全文档首页图
</p>
</p>

<p align="center">
<img src="https://github.com/Jarrettluo/document-sharing-site/blob/main/images/docList.png" alt="docList" height="400">
<p align="center">
文档列表
</p>
</p>

<p align="center">
<img src="https://github.com/Jarrettluo/document-sharing-site/blob/main/images/searchPage.png" alt="searchPage" height="400">
<p align="center">
文档搜索页面
</p>
</p>

<p align="center">
<img src="https://github.com/Jarrettluo/document-sharing-site/blob/main/images/fileUpload.png" alt="fileUpload" height="400">
<p align="center">
文档上传页面
</p>
</p>

<p align="center">
<img src="https://github.com/Jarrettluo/document-sharing-site/blob/main/images/userPage.png" alt="userPage" height="400">
<p align="center">
用户个人信息页面
</p>
</p>

### 开源地址

前端项目
<a href="https://github.com/Jarrettluo/all-documents-vue.git">
https://github.com/Jarrettluo/all-documents-vue.git
</a>


后端项目 
<a href="https://github.com/Jarrettluo/document-sharing-site.git">
https://github.com/Jarrettluo/document-sharing-site.git
</a>

选择MongoDB作为主要的数据库，存储文档和文件。

后端技术：SpringBoot + MongoDB + ES

前端技术：Vue + axios

### 项目部署

- [全文档在Windows环境下部署](https://github.com/Jarrettluo/document-sharing-site/blob/main/deploy/depoly_win_zh.md)
- [全文档在Linux环境下部署](https://github.com/Jarrettluo/document-sharing-site/blob/main/deploy/deploy_linux_zh.md)
- [全文档使用Docker部署--待补充](https://github.com/Jarrettluo/document-sharing-site/blob/main/deploy/deploy_docker_zh.md)
- [全文档使用Docker-Compose部署--最新发布](https://github.com/Jarrettluo/document-sharing-site/blob/main/deploy/deploy_docker_compose_zh.md)

<!-- ROADMAP -->
## 路线图

准备做的事情

- 记录用户上次阅读的页数 🌟
- 搜索页面样式改造 🌟🌟
- 用户对文档进行标记 🌟
- 支持视频文件 🌟
- 支持权限划分 🌟
- 支持文件树 🌟
- 支持Mobi文件 🌟

查看 [open issues](https://github.com/othneildrew/Best-README-Template/issues) 。



<!-- CONTRIBUTING -->
## 提交代码

欢迎加入微信交流群，一起玩😄，如果微信群二维码失效，可以加我微信拉你进群。

<img src="https://github.com/Jarrettluo/all-docs-vue/blob/master/images/WechatIMG349.jpg" alt="imGroup" width="200">

> 如果有二次开发或者私有部署需求，请联系我

(添加微信请备注"全文档")


<img src="https://github.com/Jarrettluo/document-sharing-site/blob/main/images/Wechat.jpeg" alt="imGroup" width="200">


1. `Fork` 该项目
2. 创建自己的分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的功能 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个PR



<!-- LICENSE -->
## 许可证

查看 `LICENSE` 文件。



<!-- CONTACT -->
## 联系我

Jarrett Luo - luojiarui2@163.com

如果您觉得项目不错，欢迎赞赏支持！
<img src="https://github.com/Jarrettluo/all-docs-vue/blob/master/images/WechatIMG351.jpg" alt="imGroup" width="200">



<!-- ACKNOWLEDGEMENTS -->
## 致谢
- 暂无





<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/Jarrettluo/document-sharing-site.svg?style=for-the-badge
[contributors-url]: https://github.com/Jarrettluo/document-sharing-site/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/Jarrettluo/document-sharing-site.svg?style=for-the-badge
[forks-url]: https://github.com/Jarrettluo/document-sharing-site/network/members
[stars-shield]: https://img.shields.io/github/stars/Jarrettluo/document-sharing-site.svg?style=for-the-badge
[stars-url]: https://github.com/Jarrettluo/document-sharing-site/stargazers
[issues-shield]: https://img.shields.io/github/issues/Jarrettluo/document-sharing-site.svg?style=for-the-badge
[issues-url]: https://github.com/Jarrettluo/document-sharing-site/issues
[license-shield]: https://img.shields.io/github/license/Jarrettluo/document-sharing-site.svg?style=for-the-badge
[license-url]: https://github.com/Jarrettluo/document-sharing-site/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/othneildrew
[product-screenshot]: images/screenshot.png
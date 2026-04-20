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

<h3 align="center">All Docs</h3>

  <p align="center">
    Document sharing and storage system with full-text search support.
    <br />
  </p>
</p>





<!-- ABOUT THE PROJECT -->
## ABOUT THE PROJECT

In small teams, there is often a large number of collaborative documents. For example, we place various types of documents in cloud storage, SVN, and other software, but there is a problem with the inability to quickly search for content within the documents. Therefore, we have developed a dedicated knowledge base for storing documents such as PPT, Word, PNG, etc., which supports private deployment and retrieval.

<p>Experience URL：<a href="https://alldocs.tech">https://alldocs.tech</a></p>

> administrator account：admin123, administrator password： admin123

## ARCHITECTURE

### Multi-Module Architecture

```
all-docs (父工程)
├── all-docs-common      # 公共模块：工具类、通用响应、异常处理
├── all-docs-domain       # 领域模块：实体、值对象、领域服务
├── all-docs-infrastructure # 基础设施模块：MongoDB、Redis、Elasticsearch、MinIO 适配
├── all-docs-application  # 应用模块：业务编排、DTO、Service
├── all-docs-api          # API 模块：Controller、配置
└── all-docs-bootstrap    # 启动模块：应用入口
```

### Technology Stack

| 组件 | 说明 |
|------|------|
| MinIO | S3 协议对象存储，替代 GridFS 存储文件（规划中） |
| MongoDB | 主数据库，存储文档元数据和用户信息 |
| Elasticsearch | 全文搜索引擎，支持中文分词检索 |
| Redis | 缓存层，提升热点数据访问性能 |

### Development Requirements

- **Java**: 17+
- **Maven**: 3.8+
- **Node.js**: 14+ (前端开发)
- **IDE**: IntelliJ IDEA

### Module Dependencies

```
bootstrap -> api -> application -> infrastructure -> domain -> common
```

## QUICK START
> test on ubuntu 18.04
> wait me for centos, thank you.

‼️ 受dockerhub拉取镜像影响，此部署方法暂时失效！‼️
```shell
# clone project
git clone https://github.com/Jarrettluo/all-docs.git

# switch user to root
su root

# switch work directory
cd document-sharing-site && cd docker

# chmod
chmod +x auto.sh

# start shell script
sudo ./auto.sh

```

## PREVIEW

<p align="center">
<img src="https://github.com/Jarrettluo/document-sharing-site/blob/main/images/homepage.png" alt="homepage" height="400">
<p align="center">
homepage
</p>
</p>

<p align="center">
<img src="https://github.com/Jarrettluo/document-sharing-site/blob/main/images/docList.png" alt="docList" height="400">
<p align="center">
list of documents
</p>
</p>

<p align="center">
<img src="https://github.com/Jarrettluo/document-sharing-site/blob/main/images/searchPage.png" alt="searchPage" height="400">
<p align="center">
page of search 
</p>
</p>

<p align="center">
<img src="https://github.com/Jarrettluo/document-sharing-site/blob/main/images/fileUpload.png" alt="fileUpload" height="400">
<p align="center">
upload page
</p>
</p>

<p align="center">
<img src="https://github.com/Jarrettluo/document-sharing-site/blob/main/images/userPage.png" alt="userPage" height="400">
<p align="center">
user's info page
</p>
</p>

### Repository URL

frontend project:
<a href="https://github.com/Jarrettluo/all-documents-vue.git">
https://github.com/Jarrettluo/all-documents-vue.git
</a>


backend project:
<a href="https://github.com/Jarrettluo/document-sharing-site.git">
https://github.com/Jarrettluo/document-sharing-site.git
</a>

We choose MongoDB as the primary database to store documents and files.

Backend：SpringBoot + MongoDB + ES

Frontend：Vue + axios

### PROJECT DEPLOYMENT

- [Deploy on Windows](https://github.com/Jarrettluo/document-sharing-site/blob/main/deploy/depoly_win_zh.md)
- [Deploy on Linux](https://github.com/Jarrettluo/document-sharing-site/blob/main/deploy/deploy_linux_zh.md)
- [Deploy by Docker](https://github.com/Jarrettluo/document-sharing-site/blob/main/deploy/deploy_docker_zh.md)
- [Deploy by Docker Compose](https://github.com/Jarrettluo/document-sharing-site/blob/main/deploy/deploy_docker_compose_zh.md)

<!-- ROADMAP -->
## ROADMAP

- Record the last page number read by the user 🌟
- Revamp the search page style 🌟🌟
- Allow users to bookmark documents 🌟
- Support video files 🌟
- Support permission management 🌟
- Support file tree 🌟
- Support Mobi files 🌟

read [open issues](https://github.com/othneildrew/Best-README-Template/issues) 。



<!-- CONTRIBUTING -->
## CONTRIBUTING

You can join our Wechat group if you are interested.

<img src="https://github.com/Jarrettluo/all-docs-vue/blob/master/images/WechatIMG349.jpg" alt="imGroup" width="200">

> If you have any requirements for customization or private deployment, please feel free to contact me.

(Please add me on WeChat and kindly mention 'All Docs' in the request.)


<img src="https://github.com/Jarrettluo/document-sharing-site/blob/main/images/Wechat.jpeg" alt="imGroup" width="200">


1. `Fork` This project
2. create a branch  (`git checkout -b feature/AmazingFeature`)
3. commit you feature (`git commit -m 'Add some AmazingFeature'`)
4. push the commit (`git push origin feature/AmazingFeature`)
5. open a pull request



<!-- CONTACT -->
## CONTACT

Read `LICENSE` file。



<!-- CONTACT -->
## CONTACT

Jarrett Luo - luojiarui2@163.com

If you find the project valuable, your support and appreciation are welcome!

<img src="https://github.com/Jarrettluo/all-docs-vue/blob/master/images/WechatIMG351.jpg" alt="imGroup" width="200">



<!-- ACKNOWLEDGEMENTS -->
## ACKNOWLEDGEMENTS
- YOU





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
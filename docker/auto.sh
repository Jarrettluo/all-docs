#!/bin/bash

# 检查是否具有管理员权限
if [ "$EUID" -ne 0 ]; then
  echo "请以管理员权限运行此脚本 (使用 sudo)"
  exit 1
fi

# 安装Docker和Docker Compose（如果尚未安装）
if ! command -v docker &> /dev/null; then
    echo "Installing Docker..."
    # 安装Docker的命令

    # 更新系统软件包列表
    apt update

    # 安装所需的依赖软件包
    apt install -y apt-transport-https ca-certificates curl software-properties-common

    # 添加 Docker 的官方 GPG 密钥
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

    # 添加 Docker 的稳定存储库
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list

    # 更新软件包列表，以获取 Docker CE 版本信息
    apt update

    # 安装 Docker CE
    apt install -y docker-ce docker-ce-cli containerd.io

    # 启动 Docker 服务并设置开机启动
    systemctl start docker
    systemctl enable docker

    # 添加当前用户到 docker 用户组以避免每次使用 Docker 时都需要 sudo
    usermod -aG docker $USER

    # 输出 Docker 安装信息
    docker --version

    echo "Docker 已成功安装并配置完成。"
fi

#----------------------------------------------------------#

if ! command -v docker-compose &> /dev/null; then
    echo "Installing Docker Compose..."
    # 安装Docker Compose的命令
    # 下载最新版本的Docker Compose二进制文件
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

    # 添加可执行权限
    sudo chmod +x /usr/local/bin/docker-compose

    # 创建软链接到/usr/bin目录（可选）
    sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
fi

#----------------------------------------------------------#
# 检查本地是否有 Redis 镜像
if docker image inspect redis:latest &> /dev/null; then
  echo "本地已存在 Redis 镜像。"
else
  echo "本地不存在 Redis 镜像，开始安装..."
  docker pull redis:latest
fi

# 检查本地是否有 MongoDB 镜像
if docker image inspect mongo:latest &> /dev/null; then
  echo "本地已存在 MongoDB 镜像。"
else
  echo "本地不存在 MongoDB 镜像，开始安装..."
  docker pull mongo:latest
fi


# 检查是否已经存在 Docker Compose 文件
if [ ! -f "docker-compose.yml" ]; then
  echo "未找到 docker-compose.yml 文件。请将 Docker Compose 文件放置在当前目录中后再运行此脚本。"
  exit 1
fi


# 检查是否存在环境变量文件
if [ -f ".env" ]; then
  echo "读取环境变量文件 .env"
  source .env
else
  echo "未找到环境变量文件 .env。请将 .env 文件放置在当前目录中后再运行此脚本。"
  exit 1
fi

# 启动Docker Compose服务
echo "Starting services with Docker Compose..."
docker-compose up -d

#----------------------------------------------------------#

# 等待服务启动
echo "Waiting for services to start..."
# 在这里可以添加等待服务启动的逻辑，比如等待MongoDB、Elasticsearch和Redis准备就绪
# 检查每个服务是否正常运行
services=("service1" "service2" "service3" "service4")  # 替换为服务名称

for service in "${services[@]}"; do
  if docker-compose ps -q "$service" > /dev/null; then
    echo "$service 服务正常运行。"
  else
    echo "$service 服务未启动或遇到问题。"
    exit 1
  fi
done

#----------------------------------------------------------#

# 安装Elasticsearch插件（如果有的话）
echo "Installing Elasticsearch plugins..."
# 在这里可以添加安装Elasticsearch插件的命令，比如使用Elasticsearch的REST API或官方提供的命令行工具安装插件
# 进入 Elasticsearch 服务容器
docker-compose exec elasticsearch bash

# 在容器中安装插件，例如插件名称为插件名
bin/elasticsearch-plugin install 插件名

#----------------------------------------------------------#

# 创建索引（如果需要）
echo "Setting up Elasticsearch indices..."
# 在这里可以添加设置Elasticsearch索引的命令，比如使用Elasticsearch的REST API或官方提供的命令行工具导入索引配置


# 示例使用curl命令来定义文本抽取管道
curl -X PUT "http://localhost:9200/_ingest/pipeline/attachment" -H "Content-Type: application/json" -d '{
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
}'

# 在这里使用curl或其他HTTP客户端库发送HTTP请求来创建索引
# 示例使用curl命令来创建名为"my_index"的索引
curl -X PUT "http://localhost:9200/my_index" -H "Content-Type: application/json" -d '{
  "mappings": {
    "properties": {
      "id":{
        "type": "keyword"
      },
      "name":{
        "type": "text",
        "analyzer": "ik_max_word"
      },
      "type":{
        "type": "keyword"
      },
      "attachment": {
        "properties": {
          "content":{
            "type": "text",
            "analyzer": "ik_smart"
          }
        }
      }
    }
  }
}'



#----------------------------------------------------------#

echo "Installation completed successfully!"
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
    # curl -fsSL https://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

    # 使用镜像
    curl -fsSL https://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | sudo apt-key add -

    # 添加 Docker 的稳定存储库
    # echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list
    add-apt-repository "deb [arch=amd64] https://mirrors.aliyun.com/docker-ce/linux/ubuntu $(lsb_release -cs) stable"

    # 更新软件包列表，以获取 Docker CE 版本信息
    apt update

    # 安装 Docker CE
    apt install -y docker-ce

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
    # cd /usr/local/bin
    # mkdir docker-compose

    # 安装Docker Compose的命令
    # 下载最新版本的Docker Compose二进制文件
    # sudo curl -SL "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

    sudo curl -SL "https://github.com/docker/compose/releases/download/v2.20.2/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose

    # 添加可执行权限
    sudo chmod +x /usr/local/bin/docker-compose

    # 创建软链接到/usr/bin目录（可选）
    sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
fi


# 指定要检查的Docker容器名称或ID的列表
container_list=("ad_mongo" "ad_elasticsearch" "ad_redis" "ad_server" "ad_web")

for container_name_or_id in "${container_list[@]}"; do
    # 检查容器是否存在
    if docker ps -a --format "{{.Names}}" | grep -q "$container_name_or_id"; then
        # 如果容器存在，停止并删除容器
        docker stop "$container_name_or_id"
        docker rm "$container_name_or_id"
        echo "容器 $container_name_or_id 已停止并删除。"
    else
        echo "容器 $container_name_or_id 不存在，无需停止和删除。"
    fi
done

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

# 创建环境变量文件
cp install.conf .env


# 检查是否存在环境变量文件
if [ -f ".env" ]; then
  echo "读取环境变量文件 .env"
  source .env
else
  echo "未找到环境变量文件 .env。请将 .env 文件放置在当前目录中后再运行此脚本。"
  exit 1
fi

# es 要求为映射的文件夹赋权
chmod 777 ./data/elasticsearch/**
chmod 777 data/elasticsearch/data

# 启动Docker Compose服务
echo "Starting services with Docker Compose..."
docker-compose up -d

#----------------------------------------------------------#

# 等待服务启动
echo "Waiting for services to start..."
# 在这里可以添加等待服务启动的逻辑，比如等待MongoDB、Elasticsearch和Redis准备就绪
# 检查每个服务是否正常运行
services=("ad_mongo" "ad_elasticsearch" "ad_redis" "ad_server" "ad_web")  # 替换为服务名称

for service in "${services[@]}"; do
  if docker-compose ps -q "$service" > /dev/null; then
    echo "$service 服务正常运行。"
  else
    echo "$service 服务未启动或遇到问题。"
    exit 1
  fi
done


# 改变es的设置
sysctl -w vm.max_map_count=262144
# 使之立即生效
sysctl -p

#----------------------------------------------------------#
# 安装Elasticsearch插件（如果有的话）
echo "Installing Elasticsearch plugins..."

# 指定要删除的文件夹的路径
folder_path="esplugins"

# 检查文件夹是否存在
if [ -d "$folder_path" ]; then
    # 如果存在，删除文件夹
    rm -r "$folder_path"
    echo "文件夹 $folder_path 已删除。"
else
    echo "文件夹 $folder_path 不存在，无需删除。"
fi

mkdir esplugins

# 在安装目录的esplugin中创建文件夹
cd esplugins && mkdir analysis-ik
# 退出到安装目录
cd ..

# 检查是否已安装 unzip
if ! command -v unzip &> /dev/null; then
    # 如果未安装，则使用 apt 安装
    sudo apt update
    sudo apt install unzip -y
    echo "unzip 已安装"
else
    echo "unzip 已经安装"
fi

# 解压缩到指定文件夹， 安装插件
unzip elasticsearch-analysis-ik-7.9.3.zip -d ./esplugins/analysis-ik/

# 使用文件安装插件以后就要重启服务
docker restart ad_elasticsearch

#----------------------------------------------------------#
# 在这里可以添加安装Elasticsearch插件的命令，比如使用Elasticsearch的REST API或官方提供的命令行工具安装插件
# 进入 Elasticsearch 服务容器
docker-compose exec ad_elasticsearch ./bin/elasticsearch-plugin install ingest-attachment



# 指定要重启的Docker容器的名称或ID
container_name_or_id="ad_elasticsearch"

# 重启Docker容器
docker restart "$container_name_or_id"

# 等待容器变为可用状态
echo "等待容器 $container_name_or_id 可用..."

# 设置一个等待循环来检查容器状态
max_retries=30
retry_count=0
container_ready=false

while [ $retry_count -lt $max_retries ]; do
    container_status=$(docker ps -f "name=$container_name_or_id" --format "{{.Status}}")

    if [[ $container_status == *"Up"* ]]; then
        container_ready=true
        break
    fi

    sleep 5
    retry_count=$((retry_count + 1))
done

# 如果容器可用，打印消息
if [ "$container_ready" = true ]; then
    echo "容器 $container_name_or_id 已经可用。"
else
    echo "无法等待容器 $container_name_or_id 变为可用。"
fi

echo "等待es服务启动"

# 等待60秒
sleep 60

echo "查看可用的插件信息"

# 查看已经安装的插件及版本
curl -m 30 -XGET 'http://localhost:9200/_cat/plugins?v&s=name'
#----------------------------------------------------------#

# 创建索引（如果需要）
echo "Setting up Elasticsearch indices..."
# 在这里可以添加设置Elasticsearch索引的命令，比如使用Elasticsearch的REST API或官方提供的命令行工具导入索引配置


# 示例使用curl命令来定义文本抽取管道
curl -m 30 -X PUT "http://localhost:9200/_ingest/pipeline/attachment" -H "Content-Type: application/json" -d '{
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

echo "开始创建索引信息"

# 在这里使用curl或其他HTTP客户端库发送HTTP请求来创建索引
# 示例使用curl命令来创建名为"my_index"的索引
curl -X PUT "http://localhost:9200/docwrite" -H "Content-Type: application/json" -d '{
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

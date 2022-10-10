#!/bin/bash

start() {

  echo "开始进行安装"

  echo "修改权限"
  chmod -R 777 ./data/elasticsearch/

  echo "复制环境变量文件"
  cp install.conf .env

  echo "检查配置信息"
  docker-compose config

  echo "开始进行启动"
  # run docker-compose
  docker-compose -f docker-compose.yml up

  docker exec -i -t 6x_elasticsearch_1 /usr/share/elasticsearch/bin/elasticsearch-plugin install ingest-attachment
}

start
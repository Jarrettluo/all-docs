#!/bin/bash

# 安装失败时退出脚本并打印错误消息
function handle_error {
    echo "Error: $1 installation failed."
    exit 1
}

# 检查是否安装了 JDK 8
if java -version 2>&1 | grep -q "1.8"; then
    echo "JDK 8 is already installed."
else
    echo "Installing JDK 8..."
    sudo apt-get update || handle_error "System update"
    sudo apt-get install -y openjdk-8-jdk || handle_error "JDK 8"
    echo "JDK 8 installed successfully."
fi

# 检查是否安装了 Redis
if redis-cli --version &>/dev/null; then
    echo "Redis is already installed."
else
    echo "Installing Redis..."
    sudo apt-get update || handle_error "System update"
    sudo apt-get install -y redis-server || handle_error "Redis"

    # 设置 Redis 密码为 123
    sudo sed -i "s/^# requirepass .*/requirepass 123/" /etc/redis/redis.conf
    sudo systemctl restart redis-server || handle_error "Redis restart"

    echo "Redis installed successfully with password: 123"
fi

# 检查是否安装了 MongoDB
if mongod --version &>/dev/null; then
    echo "MongoDB is already installed."
else
    echo "Installing MongoDB..."
    wget -qO - https://www.mongodb.org/static/pgp/server-4.4.asc | sudo apt-key add - || handle_error "MongoDB key"
    echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu $(lsb_release -cs)/mongodb-org/4.4 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-4.4.list
    sudo apt-get update || handle_error "MongoDB update"
    sudo apt-get install -y mongodb-org || handle_error "MongoDB"

    sudo systemctl enable mongod || handle_error "MongoDB enable"
    sudo systemctl start mongod || handle_error "MongoDB start"

    echo "MongoDB installed successfully."
fi

# 检查是否安装了 Elasticsearch
if curl -X GET "localhost:9200/" &>/dev/null; then
    echo "Elasticsearch is already installed."
else
    echo "Installing Elasticsearch..."
    wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | sudo apt-key add - || handle_error "Elasticsearch key"
    sudo apt-get install apt-transport-https || handle_error "apt-transport-https"
    echo "deb https://artifacts.elastic.co/packages/7.x/apt stable main" | sudo tee /etc/apt/sources.list.d/elastic-7.x.list
    sudo apt-get update || handle_error "Elasticsearch update"
    sudo apt-get install -y elasticsearch || handle_error "Elasticsearch"

    sudo systemctl enable elasticsearch || handle_error "Elasticsearch enable"
    sudo systemctl start elasticsearch || handle_error "Elasticsearch start"

    # 安装 ingest-attachment 插件
    echo "Installing Elasticsearch plugin: ingest-attachment..."
    sudo /usr/share/elasticsearch/bin/elasticsearch-plugin install ingest-attachment || handle_error "Elasticsearch ingest-attachment plugin"
    sudo systemctl restart elasticsearch || handle_error "Elasticsearch restart after plugin installation"

    echo "Elasticsearch installed successfully with ingest-attachment plugin."
fi


# 检查是否安装了 NGINX
if nginx -v &>/dev/null; then
    echo "NGINX is already installed."
else
    echo "Installing NGINX..."
    sudo apt-get update || handle_error "System update"
    sudo apt-get install -y nginx || handle_error "NGINX"

    echo "NGINX installed successfully."
fi

# 设置 NGINX 代理
NGINX_CONF="/etc/nginx/sites-available/default"

echo "Configuring NGINX proxy..."
sudo sed -i '/location \/ {/a \\n    # Proxy /api/v1.0 to localhost:8082\n    location /api/v1.0 {\n        proxy_pass http://localhost:8082;\n        proxy_set_header Host $host;\n        proxy_set_header X-Real-IP $remote_addr;\n        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;\n        proxy_set_header X-Forwarded-Proto $scheme;\n    }\n' $NGINX_CONF || handle_error "NGINX proxy configuration"

# 检查配置语法是否正确
sudo nginx -t || handle_error "NGINX configuration test"

# 重新启动 NGINX 使配置生效
sudo systemctl restart nginx || handle_error "NGINX restart"

echo "NGINX proxy setup completed successfully."

echo "All installations completed successfully."

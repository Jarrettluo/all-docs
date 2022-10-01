#!/bin/bash
# 加载环境变量
source /etc/profile

#部署的地址，将jar包上传到deploy
deploy_path="/root/all-docs/"
#日志名称
log_name="all-docs-$(date +%Y-%m-%d).log"
#运行环境
environment=""

back(){
  if [ ! -d $deploy_path ]; then
    mkdir $deploy_path
    echo "文件夹创建成功！"
  elif [ ! -f $log_name ]; then
    touch $log_name
    echo "日志文件创建成功！"
  fi
}

back
#启动方法
start(){
  echo "进入启动函数！"
  # 表示start方法后跟的第一个参数
  APP_NAME="$1";
#判断${APP_NAME}文件是否存在
  if [ ! -f $deploy_path$APP_NAME ];then
      echo "启动文件地址:$deploy_path$APP_NAME"
      echo "没有找到$APP_NAME启动文件！"
      return
  fi

  #进程是否启动，没启动直接启动，启动了就先关闭再重启，awk '{print $2}' 的作用就是打印（print）出第二列的内容，第二列正好是PID
  pid=$(ps -ef | grep -v 'grep' | egrep $APP_NAME| awk '{print $2}')
  # [ -z STRING ] 如果STRING的长度为零则返回为真，即空是真
  #如果不存在返回1，存在返回0
  if [ -z "${pid}" ]; then
    #不存在，直接启动
   $deploy_path$APP_NAME > /dev/null 2>&1 &
    nohup java -jar $deploy_path$APP_NAME --spring.profiles.active=$environment > $deploy_path$log_name 2>&1 &
    echo "不存在进程,项目启动成功！"
  else
    echo "项目$1已经启动,进程pid是${pid}！"
    echo "现在开始关闭进程，重启$APP_NAME！"
    for i in $pid
    do
            kill -9 $i
            echo "杀死进程$i成功！"
    done
    nohup java -jar $deploy_path$APP_NAME --spring.profiles.active=$environment > $deploy_path$log_name 2>&1 &
    echo "项目启动成功！"
  fi
}
# jar包的名称，对应start函数中的$1
start document-sharing-site-1.0-SNAPSHOT.jar

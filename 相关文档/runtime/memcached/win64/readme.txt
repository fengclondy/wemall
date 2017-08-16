安装memcached：
1. 解压memcached-win64-1.4.4-14的文件，把它放到一个目录中，如D:\memcached。
2. 打开cmd，进入memcached解压后存放的目录，运行命令：
   memcached.exe -d install
   如果没有意外的话已经安装成功了。
  （目录中应该有memcached.exe这个文件）
3. 测试是否安装成功：
   打开cmd，进入memcached解压后存放的目录，运行命令：
   memcached -h，若安装成功，会提示memcached的帮助信息
4. 启动memcached：
  打开cmd，进入memcached解压后存放的目录，运行命令：
  memcached.exe -d start
  这时memcached已经启动，在任务管理器中可以看到这个进程
5. 连接Memcached：
  打开cmd，运行命令：
  telnet 127.0.0.1 11211
  即可连接Memcached
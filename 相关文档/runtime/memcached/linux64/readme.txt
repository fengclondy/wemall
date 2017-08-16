ubuntu 14.04 memcached配置  
1. 安装memcached
sudo apt-get install memcached
2. 查看memcached帮助
可以使用memcached -h命令或者man memcached命令查看准确的说明，如
memcached -h
memcached 1.4.14
-p <num>      TCP port number to listen on (default: 11211)
-U <num>      UDP port number to listen on (default: 11211, 0 is off)
-s <file>     UNIX socket path to listen on (disables network support)
-a <mask>     access mask for UNIX socket, in octal (default: 0700)
-l <addr>     interface to listen on (default: INADDR_ANY, all addresses)
              <addr> may be specified as host:port. If you don't specify
              a port number, the value you specified with -p or -U is
              used. You may specify multiple addresses separated by comma
              or by using -l multiple times
-d            run as a daemon
-r            maximize core file limit
-u <username> assume identity of <username> (only when run as root)
-m <num>      max memory to use for items in megabytes (default: 64 MB)
-M            return error on memory exhausted (rather than removing items)
-c <num>      max simultaneous connections (default: 1024)
-k            lock down all paged memory.  Note that there is a
              limit on how much memory you may lock.  Trying to
              allocate more than that would fail, so be sure you
              set the limit correctly for the user you started
              the daemon with (not for -u <username> user;
              under sh this is done with 'ulimit -S -l NUM_KB').
-v            verbose (print errors/warnings while in event loop)
-vv           very verbose (also print client commands/reponses)
-vvv          extremely verbose (also print internal state transitions)
-h            print this help and exit
-i            print memcached and libevent license
-P <file>     save PID in <file>, only used with -d option
-f <factor>   chunk size growth factor (default: 1.25)
-n <bytes>    minimum space allocated for key+value+flags (default: 48)
-L            Try to use large memory pages (if available). Increasing
              the memory page size could reduce the number of TLB misses
              and improve the performance. In order to get large pages
              from the OS, memcached will allocate the total item-cache
              in one large chunk.
-D <char>     Use <char> as the delimiter between key prefixes and IDs.
              This is used for per-prefix stats reporting. The default is
              ":" (colon). If this option is specified, stats collection
              is turned on automatically; if not, then it may be turned on
              by sending the "stats detail on" command to the server.
-t <num>      number of threads to use (default: 4)
-R            Maximum number of requests per event, limits the number of
              requests process for a given connection to prevent 
              starvation (default: 20)
-C            Disable use of CAS
-b            Set the backlog queue limit (default: 1024)
-B            Binding protocol - one of ascii, binary, or auto (default)
-I            Override the size of each slab page. Adjusts max item size
              (default: 1mb, min: 1k, max: 128m)
-S            Turn on Sasl authentication
-o            Comma separated list of extended or experimental options
              - (EXPERIMENTAL) maxconns_fast: immediately close new
                connections if over maxconns limit
              - hashpower: An integer multiplier for how large the hash
                table should be. Can be grown at runtime if not big enough.
                Set this based on "STAT hash_power_level" before a 
                restart.
3. 配置memcached
vim /etc/memcached.conf
-m 最大内存使用，单位MB。默认64MB
-p 参数用于告诉Memcached监听那个端口，如果使用了连同UDP端口也会同时修改。
-u 以的身份运行 (仅在以memcache运行的时候有效)
-l 连接的IP地址, 默认是本机
-c 最大同时连接数，默认是1024
-t 使用线程数的个数
以上参数根据具体情况进行配置
4. 清空memcached缓存
telnet localhost 11211
flush_all
quit
5. memcached监控
echo "stats settings" | nc localhost 11211
STAT maxbytes 67108864
STAT maxconns 1024
STAT tcpport 11211
STAT udpport 11211
STAT inter localhost
STAT verbosity 0
STAT oldest 0
STAT evictions on
STAT domain_socket NULL
STAT umask 700
STAT growth_factor 1.25
STAT chunk_size 48
STAT num_threads 4
STAT num_threads_per_udp 4
STAT stat_key_prefix :
STAT detail_enabled no
STAT reqs_per_event 20
STAT cas_enabled yes
STAT tcp_backlog 1024
STAT binding_protocol auto-negotiate
STAT auth_enabled_sasl no
STAT item_size_max 1048576
STAT maxconns_fast no
STAT hashpower_init 0
STAT slab_reassign no
STAT slab_automove 0
END
检查memcached的配置
或者
telnet localhost 11211
stats
quit

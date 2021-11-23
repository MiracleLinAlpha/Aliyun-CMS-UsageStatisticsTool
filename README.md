Aliyun-CMS-UsageStatisticsTool
---------

```
          _ _                     _______          _ 
    /\   | (_)                   |__   __|        | |
   /  \  | |_ _   _ _   _ _ __      | | ___   ___ | |
  / /\ \ | | | | | | | | | '_ \     | |/ _ \ / _ \| |
 / ____ \| | | |_| | |_| | | | |    | | (_) | (_) | |
/_/    \_\_|_|\__, |\__,_|_| |_|    |_|\___/ \___/|_|
               __/ |                                 
              |___/                                  

```

## 注意事项
1、该脚本适用于阿里云专有云V3版

2、本地编译打包需自行从阿里云官方文档页面下载ASAPI SDK加入本地仓库


## 功能
调用ASAPI的CMS接口，导出ECS资源使用率为excel-xlsx格式，可自选导出的时间范围


## 更新记录

* 2021-09-29 `Release v1.0.0` 初稿完成
* 2021-11-23 `Release v2.0.0` 增加磁盘总容量，带宽及磁盘使用率

## 准备动作

编写配置文件放置入脚本JAR包同目录中

```
conf.json

{
    "RegionId" : "XXX",
    "ApiGateWay" : "XXX",
    "AccessKeyId" : "XXX",
    "AccessKeySecret" : "XXX"
}
```

注意事项：

1、配置文件名必须为conf.json

2、该脚本调用阿里云（专有云）的ASAPI，其中*ApiGateWay*可用以下方法获得

天基(tianji)  -》 报表  -》 服务注册变量  -》筛选*ASAPI*  -》 endpoint



### 使用

```

java -jar Aliyun-CMS-UsageStatisticsTool-*.jar

```







## License
除 “版权所有（C）阿里云计算有限公司” 的代码文件外，遵循 [MIT license](http://opensource.org/licenses/MIT) 开源。



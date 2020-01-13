# rainydays

## 项目介绍

`rainydays`项目为一个商品订单权益业务系统，对接上游NG（订单同步、申请出货、地址修改、申请退货）和下游点壹平台（出货通知、签收通知、完成退货通知）
，调用第三方信源平台开通权益，当权益开通失败后，需发送邮件告警，当商品订单业务系统宕机，也需发送邮件告警。

### 组织结构

``` lua
rainydays
├── order_api     -- 接口、通用工具
├── order_monitor -- Eureka服务节点监控
├── order_prize   -- 商品订单权益业务系统
└── order_stub    -- 对接系统测试桩
```

### 项目流程

- 商品订单权益业务流程:[订单流程图.jsp](document/resource/订单流程图.jpg)
  
### 开发环境

| 工具          | 版本号 | 下载                                                         |
| ------------- | ------ | ------------------------------------------------------------ |
| JDK           | 1.8    | https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html |
| Mysql         | 5.7    | https://www.mysql.com/                                       |

### 部署说明

> windows环境部署
#### 1、数据库操作（默认你已安装好mysql，并且是通过docker安装）

- 登录数据库：打开cmd命令行界面，假设你mysql的root用户密码是123456
  mysql -uroot -p123456进入mysql的root用户
- 创建新用户prize：在mysql命令行下执行 create user 'prize'@'%' identified by 'prize';
- 赋予新用户权限：在mysql命令行下执行 grant all privileges on `prize`.* to 'prize'@'%';
- 退出mysql的root用户并进入prize用户：在mysql命令行下执行 exit; mysql -uprize -pprize    
- 创建数据库：create database prize character set 'utf8' collate 'utf8_general_ci';

#### 2、打包

- 确认你要部署的环境，假设你就一个开发环境：在application.properties配置对应的spring.profiles.active值为dev
- 配置文件中的eureka客户端地址配置（eureka.client.service-url.defaultZone）与order_monitor项目中的配置项
  eureka.client.service-url.defaultZone保持一致   

#### 3、运行（默认你的环境已安装了jdk）

- 分别运行order_monitor、order_prize和order_stub的SpringBootApplication类

#### 4、重回数据库，确认下字符编码与存储引擎（默认你数据库窗口还在）

- 在mysql命令下执行 show create table prize.t_order; 看最后那一行，如“ENGINE=MyISAM DEFAULT CHARSET=latin1”
- "ENGINE=MyISAM"则需改为InnoDB，在mysql命令行下执行 alter table t_order engine=InnoDB;
- "DEFAULT CHARSET=latin1"则需改为utf8，在mysql命令行下执行 
  alter table prize.t_order convert to character set utf8 collate utf8_general_ci;
- 重复以上操作，依次检查t_product、t_logistics、t_returnorder和t_infosource


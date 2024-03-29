<p align="center">
    <img src=https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/%E7%BE%8A%E8%85%BF_800x600-removebg-preview.png />
</p>
<h1 align="center">羊腿Api 接口开放平台</h1>
<p align="center"><strong>羊腿Api 接口开放平台是一个为用户和开发者提供全面API接口调用服务的平台 🛠</strong></p>
<div align="center">
<a href="https://github.com/PanYW-Git/gigot-api" target="_blank">
    <img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/PanYW-Git/gigot-api">
</a>
<a href="https://github.com/PanYW-Git/gigot-api" target="_blank">
	<img alt="GitHub forks" src="https://img.shields.io/github/forks/PanYW-Git/gigot-api">
</a>
    <img alt="Maven" src="https://raster.shields.io/badge/Maven-3.8.1-red.svg"/>
<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
        <img alt="" src="https://img.shields.io/badge/JDK-1.8+-green.svg"/>
</a>
    <img alt="SpringBoot" src="https://raster.shields.io/badge/SpringBoot-2.7+-green.svg"/>
</div>




## 项目介绍 🙋

**欢迎来到我们的羊腿API开放平台**，这是一个专为开发者设计的平台，我们提供一系列强大的API接口，让您能够轻松地访问和使用我们的数据。

我们的API接口涵盖了各种类型的数据，无论您是想开发一个新的应用，还是想进行数据快速获取，我们的API都能为您提供强大的支持。

为了让您能够快速上手，我们还提供了一套完整的SDK，让您能够轻松地将我们的API接入到您的应用中。

我们的开放平台致力于提供高质量、实时的数据，以满足您的各种需求。我们的API接口设计简洁易用，文档详尽，让您能够快速上手。

## 网站导航 🧭

- [**羊腿Api 后端 🏘️**](https://github.com/PanYW-Git/gigot-api/tree/main/gigotapi)
- [**羊腿Api 前端 🏘**️](https://github.com/PanYW-Git/gigot-api/tree/main/gigotapi-fontend)
- **[羊腿Api-SDK](https://github.com/PanYW-Git/gigot-api/tree/main/gigotapi-client-sdk)** 🛠
- **[羊腿Api 接口开放平台 🔗](https://api.panyuwen.top/)**
-  **[羊腿Api-DOC 开发者文档 📖](https://doc.panyuwen.top/)**


## 目录结构 📑


| 目录                                                     | 描述               |
|--------------------------------------------------------| ------------------ |
| **🏘️ [gigotapi-backend](https://github.com/PanYW-Git/gigot-api/tree/main/gigotapi)**             | 羊腿Api后端服务模块 |
| **🏘️ [gigotapi-common](https://github.com/PanYW-Git/gigot-api/tree/main/gigotapi)**               | 公共服务模块       |
| **🕸️ [gigotapi-gateway](https://github.com/PanYW-Git/gigot-api/tree/main/gigotapi)**             | 网关模块           |
| **🔗 [gigotapi-interface](https://github.com/PanYW-Git/gigot-api/tree/main/gigotapi)**          | 接口模块           |
| **🛠 [gigotapi-client-sdk](https://github.com/PanYW-Git/gigot-api/tree/main/gigotapi-client-sdk)** | 开发者调用sdk      |
| **📘 [gigotapi-doc](https://doc.panyuwen.top/)**            | 接口在线文档       |
| **📘 [gigotapi-xcx](https://github.com/PanYW-Git/gigot-api/tree/main/gigotapi-xcx)** | 微信小程序认证 |
| **✔️ [gigotapi-test](https://github.com/PanYW-Git/gigot-api/tree/main/gigotapi)** | sdk调用Demo |

## 我们的优势 😎

1. **客户端SDK支持：** 提供客户端SDK，方便您快速集成到你的项目中。
2. **简洁高效：** 稳定、安全、高效的接口调用服务，帮助您实现更快速、便捷的开发和调用体验。
3. **开发者文档和技术支持：** 平台提供了完善的开发者文档和后续技术支持，帮助开发者快速接入和发布接口，解决遇到的问题和困难。
4. **多样化的接口选择：** 丰富多样的接口供您选择，涵盖了各个领域的功能和服务，满足不同的需求。
5. **高质量数据**：我们的开放平台致力于提供高质量、实时的数据，以满足开发者的各种需求。
6. **可视化在线调试：** 可视化展示更加直观，快速验证接口的功能和效果，提高开发效率。

## 项目流程 🗺️

- 系统架构图

  ![系统架构图 (1)](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291859580.jpeg)

- 系统运行流程图（简图）

  ![image-20240105132518203](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291858698.png)

- 系统运行流程图

  ![系统运行流程图](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291859560.jpeg)

## 快速启动 🚀

### 前端

环境要求：Node.js >= 16

安装依赖：

```bash
yarn or  npm install
```

启动：

```bash
yarn run dev or npm run start:dev
```

部署：

```bash
yarn build or npm run build
```

### 后端

1. 执行sql目录下gigot-api.sql

2. 修改application-demo.yml文件

## 技术选型 🎯

## 后端

- SpringBoot
- SpringCloud-Gateway网关
- Dubbo
- Maven
- Nacos
- Mysql
- Fastjson
- Lombok
- Junit
- geoip2
- Mybatis-plus
- Hutool
- Redis
- Redisson
- commons-email
- 微信开放平台
- 微信支付

## 前端

- Ant Design Pro Umi
- AntV
- React
- Umi
- axios
- VuePress
- Hexo
- 微信小程序

## 部署环境

- 阿里云/腾讯云服务器
- 腾讯云DNS解析
- 宝塔控制面板
- Docker
- CentOS 7.9/Windows 11
- JDK8
- Nginx
- SSL证书
- GIT
- XShell
- XFTP



## 功能介绍 📋

`金币`即积分，用于平台接口调用。

| **功能**                                                 | 游客 | **普通用户** | **管理员** |
| -------------------------------------------------------- | ---- | ------------ | ---------- |
| [**SDK**](https://api.panyuwen.top/api/file/sdk)快速接入 | ✅    | ✅            | ✅          |
| **[开发者API在线文档](https://doc.panyuwen.top/)**       | ✅    | ✅            | ✅          |
| 邀请好友注册得坤币                                       | ❌    | ✅            | ✅          |
| 微信付款                                                 | ❌    | ✅            | ✅          |
| 在线调试接口                                             | ❌    | ✅            | ✅          |
| 每日签到得金币                                           | ❌    | ✅            | ✅          |
| 接口大厅搜索接口、浏览接口                               | ❌    | ✅            | ✅          |
| 微信小程序登录                                           | ✅    | ✅            | ✅          |
| 邮箱验证码登录注册                                       | ✅    | ✅            | ✅          |
| Api密钥生成/更新                                         | ❌    | ✅            | ✅          |
| 钱包充值                                                 | ❌    | ✅            | ✅          |
| 支付成功邮箱通知(需要绑定邮箱)                           | ❌    | ✅            | ✅          |
| 更新头像                                                 | ❌    | ✅            | ✅          |
| 绑定、换绑、解绑邮箱                                     | ❌    | ✅            | ✅          |
| 取消订单、删除订单                                       | ❌    | ✅            | ✅          |
| 用户管理、封号解封等                                     | ❌    | ❌            | ✅          |
| 接口管理、接口发布审核、下架                             | ❌    | ❌            | ✅          |
| 运行分析                                                 | ❌    | ❌            | ✅          |

## 功能展示 ✨

### 开发者文档

![image-20240329191506694](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291915767.png)

### 首页

![](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291917464.png)

### 接口集市

### 开发者在线文档

![image-20240329192119797](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291921862.png)

![image-20240329192149825](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291921889.png)

### 接口描述

#### **在线API**

![image-20240329192219748](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291922074.png)

#### 在线调试工具

![image-20240329192236712](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291922767.png)

#### **错误码参考**

#### ![image-20240329192254394](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291922454.png)

### 管理页

#### 用户管理

![admin-userManagement](https://img.qimuu.icu/typory/admin-userManagement.png)

#### 商品管理![admin-productManagement](https://img.qimuu.icu/typory/admin-productManagement.png)

#### 接口管理![admin-interfaceManagement](https://img.qimuu.icu/typory/admin-interfaceManagement.png)

#### 动态更新请求响应参数![dynamicRequestParameters](https://img.qimuu.icu/typory/dynamicRequestParameters.png)


### 充值服务

![image-20240329192439900](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291924966.png)

### 订单支付

![image-20240329192517549](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291925614.png)

### 个人信息

#### 信息展示

![image-20240329192607712](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291926770.png)



### 登录/注册

#### 小程序登录

![image-20240329191605344](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291916455.png)

#### 其他登录方式

![image-20240329191625656](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291916762.png)

![image-20240329191641437](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291916563.png)

### 订单管理

- **我的订单**

  ![image-20240329192708251](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291927318.png)

- **详细订单**

  ![image-20240329192724448](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291927506.png)
### 主题切换

#### 深色主题

![image-20240329192751700](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291927764.png)

#### 顶部菜单

#### ![image-20240329192843291](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291928356.png)

#### 色弱模式

![image-20240329192928615](https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/202403291929677.png)

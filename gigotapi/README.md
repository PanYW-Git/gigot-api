<p align="center">
    <img src=https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/%E7%BE%8A%E8%85%BF_800x600-removebg-preview.png />
</p>
<h1 align="center">羊腿Api 接口开放平台</h1>
<p align="center"><strong>羊腿Api 接口开放平台是一个为用户和开发者提供全面API接口调用服务的平台 🛠</strong></p>
<div align="center">
<a href="https://github.com/PanYW-Git/gigotapi" target="_blank">
    <img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/PanYW-Git/gigotapi">
</a>
<a href="https://github.com/PanYW-Git/gigotapi" target="_blank">
	<img alt="GitHub forks" src="https://img.shields.io/github/forks/PanYW-Git/gigotapi">
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

- [**羊腿Api 后端 🏘️**](https://github.com/PanYW-Git/gigotapi)
- [**羊腿Api 前端 🏘**️](https://github.com/qimu666/羊腿Api-frontend)
- **[羊腿Api-SDK](https://github.com/qimu666/羊腿Api-sdk)** 🛠
- **[羊腿Api 接口开放平台 🔗](https://api.panyuwen.top/)**
-  **[羊腿Api-DOC 开发者文档 📖](https://doc.panyuwen.top/)**


## 目录结构 📑


| 目录                                                     | 描述               |
|--------------------------------------------------------| ------------------ |
| **🏘️ [羊腿Api-backend](./羊腿Api-backend)**             | 羊腿Api后端服务模块 |
| **🏘️ [羊腿Api-common](./羊腿Api-common)**               | 公共服务模块       |
| **🕸️ [羊腿Api-gateway](./羊腿Api-gateway)**             | 网关模块           |
| **🔗 [羊腿Api-interface](./羊腿Api-interface)**          | 接口模块           |
| **🛠 [qi-qpi-sdk](https://github.com/qimu666/羊腿Api-sdk)** | 开发者调用sdk      |
| **📘 [羊腿Api-doc](https://doc.qimuu.icu/)**            | 接口在线文档       |
| **✔️ [羊腿Api-SDK-Demo](https://github.com/qimu666/羊腿Api-sdk-demo/blob/master/src/main/java/icu/qimuu/qiapisdkdemo/controller/InvokeController.java)** | sdk调用Demo |

## 我们的优势 😎

1. **客户端SDK支持：** 提供客户端SDK，方便您快速集成到你的项目中。
2. **简洁高效：** 稳定、安全、高效的接口调用服务，帮助您实现更快速、便捷的开发和调用体验。
3. **开发者文档和技术支持：** 平台提供了完善的开发者文档和后续技术支持，帮助开发者快速接入和发布接口，解决遇到的问题和困难。
4. **多样化的接口选择：** 丰富多样的接口供您选择，涵盖了各个领域的功能和服务，满足不同的需求。
5. **高质量数据**：我们的开放平台致力于提供高质量、实时的数据，以满足开发者的各种需求。
6. **可视化在线调试：** 可视化展示更加直观，快速验证接口的功能和效果，提高开发效率。

## 项目流程 🗺️

![QiAPI 接口开放平台](https://img.qimuu.icu/typory/QiAPI%2520%25E6%258E%25A5%25E5%258F%25A3%25E5%25BC%2580%25E6%2594%25BE%25E5%25B9%25B3%25E5%258F%25B0.png)

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

1. 执行sql目录下gigotapi.sql

2. 修改application-demo.yml文件

## 技术选型 🎯

### **后端**

- Spring Boot 2.7.0
- Spring MVC
- MySQL 数据库
- 腾讯云COS存储
- Dubbo 分布式（RPC、Nacos）
- Spring Cloud Gateway 微服务网关
- API 签名认证（Http 调用）
- IJPay-AliPay  支付宝支付
- WeiXin-Java-Pay  微信支付
- Swagger + Knife4j 接口文档
- Spring Boot Starter（SDK 开发）
- Jakarta.Mail 邮箱通知、验证码
- Spring Session Redis 分布式登录
- Apache Commons Lang3 工具类
- MyBatis-Plus 及 MyBatis X 自动生成
- Hutool、Apache Common Utils、Gson 等工具库

### 前端

- React 18

- Ant Design Pro 5.x 脚手架

- Ant Design & Procomponents 组件库

- Umi 4 前端框架

- OpenAPI 前端代码生成

  

## 功能介绍 📋

``即积分，用于平台接口调用。

|                          **功能**                           | 游客 | **普通用户** | **管理员** |
| ----------------------------------------------------- |--------------|-----|-----|
| [**羊腿Api-SDK**](https://github.com/qimu666/羊腿Api-sdk)使用 | ✅ | ✅ |     ✅      |
|        **[开发者API在线文档](http://doc.qimuu.icu)**        | ✅ | ✅ |     ✅      |
|                     邀请好友注册得坤币                      | ❌ | ✅ |     ✅      |
|                    切换主题、深色、暗色                     | ✅ | ✅ | ✅ |
|                       微信支付宝付款                        | ❌ | ✅ | ✅ |
|                        在线调试接口                         | ❌ | ✅ | ✅ |
|                       每日签到得坤币                        | ❌ | ✅ | ✅ |
|                 接口大厅搜索接口、浏览接口                  | ✅ | ❌ | ✅ |
|                     邮箱验证码登录注册                      | ✅ | ✅ | ✅ |
|                          钱包充值                           | ❌ | ❌ | ✅ |
|                     支付成功邮箱通知(需要绑定邮箱)                     | ❌ | ✅ | ✅ |
|                          更新头像                           | ❌ | ✅ | ✅ |
|                    绑定、换绑、解绑邮箱                     | ❌ | ✅ | ✅ |
|                          取消订单、删除订单                          | ❌ | ✅ | ✅ |
|                    商品管理、上线、下架                     | ❌ | ❌ |✅|
|                    用户管理、封号解封等                     | ❌ | ❌ | ✅ |
|                接口管理、接口发布审核、下架                 | ❌ | ❌ | ✅ |
|                            退款                             | ❌ | ❌| ❌ |

## 功能展示 ✨

### 首页

![index](https://img.qimuu.icu/typory/index.png)

### 接口广场

![interfaceSquare](https://img.qimuu.icu/typory/interfaceSquare.png)

### 开发者在线文档

![api](https://img.qimuu.icu/typory/api.png)

![api2](https://img.qimuu.icu/typory/api2.png)

### 接口描述

#### **在线API**

![interfaceinfo-api](https://img.qimuu.icu/typory/interfaceinfo-api.png)

#### 在线调试工具![interfaceinfo-tools](https://img.qimuu.icu/typory/interfaceinfo-tools.png)

#### **错误码参考**![interfaceinfo-errorcode](https://img.qimuu.icu/typory/interfaceinfo-errorcode.png)

#### **接口调用代码示例**![interfaceinfo-sampleCode](https://img.qimuu.icu/typory/interfaceinfo-sampleCode.png)

### 管理页

#### 用户管理

![admin-userManagement](https://img.qimuu.icu/typory/admin-userManagement.png)

#### 商品管理![admin-productManagement](https://img.qimuu.icu/typory/admin-productManagement.png)

#### 接口管理![admin-interfaceManagement](https://img.qimuu.icu/typory/admin-interfaceManagement.png)

#### 动态更新请求响应参数![dynamicRequestParameters](https://img.qimuu.icu/typory/dynamicRequestParameters.png)


### 积分商城

![pointPurchase](https://img.qimuu.icu/typory/pointPurchase.png)

### 订单支付![pay](https://img.qimuu.icu/typory/pay.png)

### 个人信息

#### 信息展示

![userinfo](https://img.qimuu.icu/typory/userinfo.png)

#### 每日签到

##### 签到成功![successfullySignedIn](https://img.qimuu.icu/typory/successfullySignedIn.png)

##### 签到失败![errorfullySignedIn](https://img.qimuu.icu/typory/errorfullySignedIn.png)

### 好友邀请

#### **发送邀请**![Invitefriends](https://img.qimuu.icu/typory/Invitefriends.png)

#### **接收邀请**![registerThroughInvitationCode](https://img.qimuu.icu/typory/registerThroughInvitationCode.png)

### 登录/注册![login](https://img.qimuu.icu/typory/login.png)

![register](https://img.qimuu.icu/typory/register.png)

### 订单管理

- **我的订单**![orderinfo](https://img.qimuu.icu/typory/orderinfo.png)

- **详细订单**![orderDetails](https://img.qimuu.icu/typory/orderDetails.png)
### 主题切换

#### 深色主题![darkTheme](https://img.qimuu.icu/typory/darkTheme.png)

#### 浅色主题![index](https://img.qimuu.icu/typory/index.png)

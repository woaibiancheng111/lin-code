# Lin Code

基于 Spring Boot 3 的电商管理系统后端示例项目，覆盖用户认证、角色权限、商品管理、订单管理、JWT 鉴权、Redis 缓存和 RabbitMQ 异步扣减库存等核心能力。

这个项目定位为面试展示型后端项目，重点体现：

- 基于 `Spring Security + JWT` 的无状态认证
- 基于 `MyBatis-Plus` 的数据访问与逻辑删除
- 基于 `RabbitMQ` 的下单后异步扣减库存
- 基于 `Redis` 的缓存能力封装
- 清晰的分层结构：`controller / service / mapper / entity / dto`

## 技术栈

- Java 21
- Spring Boot 3.5.13
- Spring Security
- MyBatis-Plus 3.5.5
- MySQL 8.x
- Redis
- RabbitMQ
- JWT
- Knife4j / OpenAPI 3
- Maven

## 项目结构

```text
src/main/java/com/shixi
├─ common      通用返回、异常处理、错误码
├─ config      Security、Redis、RabbitMQ、MyBatis-Plus 配置
├─ controller  认证、商品、订单接口
├─ dto         请求/响应对象
├─ entity      实体类
├─ mapper      MyBatis-Plus Mapper
├─ mq          订单消息生产者 / 消费者
├─ security    JWT 工具类与过滤器
└─ service     业务服务

src/main/resources
├─ application.yaml
├─ application-local.yml
└─ db/init.sql
```

## 功能模块

### 1. 用户与认证

- 用户注册
- 用户登录
- 获取当前登录用户信息
- JWT Token 鉴权

### 2. 角色权限

- 用户与角色关联
- 管理员接口基于 `@PreAuthorize` 控制访问

### 3. 商品管理

- 商品分页查询
- 商品详情查询
- 管理员创建商品
- 管理员修改商品
- 管理员删除商品

### 4. 订单管理

- 创建订单
- 查询我的订单
- 查询订单详情
- 取消订单
- 支付订单
- 确认收货
- 管理员发货
- 管理员查询全部订单

### 5. 异步库存处理

- 订单创建成功后发送 RabbitMQ 消息
- 消费消息后异步扣减商品库存并累计销量

## 数据库设计

当前项目采用简化版电商表结构，适合面试展示和功能演示：

- `sys_user`：用户表
- `sys_role`：角色表
- `sys_user_role`：用户角色关联表
- `pms_product`：商品表
- `oms_order`：订单表
- `oms_order_item`：订单明细表

初始化脚本位置：

`src/main/resources/db/init.sql`

默认会初始化：

- 管理员账号：`admin`
- 初始密码：`admin123`
- 初始角色：`ADMIN`、`USER`
- 几条商品测试数据

## 本地启动

### 1. 环境准备

请先准备以下运行环境：

- JDK 21
- Maven 3.9+
- MySQL 8.x
- Redis
- RabbitMQ

### 2. 初始化数据库

先创建数据库并执行初始化脚本：

```sql
source src/main/resources/db/init.sql
```

或者直接在数据库工具中执行 `init.sql`。

### 3. 修改本地配置

本地配置文件：

`src/main/resources/application-local.yml`

需要确认以下配置是否和你的本机一致：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lin_code?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: 123456

  data:
    redis:
      host: localhost
      port: 6379
      password:

  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

jwt:
  secret: lin-code-jwt-secret-key-must-be-at-least-256-bits-long-for-hs256
```

### 4. 启动项目

使用 Maven：

```bash
mvn spring-boot:run
```

或直接运行启动类：

`com.shixi.LinCodeApplication`

### 5. 访问文档

启动成功后默认端口为 `8123`。

接口文档地址：

- `http://localhost:8123/doc.html`
- `http://localhost:8123/swagger-ui.html`

## 认证说明

登录成功后会返回 JWT Token。

后续访问受保护接口时，请在请求头中携带：

```http
Authorization: Bearer <token>
```

放行接口：

- `POST /api/auth/register`
- `POST /api/auth/login`
- 文档相关接口

其余接口默认都需要登录后访问。

## 核心接口概览

### 认证模块

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/info`

### 商品模块

- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`

### 订单模块

- `GET /api/orders`
- `GET /api/orders/{id}`
- `POST /api/orders`
- `POST /api/orders/{id}/cancel`
- `POST /api/orders/{id}/pay`
- `POST /api/orders/{id}/receive`
- `POST /api/orders/{id}/delivery`
- `GET /api/orders/admin`

## 请求示例

### 1. 登录

```json
{
  "username": "admin",
  "password": "admin123"
}
```

### 2. 创建商品

```json
{
  "name": "示例商品",
  "description": "商品描述",
  "price": 199.00,
  "originalPrice": 299.00,
  "stock": 100,
  "image": "https://example.com/demo.jpg",
  "categoryId": 1,
  "status": 1
}
```

### 3. 创建订单

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ],
  "receiverName": "张三",
  "receiverPhone": "13800138000",
  "receiverAddress": "上海市浦东新区XX路100号",
  "remark": "请尽快发货"
}
```

## 业务说明

### 订单状态

- `0`：待支付
- `1`：已支付
- `2`：已发货
- `3`：已完成
- `4`：已取消

### 商品状态

- `0`：下架
- `1`：上架

### 逻辑删除

用户、角色、商品、订单使用 MyBatis-Plus 逻辑删除字段 `deleted` 控制数据状态。

## 项目亮点

- 使用 JWT 实现无状态认证，适合前后端分离场景
- 使用 `@PreAuthorize` 控制管理员权限
- 订单明细冗余商品名称、图片、价格，保留历史订单快照
- 使用 RabbitMQ 解耦下单与库存扣减流程
- 使用统一返回结构和全局异常处理，便于前端接入
- 使用 MyBatis-Plus 分页与逻辑删除，开发效率较高

## 面试可讲点

- 为什么选择 JWT 而不是 Session
- 为什么订单明细要保留商品快照字段
- 为什么库存扣减放到消息队列异步处理
- 逻辑删除在后台管理系统中的适用场景
- 当前设计是简化版模型，优先保证主流程闭环，便于演示和扩展

## 当前可优化点

- 商品分类表尚未补齐，`category_id` 目前预留
- 订单与订单明细可继续补外键和更多索引
- 库存扣减目前是基础实现，还可以进一步增强并发一致性
- 可继续补充单元测试、集成测试和接口测试

## 许可证

仅用于学习、练习和面试展示。

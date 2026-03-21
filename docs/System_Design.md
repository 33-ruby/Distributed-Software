# 商品库存与秒杀系统设计文档 (System Design)

---

## 1. 项目概述
本系统是一个基于微服务架构的高并发秒杀系统。核心目标是在大流量冲击下，保证系统的**高可用性**和**数据一致性**（防止超卖），并提供流畅的用户体验。

---

## 2. 系统架构图
系统采用前后端分离及微服务拆分模式：

* **流量接入层**：Nginx 负责负载均衡。
* **网关层 (Spring Cloud Gateway)**：统一鉴权、限流、请求分发。
* **核心服务层**：
    * **User Service (用户服务)**：处理登录、注册、分布式 Session 校验。
    * **Product Service (商品服务)**：展示秒杀商品列表及详情。
    * **Order Service (订单服务)**：处理下单逻辑、订单状态维护。
    * **Stock Service (库存服务)**：处理库存扣减、数据同步。
* **数据层**：
    * **Redis**：缓存秒杀商品库存，利用 Lua 脚本实现原子性扣减。
    * **MySQL**：核心数据的持久化存储（订单、用户、商品）。
    * **RabbitMQ**：异步下单，实现流量削峰。

---

## 3. 数据库设计 (ER 图)

### 3.1 用户表 (`t_user`)
| 字段名 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | BigInt | 用户 ID |
| `nickname` | Varchar | 昵称 |
| `password` | Varchar | 加密后的密码 |
| `salt` | Varchar | 混淆盐值 |
| `register_date` | DateTime | 注册时间 |

### 3.2 商品表 (`t_goods`)
| 字段名 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | BigInt | 商品 ID |
| `goods_name` | Varchar | 商品名称 |
| `goods_price` | Decimal | 商品原价 |
| `goods_stock` | Integer | 商品总库存 |

### 3.3 秒杀商品表 (`t_seckill_goods`)
| 字段名 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | BigInt | 秒杀关联 ID |
| `goods_id` | BigInt | 关联商品 ID |
| `seckill_price` | Decimal | 秒杀价格 |
| `stock_count` | Integer | 秒杀剩余库存 |
| `start_date` | DateTime | 秒杀开始时间 |
| `end_date` | DateTime | 秒杀结束时间 |

### 3.4 订单表 (`t_order`)
| 字段名 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | BigInt | 订单 ID |
| `user_id` | BigInt | 用户 ID |
| `goods_id` | BigInt | 商品 ID |
| `status` | TinyInt | 0-未支付, 1-已支付, 2-已取消 |
| `create_date` | DateTime | 下单时间 |

---

## 4. 核心接口 API (RESTful)

### 4.1 用户服务
* `POST /user/login`：验证账号密码，成功后返回 `Token`。
* `POST /user/register`：新用户注册。

### 4.2 商品服务
* `GET /product/list`：查询当前所有秒杀商品。
* `GET /product/{id}`：获取商品详情及秒杀倒计时。

### 4.3 秒杀服务
* `POST /seckill/do`：执行秒杀请求。

---

## 5. 关键技术选型说明

1.  **Spring Boot 3 + JDK 21**：利用新特性提升并发处理能力。
2.  **Redis 预扣库存**：防止流量直接冲击 MySQL。
3.  **分布式 Session**：将用户信息存入 Redis 实现跨服务登录状态共享。
4.  **接口限流 (Sentinel)**：防止恶意刷接口。
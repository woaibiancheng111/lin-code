# 单元测试运行指南

## 运行所有测试

### 方式一：使用 IDEA 图形界面
1. 右键点击 `src/test/java` 目录
2. 选择 `Run 'All Tests'`

### 方式二：使用 Maven 命令
```bash
# 在 PowerShell 中执行
cd E:\code\lin-code
.\mvnw.cmd clean test
```

### 方式三：运行单个测试类
右键点击某个测试类（如 `UserServiceImplTest`），选择 `Run`

---

## 测试覆盖范围

| 测试类 | 测试数量 | 覆盖功能 |
|--------|---------|---------|
| UserServiceImplTest | 10 | 注册、登录、用户查询、异常场景 |
| ProductServiceImplTest | 11 | 商品 CRUD、分页查询、库存扣减 |
| JwtUtilsTest | 7 | Token 生成、验证、解析 |
| **总计** | **28** | **核心业务逻辑** |

---

## 测试设计说明

### 1. 使用 H2 内存数据库
- 不依赖外部 MySQL
- 每个测试方法执行前自动初始化数据
- 测试配置文件：`src/test/resources/application-test.yaml`

### 2. Given-When-Then 模式
所有测试用例遵循标准的三段式结构：
```java
// given - 准备测试数据
// when - 执行被测试的方法
// then - 断言验证结果
```

### 3. 测试场景覆盖
- ✅ 正常流程（Happy Path）
- ✅ 边界条件（库存不足、用户不存在等）
- ✅ 异常场景（密码错误、用户禁用等）
- ✅ 数据验证（字段值、状态码等）

---

## 后续可以补充的测试

1. **OrderService 测试** - 需要 Mock RabbitMQ
2. **Controller 层测试** - 使用 MockMvc 测试接口
3. **集成测试** - 连接真实的 MySQL 和 Redis
4. **并发测试** - 库存扣减的线程安全

---

## 注意事项

⚠️ **运行测试前需要解决以下问题**：

1. **Redis 和 RabbitMQ 未启动**
   - 测试配置中禁用了这些组件
   - 如果测试报错，检查是否有代码强制依赖这些服务

2. **UserRoleMapper 依赖**
   - 测试数据中需要确保角色关联表有数据
   - 检查 `UserRoleMapper.selectRoleCodesByUserId` 的 SQL 是否正确

3. **如果测试失败**
   - 查看 IDEA 的 Test Runner 输出
   - 检查是否有 SQL 语法问题（H2 和 MySQL 有差异）

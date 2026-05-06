# CODING\_STANDARDS �?编码规范手册

> 本文档是 ADR-001 的实施细节补充，供人阅读（尤其是新成�?onboarding）。AI 编程指令见项目根目录 
>
> `.cursorrules`
>
> �?


***

## 一、后端编码规�?
### 1.1 包与命名



| 规范�?          | 要求                                                                        |
| ------------- | ------------------------------------------------------------------------- |
| 根包            | `com.by.microservices.{模块名}`                                              |
| Entity        | `domain/entity/` 下，类名首字母大写（�?`User.java`�?                                |
| Repository 接口 | `domain/repository/` 下，纯接口（无任何框架注解），命名后缀 `Repository`                     |
| DTO           | `application/dto/` 下，按用途添加后缀：`Command`（命令）、`Query`（查询）、`Response`（响应）     |
| Controller    | `interfaces/rest/` 下，命名后缀 `Controller`                                    |
| 应用服务          | `application/service/` 下，命名后缀 `ApplicationService`                        |
| 用例实现          | `application/usecase/` 下，命名前缀 `{业务动作} + UseCase`（如 `RegisterUserUseCase`�?|

### 1.2 DTO 规范



* 优先使用 Java 16+ `record`（不可变对象），复杂场景辅以 Lombok `@Getter @Builder`（禁�?`@Data` 避免 Setter�?
* 必须添加 JSR-303 校验注解（`@NotBlank`、`@NotNull`、`@Email`、`@Size` 等），明确字段约�?
* 禁止使用 `Map`、`JSONObject` 或无类型对象接收 Controller 参数

* 字段命名采用小驼峰（�?JSON 序列化保持一致）



```
// �?正确（record 方式，推荐）

public record RegisterUserCommand(

&#x20;   @NotBlank(message = "邮箱不能为空")

&#x20;   @Email(message = "邮箱格式不正�?)

&#x20;   String email,

&#x20;   @NotBlank(message = "密码不能为空")

&#x20;   @Size(min = 8, max = 32, message = "密码长度必须�?8-32 位之�?)

&#x20;   String password,

&#x20;   @NotBlank(message = "用户名不能为�?)

&#x20;   @Size(min = 2, max = 20, message = "用户名长度必须在 2-20 位之�?)

&#x20;   String username

) {}

// �?正确（复杂场�?Lombok 方式�?
@Getter

@Builder

public class UpdateUserInfoCommand {

&#x20;   @NotNull(message = "用户ID不能为空")

&#x20;   private final Long userId;

&#x20;   @Size(max = 20, message = "昵称长度不能超过 20 �?)

&#x20;   private final String nickname;

&#x20;   @Pattern(regexp = "^1\[3-9]\\\d{9}\$", message = "手机号格式不正确")

&#x20;   private final String phone;

}

// �?错误（无类型约束�?
public void register(@RequestBody Map> request) { ... }

// �?错误（缺少校验注解）

public record LoginCommand(String username, String password) {}
```

### 1.3 响应格式



* 全局统一响应包装�?\`ApiResponse 结构固定�?


```
{

&#x20; "code": 200,    // 状态码�?00 成功，非 200 失败�?
&#x20; "message": "OK",// 提示信息（失败时返回具体原因�?
&#x20; "data": {}      // 响应数据（成功时返回，失败时可为 null�?
}
```



* 错误码统一使用全局枚举 `ErrorCode`，禁止硬编码数字

* 通过 `GlobalExceptionHandler` 统一捕获异常并返回标准格式，禁止 Controller 中手动捕获后自定义返�?


```
// 全局响应类示�?
@Getter

public class ApiResponse> {

&#x20;   private final int code;

&#x20;   private final String message;

&#x20;   private final T data;

&#x20;   // 成功响应（带数据�?
&#x20;   public static \<T> success(T data) {

&#x20;       return new ApiResponse.getCode(), ErrorCode.SUCCESS.getMessage(), data);

&#x20;   }

&#x20;   // 成功响应（无数据�?
&#x20;   public static \<T> ApiResponse success() {

&#x20;       return success(null);

&#x20;   }

&#x20;   // 失败响应

&#x20;   public static > ApiResponse(ErrorCode errorCode) {

&#x20;       return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null);

&#x20;   }

}

// 全局异常处理器示�?
@RestControllerAdvice

public class GlobalExceptionHandler {

&#x20;   // 参数校验异常

&#x20;   @ExceptionHandler(MethodArgumentNotValidException.class)

&#x20;   public ApiResponseException(MethodArgumentNotValidException e) {

&#x20;       String message = e.getBindingResult().getFieldErrors().stream()

&#x20;               .map(FieldError::getDefaultMessage)

&#x20;               .collect(Collectors.joining("�?));

&#x20;       return ApiResponse.fail(ErrorCode.PARAM\_VALIDATION\_FAILED.setMessage(message));

&#x20;   }

&#x20;   // 业务异常

&#x20;   @ExceptionHandler(BusinessException.class)

&#x20;   public ApiResponse handleBusinessException(BusinessException e) {

&#x20;       return ApiResponse.fail(e.getErrorCode());

&#x20;   }

}
```

### 1.4 对象映射



* 必须使用 MapStruct 进行对象转换（Entity �?DTO、VO 等），编译期生成代码，零运行时开销

* 禁止使用 `BeanUtils.copyProperties`（浅拷贝、无类型校验、性能差）

* MapStruct 接口放在 `infrastructure/mapper/struct/` 目录下，命名后缀 `Mapper`



```
// MapStruct 转换接口示例

@Mapper(componentModel = "spring")

public interface UserStructMapper {

&#x20;   UserStructMapper INSTANCE = Mappers.getMapper(UserStructMapper.class);

&#x20;   // Entity �?Response DTO

&#x20;   UserResponse toResponse(User user);

&#x20;   // Command �?Entity

&#x20;   @Mapping(target = "id", ignore = true) // ID 自增，忽略赋�?
&#x20;   @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")

&#x20;   @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")

&#x20;   @Mapping(target = "deleted", constant = "false")

&#x20;   User toEntity(RegisterUserCommand command);

}
```

### 1.5 方法长度与复杂度



| 方法类型                      | 最大行�?| 备注                       |
| ------------------------- | ---- | ------------------------ |
| 普通业务方法（Service / UseCase�?| 50 �?| 超出则拆分为私有方法或独立类           |
| 工具类方�?                    | 80 �?| 允许适当放宽，但需加详细注�?          |
| Controller 方法             | 20 �?| 仅做参数接收、权限校验、调用服务，不包含业务逻辑 |
| MyBatis Mapper 接口方法       | 1 �? | 仅定义方法签名，SQL 写在 XML �?    |



* 方法参数个数不超�?5 个，超出则封装为 DTO

* 禁止嵌套超过 3 层条件判断（if/else、for 循环），通过提前返回、策略模式优�?
### 1.6 注释规范



* **类注�?*：所�?public 类必须添�?Javadoc 注释，说明类职责、作者、创建日�?
* **方法注释**：所�?public 方法必须添加 Javadoc 注释，说明功能、参数含义、返回值、抛出异�?
* **字段注释**：DTO 校验注解�?`message` 已说明约束的，无需额外注释；复杂字段（如状态码）需补充说明

* **复杂逻辑注释**：私有方法或复杂业务逻辑（如算法、多条件判断）需添加行注释，说明设计意图

* **禁止事项**：禁止留�?`TODO`（转�?GitHub Issue 并标注链接）、`FIXME` 等临时注�?


```
/\*\*

&#x20;\* 用户注册用例实现

&#x20;\* 负责处理用户注册的核心业务逻辑：参数校验、密码加密、数据入库、发送注册事�?
&#x20;\*

&#x20;\* @author 开发者姓�?
&#x20;\* @date 2026-04-29

&#x20;\*/

@Service

public class RegisterUserUseCase {

&#x20;   private final UserRepository userRepository;

&#x20;   private final PasswordEncoder passwordEncoder;

&#x20;   private final EventPublisher eventPublisher;

&#x20;   /\*\*

&#x20;    \* 执行用户注册

&#x20;    \*

&#x20;    \* @param command 注册命令（包含邮箱、密码、用户名�?
&#x20;    \* @return 注册成功的用户ID

&#x20;    \* @throws BusinessException 当邮箱已被注册时抛出

&#x20;    \*/

&#x20;   public Long execute(RegisterUserCommand command) {

&#x20;       // 1. 校验邮箱是否已注册（业务约束：邮箱唯一�?
&#x20;       if (userRepository.existsByEmail(command.email())) {

&#x20;           throw new BusinessException(ErrorCode.EMAIL\_ALREADY\_REGISTERED);

&#x20;       }

&#x20;       // 2. 密码加密（BCrypt 加盐哈希�?
&#x20;       String encryptedPassword = passwordEncoder.encode(command.password());

&#x20;       // 3. 转换�?Entity 并保�?
&#x20;       User user = UserStructMapper.INSTANCE.toEntity(command);

&#x20;       user.setPassword(encryptedPassword);

&#x20;       User savedUser = userRepository.save(user);

&#x20;       // 4. 发送注册成功事件（异步通知其他服务�?
&#x20;       eventPublisher.publish(new UserRegisteredEvent(savedUser.getId(), savedUser.getEmail()));

&#x20;       return savedUser.getId();

&#x20;   }

}
```

### 1.7 禁止事项



* 禁止使用 `System.out.println`、`e.printStackTrace()`（统一使用 Logback 日志框架�?
* 禁止硬编码魔法数字、字符串（抽取为常量或枚举，常量类放�?`domain/constant/` 下）

* 禁止抛出未捕获的 `RuntimeException`（自定义业务异常 `BusinessException` 统一处理�?
* MyBatis 中禁止使�?`${...}` 拼接 SQL（仅�?`#{...}` 预编译，防止 SQL 注入�?
* 禁止�?`domain` 层引入任何框架依赖（�?Spring `@Component`、MyBatis `@Mapper` 等）

* 禁止使用 `static` 静态变量存储业务状态（易引发并发问题）

* 禁止�?Controller 中编写业务逻辑（仅做请求适配和响应包装）

### 1.8 MyBatis 规范



* Mapper 接口放在 `infrastructure/persistence/` 下，命名后缀 `Mapper`

* Mapper XML 文件放在 `src/main/resources/mapper/` 下，�?Mapper 接口同名，目录结构一�?
* SQL 语句必须添加注释，说明功能和参数含义

* 动�?SQL 优先使用 `<if>`、`<foreach>`，禁止字符串拼接

* 查询结果必须映射到实体类�?DTO，禁止返�?\`List\<Map

* 批量操作使用 \` 避免循环调用单条 SQL

* 分页查询必须使用 `PageHelper` �?MyBatis-Plus 分页插件，禁止手�?`LIMIT ? OFFSET ?`



```
\<!-- �?正确示例 -->

1.0" encoding="UTF-8"?>

\<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"&#x20;

"http://mybatis.org/dtd/mybatis-3-mapper.dtd">

\="com.by.microservices.user.infrastructure.persistence.UserMapper">

&#x20;   列表（分页） -->

&#x20;   UserList" resultType="com.by.microservices.user.application.dto.UserResponse">

&#x20;       SELECT

&#x20;           id AS userId,

&#x20;           username,

&#x20;           email,

&#x20;           nickname,

&#x20;           created\_at AS createdAt

&#x20;       FROM tb\_user

&#x20;       \>

&#x20;           deleted = 0

&#x20;           \="username != null and username != ''">

&#x20;               AND username LIKE CONCAT('%', #{username}, '%')

&#x20;             null and email != ''">

&#x20;               AND email = #{email}

&#x20;           \>

&#x20;      &#x20;

&#x20;       ORDER BY created\_at DESC

&#x20;  &#x20;
```



***

## 二、前端编码规�?
### 2.1 API 调用规范



* API 接口统一放在 `src/api/` 目录下，按业务模块划分文件（�?`user.ts`、`order.ts`�?
* 所�?API 函数返回 `PromiseResponse�?`@microservices/types\` 导入（禁止手动定义）

* 统一处理请求拦截器（添加 Token）、响应拦截器（统一错误处理�?
* 函数命名采用小驼峰，前缀明确业务动作（`get`/`create`/`update`/`delete`�?


```
// �?正确示例

import type { User, UserQueryParams, PageResponse } from '@microservices/types';

import { request } from '@/utils/request'; // 封装后的请求工具（含拦截器）

/\*\*

&#x20;\* 查询用户列表（分页）

&#x20;\* @param params 查询参数

&#x20;\*/

export const getUserList = async (

&#x20; params: UserQueryParams

): Promise\<ApiResponse\<PageResponse

&#x20; return request({

&#x20;   url: '/api/users',

&#x20;   method: 'GET',

&#x20;   params,

&#x20; });

};

/\*\*

&#x20;\* 创建用户

&#x20;\* @param data 用户创建参数

&#x20;\*/

export const createUser = async (data: Omitid'>): PromiseResponse  return request({

&#x20;   url: '/api/users',

&#x20;   method: 'POST',

&#x20;   data,

&#x20; });

};
```

### 2.2 目录结构规范（admin /web 通用�?


```
src/

├── api/           # API 调用层（按模块划分）

├── views/         # 页面组件（路由对应页面）

�?  ├── user/      # 业务模块目录

�?  �?  ├── UserList.vue  # 列表�?
�?  �?  ├── UserDetail.vue # 详情�?
�?  �?  └── UserForm.vue  # 表单�?
├── components/     # 公共组件（全局复用�?
�?  ├── common/     # 通用组件（按钮、输入框等）

�?  └── business/   # 业务组件（用户卡片、订单表格等�?
├── stores/        # 状态管理（Pinia�?
�?  ├── userStore.ts # 用户相关状�?
�?  └── appStore.ts  # 应用全局状�?
├── router/        # 路由配置（按模块拆分�?
�?  ├── index.ts    # 路由入口

�?  └── modules/    # 模块路由

�?      ├── userRouter.ts

�?      └── orderRouter.ts

├── utils/         # 工具函数（格式化、校验等�?
├── styles/        # 全局样式（主题、重置样式等�?
└── types/         # 本地类型补充（共享类型优先从 packages/types 导入�?```

### 2.3 TypeScript 规范



* 禁止使用 `any` 类型（未知类型用 `unknown` + 类型守卫，临时兼容用 `// @ts-ignore` 并标注原因）

* 接口 / 类型定义优先使用 `interface`（可扩展），简单类型别名用 `type`

* 共享类型必须�?`@microservices/types` 导入，禁止手动复制或重复定义

* 组件 Props 必须通过 `defineProps` 定义并指定类型，禁止无类型传�?
* 事件通过 `defineEmits` 声明，明确参数类�?


```
ts">

// �?正确示例（组�?Props �?Emits�?
import type { User } from '@microservices/types';

const props = defineProps: User;

&#x20; isEditable: boolean;

}>();

const emit = defineEmits (e: 'edit', userId: number): void;

&#x20; (e: 'delete', userId: number): void;

}>();

// �?正确示例（类型守卫）

function formatUser(user: unknown): string {

&#x20; if (typeof user !== 'object' || user === null) {

&#x20;   return '未知用户';

&#x20; }

&#x20; const userObj = user as Partial return userObj.username || userObj.email || '未知用户';

}

// �?错误示例（禁�?any�?
function handleUserData(data: any) {

&#x20; console.log(data.username);

}
```

### 2.4 组件规范



* 优先使用组合�?API（`setup` 语法糖），禁止选项�?API

* 组件命名采用 PascalCase（如 `UserForm.vue`），�?Vue 官方推荐一�?
* 公共组件注册到全局，业务组件局部导�?
* 组件内部逻辑拆分�?`composables/` 目录（如 `useUserForm.ts`），保持组件简�?
* 禁止在模板中编写复杂表达式，提取为计算属性或方法



```
\>

&#x20; \="user-card">

&#x20;   -title">{{ user.username }}2>

&#x20;    class="card-content">

&#x20;     {{ user.email }}

&#x20;     时间：{{ formatDateTime(user.createdAt) }}

&#x20;        v-if="isEditable"

&#x20;     type="primary"

&#x20;     @click="emit('edit', user.id)"

&#x20;   \>

&#x20;     编辑

&#x20;   \>

&#x20;&#x20;

\</template>

&#x20;setup lang="ts">

import { formatDateTime } from '@/utils/date';

import type { User } from '@microservices/types';

const props = defineProps: User;

&#x20; isEditable: boolean;

}>();

const emit = defineEmits (e: 'edit', userId: number): void;

}>();

\>

/\* 局部样式，避免污染全局 \*/

.card-title {

&#x20; font-size: 18px;

&#x20; margin-bottom: 16px;

}

\---

\## 三、数据库规范

\### 3.1 Flyway 迁移规范

\- 脚本命名格式：\`V{版本号}\_\_{功能描述}.sql\`（版本号递增，双下划线分隔），示例：\`V1\_\_init\_user\_table.sql\`、\`V2\_\_add\_user\_nickname\_column.sql\`

\- 每个服务独立维护迁移脚本，放�?\`src/main/resources/db/migration/\` 目录�?
\- 迁移脚本必须是幂等的（多次执行无副作用），新增字段需指定默认�?
\- 禁止修改已提交到 Git 仓库且已在生产环境执行的迁移脚本（如需修改，新增迁移脚本）

\- 脚本中必须添加注释，说明迁移目的和变更内�?
\`\`\`sql

\-- V2\_\_add\_user\_nickname\_column.sql

\-- 为用户表添加昵称字段（默认空字符串，非必填）

ALTER TABLE tb\_user

ADD COLUMN nickname VARCHAR(20) NOT NULL DEFAULT '' COMMENT '用户昵称' AFTER username;

\-- 为昵称添加索引（优化查询�?
CREATE INDEX idx\_tb\_user\_nickname ON tb\_user(nickname);
```

### 3.2 表设计规�?


| 规范�? | 要求                                                                               | 示例                                                                                                                                                                          |
| ---- | -------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 表名   | 全小写，下划线分隔，前缀 `tb_` + 业务模块�?                                                      | `tb_user`（用户表）、`tb_user_role`（用户角色关联表�?                                                                                                                                     |
| 字段�? | 全小写，下划线分隔，语义明确                                                                   | `user_id`（用�?ID）、`login_time`（登录时间）                                                                                                                                         |
| 主键   | 统一命名 `id`，类�?`BIGINT AUTO_INCREMENT`（自增主键）                                       | `id BIGINT AUTO_INCREMENT PRIMARY KEY`                                                                                                                                      |
| 时间�? | 必须包含 `created_at`（创建时间）、`updated_at`（更新时间），类�?`DATETIME`，默认�?`CURRENT_TIMESTAMP` | `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'`、`updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'` |
| 软删�? | 统一使用 `deleted` 字段，类�?`TINYINT`�? = 未删除，1 = 已删除），默认�?0                            | `deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除�?=否，1=是）'`                                                                                                                |
| 字段类型 | 按业务场景选择最小可行类型（如手机号�?`CHAR(11)` 而非 `VARCHAR(20)`�?                                | 用户名：`VARCHAR(20)`、密码哈希：`CHAR(60)`（BCrypt 加密后固定长度）                                                                                                                           |
| 注释   | 表和字段必须添加 `COMMENT` 注释，说明用�?                                                      | `COMMENT '用户表（存储用户基础信息�?`                                                                                                                                                   |
| 索引   | 按查询频率添加索引，避免全表扫描；联合索引遵循「最左前缀原则�?                                                 | 登录查询：`INDEX idx_tb_user_email (email)`；列表筛选：`INDEX idx_tb_user_status_create_time (status, created_at)`                                                                     |

### 3.3 SQL 编写规范



* 关键字大写（`SELECT`、`FROM`、`WHERE`、`JOIN` 等），表名和字段名小写，区分大小写提升可读�?
* 多表关联查询必须使用表别名，避免字段歧义

* 禁止 `SELECT *`，明确指定需要查询的字段（减少数据传输、避免字段变更影响）

* `WHERE` 条件中优先使用索引字段，避免使用 `!=`、`NOT IN`、`IS NULL` 等导致索引失效的操作

* 分页查询必须指定 `ORDER BY`（避免分页结果不一致），且排序字段需添加索引



```
\-- �?正确示例

SELECT

&#x20; u.id AS user\_id,

&#x20; u.username,

&#x20; u.email,

&#x20; r.name AS role\_name

FROM tb\_user u

LEFT JOIN tb\_user\_role ur ON u.id = ur.user\_id

LEFT JOIN tb\_role r ON ur.role\_id = r.id

WHERE

&#x20; u.deleted = 0

&#x20; AND u.status = 1

&#x20; AND u.created\_at >= '2026-01-01'

ORDER BY u.created\_at DESC

LIMIT 10 OFFSET 20;

\-- �?错误示例（SELECT \* + �?ORDER BY + 索引失效�?
SELECT \* FROM tb\_user WHERE status != 0 LIMIT 10 OFFSET 20;
```



***

## 四、Dockerfile 规范

### 4.1 通用要求



* 所有服务的 Dockerfile 放在 `deployments/` 目录下（�?`apps/user-service/deployments/Dockerfile`�?
* 采用多阶段构建（Build Stage + Runtime Stage），减小镜像体积

* 基础镜像优先选择 Alpine 版本（轻量），Java 服务使用 `eclipse-temurin:21-jre-alpine`（仅�?JRE，不�?JDK�?
* 容器内禁止使�?root 用户运行服务，创建专用非 root 用户

* 暴露服务端口（`EXPOSE` 指令），注明端口用�?
### 4.2 后端服务 Dockerfile 示例



```
\# 第一阶段：构建（Maven 构建环境�?
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

\# 设置工作目录

WORKDIR /app

\# 复制 pom.xml 和依赖文件，缓存依赖（加速构建）

COPY pom.xml .

COPY src ./src

\# 构建 Jar 包（跳过测试，生产构建需移除 -DskipTests�?
RUN mvn clean package -DskipTests -U

\# 第二阶段：运行（仅含 JRE，减小镜像体积）

FROM eclipse-temurin:21-jre-alpine

\# 创建�?root 用户（安全最佳实践）

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

\# 设置工作目录

WORKDIR /app

\# 从构建阶段复�?Jar �?
COPY --from=builder /app/target/\*.jar app.jar

\# 授权给非 root 用户

RUN chown -R appuser:appgroup /app

\# 切换用户

USER appuser

\# 暴露服务端口（与 application.yml 一致）

EXPOSE 8081

\# 启动命令（指定环境变量，支持配置覆盖�?
ENTRYPOINT \["java", "-jar", "app.jar", "--spring.profiles.active=\${SPRING\_PROFILES\_ACTIVE:dev}"]
```

### 4.3 前端服务 Dockerfile 示例



```
\# 第一阶段：构建（Node 构建环境�?
FROM node:22.11.0-alpine AS builder

\# 设置工作目录

WORKDIR /app

\# 复制 package.json �?pnpm-lock.yaml

COPY package.json pnpm-lock.yaml ./

\# 安装 pnpm

RUN npm install -g pnpm

\# 安装依赖（缓存依赖）

RUN pnpm install

\# 复制源代�?
COPY . .

\# 构建生产版本

RUN pnpm build

\# 第二阶段：运行（Nginx 静态服务器�?
FROM nginx:alpine

\# 从构建阶段复制构建产物到 Nginx 静态目�?
COPY --from=builder /app/dist /usr/share/nginx/html

\# 复制 Nginx 配置文件（如需自定义端口、反向代理等�?
COPY deployments/nginx.conf /etc/nginx/conf.d/default.conf

\# 暴露端口

EXPOSE 80

\# 启动 Nginx

CMD \["nginx", "-g", "daemon off;"]
```



***

## 五、可观测规范

### 5.1 日志规范



* 日志框架：Logback + Logstash Encoder，输�?JSON 格式日志（便�?Loki 收集�?
* 日志级别：`ERROR`（错误）、`WARN`（警告）、`INFO`（重要信息）、`DEBUG`（调试信息），生产环境禁�?`DEBUG`

* 必须包含字段：`traceId`（链路追�?ID）、`spanId`（跨�?ID）、`timestamp`（时间戳）、`level`（日志级别）、`logger`（日志器名）、`message`（日志信息）、`serviceName`（服务名�?
* 敏感信息脱敏：密码、Token、手机号、身份证号等敏感信息必须脱敏后输出（如手机号显示�?`138****1234`�?
* 日志输出位置：标准输出（STDOUT），禁止写入本地文件（容器化部署日志通过 Docker 收集�?


```
配置示例 -->

\>

&#x20;    name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">

&#x20;       \="net.logstash.logback.encoder.LogstashEncoder">

&#x20;           Fields>{"serviceName":"user-service"}>

&#x20;           \>

&#x20;               timestamp \<level>level>

&#x20;               logger \<message>message>

&#x20;               \>traceId>

&#x20;               \>spanId>

&#x20;           \>

&#x20;      &#x20;

&#x20;  &#x20;

&#x20;   INFO">

&#x20;       ender-ref ref="CONSOLE" />

&#x20;   \</root>

&#x20;   框架日志级别调整 -->

&#x20;   .springframework" level="WARN" />

&#x20;   \="com.by.microservices" level="INFO" />
```

### 5.2 链路追踪规范



* 使用 OpenTelemetry 自动埋点，无需手动调用 `Tracer.getCurrentSpan()`

* 所有微服务、网关、消息队列必须接入链路追踪，确保 `traceId` 跨服务、跨线程、跨消息传�?
* 关键业务流程（如用户注册、订单创建）需添加自定义跨度（Span），标注业务动作

* 链路追踪数据导出�?Jaeger，通过 Jaeger UI 查看全链路耗时和调用关�?


```
// 自定义链路跨度示例（关键业务流程�?
@Service

public class OrderCreateUseCase {

&#x20;   private final Tracer tracer;

&#x20;   public Long execute(CreateOrderCommand command) {

&#x20;       // 创建自定义跨度，标注业务动作

&#x20;       Span span = tracer.spanBuilder("order-create-usecase")

&#x20;               .setAttribute("order.userId", command.userId())

&#x20;               .setAttribute("order.productCount", command.products().size())

&#x20;               .startSpan();

&#x20;       try (Scope scope = span.makeCurrent()) {

&#x20;           // 业务逻辑...

&#x20;           return orderId;

&#x20;       } catch (Exception e) {

&#x20;           span.recordException(e);

&#x20;           throw e;

&#x20;       } finally {

&#x20;           span.end();

&#x20;       }

&#x20;   }

}
```



***

## 六、Git 规范

### 6.1 分支策略



* `main`：主分支，生产环境代码，禁止直接提交

* `develop`：开发分支，集成测试通过后合并到 `main`

* `feature/{功能名}`：功能分支，�?`develop` 分支创建，完成后合并�?`develop`（如 `feature/user-register`�?
* `bugfix/{问题描述}`：bug 修复分支，从 `develop` 分支创建，修复后合并�?`develop`（如 `bugfix/email-validation`�?
* `hotfix/{问题描述}`：紧急修复分支，�?`main` 分支创建，修复后同时合并�?`main` �?`develop`（如 `hotfix/login-failure`�?
### 6.2 Commit 规范



* 格式：`{type}: {subject}`（类型：简短描述），示例：`feat: 实现用户注册功能`、`fix: 修复邮箱格式校验bug`

* **Type 类型**�?

  * `feat`：新功能

  * `fix`：bug 修复

  * `docs`：文档更新（�?README、编码规范）

  * `style`：代码格式调整（不影响功能，如缩进、空格）

  * `refactor`：代码重构（不影响功能，如方法拆分、变量重命名�?
  * `test`：添加或修改测试用例

  * `chore`：构建脚本、依赖更新等杂项

* **Subject 描述**�?

  * 首字母小写，结尾不加标点

  * 简洁明了（不超�?50 字符），说明「做了什么」而非「怎么做�?
### 6.3 提交约束



* 禁止提交 `.env` 文件、IDE 配置文件（如 `.idea`、`.vscode`）、构建产物（�?`target`、`dist`�?
* 禁止�?commit message 中包含密码、Token、密钥等敏感信息

* 每次提交只包含一个功能或一�?bug 修复，避免大杂烩提交

* 提交前必须运行本地测试和 Lint，确保代码符合规�?
### 6.4 PR/MR 规范



* 功能分支完成后，通过 Pull Request/Merge Request 合并到目标分�?
* PR/MR 标题格式�?Commit 一致（`{type}: {subject}`�?
* PR/MR 描述需说明功能点、测试场景、影响范�?
* 至少需�?1 名团队成�?Code Review 通过后才能合�?
* 合并前必须通过 CI 流水线（测试、Lint、构建）



***

## 七、测试规�?
### 7.1 测试覆盖率要�?


| 代码层级                          | 最低覆盖率 | 测试类型                            |
| ----------------------------- | ----- | ------------------------------- |
| Controller                    | 80%   | 单元测试（Mock 服务层）                  |
| Application Service / UseCase | 90%   | 单元测试（Mock Repository�?          |
| Repository                    | 95%   | 集成测试（Testcontainers + 真实 MySQL�?|
| 工具�?                          | 95%   | 单元测试（覆盖所有分支）                    |

### 7.2 测试场景覆盖

每个接口 / 方法必须覆盖以下场景�?


* 正常业务流程（参数合法、逻辑正确�?
* 参数校验失败（必填项为空、格式错误、长度超出限制）

* 权限不足 / 未认证（如未登录访问需授权接口�?
* 资源不存在（如查询不存在的用�?ID�?
* 业务异常（如余额不足、库存不够）

* 并发场景（如秒杀、并发更新同一资源�?
### 7.3 测试规范



* 使用 JUnit 5 + Mockito 进行单元测试，Testcontainers 进行集成测试

* 测试类命名：`{被测试类名}Test`（如 `UserServiceTest`�?
* 测试方法命名：`{测试场景} + Should + {预期结果}`（如 `registerWithValidParamShouldReturnUserId`�?
* 禁止使用 `@Disabled` 跳过测试（除非有特殊原因并标注说明）

* 禁止在测试中使用 `System.out` 替代断言（使�?AssertJ 断言库，语义更清晰）

* 测试数据使用随机生成（如 `RandomStringUtils`），禁止硬编码固定数�?


```
// �?正确示例（单元测试）

@ExtendWith(MockitoExtension.class)

public class RegisterUserUseCaseTest {

&#x20;   @Mock

&#x20;   private UserRepository userRepository;

&#x20;   @Mock

&#x20;   private PasswordEncoder passwordEncoder;

&#x20;   @InjectMocks

&#x20;   private RegisterUserUseCase registerUserUseCase;

&#x20;   @Test

&#x20;   void registerWithValidParamShouldReturnUserId() {

&#x20;       // 1. 准备测试数据

&#x20;       RegisterUserCommand command = new RegisterUserCommand(

&#x20;           "test@example.com",

&#x20;           "Password123",

&#x20;           "testuser"

&#x20;       );

&#x20;       Long expectedUserId = 1L;

&#x20;       User mockUser = User.builder()

&#x20;           .id(expectedUserId)

&#x20;           .email(command.email())

&#x20;           .username(command.username())

&#x20;           .password("encryptedPassword")

&#x20;           .build();

&#x20;       // 2. Mock 依赖行为

&#x20;       when(userRepository.existsByEmail(command.email())).thenReturn(false);

&#x20;       when(passwordEncoder.encode(command.password())).thenReturn("encryptedPassword");

&#x20;       when(userRepository.save(any(User.class))).thenReturn(mockUser);

&#x20;       // 3. 执行测试

&#x20;       Long actualUserId = registerUserUseCase.execute(command);

&#x20;       // 4. 断言结果

&#x20;       assertThat(actualUserId).isEqualTo(expectedUserId);

&#x20;       verify(userRepository).existsByEmail(command.email());

&#x20;       verify(passwordEncoder).encode(command.password());

&#x20;       verify(userRepository).save(any(User.class));

&#x20;   }

&#x20;   @Test

&#x20;   void registerWithDuplicateEmailShouldThrowException() {

&#x20;       // 1. 准备测试数据

&#x20;       RegisterUserCommand command = new RegisterUserCommand(

&#x20;           "duplicate@example.com",

&#x20;           "Password123",

&#x20;           "testuser"

&#x20;       );

&#x20;       // 2. Mock 依赖行为（邮箱已存在�?
&#x20;       when(userRepository.existsByEmail(command.email())).thenReturn(true);

&#x20;       // 3. 执行测试并断言异常

&#x20;       BusinessException exception = assertThrows(BusinessException.class, () -> {

&#x20;           registerUserUseCase.execute(command);

&#x20;       });

&#x20;       assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL\_ALREADY\_REGISTERED);

&#x20;       verify(userRepository).existsByEmail(command.email());

&#x20;       verifyNoMoreInteractions(passwordEncoder, userRepository);

&#x20;   }

}
```



***

## 八、六边形架构实施细则

### 8.1 分层依赖规则



* 依赖方向：`interfaces` �?`application` �?`domain` �?`infrastructure`（严格单向依赖）

* `domain` 层：核心层，零依赖（不依赖任何框架、其他层代码），仅包含纯 Java 代码（POJO、接口、枚举）

* `application` 层：依赖 `domain` 层，不依�?`infrastructure` �?`interfaces` 层，定义业务用例�?DTO

* `infrastructure` 层：依赖 `domain` 层，实现 `domain` 层定义的接口（如 Repository、EventPublisher），包含框架依赖和外部资源访�?
* `interfaces` 层：依赖 `application` 层，负责接收外部请求（HTTP、MQ）并转发给应用服务，不包含业务逻辑

### 8.2 包结构完整示例（user-service�?


```
com.by.microservices.user/

├── UserServiceApplication.java  # 应用入口（仅配置扫描，无业务逻辑�?
�?
├── domain/                      # 领域层（零依赖）

�?  ├── entity/

�?  �?  └── User.java            # 领域实体（纯 POJO，@Getter @Builder�?
�?  ├── vo/

�?  �?  └── Email.java           # 值对象（封装邮箱校验逻辑�?
�?  ├── event/

�?  �?  ├── UserRegisteredEvent.java  # 领域事件

�?  �?  └── EventPublisher.java       # 事件发布接口（定义在 domain�?
�?  ├── repository/

�?  �?  └── UserRepository.java       # 仓储接口（定义在 domain�?
�?  └── constant/

�?      └── UserStatus.java           # 领域常量（枚举）

�?
├── application/                 # 应用层（依赖 domain�?
�?  ├── service/

�?  �?  └── UserApplicationService.java  # 应用服务接口

�?  ├── usecase/

�?  �?  ├── RegisterUserUseCase.java     # 注册用例实现

�?  �?  └── QueryUserUseCase.java        # 查询用例实现

�?  └── dto/

�?      ├── RegisterUserCommand.java     # 命令 DTO

�?      ├── QueryUserQuery.java          # 查询 DTO

�?      └── UserResponse.java            # 响应 DTO

�?
├── infrastructure/              # 基础设施层（依赖 domain，实现其接口�?
�?  ├── persistence/

�?  �?  ├── UserMapper.java              # MyBatis Mapper 接口

�?  �?  └── UserRepositoryImpl.java      # 仓储接口实现（实�?domain.UserRepository�?
�?  ├── messaging/

�?  �?  └── RabbitMQEventPublisher.java  # 事件发布实现（实�?domain.EventPublisher�?
�?  ├── external/

�?  �?  └── SmsAdapter.java              # 外部服务适配器（如短信发送）

�?  └── config/

�?      ├── MyBatisConfig.java           # MyBatis 配置

�?      └── RabbitMQConfig.java          # RabbitMQ 配置

�?
└── interfaces/                  # 接口适配层（依赖 application�?
&#x20;   ├── rest/

&#x20;   �?  └── UserController.java          # REST 接口（调�?application 服务�?
&#x20;   └── consumer/

&#x20;       └── OrderCreatedConsumer.java    # MQ 消费者（调用 application 服务�?```

### 8.3 关键实施约束



* `domain` 层禁止出现任何框架注解（�?`@Entity`、`@Mapper`、`@Component`、`@Autowired` 等）

* `application` 层禁止直接访问数据库、MQ、外部服务（通过 `domain` 接口间接访问�?
* `infrastructure` 层的实现类必须通过 Spring 注入�?`application` 层，禁止 `new` 关键字创�?
* 跨服务通信必须通过 API 网关或消息队列，禁止服务间直接调�?
* 业务逻辑变更仅修�?`domain` �?`application` 层，`infrastructure` �?`interfaces` 层尽量不�?


***

## 九、安全配置实施细�?
### 9.1 JWT 配置规范



| 配置�?                | 要求                                          | 说明                                     |
| ------------------- | ------------------------------------------- | -------------------------------------- |
| 签名算法                | HS256（HMAC-SHA256�?                         | 对称加密算法，部署简单，适合内部微服务认�?                 |
| Token 传递方�?         | \`Authorization: Bearer  OAuth 2.0 标准，请求头携带 |                                        |
| `access_token` 有效�? | 30 分钟�?800 秒）                               | 短期有效，降低泄露风�?                           |
| `refresh_token` 有效�?| 7 �?                                        | 长期有效，用于刷�?`access_token`               |
| 密钥来源                | 环境变量 `JWT_SECRET`                           | 密钥长度 �?256 bits（即 32 �?ASCII 字符），禁止硬编�?|
| 密钥轮换                | �?90 天轮换一�?                                 | 轮换时需兼容旧密钥验证（双密钥共存过渡期�?                 |

#### JWT 配置代码示例（Spring Security�?


```
@Configuration

@EnableWebSecurity

public class JwtSecurityConfig extends WebSecurityConfigurerAdapter {

&#x20;   private final String jwtSecret;

&#x20;   private final long accessTokenExpireSeconds = 1800; // 30分钟

&#x20;   private final long refreshTokenExpireSeconds = 60 \* 60 \* 24 \* 7; // 7�?
&#x20;   // 从环境变量注入密钥，禁止硬编�?
&#x20;   public JwtSecurityConfig(@Value("\${jwt.secret}") String jwtSecret) {

&#x20;       this.jwtSecret = jwtSecret;

&#x20;       // 校验密钥长度

&#x20;       if (jwtSecret.length()  {

&#x20;           throw new IllegalArgumentException("JWT\_SECRET 长度必须�?2字符�?56bits�?);

&#x20;       }

&#x20;   }

&#x20;   // JWT 令牌生成�?
&#x20;   @Bean

&#x20;   public JwtTokenProvider jwtTokenProvider() {

&#x20;       return new JwtTokenProvider(

&#x20;           jwtSecret,

&#x20;           accessTokenExpireSeconds,

&#x20;           refreshTokenExpireSeconds

&#x20;       );

&#x20;   }

&#x20;   // 密码加密器（�?9.3 密码策略一致）

&#x20;   @Bean

&#x20;   public PasswordEncoder passwordEncoder() {

&#x20;       return new BCryptPasswordEncoder(10);

&#x20;   }

&#x20;   // 安全规则配置

&#x20;   @Override

&#x20;   protected void configure(HttpSecurity http) throws Exception {

&#x20;       http

&#x20;           .csrf(csrf -> csrf.disable()) // 微服务间调用禁用 CSRF

&#x20;           .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 无状�?
&#x20;           .authorizeRequests(auth -> auth

&#x20;               // 放行接口（与 9.4 一致）

&#x20;               .antMatchers("/actuator/health", "/actuator/prometheus").permitAll()

&#x20;               .antMatchers("/swagger-ui/\*\*", "/v3/api-docs/\*\*").permitAll()

&#x20;               .antMatchers(HttpMethod.POST, "/auth/login", "/auth/register").permitAll()

&#x20;               // 其他接口需认证

&#x20;               .anyRequest().authenticated()

&#x20;           )

&#x20;           // JWT 过滤器（验证 access\_token�?
&#x20;           .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

&#x20;   }

&#x20;   // JWT 认证过滤器（自定义实现）

&#x20;   @Bean

&#x20;   public JwtAuthenticationFilter jwtAuthenticationFilter() {

&#x20;       return new JwtAuthenticationFilter(jwtTokenProvider());

&#x20;   }

}
```

### 9.2 认证接口规范

#### 9.2.1 用户登录



* **请求方式**：POST

* **请求路径**：`/auth/login`

* **Content-Type**：application/json

* **请求�?*�?


```
{

&#x20; "username": "string", // 用户名（唯一标识�?
&#x20; "password": "string"  // 明文密码（传输过程需 HTTPS 加密�?
}
```



* **成功响应**�?00 OK）：



```
{

&#x20; "code": 200,

&#x20; "message": "OK",

&#x20; "data": {

&#x20;   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", // JWT 访问令牌

&#x20;   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", // 刷新令牌

&#x20;   "expiresIn": 1800 // accessToken 有效期（秒）

&#x20; }

}
```



* **失败响应**�?01 Unauthorized）：



```
{

&#x20; "code": 401,

&#x20; "message": "用户名或密码错误",

&#x20; "data": null

}
```

#### 9.2.2 用户注册



* **请求方式**：POST

* **请求路径**：`/auth/register`

* **Content-Type**：application/json

* **请求�?*（需满足 9.3 密码策略）：



```
{

&#x20; "username": "string", // 用户名（2-20位，唯一�?
&#x20; "password": "string", // 密码�?-32位，含字�?数字�?
&#x20; "email": "string"     // 邮箱（格式合法，唯一�?
}
```



* **成功响应**�?00 OK）：



```
{

&#x20; "code": 200,

&#x20; "message": "注册成功",

&#x20; "data": {

&#x20;   "userId": 1 // 注册生成的用户ID

&#x20; }

}
```



* **失败响应**�?00 Bad Request）：



```
{

&#x20; "code": 400,

&#x20; "message": "邮箱格式不正确；密码长度必须�?-32位之�?,

&#x20; "data": null

}
```

#### 9.2.3 刷新 Token



* **请求方式**：POST

* **请求路径**：`/auth/refresh-token`

* **Content-Type**：application/json

* **请求�?*�?


```
{

&#x20; "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

}
```



* **成功响应**�?00 OK）：



```
{

&#x20; "code": 200,

&#x20; "message": "OK",

&#x20; "data": {

&#x20;   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",

&#x20;   "expiresIn": 1800

&#x20; }

}
```

### 9.3 密码安全策略



| 配置�?  | 要求                | 实施方式                        |
| ----- | ----------------- | --------------------------- |
| 加密算法  | BCrypt            | 不可逆加盐哈希，自带随机�?              |
| 加密强度  | log rounds = 10   | 平衡安全性与性能（值越大越安全但耗时更长�?      |
| 最小长�? | �?8 �?            | DTO 层通过 `@Size(min = 8)` 校验 |
| 复杂度要�?| 必须包含字母 + 数字       | DTO 层通过 `@Pattern` 正则校验     |
| 存储格式  | 存储 BCrypt 加密后的哈希�?| 禁止存储明文�?MD5 等弱哈希            |
| 密码重置  | 生成临时链接（有效期 24 小时�?| 禁止直接返回原始密码                  |

#### 密码校验代码示例（DTO 层）



```
public record RegisterUserCommand(

&#x20;   @NotBlank(message = "用户名不能为�?)

&#x20;   @Size(min = 2, max = 20, message = "用户名长度必须在2-20位之�?)

&#x20;   String username,

&#x20;   @NotBlank(message = "密码不能为空")

&#x20;   @Size(min = 8, max = 32, message = "密码长度必须�?-32位之�?)

&#x20;   @Pattern(regexp = "^(?=.\*\[A-Za-z])(?=.\*\\\d).+\$", message = "密码必须包含字母和数�?)

&#x20;   String password,

&#x20;   @NotBlank(message = "邮箱不能为空")

&#x20;   @Email(message = "邮箱格式不正�?)

&#x20;   String email

) {}
```

#### 密码加密代码示例（Service 层）



```
@Service

public class AuthService {

&#x20;   private final PasswordEncoder passwordEncoder;

&#x20;   private final UserRepository userRepository;

&#x20;   private final JwtTokenProvider jwtTokenProvider;

&#x20;   // 构造函数注入（禁止 @Autowired�?
&#x20;   public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {

&#x20;       this.passwordEncoder = passwordEncoder;

&#x20;       this.userRepository = userRepository;

&#x20;       this.jwtTokenProvider = jwtTokenProvider;

&#x20;   }

&#x20;   public Long register(RegisterUserCommand command) {

&#x20;       // 1. 校验邮箱是否已注�?
&#x20;       if (userRepository.existsByEmail(command.email())) {

&#x20;           throw new BusinessException(ErrorCode.EMAIL\_ALREADY\_REGISTERED);

&#x20;       }

&#x20;       // 2. 密码加密（BCrypt 自动加盐�?
&#x20;       String encryptedPassword = passwordEncoder.encode(command.password());

&#x20;       // 3. 保存用户

&#x20;       User user = User.builder()

&#x20;           .username(command.username())

&#x20;           .password(encryptedPassword) // 存储加密后的哈希�?
&#x20;           .email(command.email())

&#x20;           .status(UserStatus.ACTIVE)

&#x20;           .createdAt(LocalDateTime.now())

&#x20;           .updatedAt(LocalDateTime.now())

&#x20;           .deleted(false)

&#x20;           .build();

&#x20;       userRepository.save(user);

&#x20;       return user.getId();

&#x20;   }

&#x20;   public JwtTokenPair login(LoginCommand command) {

&#x20;       // 1. 查询用户

&#x20;       User user = userRepository.findByUsername(command.username())

&#x20;           .orElseThrow(() -> new BusinessException(ErrorCode.USER\_NOT\_FOUND));

&#x20;       // 2. 校验密码（明文与哈希值比对）

&#x20;       if (!passwordEncoder.matches(command.password(), user.getPassword())) {

&#x20;           throw new BusinessException(ErrorCode.PASSWORD\_INCORRECT);

&#x20;       }

&#x20;       // 3. 生成 Token �?
&#x20;       return jwtTokenProvider.generateTokenPair(user);

&#x20;   }

}
```

### 9.4 接口放行规则（无需认证�?


| 请求方法 | 路径                     | 用�?          | 安全说明                      |
| ---- | ---------------------- | ------------ | ------------------------- |
| GET  | `/actuator/health`     | 服务健康检�?      | 无敏感信息，公开访问                |
| GET  | `/actuator/prometheus` | 监控指标采集       | 仅暴露非敏感指标，生产环境需限制 IP       |
| GET  | `/swagger-ui/**`       | API 文档页面     | 仅开�?/ 测试环境启用              |
| GET  | `/v3/api-docs/**`      | OpenAPI 协议文件 | 仅开�?/ 测试环境启用              |
| POST | `/auth/login`          | 用户登录         | 公开访问，需 HTTPS 加密           |
| POST | `/auth/register`       | 用户注册         | 公开访问，需参数校验                |
| POST | `/auth/refresh-token`  | 刷新 Token     | 公开访问，需校验 refreshToken 有效�?|

### 9.5 安全红线（严格禁止）



1. **禁止�?JWT 中存放敏感信�?*：包括密码、身份证号、手机号、银行卡号等，仅允许存放用户 ID、用户名等非敏感标识

2. **禁止接口返回明文密码**：无论成�?/ 失败响应，均不得包含明文密码（如登录失败提示 “密码错误�?而非 “密�?123456 错误”）

3. **禁止日志输出敏感信息**：Token、密码、手机号等需脱敏后输出（�?Token 只保留前 6 位和�?4 位）

4. **禁止硬编码密�?*：JWT\_SECRET、数据库密码等必须从环境变量或配置中心读取，禁止写死在代�?/ 配置文件�?
5. **禁止�?HTTPS 传输**：生产环境所有接口必须通过 HTTPS 传输，防止数据窃�?
6. **禁止弱密码策�?*：不得降低密码长度、复杂度要求，不得使�?MD5、SHA-1 等弱哈希算法

7. **禁止直接暴露 Actuator 接口**：生产环境需通过 IP 白名单限�?Actuator 访问，禁止全网公开

#### 敏感信息脱敏示例（日志输出）



```
// �?正确（Token 脱敏�?
log.info("用户登录成功，userId: {}, token: {}\*\*\*\*{}",&#x20;

&#x20;   userId,&#x20;

&#x20;   token.substring(0, 6),&#x20;

&#x20;   token.substring(token.length() - 4)

);

// �?错误（输出完�?Token�?
log.info("用户登录成功，token: {}", token);
```



```
```

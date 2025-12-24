# 中医院管理系统 (TCM Hospital Management System)

基于 Spring Boot 和 MYSQL 的中医院管理系统，支持完整的医院管理功能，并集成了中医诊断图片处理功能。

## 功能特性

- ✅ **完整的CRUD操作** - 为所有实体提供增删改查功能
- ✅ **PostgreSQL数据库** - 支持复杂查询和数据关系
- ✅ **雪花算法ID生成** - 分布式唯一ID生成机制
- ✅ **类型安全** - 使用Java确保类型安全
- ✅ **高效性能** - 基于Spring Boot框架
- ✅ **完整的API接口** - 支持患者、医生、就诊、处方、药品管理全流程
- ✅ **中医诊断图片处理** - 支持舌象等中医诊断图片的上传、格式转换和尺寸调整
- ✅ **标准部署** - 支持本地和服务器标准部署方式

## 中医诊断图片功能

### 图片处理功能
- **格式转换**：将所有上传的图片统一转换为PNG格式
- **尺寸调整**：当图片尺寸超过512×512时，自动从中心向边缘裁剪到512×512
- **小图处理**：小于512×512的图片保持原始尺寸

### API端点

#### 通用诊断图片
- `POST /api/diagnostic-images/upload` - 上传诊断图片
  - 参数：
    - `visitId`: 就诊记录ID
    - `file`: 图片文件
    - `imageType`: 图片类型 (tongue, face, pulse, 等) - 默认为tongue
    - `description`: 图片描述（可选）

- `GET /api/diagnostic-images/{id}` - 获取单个诊断图片信息
- `GET /api/diagnostic-images/visit/{visitId}` - 获取指定就诊记录的诊断图片列表
- `GET /api/diagnostic-images/type/{imageType}` - 按类型获取诊断图片列表
- `DELETE /api/diagnostic-images/{id}` - 删除诊断图片

#### 舌象图片专用端点
- `POST /api/visits/{visitId}/tongue-image` - 上传舌象图片
  - 参数：
    - `file`: 图片文件
    - `description`: 图片描述（可选）

### 上传示例

使用 cURL 上传舌象图片：
```bash
curl -X POST "http://localhost:58081/api/visits/{visitId}/tongue-image" \
     -F "file=@tongue_image.jpg" \
     -F "description=舌象诊断图片"
```

## 数据库实体

1. **患者 (Patient)** - 患者基本信息管理
2. **医生 (Doctor)** - 医生信息管理
3. **就诊记录 (Visit)** - 患者就诊记录，包含舌象图片路径字段
4. **处方 (Prescription)** - 中药处方管理
5. **处方明细 (PrescriptionDetail)** - 处方中具体药材信息
6. **药品库存 (MedicineInventory)** - 药品库存管理
7. **随访记录 (FollowUp)** - 治疗后跟踪记录
8. **病史 (MedicalHistory)** - 患者病史记录
9. **诊断图片 (DiagnosticImage)** - 中医诊断图片信息

## API 端点

### 系统健康检查
- `GET /health` - 服务健康状况检查

### 患者管理
- `GET /api/patients` - 获取所有患者
- `GET /api/patients/{id}` - 获取患者信息
- `POST /api/patients` - 创建患者
- `PUT /api/patients/{id}` - 更新患者信息
- `DELETE /api/patients/{id}` - 删除患者
- `GET /api/patients/id-card/{idCard}` - 根据身份证号查找患者
- `GET /api/patients/phone/{phone}` - 根据电话号码查找患者

### 医生管理
- `GET /api/doctors` - 获取所有医生
- `GET /api/doctors/{id}` - 获取医生信息
- `POST /api/doctors` - 创建医生
- `PUT /api/doctors/{id}` - 更新医生信息
- `DELETE /api/doctors/{id}` - 删除医生
- `GET /api/doctors/license/{licenseNumber}` - 根据执业资格证号查找医生
- `GET /api/doctors/phone/{phone}` - 根据电话号码查找医生

### 就诊记录
- `GET /api/visits` - 获取所有就诊记录
- `GET /api/visits/{id}` - 获取就诊记录
- `POST /api/visits` - 创建就诊记录
- `PUT /api/visits/{id}` - 更新就诊记录
- `DELETE /api/visits/{id}` - 删除就诊记录
- `GET /api/visits/patient/{patientId}` - 根据患者ID获取就诊记录
- `GET /api/visits/doctor/{doctorId}` - 根据医生ID获取就诊记录

### 中医诊断图片
- `POST /api/diagnostic-images/upload` - 上传诊断图片
- `GET /api/diagnostic-images/{id}` - 获取诊断图片
- `GET /api/diagnostic-images/visit/{visitId}` - 根据就诊ID获取诊断图片
- `GET /api/diagnostic-images/type/{imageType}` - 根据类型获取诊断图片
- `DELETE /api/diagnostic-images/{id}` - 删除诊断图片
- `POST /api/visits/{visitId}/tongue-image` - 上传舌象图片

## 安装和运行

### 前置要求

- Java 17
- PostgreSQL (12+)
- Maven

### 安装步骤

1. **安装 PostgreSQL**:
   ```bash
   sudo apt install postgresql postgresql-contrib  # Ubuntu/Debian
   # 或
   sudo pacman -S postgresql postgresql-docs       # Arch Linux
   ```

2. **启动并配置 PostgreSQL**:
   ```bash
   sudo systemctl start postgresql
   sudo systemctl enable postgresql
   ```

3. **创建数据库用户和数据库**:
   ```bash
   sudo -u postgres createuser --interactive admin
   sudo -u postgres createdb tcm_hospital
   sudo -u postgres psql -c "ALTER USER admin WITH PASSWORD 'password';"
   sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE tcm_hospital TO admin;"
   ```

4. **运行项目**:
   ```bash
   mvn spring-boot:run
   ```

## 配置

在 `src/main/resources/application.properties` 文件中配置以下参数：

```properties
# 服务器配置
server.port=58081

# PostgreSQL数据库配置
spring.datasource.url=jdbc:postgresql://localhost:5432/tcm_hospital
spring.datasource.username=admin
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA配置
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# 文件上传配置
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## 项目结构

```
tcm-hospital-management/
├── pom.xml                    # Maven 项目配置文件
├── README.md                 # 项目说明
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/tcm/
│   │   │       ├── TcmApplication.java          # 应用主类
│   │   │       ├── api/                         # API 控制器
│   │   │       ├── model/                       # 数据模型
│   │   │       ├── service/                     # 业务逻辑服务
│   │   │       ├── service/impl/                # 服务实现
│   │   │       ├── repository/                  # 数据访问层
│   │   │       ├── config/                      # 配置类
│   │   │       ├── dto/                         # 数据传输对象
│   │   │       └── utils/                       # 工具类
│   │   └── resources/
│   │       └── application.properties          # 应用配置
│   └── test/
├── uploads/                   # 图片上传存储目录
│   └── diagnostic_images/     # 诊断图片存储目录
└── target/
```

## 技术栈

- **后端框架**: Spring Boot 3.2
- **Web 框架**: Spring MVC
- **数据库**: PostgreSQL
- **数据访问**: Spring Data JPA
- **ID 生成算法**: 雪花算法 (Snowflake)
- **连接池**: HikariCP
- **序列化**: Jackson
- **图片处理**: Java AWT

## 扩展指南

如果需要添加新功能或API端点，遵循以下步骤：

1. 创建新的实体模型 (在 `model/` 包中)
2. 创建对应的 Repository 接口 (在 `repository/` 包中)
3. 创建 Service 接口和实现类 (在 `service/` 和 `service/impl/` 包中)
4. 创建 API 控制器 (在 `api/` 包中)
5. 如需要，更新 `DatabaseConfig` 配置

## 部署

### 生产环境部署
1. 配置生产环境的数据库连接
2. 设置适当的环境变量
3. 构建项目: `mvn clean package`
4. 运行应用: `java -jar target/tcm_project-1.0.0.jar`

### Docker 部署
使用提供的 `docker-compose.yml` 文件：
```bash
docker-compose up -d
```

## 许可证

本项目采用 MIT 许可证。
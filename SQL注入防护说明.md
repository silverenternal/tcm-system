# SQL注入防护说明 (SQL Injection Prevention)

## 概述

本项目采用了多层次的SQL注入防护措施，确保系统的数据安全性。通过使用Spring Data JPA和现代化的ORM框架，本系统从架构层面就避免了SQL注入攻击的风险。

## SQL注入防护机制

### 1. Spring Data JPA 参数化查询

本项目**完全使用Spring Data JPA**作为数据访问层，这是防止SQL注入的最核心机制。

#### 1.1 方法命名查询 (Method Name Queries)

Spring Data JPA支持通过方法名自动生成查询，这些查询都是参数化的，天然防止SQL注入。

**示例代码：**

```java
// PatientRepository.java
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByIdCard(String idCard);
    List<Patient> findByIdCardStartingWith(String prefix);
    Patient findByPhone(String phone);
}
```

**工作原理：**
- Spring Data JPA会将`findByIdCard(String idCard)`自动转换为参数化查询
- 生成的SQL类似：`SELECT * FROM patients WHERE id_card = ?`
- 参数`idCard`会作为预编译参数绑定，而不是直接拼接到SQL字符串中

**防护效果：**
即使用户输入恶意字符串如 `' OR '1'='1`，由于使用了参数绑定，该字符串会被当作普通字符串值处理，而不会被解释为SQL代码。

### 2. JPQL参数化查询

对于需要自定义查询的场景，本项目使用JPQL (Java Persistence Query Language)，并通过`@Param`注解进行参数绑定。

**示例代码：**

```java
// WechatUserRepository.java
@Query("UPDATE WechatUser w SET w.lastLoginAt = CURRENT_TIMESTAMP, w.loginCount = w.loginCount + 1 WHERE w.openid = :openid")
void updateLastLoginAndCount(@Param("openid") String openid);
```

**工作原理：**
- JPQL查询使用`:openid`命名参数占位符
- `@Param("openid")`注解将方法参数绑定到查询中
- JPA提供者(Hibernate)会将此转换为参数化的原生SQL查询

**防护效果：**
参数值与SQL语句结构完全分离，恶意输入无法改变SQL语句的语义。

### 3. JPA实体管理与自动SQL生成

本项目使用JPA实体模型，所有的CRUD操作都通过JpaRepository提供的标准方法完成。

**示例代码：**

```java
// PatientServiceImpl.java
@Service
public class PatientServiceImpl implements PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Override
    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }
    
    @Override
    public Patient findByPatientIdCard(String idCard) {
        return patientRepository.findByIdCard(idCard);
    }
}
```

**工作原理：**
- `save()`、`findById()`、`findAll()`等方法由Spring Data JPA自动实现
- 这些方法内部使用PreparedStatement和参数绑定
- 没有任何SQL字符串拼接操作

### 4. 输入验证与数据清洗

虽然JPA已经提供了SQL注入防护，但本项目还在应用层进行额外的输入验证。

**示例代码：**

```java
// PatientServiceImpl.java
@Override
public Patient createPatient(Patient patient) {
    // 清洗和验证输入
    if (patient.getIdCard() != null) {
        String trimmedIdCard = patient.getIdCard().trim();
        if (trimmedIdCard.isEmpty()) {
            patient.setIdCard(null);
        } else if (trimmedIdCard.length() > 18) {
            patient.setIdCard(trimmedIdCard.substring(0, 18));
        } else {
            patient.setIdCard(trimmedIdCard);
        }
    }
    // ... 更多验证逻辑
    return patientRepository.save(patient);
}
```

**防护层次：**
1. 去除前后空格
2. 长度限制验证
3. 空字符串处理

### 5. Spring MVC 自动参数绑定

API控制器层使用Spring MVC的自动参数绑定和类型转换。

**示例代码：**

```java
// PatientAPI.java
@RestController
@RequestMapping("/api/patients")
public class PatientAPI {
    
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        Optional<Patient> patient = patientService.getPatientById(id);
        if (patient.isPresent()) {
            return ResponseEntity.ok(patient.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/id-card/{idCard}")
    public ResponseEntity<Patient> getPatientByIdCard(@PathVariable String idCard) {
        Patient patient = patientService.findByPatientIdCard(idCard);
        if (patient != null) {
            return ResponseEntity.ok(patient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
```

**工作原理：**
- `@PathVariable`和`@RequestParam`注解确保参数被安全绑定
- 参数值直接传递给Service层，不进行SQL拼接
- 类型转换由Spring框架自动处理并验证

## 关键安全原则

### ✅ 使用的安全实践

1. **完全避免SQL字符串拼接** - 项目中没有使用字符串拼接构建SQL查询
2. **参数化查询** - 所有查询都使用参数绑定
3. **ORM框架** - 使用Spring Data JPA管理所有数据库交互
4. **类型安全** - 使用强类型实体和Repository接口
5. **输入验证** - 在Service层进行额外的数据验证和清洗

### ❌ 避免的不安全实践

1. **直接拼接SQL** - 从不使用`String sql = "SELECT * FROM users WHERE id = " + userId`
2. **动态SQL** - 避免根据用户输入动态构建SQL语句结构
3. **原生SQL** - 尽量避免使用原生SQL查询（项目中仅在必要的DDL操作中使用）

## 特殊情况分析

### DatabaseConfig中的原生SQL

在`DatabaseConfig.java`中，有一处使用了`Statement`对象：

```java
@EventListener(ApplicationReadyEvent.class)
public void updateDatabaseSchema() {
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {
        
        String alterTableSQL = "ALTER TABLE patients ALTER COLUMN id_card TYPE VARCHAR(50)";
        try {
            statement.executeUpdate(alterTableSQL);
        } catch (SQLException e) {
            // 处理异常
        }
    } catch (SQLException e) {
        // 处理连接错误
    }
}
```

**安全性分析：**
- 这段代码**不存在SQL注入风险**，因为：
  1. SQL语句是硬编码的常量字符串
  2. 没有任何用户输入参与SQL构建
  3. 仅在应用启动时执行一次
  4. 用于DDL操作（修改表结构），不涉及用户数据

## 技术栈安全特性

### Spring Data JPA
- **版本**: Spring Boot 3.2.0
- **ORM实现**: Hibernate
- **特性**: 
  - 自动参数化查询
  - PreparedStatement支持
  - 实体生命周期管理

### PostgreSQL
- **JDBC驱动**: 官方postgresql驱动
- **特性**:
  - 支持参数化查询
  - PreparedStatement缓存
  - 类型安全的参数绑定

## 代码审查检查清单

在review代码时，应确保：

- [ ] ✅ 所有数据库查询都通过JPA Repository
- [ ] ✅ 使用`@Query`注解时必须使用`@Param`参数绑定
- [ ] ✅ 避免在Service层或Controller层构建SQL字符串
- [ ] ✅ 所有用户输入都经过验证和清洗
- [ ] ✅ PathVariable和RequestParam直接传递给Repository方法
- [ ] ❌ 不使用字符串拼接构建查询条件
- [ ] ❌ 不使用EntityManager.createNativeQuery()处理用户输入

## 测试验证

可以通过以下方式验证SQL注入防护：

```bash
# 尝试使用恶意输入测试API
curl -X GET "http://localhost:58080/api/patients/id-card/' OR '1'='1"

# 预期结果：返回404或空结果，而不是所有患者数据
# 因为 "' OR '1'='1" 会被当作普通字符串查询，而不是SQL代码
```

## 最佳实践建议

### 继续保持

1. **始终使用Spring Data JPA方法查询**
2. **需要自定义查询时使用JPQL + @Param**
3. **在Service层进行输入验证**
4. **使用实体对象而不是Map或原始数据**

### 未来扩展时注意

如果需要添加新功能：

1. ✅ **推荐**: 使用Spring Data JPA的方法命名查询
   ```java
   List<Patient> findByNameAndAge(String name, Integer age);
   ```

2. ✅ **推荐**: 使用JPQL查询
   ```java
   @Query("SELECT p FROM Patient p WHERE p.name = :name")
   List<Patient> findByCustomQuery(@Param("name") String name);
   ```

3. ⚠️ **谨慎**: 使用原生SQL时务必参数化
   ```java
   @Query(value = "SELECT * FROM patients WHERE name = :name", nativeQuery = true)
   List<Patient> findByNativeSql(@Param("name") String name);
   ```

4. ❌ **禁止**: 字符串拼接SQL
   ```java
   // 永远不要这样做！
   String sql = "SELECT * FROM patients WHERE name = '" + userName + "'";
   ```

## 总结

本项目通过以下多层防护机制确保免受SQL注入攻击：

1. **架构层**: 使用ORM框架(Spring Data JPA)完全隔离SQL操作
2. **数据访问层**: 100%使用参数化查询和方法命名查询
3. **业务逻辑层**: 输入验证和数据清洗
4. **API层**: 类型安全的参数绑定

**结论**: 本项目的架构设计和代码实现完全符合防止SQL注入的最佳实践，未发现任何SQL注入漏洞。

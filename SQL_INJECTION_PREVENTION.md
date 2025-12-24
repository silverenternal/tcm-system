# SQL Injection Prevention Guide

## Overview

This project implements multi-layered SQL injection prevention mechanisms to ensure data security. By using Spring Data JPA and modern ORM frameworks, the system is architecturally designed to avoid SQL injection attack risks.

## SQL Injection Prevention Mechanisms

### 1. Spring Data JPA Parameterized Queries

This project **exclusively uses Spring Data JPA** for the data access layer, which is the core mechanism for preventing SQL injection.

#### 1.1 Method Name Queries

Spring Data JPA supports automatic query generation through method names, and these queries are parameterized by nature, preventing SQL injection.

**Code Example:**

```java
// PatientRepository.java
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByIdCard(String idCard);
    List<Patient> findByIdCardStartingWith(String prefix);
    Patient findByPhone(String phone);
}
```

**How it works:**
- Spring Data JPA automatically converts `findByIdCard(String idCard)` into a parameterized query
- Generated SQL is similar to: `SELECT * FROM patients WHERE id_card = ?`
- The `idCard` parameter is bound as a prepared statement parameter, not concatenated into the SQL string

**Protection Effect:**
Even if a user inputs a malicious string like `' OR '1'='1`, due to parameter binding, the string is treated as a literal string value rather than being interpreted as SQL code.

### 2. JPQL Parameterized Queries

For scenarios requiring custom queries, this project uses JPQL (Java Persistence Query Language) with parameter binding via the `@Param` annotation.

**Code Example:**

```java
// WechatUserRepository.java
@Query("UPDATE WechatUser w SET w.lastLoginAt = CURRENT_TIMESTAMP, w.loginCount = w.loginCount + 1 WHERE w.openid = :openid")
void updateLastLoginAndCount(@Param("openid") String openid);
```

**How it works:**
- JPQL query uses `:openid` as a named parameter placeholder
- `@Param("openid")` annotation binds the method parameter to the query
- The JPA provider (Hibernate) converts this to a parameterized native SQL query

**Protection Effect:**
Parameter values are completely separated from the SQL statement structure, preventing malicious input from altering the SQL semantics.

### 3. JPA Entity Management and Automatic SQL Generation

This project uses JPA entity models, and all CRUD operations are completed through standard methods provided by JpaRepository.

**Code Example:**

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

**How it works:**
- Methods like `save()`, `findById()`, `findAll()` are automatically implemented by Spring Data JPA
- These methods internally use PreparedStatement and parameter binding
- No SQL string concatenation operations

### 4. Input Validation and Data Sanitization

Although JPA already provides SQL injection protection, this project performs additional input validation at the application layer.

**Code Example:**

```java
// PatientServiceImpl.java
@Override
public Patient createPatient(Patient patient) {
    // Sanitize and validate input
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
    // ... more validation logic
    return patientRepository.save(patient);
}
```

**Protection Layers:**
1. Trim leading and trailing whitespace
2. Length limit validation
3. Empty string handling

### 5. Spring MVC Automatic Parameter Binding

The API controller layer uses Spring MVC's automatic parameter binding and type conversion.

**Code Example:**

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

**How it works:**
- `@PathVariable` and `@RequestParam` annotations ensure parameters are safely bound
- Parameter values are directly passed to the Service layer without SQL concatenation
- Type conversion is automatically handled and validated by the Spring framework

## Key Security Principles

### ✅ Secure Practices Used

1. **Complete avoidance of SQL string concatenation** - No SQL queries are built using string concatenation
2. **Parameterized queries** - All queries use parameter binding
3. **ORM framework** - Spring Data JPA manages all database interactions
4. **Type safety** - Strong-typed entities and Repository interfaces
5. **Input validation** - Additional data validation and sanitization at the Service layer

### ❌ Unsafe Practices Avoided

1. **Direct SQL concatenation** - Never use `String sql = "SELECT * FROM users WHERE id = " + userId`
2. **Dynamic SQL** - Avoid dynamically building SQL statement structures based on user input
3. **Native SQL** - Minimize use of native SQL queries (only used in necessary DDL operations)

## Special Case Analysis

### Native SQL in DatabaseConfig

In `DatabaseConfig.java`, there is one instance of `Statement` object usage:

```java
@EventListener(ApplicationReadyEvent.class)
public void updateDatabaseSchema() {
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {
        
        String alterTableSQL = "ALTER TABLE patients ALTER COLUMN id_card TYPE VARCHAR(50)";
        try {
            statement.executeUpdate(alterTableSQL);
        } catch (SQLException e) {
            // Handle exception
        }
    } catch (SQLException e) {
        // Handle connection error
    }
}
```

**Security Analysis:**
- This code **does not pose an SQL injection risk** because:
  1. The SQL statement is a hardcoded constant string
  2. No user input is involved in SQL construction
  3. Executes only once at application startup
  4. Used for DDL operations (table structure modification), not involving user data

## Technology Stack Security Features

### Spring Data JPA
- **Version**: Spring Boot 3.2.0
- **ORM Implementation**: Hibernate
- **Features**: 
  - Automatic parameterized queries
  - PreparedStatement support
  - Entity lifecycle management

### PostgreSQL
- **JDBC Driver**: Official postgresql driver
- **Features**:
  - Parameterized query support
  - PreparedStatement caching
  - Type-safe parameter binding

## Code Review Checklist

When reviewing code, ensure:

- [ ] ✅ All database queries go through JPA Repository
- [ ] ✅ When using `@Query` annotation, must use `@Param` parameter binding
- [ ] ✅ Avoid building SQL strings in Service or Controller layers
- [ ] ✅ All user inputs are validated and sanitized
- [ ] ✅ PathVariable and RequestParam are directly passed to Repository methods
- [ ] ❌ Do not use string concatenation to build query conditions
- [ ] ❌ Do not use EntityManager.createNativeQuery() to process user input

## Testing and Validation

You can verify SQL injection protection through:

```bash
# Test API with malicious input
curl -X GET "http://localhost:58081/api/patients/id-card/' OR '1'='1"

# Expected result: Returns 404 or empty result, not all patient data
# Because "' OR '1'='1" is treated as a regular string query, not SQL code
```

## Best Practice Recommendations

### Continue to Maintain

1. **Always use Spring Data JPA method queries**
2. **Use JPQL + @Param for custom queries**
3. **Perform input validation in the Service layer**
4. **Use entity objects rather than Maps or raw data**

### Considerations for Future Extensions

When adding new features:

1. ✅ **Recommended**: Use Spring Data JPA method naming queries
   ```java
   List<Patient> findByNameAndAge(String name, Integer age);
   ```

2. ✅ **Recommended**: Use JPQL queries
   ```java
   @Query("SELECT p FROM Patient p WHERE p.name = :name")
   List<Patient> findByCustomQuery(@Param("name") String name);
   ```

3. ⚠️ **Caution**: When using native SQL, always parameterize
   ```java
   @Query(value = "SELECT * FROM patients WHERE name = :name", nativeQuery = true)
   List<Patient> findByNativeSql(@Param("name") String name);
   ```

4. ❌ **Prohibited**: String concatenation for SQL
   ```java
   // Never do this!
   String sql = "SELECT * FROM patients WHERE name = '" + userName + "'";
   ```

## Summary

This project ensures protection against SQL injection attacks through the following multi-layered defense mechanisms:

1. **Architecture Layer**: Uses ORM framework (Spring Data JPA) to completely isolate SQL operations
2. **Data Access Layer**: 100% parameterized queries and method naming queries
3. **Business Logic Layer**: Input validation and data sanitization
4. **API Layer**: Type-safe parameter binding

**Conclusion**: The architectural design and code implementation of this project fully comply with best practices for preventing SQL injection, and no SQL injection vulnerabilities were found.

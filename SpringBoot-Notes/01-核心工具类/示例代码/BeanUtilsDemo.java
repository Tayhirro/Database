package com.example.demo.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * BeanUtils 使用示例
 * 演示 Spring 和 Hutool 两种 BeanUtils 的使用
 */
public class BeanUtilsDemo {

    // ==================== Spring BeanUtils ====================
    
    /**
     * 基本属性拷贝
     */
    public static void basicCopy() {
        UserEntity source = new UserEntity();
        source.setId(1L);
        source.setName("张三");
        source.setAge(25);
        
        UserDTO target = new UserDTO();
        BeanUtils.copyProperties(source, target);
        
        System.out.println("拷贝结果: " + target);
    }
    
    /**
     * 忽略某些属性
     */
    public static void copyWithIgnore() {
        UserEntity source = new UserEntity();
        source.setId(1L);
        source.setName("张三");
        source.setPassword("123456");
        
        UserDTO target = new UserDTO();
        // 忽略 id 和 password 字段
        BeanUtils.copyProperties(source, target, "id", "password");
        
        System.out.println("拷贝结果（忽略敏感字段）: " + target);
    }
    
    // ==================== Hutool BeanUtil ====================
    
    /**
     * 忽略 null 值拷贝
     * 用于部分更新场景
     */
    public static void copyIgnoreNull() {
        // 数据库中已存在的实体
        UserEntity existingEntity = new UserEntity();
        existingEntity.setId(1L);
        existingEntity.setName("张三");
        existingEntity.setAge(25);
        existingEntity.setEmail("zhangsan@example.com");
        
        // 用户提交的更新数据（部分字段为 null）
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setName("张三丰");
        // age 为 null，不更新
        // email 为 null，不更新
        
        // 使用 Hutool 的 CopyOptions 忽略 null 值
        CopyOptions options = CopyOptions.create()
            .setIgnoreNullValue(true);
        
        BeanUtil.copyProperties(updateDTO, existingEntity, options);
        
        System.out.println("更新后的实体: " + existingEntity);
        // 结果: id=1, name=张三丰, age=25, email=zhangsan@example.com
    }
    
    /**
     * 自定义字段映射
     */
    public static void copyWithFieldMapping() {
        SourceDTO source = new SourceDTO();
        source.setUserName("张三");
        source.setUserAge(25);
        
        TargetDTO target = new TargetDTO();
        
        CopyOptions options = CopyOptions.create()
            .setFieldMapping(new java.util.HashMap<String, String>() {{
                put("userName", "name");
                put("userAge", "age");
            }});
        
        BeanUtil.copyProperties(source, target, options);
        
        System.out.println("映射结果: " + target);
    }
    
    /**
     * 批量拷贝 List
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
        return sourceList.stream()
            .map(source -> {
                try {
                    T target = targetClass.getDeclaredConstructor().newInstance();
                    BeanUtils.copyProperties(source, target);
                    return target;
                } catch (Exception e) {
                    throw new RuntimeException("对象拷贝失败", e);
                }
            })
            .collect(Collectors.toList());
    }
    
    // ==================== 测试实体类 ====================
    
    public static class UserEntity {
        private Long id;
        private String name;
        private Integer age;
        private String email;
        private String password;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        @Override
        public String toString() {
            return "UserEntity{id=" + id + ", name='" + name + "', age=" + age + 
                   ", email='" + email + "', password='" + password + "'}";
        }
    }
    
    public static class UserDTO {
        private Long id;
        private String name;
        private Integer age;
        private String email;
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        @Override
        public String toString() {
            return "UserDTO{id=" + id + ", name='" + name + "', age=" + age + ", email='" + email + "'}";
        }
    }
    
    public static class UserUpdateDTO {
        private String name;
        private Integer age;
        private String email;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    public static class SourceDTO {
        private String userName;
        private Integer userAge;
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public Integer getUserAge() { return userAge; }
        public void setUserAge(Integer userAge) { this.userAge = userAge; }
    }
    
    public static class TargetDTO {
        private String name;
        private Integer age;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        
        @Override
        public String toString() {
            return "TargetDTO{name='" + name + "', age=" + age + "}";
        }
    }
    
    // ==================== 主方法测试 ====================
    
    public static void main(String[] args) {
        System.out.println("=== 1. 基本属性拷贝 ===");
        basicCopy();
        
        System.out.println("\n=== 2. 忽略敏感字段 ===");
        copyWithIgnore();
        
        System.out.println("\n=== 3. 忽略 null 值拷贝（部分更新） ===");
        copyIgnoreNull();
        
        System.out.println("\n=== 4. 自定义字段映射 ===");
        copyWithFieldMapping();
    }
}

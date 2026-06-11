# Jakarta Servlet — logback 的 Web 容器集成，桌面应用不需要
-dontwarn jakarta.servlet.**

# Jakarta Mail — logback 邮件通知，桌面应用不需要
-dontwarn jakarta.mail.**

# CGLIB — MyBatis 延迟加载代理，桌面应用不需要
-dontwarn net.sf.cglib.**

# Commons Logging — 可选日志门面
-dontwarn org.apache.commons.logging.**

# Log4j 1.x bridge — logback 的可选依赖
-dontwarn org.apache.log4j.**

# Log4j 2.x — 已使用 SLF4J
-dontwarn org.apache.logging.log4j.**

# Janino — logback 条件配置，不需要
-dontwarn org.codehaus.commons.compiler.**
-dontwarn org.codehaus.janino.**

# XZ 压缩 — logback 压缩选项，不需要
-dontwarn org.tukaani.xz.**

# MyBatis 动态代理引用
-dontwarn org.mybatis.**
-dontwarn org.apache.ibatis.**

# 保留 MyBatis XML 映射的实体类
-keep class org.zl.team.entity.** { *; }

# 保留 MyBatis Mapper 接口
-keep class org.zl.team.mapper.** { *; }

# 保留 Service 类
-keep class org.zl.team.service.** { *; }

# 保留配置类
-keep class org.zl.team.config.** { *; }
-keep class org.zl.team.util.** { *; }

# SQLite JDBC 驱动（反射加载）
-keep class org.sqlite.JDBC { *; }
-keep class org.sqlite.** { *; }

# SLF4J + Logback 服务提供者（ServiceLoader 反射加载）
-keep class ch.qos.logback.** { *; }

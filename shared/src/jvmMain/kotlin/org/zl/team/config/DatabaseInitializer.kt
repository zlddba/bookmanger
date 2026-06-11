package org.zl.team.config

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.Properties

object DatabaseInitializer {

    private var dbPath: String? = null

    /** 应用数据根目录（用户目录下，避免安装版无权限） */
    val dataDir: Path
        get() = Paths.get(System.getProperty("user.home"), ".bookmanager", "data")

    fun init() {
        loadConfig()
        ensureDataDir()
        initDatabase()
    }

    /** 测试用：指定数据库路径初始化，仅建表不加载模拟数据 */
    fun initForTest(customDbPath: String) {
        dbPath = customDbPath
        MyBatisUtil.reset()
        executeSqlFile("db/schema.sql")
    }

    private fun resolveDbPath(): Path = if (dbPath != null) Paths.get(dbPath!!) else dataDir.resolve("bookstore.db")

    private fun ensureDataDir() {
        Files.createDirectories(dataDir)
    }

    private fun initDatabase() {
        ensureDbFileExists()
        executeSqlFile("db/schema.sql")
        executeSqlFile("db/data.sql")
    }

    private fun loadConfig() {
        val props = Properties()
        val stream = javaClass.classLoader.getResourceAsStream("config.properties")
        if (stream != null) {
            props.load(stream)
            val cfgPath = props.getProperty("db.file", "bookstore.db")
            val p = Paths.get(cfgPath)
            dbPath = (if (p.isAbsolute) p else dataDir.resolve(cfgPath)).toString()
        }
    }

    private fun ensureDbFileExists() {
        val path = resolveDbPath()
        if (!Files.exists(path)) {
            Files.createFile(path)
        }
    }

    fun getConnection(): Connection {
        return DriverManager.getConnection("jdbc:sqlite:${resolveDbPath()}")
    }

    fun getDbPath(): String = resolveDbPath().toString()

    private fun executeSqlFile(resourcePath: String) {
        val stream = javaClass.classLoader.getResourceAsStream(resourcePath)
            ?: throw IllegalStateException("SQL resource not found: $resourcePath")

        val sql = BufferedReader(InputStreamReader(stream, "UTF-8")).readText()
        val statements = splitSqlStatements(sql)

        getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                for (sqlStmt in statements) {
                    val trimmed = sqlStmt.trim()
                    if (trimmed.isNotEmpty()) {
                        stmt.executeUpdate(trimmed)
                    }
                }
            }
        }
    }

    /**
     * 按分号拆分 SQL 语句，忽略纯注释行
     */
    private fun splitSqlStatements(sql: String): List<String> {
        val statements = mutableListOf<String>()
        val current = StringBuilder()
        var inString = false

        for (ch in sql) {
            when {
                ch == '\'' -> {
                    inString = !inString
                    current.append(ch)
                }
                ch == ';' && !inString -> {
                    val stmt = stripLeadingComments(current.toString())
                    if (stmt.isNotEmpty()) {
                        statements.add(stmt)
                    }
                    current.clear()
                }
                else -> current.append(ch)
            }
        }

        // 最后一条语句（可能没有分号结尾）
        val last = stripLeadingComments(current.toString())
        if (last.isNotEmpty()) {
            statements.add(last)
        }

        return statements
    }

    /** 去除语句前面的 -- 注释行 */
    private fun stripLeadingComments(stmt: String): String {
        val lines = stmt.lines()
        val startIndex = lines.indexOfFirst { line ->
            val trimmed = line.trim()
            trimmed.isNotEmpty() && !trimmed.startsWith("--")
        }
        if (startIndex < 0) return ""
        return lines.subList(startIndex, lines.size).joinToString("\n").trim()
    }
}

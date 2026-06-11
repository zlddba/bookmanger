package org.zl.team.config

import org.zl.team.mapper.*
import org.apache.ibatis.datasource.pooled.PooledDataSource
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import java.io.File

object MyBatisUtil {

    @Volatile
    private var sqlSessionFactory: SqlSessionFactory? = null

    fun getSqlSessionFactory(): SqlSessionFactory {
        return sqlSessionFactory ?: synchronized(this) {
            sqlSessionFactory ?: buildSqlSessionFactory().also {
                sqlSessionFactory = it
            }
        }
    }

    fun getSqlSession(): org.apache.ibatis.session.SqlSession {
        return getSqlSessionFactory().openSession()
    }

    fun reset() {
        synchronized(this) { sqlSessionFactory = null }
    }

    private fun buildSqlSessionFactory(): SqlSessionFactory {
        val dbPath = DatabaseInitializer.getDbPath()
        val dbFile = File(dbPath)

        val dataSource = PooledDataSource(
            "org.sqlite.JDBC",
            "jdbc:sqlite:${dbFile.absolutePath}",
            null,
            null
        )
        dataSource.setDefaultAutoCommit(false)

        val environment = Environment("development",
            JdbcTransactionFactory(),
            dataSource
        )

        val configuration = org.apache.ibatis.session.Configuration(environment)
        configuration.isMapUnderscoreToCamelCase = true

        // 注册所有 Mapper 接口
        configuration.addMapper(AdminMapper::class.java)
        configuration.addMapper(EmployeeMapper::class.java)
        configuration.addMapper(SupplierMapper::class.java)
        configuration.addMapper(BookCategoryMapper::class.java)
        configuration.addMapper(BookstoreInfoMapper::class.java)
        configuration.addMapper(BookMapper::class.java)
        configuration.addMapper(BookPriceMapper::class.java)
        configuration.addMapper(PurchaseRecordMapper::class.java)
        configuration.addMapper(ReturnRecordMapper::class.java)
        configuration.addMapper(SaleRecordMapper::class.java)
        configuration.addMapper(MemberMapper::class.java)
        configuration.addMapper(MemberPolicyMapper::class.java)
        configuration.addMapper(FeedbackMapper::class.java)
        configuration.addMapper(BookReservationMapper::class.java)
        configuration.addMapper(BorrowRecordMapper::class.java)
        configuration.addMapper(OperationLogMapper::class.java)

        return SqlSessionFactoryBuilder().build(configuration)
    }
}

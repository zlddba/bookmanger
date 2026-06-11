package org.zl.team.service

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

object SystemConfig {
    private val stockAlertThreshold = AtomicInteger(10)
    private val criticalStockThreshold = AtomicInteger(3)
    private val backupRetentionCount = AtomicInteger(30)
    private val lateFeeDailyRate = AtomicReference(0.50)

    fun getStockAlertThreshold(): Int = stockAlertThreshold.get()
    fun setStockAlertThreshold(v: Int) { stockAlertThreshold.set(v.coerceIn(1, 100)) }

    fun getCriticalStockThreshold(): Int = criticalStockThreshold.get()
    fun setCriticalStockThreshold(v: Int) { criticalStockThreshold.set(v.coerceIn(1, 20)) }

    fun getBackupRetentionCount(): Int = backupRetentionCount.get()
    fun setBackupRetentionCount(v: Int) { backupRetentionCount.set(v.coerceIn(1, 100)) }

    fun getLateFeeDailyRate(): Double = lateFeeDailyRate.get()
    fun setLateFeeDailyRate(v: Double) { lateFeeDailyRate.set(v.coerceIn(0.0, 10.0)) }

    fun calculateLateFee(overdueDays: Int): Double {
        if (overdueDays <= 0) return 0.0
        return overdueDays * getLateFeeDailyRate()
    }
}

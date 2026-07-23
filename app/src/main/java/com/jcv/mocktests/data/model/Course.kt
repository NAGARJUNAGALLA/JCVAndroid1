package com.jcv.mocktests.data.model

data class Course(
    val sheetId: String = "",
    val sheetName: String = "",
    val title: String = "",
    val fee: Any = 0,
    val expiryDate: String? = null,
    val description: String? = null
) {
    val feeAmount: Double
        get() = when (fee) {
            is Number -> fee.toDouble()
            is String -> fee.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
}

package com.dlrp.app.risk

enum class RiskLevel {
    SAFE,
    CAUTION,
    DANGER
}

data class RiskState(
    val level: RiskLevel,
    val reasons: List<String> = emptyList(),
    val matchedKeywords: List<String> = emptyList(),
    val extractedCode: String? = null
)

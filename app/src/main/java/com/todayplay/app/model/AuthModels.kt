package com.todayplay.app.model

enum class AccountProvider {
    Google,
    LocalTester,
}

data class AccountSession(
    val provider: AccountProvider,
    val displayName: String,
    val email: String? = null,
    val avatarUrl: String? = null,
    val backendVerified: Boolean = false,
    val idTokenForBackend: String? = null,
) {
    val shareName: String
        get() = displayName.ifBlank { email ?: "TodayPlay tester" }

    val providerLabel: String
        get() = when (provider) {
            AccountProvider.Google -> if (backendVerified) "Google verified" else "Google, pending server verification"
            AccountProvider.LocalTester -> "Local tester"
        }

    companion object {
        fun localTester(): AccountSession = AccountSession(
            provider = AccountProvider.LocalTester,
            displayName = "TodayPlay tester",
        )
    }
}

data class AccountSignInResult(
    val session: AccountSession? = null,
    val message: String,
)

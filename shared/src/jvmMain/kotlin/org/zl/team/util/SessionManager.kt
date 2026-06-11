package org.zl.team.util

object SessionManager {

    @Volatile
    var currentUserId: String = ""
        private set

    @Volatile
    var currentUserName: String = ""
        private set

    @Volatile
    var currentUserRole: String = ""
        private set

    @Volatile
    var loggedIn: Boolean = false
        private set

    fun login(userId: String, role: String, userName: String = userId) {
        currentUserId = userId
        currentUserName = userName
        currentUserRole = role
        loggedIn = true
    }

    fun logout() {
        currentUserId = ""
        currentUserName = ""
        currentUserRole = ""
        loggedIn = false
    }

    fun isLoggedIn(): Boolean = loggedIn

    fun hasRole(vararg roles: String): Boolean {
        return loggedIn && roles.any { it == currentUserRole }
    }
}

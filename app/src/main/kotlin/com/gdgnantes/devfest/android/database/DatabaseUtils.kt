package com.gdgnantes.devfest.android.database

fun sqlIn(expr: String, inElements: Collection<String>, negative: Boolean = false): String {
    if (inElements.isEmpty()) {
        // SQLite doesn't allow the in clause to be empty.
        // Let's deal with that by considering it is never possible
        // to be "in an empty set".
        return if (negative) "1" else "0"
    }
    return inElements.joinToString(
            separator = ", ",
            prefix = if (negative) "$expr NOT IN (" else "$expr IN(",
            postfix = ")") { android.database.DatabaseUtils.sqlEscapeString(it) }
}
package com.example.to_do_list

sealed class VakiIntent {
    data class AddTask(val title: String) : VakiIntent()
    data class DeleteTask(val title: String) : VakiIntent()
    object CountTasks : VakiIntent()
    object ListTasks : VakiIntent()
    data class Speak(val response: String) : VakiIntent()
    object Finish : VakiIntent()
    object TellDate : VakiIntent()
    data class Unknown(val rawText: String) : VakiIntent()
}

class VakiBrain {
    /**
     * Parses raw text into a structured intent using NLP patterns.
     */
    fun parse(text: String): VakiIntent {
        val lowerText = text.lowercase().trim()

        // Check for Add Task
        VakiNLPPatterns.ADD_PATTERNS.forEach { pattern ->
            val match = pattern.find(lowerText)
            if (match != null) {
                val title = match.groupValues[1].trim()
                if (title.isNotEmpty()) return VakiIntent.AddTask(title.replaceFirstChar { it.uppercase() })
            }
        }

        // Check for Delete Task
        VakiNLPPatterns.DELETE_PATTERNS.forEach { pattern ->
            val match = pattern.find(lowerText)
            if (match != null) {
                val title = match.groupValues[1].trim()
                if (title.isNotEmpty()) return VakiIntent.DeleteTask(title)
            }
        }

        // Check for Count Tasks
        VakiNLPPatterns.COUNT_PATTERNS.forEach { pattern ->
            if (pattern.containsMatchIn(lowerText)) return VakiIntent.CountTasks
        }

        // Check for List Tasks
        VakiNLPPatterns.LIST_PATTERNS.forEach { pattern ->
            if (pattern.containsMatchIn(lowerText)) return VakiIntent.ListTasks
        }

        // Check for Stop/Finish
        VakiNLPPatterns.STOP_PATTERNS.forEach { pattern ->
            if (pattern.matches(lowerText)) return VakiIntent.Finish
        }

        // Check for Date
        VakiNLPPatterns.DATE_PATTERNS.forEach { pattern ->
            if (pattern.matches(lowerText)) return VakiIntent.TellDate
        }

        return VakiIntent.Unknown(text)
    }
}

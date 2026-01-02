package com.example.to_do_list

sealed class VakiIntent {
    data class AddTask(val title: String) : VakiIntent()
    data class DeleteTask(val title: String) : VakiIntent()
    object CountTasks : VakiIntent()
    object ListTasks : VakiIntent()
    data class Unknown(val rawText: String) : VakiIntent()
}

class VakiBrain {
    /**
     * Parses raw text into a structured intent that Vaki can execute.
     */
    fun parse(text: String): VakiIntent {
        val lowerText = text.lowercase().trim()
        
        return when {
            // Intent: ADD
            lowerText.contains("add task") || lowerText.startsWith("add ") -> {
                val title = lowerText.replace("add task", "").replace("add", "").trim()
                if (title.isNotEmpty()) VakiIntent.AddTask(title.replaceFirstChar { it.uppercase() })
                else VakiIntent.Unknown(text)
            }
            
            // Intent: DELETE
            lowerText.contains("delete") || lowerText.contains("remove") -> {
                val title = lowerText.replace("delete", "").replace("remove", "").trim()
                if (title.isNotEmpty()) VakiIntent.DeleteTask(title)
                else VakiIntent.Unknown(text)
            }
            
            // Intent: COUNT
            lowerText.contains("how many") || lowerText.contains("total") || lowerText.contains("count") -> {
                VakiIntent.CountTasks
            }
            
            // Intent: LIST
            lowerText.contains("read") || lowerText.contains("list") || lowerText.contains("tell me my tasks") -> {
                VakiIntent.ListTasks
            }
            
            else -> VakiIntent.Unknown(text)
        }
    }
}

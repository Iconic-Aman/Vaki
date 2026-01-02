package com.example.to_do_list

object VakiNLPPatterns {
    // Patterns for adding tasks
    val ADD_PATTERNS = listOf(
        Regex("(?i).*add(?:ing)?(?: a| the)? task (?:called |named )?(.+)"),
        Regex("(?i).*put (.+) (?:on|in|to)(?: my| the)? (?:list|tasks)"),
        Regex("(?i).*create(?: a)? task (?:for |to )?(.+)"),
        Regex("(?i).*remind me to (.+)"),
        Regex("(?i)^add (.+)")
    )

    // Patterns for deleting tasks
    val DELETE_PATTERNS = listOf(
        Regex("(?i).*(?:delete|remove|clear|erase)(?: the| my)? task (?:called |named )?(.+)"),
        Regex("(?i).*get rid of (.+)"),
        Regex("(?i).*take (.+) off(?: my| the)? (?:list|tasks)"),
        Regex("(?i).*(?:delete|remove) (.+)"),
        Regex("(?i)^remove (.+)")
    )

    // Patterns for listing tasks
    val LIST_PATTERNS = listOf(
        Regex("(?i).*(?:read|list|show|tell me)(?: all)?(?: my| the)? tasks"),
        Regex("(?i).*what(?: are|'s| is) on my (?:list|tasks)"),
        Regex("(?i).*what do i (?:have to do|need to do)"),
        Regex("(?i).*check my (?:list|tasks)"),
        Regex("(?i).*read everything")
    )

    // Patterns for counting tasks
    val COUNT_PATTERNS = listOf(
        Regex("(?i).*how many tasks (?:do i have|are there)"),
        Regex("(?i).*(?:count|total)(?: my| the)? tasks"),
        Regex("(?i).*how much (?:work|stuff) is left"),
        Regex("(?i).*give me the total"),
        Regex("(?i).*summary of my day")
    )
}

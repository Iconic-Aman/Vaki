package com.example.to_do_list

object VakiNLPPatterns {
    // Patterns for adding tasks
    val ADD_PATTERNS = listOf(
        Regex("(?i).*(?:can you |could you |please )?add(?:ing)?(?: a| the)? task (?:called |named )?(.+)"),
        Regex("(?i).*(?:please )?put (.+) (?:on|in|to|inside)(?: my| the)? (?:list|tasks|to-do|to do)"),
        Regex("(?i).*(?:can you |please )?create(?: a)? (?:new )?task (?:for |to )?(.+)"),
        Regex("(?i).*(?:please )?remind me (?:to |about )?(.+)"),
        Regex("(?i).*(?:i want to |i need to |i have to )(?:do|add|save|write down) (.+)"),
        Regex("(?i).*(?:write down |save |note |record )(.+)"),
        Regex("(?i)^add (.+)"),
        Regex("(?i)^new task (.+)")
    )

    // Patterns for deleting tasks
    val DELETE_PATTERNS = listOf(
        Regex("(?i).*(?:can you |could you |please )?(?:delete|remove|clear|erase|cancel)(?: the| my)? task (?:called |named )?(.+)"),
        Regex("(?i).*(?:please )?get rid of (.+)"),
        Regex("(?i).*(?:please )?take (.+) off(?: my| the)? (?:list|tasks|to-do|to do)"),
        Regex("(?i).*(?:can you |please )?(?:delete|remove|erase) (.+)"),
        Regex("(?i).*(?:i want to |i need to )?(?:remove|delete) (.+)"),
        Regex("(?i)^remove (.+)"),
        Regex("(?i)^delete (.+)")
    )

    // Patterns for listing tasks
    val LIST_PATTERNS = listOf(
        Regex("(?i).*(?:can you |could you |please )?(?:read|list|show|tell me|display|recite)(?: all)?(?: of)?(?: my| the)? (?:tasks?|list|to-?do ?(?:list)?|items?|plans?|agenda|schedule|calendar)"),
        Regex("(?i).*(?:would you mind |could you )reading my (?:list|tasks|calendar)"),
        Regex("(?i).*what(?: are|'s| is) (?:on|in)(?: my| the)? (?:list|tasks?|to-?do|schedule|calendar|plan)"),
        Regex("(?i).*what do i (?:have|have to do|need to do|got going on)(?: today)?"),
        Regex("(?i).*(?:check|open|view|see) (?:my|the)? (?:list|tasks?|to-?do)"),
        Regex("(?i).*(?:read|tell me) everything"),
        Regex("(?i)^list(?:.*tasks?)?$"),
        Regex("(?i)^read(?:.*tasks?)?$"),
        Regex("(?i)^show(?:.*tasks?)?$"),
        Regex("(?i).*what (?:tasks?|plans?) (?:do )?i have")
    )

    // Patterns for counting tasks
    val COUNT_PATTERNS = listOf(
        Regex("(?i).*(?:can you |could you |please )?(?:tell me |give me )?how many (?:tasks?|items?|things?) (?:do i have|are there|left|remaining|on the list)"),
        Regex("(?i).*(?:count|total|summary)(?: of| for)?(?: my| the)? (?:tasks?|list|work|items?)"),
        Regex("(?i).*how much (?:work|stuff|things) (?:is|are) (?:left|pending)"),
        Regex("(?i).*(?:give me|tell me|what is) the (?:total|count)"),
        Regex("(?i).*status of my (?:day|list|tasks?)"),
        Regex("(?i).*how many things to do")
    )

    // Patterns for stopping conversation
    val STOP_PATTERNS = listOf(
        Regex("(?i)^(?:no|nope|nah|nay)(?:.*)?"),
        Regex("(?i).*(?:that's|thats) (?:all|it|everything)"),
        Regex("(?i)^stop$"),
        Regex("(?i).*(?:i'm|i am) done"),
        Regex("(?i)^done$"),
        Regex("(?i)^cancel$"),
        Regex("(?i)^nothing(?: else)?$"),
        Regex("(?i)^not really$"),
        Regex("(?i)^exit$")
    )

    // Patterns for asking date
    val DATE_PATTERNS = listOf(
        Regex("(?i).*what(?:'s| is) (?:today's|the) date"),
        Regex("(?i).*tell me (?:today's|the) date"),
        Regex("(?i).*what day is (?:it|today)")
    )
}

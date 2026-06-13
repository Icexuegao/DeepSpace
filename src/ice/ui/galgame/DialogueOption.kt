package ice.ui.galgame



data class DialogueOption(val label: String, val nextId: String, val condition: (DialogueEngine.() -> Boolean)? = null)

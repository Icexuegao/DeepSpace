package ice.ui.galgame

class DialogueNode(val id: String, config: DialogueNode.() -> Unit) {
  /** 对话文本内容 */
  var text: String = ""
  /** 分支选项列表，为空时自动跳转到 nextId */
  var options: List<DialogueOption> = emptyList()
  /** 自动跳转的目标节点ID（当 options 为空时生效） */
  var nextId: String? = null
  /** 进入节点时的回调（修改变量、播放音效等） */
  var onEnter: (DialogueEngine.() -> Unit)? = null

  init {
    config.invoke(this)
  }

  /** 将文本按换行符分割成多个段落 */
  fun getParagraphs(): List<String> {
    return text.split("\n").filter { it.isNotBlank() }
  }
}
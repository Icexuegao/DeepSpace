package ice.ui.galgame

class DialogueEngine(var firstId: String) {
  private val nodes = mutableMapOf<String, DialogueNode>()
  private var currentSession: DialogueSession? = null
  private var currentNodeId: String = firstId

  fun addNode(node: DialogueNode) {
    nodes[node.id] = node
  }

  fun getNode(id: String): DialogueNode {
    return nodes[id] ?: throw IllegalArgumentException("Node $id not found")
  }

  fun start(): DialogueSession {
    currentNodeId = firstId
    val node = getNode(currentNodeId)
    node.onEnter?.invoke(this)
    currentSession = DialogueSession(node)
    return currentSession!!
  }

  fun nextNode(): DialogueSession {
    val currentNode = getNode(currentNodeId)
    val nextId = currentNode.nextId ?: throw IllegalStateException("当前节点没有 nextId")
    currentNodeId = nextId
    val nextNode = getNode(currentNodeId)
    nextNode.onEnter?.invoke(this)
    currentSession = DialogueSession(nextNode)
    return currentSession!!
  }

  fun selectOption(option: DialogueOption): DialogueSession {
    currentNodeId = option.nextId
    val nextNode = getNode(currentNodeId)
    nextNode.onEnter?.invoke(this)
    currentSession = DialogueSession(nextNode)
    return currentSession!!
  }

  fun getCurrentSession(): DialogueSession? = currentSession

  fun hasOptions(): Boolean {
    return getNode(currentNodeId).options.isNotEmpty()
  }

  fun getCurrentOptions(): List<DialogueOption> {
    return getNode(currentNodeId).options
  }
}
package ice.ui.galgame

class DialogueSession(private val node: DialogueNode) {
   val paragraphs = node.getParagraphs()
   var currentIndex = 0

  fun getCurrentText(): String {
    return if (currentIndex < paragraphs.size) {
      paragraphs[currentIndex]
    } else {
      ""
    }
  }

  fun onClickNext(): Boolean {
    return if (currentIndex + 1 < paragraphs.size) {
      currentIndex++
      true
    } else {
      false
    }
  }
  fun hasNext(): Boolean {
    return currentIndex + 1 < paragraphs.size
  }

  fun isFinished(): Boolean {
    return currentIndex >= paragraphs.size - 1 && paragraphs.isNotEmpty()
  }

  fun reset() {
    currentIndex = 0
  }
}
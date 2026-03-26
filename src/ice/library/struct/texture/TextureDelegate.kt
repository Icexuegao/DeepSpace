package ice.library.struct.texture

import arc.Events
import arc.struct.Seq
import mindustry.game.EventType

abstract class TextureDelegate {
  //按道理在内容类创建时调用TextureDelegate 此时注册的Event应该还没有开始
  companion object {
    var delegate = Seq<() -> Unit>()

    init {
      Events.on(EventType.AtlasPackEvent::class.java) {
        delegate.forEach { it() }
      }
    }
  }
}
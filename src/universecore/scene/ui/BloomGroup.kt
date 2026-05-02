package universecore.scene.ui

import arc.Core
import arc.graphics.g2d.Bloom
import arc.scene.Group

/**泛光特效容器,位于此容器内的元素绘制会经过泛光过滤处理,即赋予光效效果,泛光参数默认来自游戏设置
 *
 * 该组件与[arc.graphics.g2d.ScissorStack]或[arc.graphics.Gl.scissor]相冲突,
 * 即此容器不应存放在任何会进行裁切的父级当中,例如[arc.scene.ui.ScrollPane]等
 * 但是此容器内部支持进行裁切,应当将此容器覆盖于裁切元素上方,并将此容器的clip设为true
 *  @author EBwilson */
open class BloomGroup :Group() {
  private val bloom = Bloom(true)

  /**@see arc.scene.ui.layout.Table.clip */
  var clip: Boolean = false

  var bloomEnabled: Boolean = Core.settings.getBool("bloom", true)
  var bloomIntensity: Float = Core.settings.getInt("bloomintensity", 6) / 4f + 1f
  var bloomBlur: Int = Core.settings.getInt("bloomblur", 1)

  override fun drawChildren() {
    if (bloomEnabled) {
      bloom.resize(Core.graphics.width, Core.graphics.height)
      bloom.setBloomIntensity(bloomIntensity)
      bloom.blurPasses = bloomBlur

      bloom.capture()
    }

    if (clip) {
      val applied = clipBegin()
      super.drawChildren()
      if (applied) clipEnd()
    } else super.drawChildren()

    if (bloomEnabled) bloom.render()
  }
}
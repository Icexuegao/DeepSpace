package ice.library.scene.style

import arc.Core
import arc.func.Cons
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.scene.style.TextureRegionDrawable
import arc.util.Time
import arc.util.Tmp

/**
 * 动态纹理可绘制对象,支持基于帧的动画效果
 * @author Alon
 * @since 1.0
 * @param textureName 动态纹理名称(不加数字后缀)
 * @param config 动态纹理配置
 */
class DynamicTextureDrawable(textureName: String, config: Cons<TextureConfig>) : TextureRegionDrawable() {

  class TextureConfig {
    /** 动画帧总数,必须大于 0 */
    var frameCount = 1

    /** 每帧持续时间(秒),必须为正数 */
    var frameDuration = 15f
  }

  /**图片数组,命名从 1 开始*/
  private var frames: Array<TextureRegion>
  private val configData = TextureConfig()

  init {
    config.get(configData)
    require(configData.frameCount >= 0) {"frameCount<=0 当前值${configData.frameCount}"}
    require(configData.frameDuration >= 0) {"frameDuration<=0 当前值${configData.frameDuration}"}
    frames = Array(configData.frameCount) {
      Core.atlas.find("$textureName-${it + 1}")
    }
    set(frames.first())
  }

  override fun draw(x: Float, y: Float, width: Float, height: Float) {
    Draw.color(Tmp.c1.set(tint).mul(Draw.getColor()).toFloatBits())
    Draw.rect(frames[(Time.globalTime / configData.frameDuration).toInt() % frames.size], x + width / 2f, y + height / 2f, width, height)
  }

  override fun draw(x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float) {
    Draw.color(Tmp.c1.set(tint).mul(Draw.getColor()).toFloatBits())
    Draw.rect(frames[(Time.globalTime / configData.frameDuration).toInt() % frames.size], x + width / 2f, y + height / 2f, width * scaleX, height * scaleY, originX, originY, rotation)
  }
}
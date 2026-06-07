package ice.world.content.unit

import arc.math.geom.Rect
import mindustry.type.UnitType

interface UnitConfigurator {
  private val self: UnitType
    get() = this as UnitType
  /**设置履带矩形区域,左下角为原点
   * @param startX 单条履带宽度起始X坐标
   * @param startY 单条履带高度起始Y坐标
   * @param width 单条履带宽度
   * @param height 单条履带高度
   * @param regionWidth 贴图整体宽度
   * @param regionHeight 贴图整体高度*/
  fun setTreadRects(startX: Float, startY: Float, width: Float, height: Float, regionWidth: Float, regionHeight: Float) {
    self.treadRects = arrayOf(Rect(startX - (regionWidth / 2), startY - (regionHeight / 2), width, height))
  }
}
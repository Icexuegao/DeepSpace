package ice.world.content.blocks.effect

import arc.Core
import arc.flabel.FLabel
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.GlyphLayout
import arc.graphics.g2d.Lines
import arc.scene.actions.Actions
import arc.scene.ui.layout.Scl
import arc.util.Align
import arc.util.pooling.Pools
import ice.graphics.IceColor
import ice.library.scene.element.IceDialog
import ice.library.scene.element.typinglabel.TLabel
import ice.library.struct.addP
import ice.ui.fragment.FleshFragment
import ice.world.content.blocks.abstractBlocks.IceBlock
import mindustry.Vars
import mindustry.graphics.Pal
import mindustry.ui.Fonts

class LostBox(name: String) : IceBlock(name) {
  init {
    update = true
    solid = true
    buildType = Prov(::LostBoxBuild)
  }

  override fun drawPlaceText(text: String, x: Int, y: Int, valid: Boolean): Float {
    if (Vars.renderer.pixelate) return 0f

    val color = if (valid) Pal.accent else Pal.remove
    val font = Fonts.outline
    val layout = Pools.obtain<GlyphLayout?>(GlyphLayout::class.java, Prov { GlyphLayout() })
    val ints = font.usesIntegerPositions()
    font.setUseIntegerPositions(false)
    font.getData().setScale(1f / 4f / Scl.scl(1f))
    layout.setText(font, text)

    val width = layout.width

    font.setColor(color)
    val dx = x * Vars.tilesize + offset
    var dy = y * Vars.tilesize + offset + size * Vars.tilesize / 2f + 3
    font.draw(text, dx, dy + layout.height + 1, Align.center)
    dy -= 1f
    Lines.stroke(2f, Color.darkGray)
    Lines.line(dx - layout.width / 2f - 2f, dy, dx + layout.width / 2f + 1.5f, dy)
    Lines.stroke(1f, color)
    Lines.line(dx - layout.width / 2f - 2f, dy, dx + layout.width / 2f + 1.5f, dy)

    font.setUseIntegerPositions(ints)
    font.setColor(Color.white)
    font.getData().setScale(1f)
    Draw.reset()
    Pools.free(layout)

    return width
  }
  inner class LostBoxBuild : IceBuild() {
    var b1=true
    override fun update() {
      super.update()


      if((Vars.player.unit()?.dst(this) ?: 0f) < 8 * 10f && b1){
        b1=false
        val actor = TLabel("{SICK}{SPEED=0.1}我喜欢你,你喜欢我")
        actor.setColor(IceColor.r1)


        actor.actions(Actions.alpha(0f), Actions.alpha(1f,2f), Actions.delay(3f))
        var b=true

        val dx = x  + offset - actor.width
        val dy = y + offset + size * Vars.tilesize / 2f + 3

        actor.update {
          val mouseScreen = Core.input.mouseScreen(dx, dy)
          actor.setPosition(mouseScreen.x,mouseScreen.y)
          if (actor.hasEnded()&&b&&actor.actions.isEmpty){
            actor.actions(Actions.alpha(0f,2f), Actions.remove())
            b=false
          }
        }
        FleshFragment.group.addChild(actor)
      }
    }
    override fun tapped() {
      val tile = "祭祀残篇"
      val string = lostBoxStringMap[tile]

      IceDialog("遗弃匣").apply {
        root.add(tile).padBottom(10f).color(IceColor.r1).row()
        string?.split("\n")?.forEach { s ->
          root.add(FLabel(s.filterNot { it.isWhitespace() }).also {
            it.defaultToken = "{dfd}"
          }).color(IceColor.r1).row()
        }
        addCloseButton()
        show()
      }
    }
  }

  companion object {
    val lostBoxStringMap = HashMap<String, String>().apply {
      addP("《猩红教章》 · 其一") {
        """
                不知何时, 灾祸降临在帝国边陲之地
                日复一日, 阴霾不断蚕食帝国的土地, 带来灾难与鲜血
                彼时, 昏庸无为的帝国之主却予以蔑视
                不以为意...
                直到灾祸肆意, 万里焦土, 一片生灵涂炭
                """.trimIndent()
      }
      addP("祭祀残篇") {
        """
            血肉涌动,赤潮翻腾 
            骨骼为阶,肌理为门 
            不可名状者自深渊苏醒
            赐吾等以蠕行之恩
            那剥落的皮囊之下
            万千猩红之舌颂唱真名
            每一滴垂落的甘露
            皆是赐予的永恒之印
            断裂的指节生出新芽
            溃烂的眼眶绽放繁花
            凡愚者恐惧溃散的形骸
            恰是圣体降临的温床
            赞美那无定之形
            脉搏即圣谕,伤口即经文
            当群星腐化,大地匍匐
            唯血肉终将吞没晨昏
                    """.trimIndent()
      }
    }
  }
}
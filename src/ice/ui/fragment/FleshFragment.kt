package ice.ui.fragment

import arc.Core
import arc.flabel.FLabel
import arc.func.Boolp
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Rand
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.event.Touchable
import arc.scene.ui.layout.WidgetGroup
import arc.struct.Seq
import ice.graphics.IceColor
import ice.library.math.slope
import ice.library.scene.ui.colorR
import ice.library.util.accessFloat
import ice.world.meta.IceEffects
import mindustry.Vars
import mindustry.entities.Effect
import universecore.ui.elements.SceneEffect

object FleshFragment {
  var group: Group = WidgetGroup()
  var text = Seq<String>(String::class.java).apply {
    addAll(
      """
                都怪你
                你在哪
                我喜欢你
                捅死你喵
                为什么要给别人画贴图
                我哪里对你不好了
                不要走
                不要离开我好不好
                求你了
                都是你的错
                永远在一起好不好
                你是我的
                只能看着我
                不许看别人
                我要把你锁起来
                这样你就不会离开了
                好爱你啊
                为什么要逃呢
                我会好好对你的
                把你的眼睛挖出来
                这样就只能看到我了
                嘻嘻嘻
                你是我的玩具
                不许对别人笑
                我要把你吃掉
                融为一体
                永远不分开
                你只能属于我
                背叛的话就杀掉哦
                开玩笑的啦
                才怪
                为什么要发抖呢
                我又不会伤害你
                大概
                乖乖听话就好
                你是我的全部
                没有你我活不下去
                所以你也别想离开
                呵呵呵
                抓到你了
                这次不会再放手了
                你的血好温暖
                让我再多感受一点
                
        """.trimIndent().split("\n")
    )
  }

  fun build(parent: Group) {
    group.setFillParent(true)
    group.touchable = Touchable.childrenOnly
    group.visibility = Boolp(Vars.ui.hudfrag::shown)
    parent.addChild(group)
  }

  fun addText() {
    text()
  }
  fun random(min: Float, max: Float): Float {

    return min + (max - min) * random.nextFloat()
  }
  var random = Rand()
  var FLabel.textSpeed by accessFloat("textSpeed")
  fun text() {

    val fLabel = FLabel("{shake}${text.random()}").colorR(IceColor.r2)
    val d = fLabel.text.length * 0.3f
    fLabel.actions(Actions.delay(d), Actions.alpha(0f, 1f), Actions.remove())
    random.setSeed(System.currentTimeMillis())
    fLabel.setFontScale(random(1f,3f))
    fLabel.setScale(random(1f,3f))
    fLabel.textSpeed = 0.3f
    fLabel.setPosition(
      IceEffects.rand.nextFloat(Core.graphics.width.toFloat()-fLabel.width),
      IceEffects.rand.nextFloat((Core.graphics.height).toFloat())
    )
    SceneEffect.showOnStage(  Effect(2*60f){e->

      for(i in (1..33)) {
        random.setSeed((e.id+i).toLong())
        val x= random(0f,Core.graphics.width.toFloat())
        val y=random(0f,Core.graphics.height.toFloat())

        Draw.color(IceColor.r2)
        Draw.alpha(e.fin().slope)
        Fill.rect(x,y,8f,8f)
      }


    },fLabel.x,fLabel.y).apply {
      setScale(4f)
    }
    group.addChild(fLabel)
  }
}
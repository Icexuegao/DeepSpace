package ice.ui.menusDialog

import arc.input.KeyCode
import arc.scene.event.InputEvent
import arc.scene.event.InputListener
import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.util.Scaling
import arc.util.Tmp
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.ui.addListeners
import ice.library.struct.asDrawable
import ice.ui.dialog.BaseMenusDialog
import ice.world.meta.IceEffects
import ice.world.meta.IceStats
import universecore.ui.elements.SceneEffect

object PublicInfoDialog : BaseMenusDialog(IceStats.公告.localized(), IStyles.menusButton_publicInfo) {
  override fun build(cont: Table) {
    cont.top()
    val element = Image(IStyles.publicInfoIcon.asDrawable(0.5f), Scaling.fit)
    cont.add(element).row()
    cont.add("过去的这一年,不管是我们DS制作组还是我,其实都经历了焦虑和迷茫,因为我们确确实实感觉经历了很多特别艰难的时刻,有的声音特别的,特别特别的尖锐,然后把我们整个DS和DS项目组都贬的一无是处,还有很多人说,听不进大家的声音,但就像猫猫讲的.其实我们跟大家一样,我们也是玩家,大家感受到的事情我们也在感受,只是对于我们来讲呢,我们听到的声音实在是太多了,我们需要沉下心来,去弄清楚到底哪些是来自各位主教真实的声音(抽泣)").growX().wrap().padLeft(20f)
      .padRight(20f).color(IceColor.b4).row()
    cont.add("无尽压缩牛逼无尽压缩牛逼无尽压缩牛逼无尽压缩牛逼无尽压缩牛逼无尽压缩牛逼无尽压缩牛逼").growX().wrap().padLeft(20f)
      .padRight(20f).color(IceColor.b4).row()
    cont.add("@Alon(\"帝皇\") 我的错就是让你活着了[天使]你的三观应该是没有的，同理心也是没有的，你和那些在癌症患者家属的诉苦视频评论区说“全家遇不到”“不接”的一桌，bro肯定觉得自己老帅了吧，实际上是阴暗哥布林一个，而且是被别人孤立的哥布林[委屈],呜呜呜你真的好可怜呢也就是说你也很可恨呢[温馨]")
      .pad(20f).color(IceColor.b4).growX().wrap().row()
    val get = cont.add(
      "我想了一天,我觉得是时候该反击了[流汗表情]" +

              "既然大家想知道在说谁,我帮你补充吧,联合城是超过一年前的作品,如果想要嘲讽请嘲讽我们的最近作品," +

              "我们每周内部测试还不够找出所有缺点,虽然内部测试不公开[流汗表情]" +

              "玩家市场是不调查的,工程量事实上是不预估的大到上天的,剧本是半吊子的,世界观是没构建完的(一个成熟的,逻辑自洽的世界观IP起码一年,再少也是几个月)" +

              ",观众期望是过高的,核心玩法是一点也见不着的,内容开放是极慢的,公开内容更是只有碎片的," +

              "视觉小说向剧情怎么融入RTS+自动化+塔防也是不知道的,我难道前线压力巨大快爆炸的时候还" +

              "要看到你们卿卿我我吗?这是策划最忌讳的问题,也是独游最忌讳的问题,你的粉丝能在一两年内玩到你的内容吗?请打破我的质疑[流汗表情]" +

              "世处重在快+多人兼容性而不是单机高质,这句话相当于否认资源站近12000张地图,数百名地图作者," +

              "以及所有逻辑人的全部努力,如果你要挑起Mindustry圈内战争,那我无话可说\n" +

              "除了你这个人以外,我只想看到你的作品\n" +

              "我是玩家,我只管我玩的开心,如果作者本人让我有不好体验,那是作者本人的问题(ps:虽然别看Alon的外表可可爱爱但是内部对任何新人极度仇视," +

              "而且本人抽象得要命),而且我早就放弃了Mindustry开发了,AMO是最后的作品\n" +

              "大不了大会员结末了,我就像以前2021年开mc服那会被外挂团伙盯上了那会又注销一次账号呗,你猜我为什么lv3," +

              "而且老觉得你有点精神疾病,因为外挂团伙那群人也有点精神疾病,\n" +

              "但之后呢?内容更新怎么更?如何回应粉丝期望?更新频率需要的个人时间够吗?我们一次更新多少策划案的内容?UGC怎么回应?[流汗表情]" +

              "你赢了,赢得很彻底,非常彻底,赢了所有mindustry玩家,制作者,贡献者,请你继续你的道路,但我还有事要做,我为什么要和你死缠烂打?[流汗表情]"
    ).padLeft(20f).padRight(20f).color(IceColor.y2).growX().wrap().get()
    get.addListeners(object : InputListener() {
      override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: KeyCode?): Boolean {
        val localToStageCoordinates = get.localToStageCoordinates(Tmp.v1.set(x, y))
        val showOnStage = SceneEffect.showOnStage(IceEffects.基础子弹击中特效, localToStageCoordinates.x, localToStageCoordinates.y)
        showOnStage.setScale(4f)
        return super.touchDown(event, x, y, pointer, button)
      }

    })
  }
}
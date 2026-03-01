package ice.ui.menusDialog

import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.util.Scaling
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.struct.asDrawable
import ice.ui.dialog.BaseMenusDialog
import ice.world.meta.IceStats

object PublicInfoDialog : BaseMenusDialog(IceStats.公告.localized(), IStyles.menusButton_publicInfo) {
  override fun build(cont: Table) {
    cont.top()
    val element = Image(IStyles.publicInfoIcon.asDrawable(0.5f).apply {

    }, Scaling.fit)
    cont.add(element).row()
    cont.add(
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

              "我是玩家,我只管我玩的开心,如果作者本人让我有不好体验,那是作者本人的问题(ps:虽然别看Alon的外表可可爱爱但是内部对任何新人极度仇视,"+

              "而且本人抽象得要命),而且我早就放弃了Mindustry开发了,AMO是最后的作品\n" +

              "大不了大会员结末了,我就像以前2021年开mc服那会被外挂团伙盯上了那会又注销一次账号呗,你猜我为什么lv3,"+

              "而且老觉得你有点精神疾病,因为外挂团伙那群人也有点精神疾病,\n" +

              "但之后呢?内容更新怎么更?如何回应粉丝期望?更新频率需要的个人时间够吗?我们一次更新多少策划案的内容?UGC怎么回应?[流汗表情]" +

              "你赢了,赢得很彻底,非常彻底,赢了所有mindustry玩家,制作者,贡献者,请你继续你的道路,但我还有事要做,我为什么要和你死缠烂打?[流汗表情]"
    ).pad(20f).color(IceColor.y2).grow().wrap()
  }
}
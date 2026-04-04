package ice.ui.menusDialog

import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.util.Scaling
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.ui.iPane
import ice.library.struct.asDrawable
import ice.ui.dialog.BaseMenusDialog
import ice.world.meta.IceStats

object PublicInfoDialog : BaseMenusDialog(IceStats.公告.localized(), IStyles.menusButton_publicInfo) {
  lateinit var conts: Table
  fun addLabel(a: String) {
    conts.add(a).growX().wrap().pad(10f).padLeft(20f).padRight(20f).color(IceColor.b4).row()
  }

  override fun build(cont: Table) {

    cont.top()
    val element = Image(IStyles.publicInfoIcon.asDrawable(0.5f), Scaling.fit)
    cont.add(element).row()
    cont.iPane {
      conts = it
    }.grow()
    addLabel("中华文明五千年的历史跟说着玩一样")
    addLabel("alon我对他的印象还在女装大佬")
    addLabel("我只是不理解他为什么当时那么排斥我")
    addLabel("我也没扣帽子阿")
    addLabel("但是和畜牲一样的脾气管理能力（")
    addLabel("他在我眼里的人类含量＜0%")
    addLabel("我曾遭到三度背叛,因而懂得世间万物不过是欺瞒的幌子,其一为神,我的创造者,我的母亲,其为力量所困,视我为无用之物,其二为人,我的家人,我的朋友,及为恐惧所困,视我为可憎恶之物,其三为同类,我的期盼,羽翼尚未丰满的鸟省,其为寿限所限,背弃与我的约定.我的愤怒,绝不平息,我拼弃所有,否定并嗤笑人间一切,我的胸膛注定不会再被世俗染指,并弃掉人类低劣的情感,空洞的部分将如诞生之初的纯白卷轴那般,以满载神性的神明之心来填满.无需恐惧,死亡只是一瞬,你们的时代,就要结束了.")
    addLabel(
      "@val alon =Alon(\"抽象\") 说话别这么冲,也别带脏字.大家都是玩模组的,没必要戾气这么重.好好说话,互相尊重,比什么都强\n" + "什么玩意跟我要尊重\n" + "@val alon =Alon(\"抽象\") 尊重是互相的,不是谁求谁给的.我只是提醒你好好说话,没别的意思,没必要这么冲.\n" + "你说的对,但是我是皇帝\n" + "@val alon =Alon(\"抽象\") 道理说得过就讲道理,说不过就拿权限禁言,这下看懂了\n" + "我是懒得说了 绒影灭神使被你禁言29天\n" + "@val alon =Alon(\"抽象\") 懒得说就等于禁言我一个月,这格局和阿Q也没什么区别了 \n" + "绒影灭神使被你禁言29天"
    )
    addLabel("过去的这一年,不管是我们DS制作组还是我,其实都经历了焦虑和迷茫,因为我们确确实实感觉经历了很多特别艰难的时刻,有的声音特别的,特别特别的尖锐,然后把我们整个DS和DS项目组都贬的一无是处,还有很多人说,听不进大家的声音,但就像猫猫讲的.其实我们跟大家一样,我们也是玩家,大家感受到的事情我们也在感受,只是对于我们来讲呢,我们听到的声音实在是太多了,我们需要沉下心来,去弄清楚到底哪些是来自各位主教真实的声音(抽泣)")
    addLabel("压制,手法,绝境,突破,自我,证明,巅峰,枷锁,答案,依赖,尽头,诠释,冷静,逆境,自信,无限,打破,困局,质疑,逆转,磨炼,极限,止步,假象,结果,终将,问鼎,挣脱,限制,领域,绝巅,过去,诀别,压制,手法,绝境,突破,自我,证明,巅峰,枷锁,答案,依赖,尽头,诠释,冷静,逆境,自信,无限,打破,困局,质疑,逆转,磨炼,极限,止步,假象,结果,终将,问鼎,挣脱,限制,领域,绝巅,过去,诀别")
    addLabel("无尽压缩牛逼无尽压缩牛逼无尽压缩牛逼无尽压缩牛逼无尽压缩牛逼无尽压缩牛逼无尽压缩牛逼")
    addLabel("@Alon(\"帝皇\") 我的错就是让你活着了[天使]你的三观应该是没有的,同理心也是没有的,你和那些在癌症患者家属的诉苦视频评论区说“全家遇不到”“不接”的一桌,bro肯定觉得自己老帅了吧,实际上是阴暗哥布林一个,而且是被别人孤立的哥布林[委屈],呜呜呜你真的好可怜呢也就是说你也很可恨呢[温馨]")
    addLabel(
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
    )/*get.addListeners(object : InputListener() {
      override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: KeyCode?): Boolean {
        val localToStageCoordinates = get.localToStageCoordinates(Tmp.v1.set(x, y))
        val showOnStage = SceneEffect.showOnStage(IceEffects.基础子弹击中特效, localToStageCoordinates.x, localToStageCoordinates.y)
        showOnStage.setScale(4f)
        return super.touchDown(event, x, y, pointer, button)
      }

    }*/
  }
}
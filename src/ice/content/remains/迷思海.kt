package ice.content.remains

import arc.Core
import arc.flabel.FLabel
import arc.func.Boolp
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import arc.math.Rand
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.event.Touchable
import arc.scene.ui.TextField.TextFieldStyle
import arc.scene.ui.layout.Table
import arc.scene.ui.layout.WidgetGroup
import arc.struct.Seq
import arc.util.Scaling
import arc.util.Time
import ice.graphics.IceColor
import ice.library.IFiles
import ice.library.IFiles.appendModName
import ice.library.math.slope
import ice.library.scene.style.DynamicTextureDrawable
import ice.library.scene.ui.addLine
import ice.library.scene.ui.colorR
import ice.library.struct.ConfigPropertyDelegate
import ice.library.util.accessFloat
import ice.type.Remains
import ice.ui.bundle.bundle
import ice.world.meta.IceEffects
import mindustry.Vars
import mindustry.entities.Effect
import mindustry.gen.Tex
import mindustry.ui.Fonts
import singularity.core.UpdatePool
import universecore.ui.elements.SceneEffect
import kotlin.math.max
import kotlin.math.sin

class 迷思海 :Remains("remains_mystic_sea") {

  var b: Float by ConfigPropertyDelegate(0.5f, "$name-fleshFragmentSeed")

  init {
    remainsColor = IceColor.r2
    bundle {
      desc(zh_CN, "迷思海", "你可悲的一部分正期望着回到你的身上")
    }
    setDescription(getDescription())
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 32
      it.frameDuration = 60f / 8f
    }
    effect = "未完成"
    install = {
      var i = 0f
      val fx = Effect(2 * 60f) { e ->
        Draw.color(IceColor.r2)
        Draw.alpha(e.fin().slope)
        Fill.rect(e.x, e.y, 8f, 8f, Time.time % 360f * e.fin())
      }


      UpdatePool.receive("remains_mystic_sea") {
        if (!Vars.state.isGame || Core.scene.hasDialog() || Vars.state.isPaused) return@receive

        if (Mathf.chance(0.06 * b)) {
          SceneEffect.showOnStage(fx, random(0f, Core.graphics.width.toFloat()), random(0f, Core.graphics.height.toFloat())) {
            IceEffects.rand.setSeed(it.id.toLong())
            val f = Time.delta * 3f
            if (IceEffects.rand.nextInt(2) == 0) {
              it.x += f
            } else it.x -= f
            it.setScale(max(4 * (1f - it.time / it.lifetime), 1f))
            it.y -= f
          }
        }


        i += Time.delta
        if (i > 600f * (1f - b)) {
          addText(1f - b)
          i = 0f
        }
      }
    }
    uninstall = {
      UpdatePool.remove("remains_mystic_sea")
    }
  }

  override fun getTiTleTable(): Table {
    return Table().also {
      it.image(icon).scaling(Scaling.fit).size(120f).pad(30f).padTop(0f).row()
      it.table { table ->
        table.table(IFiles.createNinePatch("Uwdwdqddw")) { it1 ->
          it1.add("遗物").color(IceColor.b4).expandX().left().padLeft(4f)
        }.width(100f).height(30f).color(IceColor.b6).expandX().left().row()
        table.add(getLocalizedName()).color(remainsColor).fontScale(1.5f).pad(5f).padLeft(0f).expandX().left().row()
      }.grow().row()
      it.addLine().pad(3f)
      it.table { table ->
        table.add("效果: $effect").color(remainsColor).pad(5f).fontScale(1.3f).wrap().grow()
      }.marginLeft(9f).grow().row()
      it.add(customTable).grow().row()
      it.table {
        it.add("同化程度: ").color(remainsColor)
        it.field(b.toString()) { it1 ->
          it1.toFloatOrNull()?.let { it2 ->
            b = Mathf.clamp(it2, 0.1f, 0.9f)
          }
        }.color(remainsColor).growX().get().style = TextFieldStyle().apply {

          font = Fonts.def
          fontColor = remainsColor
          disabledFontColor = Color.gray
          disabledBackground = Tex.underlineDisabled
          selection = Tex.selection
          background = Tex.underlineWhite
          invalidBackground = Tex.underlineWhite

          cursor = Tex.cursor
          messageFont = Fonts.def
          messageFontColor = Color.gray

        }
      }.growX()

    }
  }

  var group: Group = WidgetGroup()
  var rands =
    "ｿ關蜈逋蜈皮噪豢樒ｩ荵倶ｭ蜿謔ｲ逧諢夊逧蟆丞大ｨ倡鯵荳荳蝨ｨ荳榊庄諤晁荵句嵜貍ｫ豁逹迢よｰ秘先蝉ｾ陏迪ｫ蜆ｿ蜿大蝌ｲ隨譌莨第裏豁逧幻莨壻郤闌ｶ荵溷ｷｲ扈丞蜃蟆ｱ霑櫁ｿ呎ｨ讓邉顔ｳ顔噪諢剰ｹ豺蛹悶∵蜃豺ｷ蜷亥惠荳襍ｷ蜿譛我画律譛育噪譛亥ｱ譏ｭ遉逹邇ｰ蝨ｨ逧慮髣隨醍捩逧賢蜥螯よｭ､貂ｩ譟辷辷荵溷縲∝ｦ亥ｦ井ｹ溽縲∝驛荳埼怙隕∽譛臥擅鮠蜿莉蜿匁囑譛句暑荵溷ｷｲ扈丈榊ｿｦ∽蜈ｨ驛ｨ驛蝮乗脂蜷荵ｱ菴應蝗逧謨荳荳也阜蟆ｱ霑櫁ｿ咎復蟄宣㈹荵滓裏豕墓丐蜃譴ｦ諠ｳ荳主梧悍逵句賦閭悟鋤逧辷ｱ荳荳蟆ｱ霑樊ｷ鮴滓ｱ､蝠螯ゆｻ贋ｹ溯螟荳句朕莠操蛛夂捩莉取悄蠕ｭ騾ｰ逧ｦ逵句賦閭悟鋤逧辷ｱ荳荳螟ｱ譛帷噪逵ｼ逾樣㈹鬚､謚也捩縲∝ｳ諤慕捩縲∝働豕｣逹蟆ｱ霑櫁ｿ咎復蟄宣㈹荵滓裏豕墓丐蜃譴ｦ諠ｳ荳主梧悍蜻逵句操譏辷ｱ蜩ｭ鬯ｼ辷ｱ荳荳蜿譏蟄伜惠莠取ｭ､螟ｰｱ蜿莉莠逃蜿謔ｲ逧諢夊逧蟆丞大ｨ倡鯵荳荳"
  val texts = Seq<String>(String::class.java).apply {
    addAll(
      """
        alon我对他的印象还在女装大佬
        我只是不理解他为什么当时那么排斥我
        我也没扣帽子阿
        中华文明五千年的历史跟说着玩一样
        但是和畜牲一样的脾气管理能力
        他在我眼里的人类含量＜0%
        你赢了,赢得很彻底,非常彻底,赢了所有mindustry玩家,制作者,贡献者,请你继续你的道路,但我还有事要做,我为什么要和你死缠烂打
        我想了一天,我觉得是时候该反击了[流汗表情]
        既然大家想知道在说谁,我帮你补充吧,联合城是超过一年前的作品,如果想要嘲讽请嘲讽我们的最近作品
        我们每周内部测试还不够找出所有缺点,虽然内部测试不公开[流汗表情]"
        玩家市场是不调查的,工程量事实上是不预估的大到上天的,剧本是半吊子的,世界观是没构建完的(一个成熟的,逻辑自洽的世界观IP起码一年,再少也是几个月)
        ,观众期望是过高的,核心玩法是一点也见不着的,内容开放是极慢的,公开内容更是只有碎片的
        视觉小说向剧情怎么融入RTS+自动化+塔防也是不知道的,我难道前线压力巨大快爆炸的时候还
        要看到你们卿卿我我吗?这是策划最忌讳的问题,也是独游最忌讳的问题,你的粉丝能在一两年内玩到你的内容吗?请打破我的质疑[流汗表情]
        世处重在快+多人兼容性而不是单机高质,这句话相当于否认资源站近12000张地图,数百名地图作
        以及所有逻辑人的全部努力,如果你要挑起Mindustry圈内战争,那我无话可说
        除了你这个人以外,我只想看到你的作品
        我是玩家,我只管我玩的开心,如果作者本人让我有不好体验,那是作者本人的问题(ps:虽然别看Alon的外表可可爱爱但是内部对任何新人极度仇视
        而且本人抽象得要命),而且我早就放弃了Mindustry开发了,AMO是最后的作品
        大不了大会员结末了,我就像以前2021年开mc服那会被外挂团伙盯上了那会又注销一次账号呗,你猜我为什么lv3,"
        而且老觉得你有点精神疾病,因为外挂团伙那群人也有点精神疾病
        但之后呢?内容更新怎么更?如何回应粉丝期望?更新频率需要的个人时间够吗?我们一次更新多少策划案的内容?UGC怎么回应?[流汗表情]
        你赢了,赢得很彻底,非常彻底,赢了所有mindustry玩家,制作者,贡献者,请你继续你的道路,但我还有事要做,我为什么要和你死缠烂打?[流汗表情]
        我的错就是让你活着了[天使]你的三观应该是没有的,同理心也是没有的,你和那些在癌症患者家属的诉苦视频评论区说“全家遇不到”“不接”的一桌
        bro肯定觉得自己老帅了吧,实际上是阴暗哥布林一个,而且是被别人孤立的哥布林[委屈],呜呜呜你真的好可怜呢也就是说你也很可恨呢[温馨]
    """.trimIndent().split("\n")
    )
  }

  init {
    build(Vars.ui.hudGroup)
  }

  fun build(parent: Group) {
    group.setFillParent(true)
    group.touchable = Touchable.childrenOnly
    group.visibility = Boolp(::unlock)
    parent.addChild(group)

    group.fill { f, f1, f2, f3 ->
      Draw.color(IceColor.r1)
      Draw.alpha(0.1f + 0.2f * sin(Time.time / 60f))
      Draw.rect("whiteui", f, f1, f2 * 2, f3 * 2)
    }
  }

  fun addText(charge: Float) {
    text(charge)
  }

  fun random(min: Float, max: Float): Float {

    return min + (max - min) * random.nextFloat()
  }

  var random = Rand()
  var FLabel.textSpeed by accessFloat("textSpeed")
  fun text(charge: Float) {

    val random1 = texts.random()
    val indices = generateRandomIndices(random1.length, charge)

    val result = random1.mapIndexed { index, char ->
      if (index in indices) rands.random() else char
    }.joinToString("")

    val fLabel = FLabel("{shake}$result").colorR(IceColor.r2)
    val d = fLabel.text.length * 0.3f
    fLabel.actions(Actions.delay(d), Actions.alpha(0f, 1f), Actions.remove())
    random.setSeed(System.currentTimeMillis())
    fLabel.setFontScale(random(1f, 3f))
    fLabel.setScale(random(1f, 3f))
    fLabel.setRotation(random(-30f, 30f))
    fLabel.textSpeed = 0.3f

    val rotation = random(-30f, 30f)
    val container = object :Group() {
      init {
        transform = true
      }
    }

    container.setRotation(rotation)
    container.addChild(fLabel)

    val x = IceEffects.rand.nextFloat(max(Core.graphics.width.toFloat() - fLabel.width, 1f))
    val y = IceEffects.rand.nextFloat((Core.graphics.height).toFloat())
    /* fLabel.setPosition(x, y)
     group.addChild(fLabel)*/
    container.setPosition(x, y)
    group.addChild(container)
  }

  fun generateRandomIndices(length: Int, ratio: Float): List<Int> {
    require(ratio in 0.0f..1.0f) { "Ratio must be between 0.0 and 1.0" }
    require(length > 0) { "Length must be positive" }

    val count = (length * ratio).toInt().coerceIn(0, length)

    return (0 until length).shuffled().take(count).sorted()
  }

}
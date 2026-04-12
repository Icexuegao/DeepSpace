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

  var b: Float by ConfigPropertyDelegate(0.5f, "remains_mystic_sea-fleshFragmentSeed")

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

        if (Mathf.chance(0.02)) {
          SceneEffect.showOnStage(fx, random(0f, Core.graphics.width.toFloat()), random(0f, Core.graphics.height.toFloat())) {
            IceEffects.rand.setSeed(it.id.toLong())
            val f = Time.delta * 3f
            if (IceEffects.rand.nextInt(2) == 0) {
              it.x += f
            } else it.x -= f
            it.setScale(max(4 * (1f - it.time / it.lifetime), 1f))
            it.y -= f
          }.apply {
            setScale(4f)
          }
        }


        i += Time.delta
        if (i > 300f) {
          addText(b)
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
      it.field(b.toString()) {
        it.toFloatOrNull()?.let {
          b = it
        }
      }.color(remainsColor).growX().get().style = TextFieldStyle().apply {

        font = Fonts.def
        fontColor = remainsColor
        disabledFontColor = Color.gray
        disabledBackground = Tex.underlineDisabled
        selection = Tex.selection
        background = Tex.underline
        invalidBackground = Tex.underlineRed
        cursor = Tex.cursor
        messageFont = Fonts.def
        messageFontColor = Color.gray

      }
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
        我曾遭到三度背叛,因而懂得世间万物不过是欺瞒的幌子,其一为神,我的创造者,我的母亲,其为力量所困,视我为无用之物,其二为人,我的家人,我的朋友,及为恐惧所困,视我为可憎恶之物,其三为同类,我的期盼,羽翼尚未丰满的鸟省,其为寿限所限,背弃与我的约定.我的愤怒,绝不平息,我拼弃所有,否定并嗤笑人间一切,我的胸膛注定不会再被世俗染指,并弃掉人类低劣的情感,空洞的部分将如诞生之初的纯白卷轴那般,以满载神性的神明之心来填满.无需恐惧,死亡只是一瞬,你们的时代,就要结束了
        你赢了,赢得很彻底,非常彻底,赢了所有mindustry玩家,制作者,贡献者,请你继续你的道路,但我还有事要做,我为什么要和你死缠烂打
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
      Draw.alpha(0.1f+0.2f* sin(Time.time/60f))
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
    fLabel.textSpeed = 0.3f
    fLabel.touchable = Touchable.disabled
    val x = IceEffects.rand.nextFloat(max(Core.graphics.width.toFloat() - fLabel.width, 1f))
    val y = IceEffects.rand.nextFloat((Core.graphics.height).toFloat())
    fLabel.setPosition(x, y)
    group.addChild(fLabel)
  }

  fun generateRandomIndices(length: Int, ratio: Float): List<Int> {
    require(ratio in 0.0f..1.0f) { "Ratio must be between 0.0 and 1.0" }
    require(length > 0) { "Length must be positive" }

    val count = (length * ratio).toInt().coerceIn(0, length)

    return (0 until length).shuffled().take(count).sorted()
  }

}
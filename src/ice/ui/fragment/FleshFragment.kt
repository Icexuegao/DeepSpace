package ice.ui.fragment

import arc.Core
import arc.flabel.FLabel
import arc.func.Boolp
import arc.math.Rand
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.event.Touchable
import arc.scene.ui.layout.WidgetGroup
import arc.struct.Seq
import ice.graphics.IceColor
import ice.library.scene.ui.colorR
import ice.library.util.accessFloat
import ice.world.meta.IceEffects
import mindustry.Vars

object FleshFragment {
  var group: Group = WidgetGroup()
  var text = Seq<String>(String::class.java).apply {
    addAll(
      """
         ・ｿ關ｽ蜈･逋ｽ蜈皮噪豢樒ｩｴ荵倶ｸｭ
         蜿ｯ謔ｲ逧・諢夊｢逧・蟆丞ｧ大ｨ倡鯵荳ｽ荳・
         蝨ｨ荳榊庄諤晁ｮｮ荵句嵜貍ｫ豁･逹
         迢よｰ秘先ｸ蝉ｾｵ陏 迪ｫ蜆ｿ蜿大・蝌ｲ隨・
         譌莨第裏豁｢逧・幻莨壻ｸ・郤｢闌ｶ荵溷ｷｲ扈丞・蜃・
         蟆ｱ霑櫁ｿ呎ｨ｡讓｡邉顔ｳ顔噪諢剰ｯ・ｹ・
         豺｡蛹悶∵ｺ｢蜃ｺ 豺ｷ蜷亥惠荳襍ｷ
         蜿ｪ譛我ｸ画律譛育噪譛亥ｽｱ 譏ｭ遉ｺ逹邇ｰ蝨ｨ逧・慮髣ｴ
         隨醍捩逧・賢蜥ｪ 螯よｭ､貂ｩ譟・
         辷ｸ辷ｸ荵溷･ｽ縲∝ｦ亥ｦ井ｹ溽ｽ｢縲∝・驛ｽ荳埼怙隕∽ｺ・
         譛臥擅鮠蜿ｯ莉･蜿匁囑 譛句暑荵溷ｷｲ扈丈ｸ榊ｿ・ｦ∽ｺ・
         蜈ｨ驛ｨ 驛ｽ蝮乗脂蜷ｧ 荵ｱ菴應ｸ蝗｢逧・
         謨ｴ荳ｪ荳也阜
         蟆ｱ霑櫁ｿ咎復蟄宣㈹ 荵滓裏豕墓丐蜃ｺ譴ｦ諠ｳ荳主ｸ梧悍
         逵句賦 閭悟鋤逧・辷ｱ荳ｽ荳・
         蟆ｱ霑樊ｵｷ鮴滓ｱ､蝠・
         螯ゆｻ贋ｹ溯・螟・荳句朕莠・操
         蛛夂捩莉取悄蠕・ｸｭ騾・ｵｰ逧・｢ｦ
         逵句賦 閭悟鋤逧・辷ｱ荳ｽ荳・
         螟ｱ譛帷噪逵ｼ逾樣㈹ 鬚､謚也捩縲∝ｮｳ諤慕捩縲∝働豕｣逹
         蟆ｱ霑櫁ｿ咎復蟄宣㈹ 荵滓裏豕墓丐蜃ｺ譴ｦ諠ｳ荳主ｸ梧悍蜻｢
         逵句操 譏ｯ辷ｱ蜩ｭ鬯ｼ 辷ｱ荳ｽ荳・
         蜿ｪ譏ｯ蟄伜惠莠取ｭ､螟・ｰｱ蜿ｯ莉･莠・逃
         蜿ｯ謔ｲ逧・諢夊｢逧・蟆丞ｧ大ｨ倡鯵荳ｽ荳
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
    group.addChild(fLabel)
  }
}
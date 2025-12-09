package ice.ui.dialog

import arc.flabel.FLabel
import arc.scene.actions.Actions
import arc.scene.event.Touchable
import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Scaling
import ice.graphics.Characters
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.ui.*
import ice.library.struct.isNotEmpty
import ice.world.meta.IceStats
import mindustry.gen.Icon

object ThankDialog : BaseMenusDialog(IceStats.鸣谢.localized(), Icon.bookOpen) {
    private val hint = Seq<String>().apply {
        add("你就快要成功了是吗?")
        add("干嘛这么看着我?")
        add("你又是为何来到这里?")
        add("情况还真是急转直下啊")
        add("你戳够了没?!")
        add("你知道alon和Alon的区别吗")
    }

    override fun build() {
        cont.table {
            it.image(IStyles.tanksui)
        }.height(200f).pad(10f).row()

        cont.iPaneG { ta ->
            ta.table {
                it.add("原作者: 雪糕(不再参与)", IceColor.b4).fontScale(1.2f).padRight(50f)
                it.add("项目主管: Alon", IceColor.b4).fontScale(1.2f)
            }.padBottom(40f).row()
            ta.table {
                it.layoutLabel("主要贡献者:").fontScale(1.2f).row()
                it.layoutLabel("帕奇维克 - 血肉诅咒").itooltip("广告招租位").pad(5f).row()
                it.layoutLabel("ZL洋葱(不再参与) - 物品材质贴图包").itooltip("你知道吗,模组作者在QQ短视频上推过意义不明的奥特曼视频").pad(5f).row()
                it.layoutLabel("Reflcaly_反射 - 人物立绘").itooltip("期待与你的再次见面!再见!")
            }.padBottom(20f).row()
            ta.iTable { itable ->
                itable.layoutLabel("特别感谢:").fontScale(1.2f).row()
                itable.iTable {
                    it.left()
                    it.setRowsize(2)
                    it.layoutLabel("硫缺铅").itooltip(
                        "你的身体啊回到堕乐园啊,你的灵魂水啊回到爱之城\n你从爱的一部分,回到爱里,你温暖了乐园啊你继续爱着世界"
                    )
                    it.layoutLabel("前之骈").itooltip("请务必关注neurosama喵,谢谢喵!")
                    it.layoutLabel("喵喵怪").itooltip("界限?狗都不玩!")
                    it.layoutLabel("MrT").itooltip("某个憨批笑脸头套")
                    it.layoutLabel("坠机的牢阔").itooltip("有机会也试试[#F6A34FFF]战锤[]这个模组吖")
                    it.layoutLabel("zzc").itooltip("可以来看看牢z的独游喵")
                    it.layoutLabel("松鼠").itooltip("全部草飞")
                    it.layoutLabel("试听").itooltip("[#00F7FFFF]你说的对[][#FF0000FF]后面忘了...")
                    it.layoutLabel("Novarc").itooltip("等终末地出了我再继续写mod")
                    it.layoutLabel("HOOHHOOH").itooltip("愚昧的活着不如清醒的死去")
                    it.layoutLabel("喵子").itooltip("胡萝卜素星球")
                    it.layoutLabel("年年有鱼").itooltip("人生总有起落轻轻一笑,调整自我明天还是美好的")
                    it.layoutLabel("种余明的玉米").itooltip("你要这样我可要宣传我模了")
                    it.layoutLabel("维生素X").itooltip("[#A4A5F5FF]我爱[][#F5BAE9FF]玲纱![]")
                    it.layoutLabel("GRACHA").itooltip("*大屠戮的最后一刀刺向了自己的心脏 污浊随之翻滚喷涌")
                    it.layoutLabel("zxs").itooltip("JS异端")
                }
            }
        }.touchable(Touchable.childrenOnly)
        val x = Scl.scl(MenusDialog.backMargin)
        val characters = Characters.alon
        val fLabel = FLabel("").also { it.setColor(IceColor.b4) }
        val image = Image(characters.gal)
        val table = Table(IStyles.background33).apply {
            margin(20f)
            marginLeft(30f)
            update {
                pack()
                setPositions(MenusDialog.backMargin + image.width, MenusDialog.backMargin + image.height / 3f * 2f)
            }
            add(fLabel)
        }

        image.setPositions(x, x).apply {
            setScaling(Scaling.fit)
            val scl = 4f
            setSize(52 * scl, 72f * scl)
            update {
                characters.upfate(this) { table.actions.isEmpty }
            }
            tapped {
                if (table.actions.isNotEmpty()) return@tapped
                val newText = hint.random()
                fLabel.restart(newText)
                table.actions(
                    Actions.alpha(1f), Actions.delay((newText.split("").size * 8 / 60) + 1f), Actions.alpha(0f, 1f), Actions.remove()
                )
                cont.addChild(table)
            }
            cont.addChild(this)
        }
    }

    private fun Table.layoutLabel(string: String): Cell<Label> {
        val label = Label(string)
        return add(label.colorR(IceColor.b4)).pad(5f).expand()
    }
}
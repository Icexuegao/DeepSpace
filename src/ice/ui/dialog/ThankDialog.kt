package ice.ui.dialog

import arc.scene.Element
import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.Tooltip
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Table
import ice.library.scene.texs.Characters
import ice.library.scene.texs.Colors
import ice.library.scene.texs.Texs
import ice.ui.iPaneG
import ice.ui.iTable

object ThankDialog {
    val cont = MenusDialog.cont
    fun show() {
        cont.table {
            it.image(Texs.tanksui)
        }.height(200f).pad(10f).row()

        cont.iPaneG { ta ->
            ta.table {
                it.add("原作者: 雪糕(不再参与)", Colors.b4).padRight(50f)
                it.add("项目主管: Alon", Colors.b4)
            }.padBottom(40f).row()
            ta.table {
                it.adds("主要贡献者:").row()
                it.adds("ZL洋葱(不再参与) - 物品材质贴图包").tb("你知道吗,模组作者在QQ短视频上推过意义不明的奥特曼视频")
                    .pad(5f).grow().row()
                it.adds("Reflcaly_反射(不再参与) - 人物立绘").tb("期待与你的再次见面!再见!")
            }.padBottom(20f).row()
            ta.iTable { itable ->
                itable.adds("特别感谢:").row()
                itable.table {
                    it.left()
                    it.adds("zxs").tb("JS异端")
                    it.adds("喵喵怪").tb("界限?狗都不玩!").row()
                    it.adds("坠机的牢阔").tb("有机会也试试[#F6A34FFF]战锤[]这个模组吖")
                    it.adds("zzc").tb("可以来看看牢z的独游喵").row()
                    it.adds("松鼠").tb("全部草飞")
                    it.adds("试听").tb("[#00F7FFFF]你说的对[][#FF0000FF]后面忘了...").row()
                    it.adds("Novarc").tb("<<赞小杯子玉足>>杯子玉足香透骨,橙薤添味客忘归<>")
                    it.adds("HOOHHOOH").tb("愚昧的活着不如清醒的死去").row()
                    it.adds("喵子").tb("胡萝卜素星球")
                    it.adds("年年有鱼").tb("人生总有起落轻轻一笑,调整自我明天还是美好的").row()
                    it.adds("种余明的玉米").tb("你要这样我可要宣传我模了")
                    it.adds("维生素X").tb("[#A4A5F5FF]我爱[][#F5BAE9FF]玲纱![]").row()
                    it.adds("GRACHA").tb("*大屠戮的最后一刀刺向了自己的心脏 污浊随之翻滚喷涌")
                }
            }

            ta.addChild(Image(Characters.alon.gal).apply {sizeBy(52f*2,74f*2)})
        }
    }

    private fun Table.adds(string: String): Cell<Label> {
        return add(string, Colors.b4).pad(5f).expand()
    }

    private fun <T : Element> Cell<T>.tb(string: String): Cell<T> {
        get().addListener(Tooltip { tool ->
            tool.background(Texs.background32).margin(20f)
            tool.add(string, Colors.b4)
        })
        return this
    }
}
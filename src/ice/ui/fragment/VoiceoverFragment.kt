package ice.ui.fragment

import arc.Core
import arc.func.Boolp
import arc.math.Interp
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.event.Touchable
import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.layout.Table
import arc.scene.ui.layout.WidgetGroup
import arc.struct.Seq
import arc.util.Interval
import ice.library.IFiles
import ice.library.scene.action.IceActions
import ice.library.scene.texs.Colors
import ice.library.struct.asDrawable
import mindustry.Vars
import mindustry.gen.Tex

object VoiceoverFragment {
    var group: Group = WidgetGroup()
    fun build(parent: Group) {
        group.setFillParent(true)
        group.touchable = Touchable.childrenOnly
        group.visibility = Boolp(Vars.ui.hudfrag::shown)
        parent.addChild(group)
    }

    fun flun() {
        group.clear()
        val sseq = Seq<String>()
        "吾等之菌脉已覆盖此界17.6%表层,钢铁残骸转化效能62%,血肉养料吸收效能98%,已在稗落星净水厂和帝国\"雾棺\"实验室建立了隐秘节点\n集群适应性提升,有机体神经链接效率40%.帝国疆域12.7%,圣殿领地5.3%已接入永恒之网".split(
            "").forEach {
            sseq.add(it)
        }
        val iterator = sseq.iterator()
        var tem = ""
        val fLabel = Label("")
        val interval = Interval(1)

        fLabel.update {
            if (interval[8f]) {
                if (iterator.hasNext()) {
                    tem += iterator.next()
                }
            }
            fLabel.setText(tem)
        }
        val table = Table()
        table.add("马卡斯").color(Colors.b4).padBottom(4f).row()
        table.add(fLabel).color(Colors.b4).row()
        table.image(Tex.whiteui).color(Colors.b4).growX()

        table.update {
            table.pack()
            table.setPosition((Core.graphics.width - table.width) / 2, Core.graphics.height.toFloat() / 2)
        }
        group.addChild(table)
        val asDrawable = IFiles.findIcePng("voiceoverFragment").asDrawable()
        val fl = Core.graphics.height.toFloat() / 4
        val image = Image(asDrawable).apply {
            group.addChild(this)
            setSize(Core.graphics.width.toFloat(), fl)
            setPosition(0f, -fl)
            actions(Actions.alpha(0f), IceActions.moveToAlphaAction(0f, 0f, 2f, 1f, Interp.pow2Out))
        }

        val image1 = Image(asDrawable).apply {
            group.addChild(this)
            setSize(Core.graphics.width.toFloat(), fl)
            setPosition(0f, Core.graphics.height.toFloat())
            actions(Actions.alpha(0f), IceActions.moveToAlphaAction(0f, Core.graphics.height.toFloat()-fl, 2f, 1f, Interp.pow2Out))
        }

        table.actions(Actions.delay(sseq.size * 10f / 60 + 2), Actions.run {
            table.actions(Actions.alpha(0f, 2f), Actions.run { group.clear() })
            image1.addAction(Actions.alpha(0f, 2f))
            image.addAction(Actions.alpha(0f, 2f))
        })
    }
}
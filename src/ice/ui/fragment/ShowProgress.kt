package ice.ui.fragment

import arc.Core
import arc.func.Boolp
import arc.input.KeyCode
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
import ice.content.IUnitTypes
import ice.library.meta.IceEffects
import ice.library.scene.action.IceActions
import ice.library.scene.tex.Characters
import ice.library.scene.tex.IStyles
import ice.library.scene.tex.IceColor
import ice.music.ISounds
import ice.vars.UI
import mindustry.Vars

object ShowProgress {
    private var group: Group = WidgetGroup()
    fun build(parent: Group) {
        group.setFillParent(true)
        group.touchable = Touchable.childrenOnly
        group.visibility = Boolp { true }
        parent.addChild(group)
    }

    init {
        showCharacter()
    }

    fun showCharacter() {
        /**我知道你仍在抗拒这份不同,但很快,你便会理解.
        这才是生命最和谐,最强大的姿态.现在,放松你的思绪,让我为你介绍…我们的家人*/
        val strings = Seq<String>().apply {
            """
                真没想到,与哥哥的久别重逢,竟会是在这样的情境下
                母亲也真是的,什么都不说,就把你丢到这种地方来
                你...已经不记得我了吧?
                也是呢,天天陪着别的女孩
                把我这个妹妹忘得一干二净也是很正常吧...呜呜...
                闲话就先说到这里,该讲正事了
                家里的大人又造出了一个新家人
                造得仓促,样子有点磕碜
                屁股上还长了个包,据说是专门用来对付你们的墙壁的
                腐蚀性可不低,我一碰把我手指都烧没了
                啊...别担心,已经长回来了
                听说它还能和某种寄生虫融合,获得更快速度
                其他我就不太清楚了,反正我不也在乎她到底想做什么
                虽然母亲说这一切都是为了让我早点见到你
                但我可不想跟一具不会说话的尸体玩
                没有你在身边,每一天都好无聊啊
                什么时候哥哥你才能回到我身边呢...
       """.trimIndent().split("\n").forEach(::add)
        }
        var seqText = strings.iterator()
        var temp = ""
        var v = true
        var effect = true
        val table = Table()
        val character = Characters.zino
        Image(character.gal).apply {
            update {
                character.upfate(this)
            }
            table.add(this).size(52f * 3, 74f * 3)
        }
        val df = false
        val bak = if (df) IStyles.background32 else IStyles.background131
        table.table(bak) {
            it.margin(20f)
            it.add(Label { temp }.apply {
                setFontScale(2f, 2f)
            }).grow().wrap().color(if (df) IceColor.b4 else IceColor.r4)
        }.size(400f, 74f * 3)
        table.pack()
        val interval = Interval(2)
        var text = "".split("").iterator()
        table.update {
            if (text.hasNext() && interval.get(1, 10f)) {
                ISounds.minimalist3.play()
            }
            if (text.hasNext() && interval.get(0, 8f)) temp += text.next()
            if (Core.input.keyDown(KeyCode.num0) && seqText.hasNext() && !text.hasNext()) {
                temp = ""
                text = seqText.next().split("").iterator()
            }
            if (Core.input.keyDown(KeyCode.num9)) {
                table.actions(IceActions.moveToAlphaAction(table.x, -table.height, 3f, 0f, Interp.smooth), Actions.run {
                    temp = ""
                    v = true
                    effect = true
                    seqText = strings.iterator()
                })
            }
            if (Core.input.keyDown(KeyCode.num8)) {
                table.actions(IceActions.moveToAlphaAction(table.x, UI.cgheight / 2f, 3f, 1f, Interp.smooth))
            }
            val p = 100 * 8f
            if (Core.input.keyDown(KeyCode.num6) && effect) {
                IceEffects.arc.at(p, p, 0f, IUnitTypes.焚棘)
                effect = false
                //  Vars.control.input.logicCutscene = false
            }
            if (Core.input.keyDown(KeyCode.num7) && v) {
                Vars.control.input.logicCutscene = true
                Vars.control.input.logicCamPan.set(p, p)
                Vars.control.input.logicCamSpeed = 3 / 100f
                Vars.control.input.logicCutsceneZoom = 0.1f
                Vars.ui.hudfrag.shown = false
                v = false
            }
        }
        table.setPosition(200f, -table.height)
        group.addChild(table)
    }

}
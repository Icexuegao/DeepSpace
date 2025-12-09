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
import arc.util.Scaling
import ice.audio.ISounds
import ice.content.IUnitTypes
import ice.graphics.Characters
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.action.IceActions
import ice.world.meta.IceEffects
import ice.ui.UI
import mindustry.Vars
import mindustry.game.Team

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
        val strings = Seq<String>().apply {
            """
                啊啦啊啦,是新面孔呢~
                让我猜猜...你一定就是那个让帝国吃了大亏的帝国督察官对吧
                比我想象中要年轻呢~
                我是迷蕊斯,负责处理玛吉雅上的异端
                亲爱的~你的事情在教会可不是什么秘密哦~
                独自带队撕开帝国防线,带着平民突围这种事情可不多见
                有件事我很好奇
                是什么让你最终下定决心跟随枢机的呢?
                呵呵,不愿意吗,那就不追问你的小秘密咯~
                每个人都有不想说的过去
                重要的是你现在站在这里,在教会,在我面前   
                啊啦,看你眼睛都亮了呢~
                这只小可爱,就当是...姐姐送给新人的见面礼吧
                时间不早了,我还有一堆麻烦事要处理呢
                哦对了,如果你把它弄坏了…我可是会生气的哦~
       """.trimIndent().split("\n").forEach(::add)
        }
        var seqText = strings.iterator()
        var temp = ""
        val table = Table()
        val character = Characters.迷惢思
        Image(character.gal).apply {
            setScaling(Scaling.fit)
            update {
               // character.upfate(this)
            }
            table.add(this).grow().width(200f)
        }
        val bak = IStyles.background81// IStyles.background32 else IStyles.background131
        table.table(bak) {
            it.margin(20f)
            it.add(Label { temp }.apply {
                setFontScale(2f, 2f)
            }).grow().wrap().color(IceColor.b4 /*else IceColor.r4*/)
        }.size(400f, 74f * 3)
        table.pack()
        val interval = Interval(2)
        var text = "".split("").iterator()
        table.update {
            if (text.hasNext() && interval.get(1, 10f)) {
                ISounds.minimalist3.play()
            }
            if (text.hasNext() && interval.get(0, 8f)) {
                temp += text.next()
            }
            if (Core.input.keyDown(KeyCode.num0) && seqText.hasNext() && !text.hasNext()) {
                temp = ""
                text = seqText.next().split("").iterator()
            }
            if (Core.input.keyTap(KeyCode.num9)) {
                table.actions(IceActions.moveToAlphaAction(table.x, -table.height, 3f, 0f, Interp.smooth), Actions.run {
                    temp = ""
                    seqText = strings.iterator()
                })
            }
            if (Core.input.keyTap(KeyCode.num8)) {
                table.actions(IceActions.moveToAlphaAction(table.x, UI.cgheight / 2f, 3f, 1f, Interp.smooth))
            }

            if (Core.input.keyTap(KeyCode.num1)) {
                val x1 = Core.input.mouseWorldX()
                val y1 = Core.input.mouseWorldY()
                IceEffects.getPhaseJump(x1, y1, 0f, IceColor.r5, Vars.player.team(), IUnitTypes.虚宿, IStyles.afehs)
            }
            if (Core.input.keyTap(KeyCode.num2)) {
                val x1 = Core.input.mouseWorldX()
                val y1 = Core.input.mouseWorldY()
                IceEffects.getPhaseJump(x1, y1, 180f, IceColor.r5, Team.crux, IUnitTypes.无畏, IStyles.empire)
                val kz = Vars.content.unit("curse-of-flesh-空炸2")
                IceEffects.getPhaseJump(x1, y1 + 60, 180f, IceColor.r5, Team.crux, kz, IStyles.empire)
                IceEffects.getPhaseJump(x1, y1 - 60, 180f, IceColor.r5, Team.crux, kz, IStyles.empire)
            }

            if (Core.input.keyTap(KeyCode.num6)) {
                Vars.control.input.logicCutscene = true
                Vars.control.input.logicCamPan.set(Core.input.mouseWorldX(), Core.input.mouseWorldY())
                Vars.control.input.logicCamSpeed = 3 / 100f
                Vars.control.input.logicCutsceneZoom = 0.05f
                Vars.ui.hudfrag.shown = false
            }

            if (Core.input.keyTap(KeyCode.num5)) {
                Vars.control.input.logicCutscene = false
            }
        }
        table.setPosition(200f, -table.height)
        group.addChild(table)
    }
}
package ice.ui.dialog

import arc.Core
import arc.func.Cons
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Scaling
import ice.Ice
import ice.content.IBlocks
import ice.content.IItems
import ice.content.IStatus
import ice.content.IUnitTypes
import ice.library.IFiles
import ice.library.content.blocks.environment.IceOreBlock
import ice.library.content.unit.ability.InterceptAbilty
import ice.library.meta.stat.IceStats
import ice.library.scene.tex.IStyles
import ice.library.scene.tex.IceColor
import ice.library.struct.forEach
import ice.music.ISounds
import ice.ui.iTable
import ice.ui.iTableG
import ice.ui.icePane
import ice.ui.itooltip
import ice.vars.UI
import mindustry.Vars
import mindustry.gen.Icon
import mindustry.world.consumers.ConsumeItems
import mindustry.world.meta.Stats

object RemainsDialog: BaseDialog(IceStats.遗物.localized(),Icon.logic){
    val remainsSeq = Seq<Remains>()
    val enableSeq = Seq<Remains>()
    lateinit var tempRemain: Remains
    var slotPos = 4
    lateinit var remainsTable: Table
    lateinit var enableTable: Table
    fun load() {
        Remains("娜雅的手串") {
            setDescription("一串温润的玉石手串,在帝国任职期间由娜雅赠予")
            effect = "核心机增加拦截护盾"
            val lucifer = IUnitTypes.路西法
            val abilitie = InterceptAbilty(40f, lucifer.hitSize + 5)
            installFun {
                lucifer.abilities.addUnique(abilitie)
                lucifer.stats = Stats()
                lucifer.checkStats()
            }
            uninstallFun {
                lucifer.abilities.remove(abilitie)
                lucifer.stats = Stats()
                lucifer.checkStats()
            }
        }
        Remains("坚固的装甲板") {
            val hea = 500
            setDescription("由多层淬火钢板铆接而成,表面布满划痕与凹坑")
            effect = "单位[${IUnitTypes.断业.localizedName}]的生命值提升[$hea]"
            installFun {
                IUnitTypes.断业.health += hea
                IUnitTypes.断业.stats = Stats()
                IUnitTypes.断业.checkStats()
            }
            uninstallFun {
                IUnitTypes.断业.health -= hea
                IUnitTypes.断业.stats = Stats()
                IUnitTypes.断业.checkStats()
            }
        }
        Remains("不焚者的余烬") {
            val f = 5
            setDescription("温热的结晶体,烈焰中被焚尽却未曾死去之人的最后残留")
            effect = "单位[${IUnitTypes.仆从.localizedName}]的武器伤害提升[$f]"
            installFun {
                IUnitTypes.仆从.weapons.forEach {
                    it.bullet.damage += if (it.mirror) f / 2 else f
                }
                IUnitTypes.仆从.stats = Stats()
                IUnitTypes.仆从.checkStats()
            }
            uninstallFun {
                IUnitTypes.仆从.weapons.forEach {
                    it.bullet.damage -= if (it.mirror) f / 2 else f
                }
                IUnitTypes.仆从.stats = Stats()
                IUnitTypes.仆从.checkStats()
            }
        }
        Remains("纯净水晶坠饰") {
            setDescription("一块天然形成,毫无杂质的透明白水晶")
            effect = "玩家核心机[免疫所有状态]"
        }
        Remains("不朽者胚胎") {
            color = IceColor.r2
            installFun {
                slotPos += 2
            }
            uninstallFun {
                slotPos -= 2
                enableSeq.forEach(enableSeq.size - slotPos) {
                    it.setEnabled(false)
                }
            }
            val text = "一个被囚禁的血肉胚胎\n拥抱我,我将赐你永恒\n不必畏惧刀剑与瘟疫,不必屈服于时光与死亡\n用你的过去,换取未来\n用你的灵魂,换取存在\n直至你我合而为一"
            setDescriptionTable {
                for (string in text.split("\n")) {
                    it.add(string).pad(5f).color(color).row()
                }
            }
            effect = "遗物槽位+[2]"
        }
        Remains("脊骨寄生虫") {
            color = IceColor.r2
            setDescriptionTable {
                it.add("一种具有高度神经亲和性的节状生物,渴望与血肉生物的中枢神经系统结合").pad(5f).color(color).row()
                it.table { it ->
                    it.add("影响单位: ").pad(5f).color(color)
                    it.image(IUnitTypes.蚀虻.uiIcon).size(45f).scaling(Scaling.fit)
                        .itooltip("${IUnitTypes.蚀虻.localizedName}")
                }
            }
            val fg = 1.2f
            effect = "[爬行类]血肉畸变体速度提升[${((fg - 1) * 100).toInt()}%]"
            installFun {
                IUnitTypes.蚀虻.speed *= fg
                IUnitTypes.蚀虻.stats = Stats()
                IUnitTypes.蚀虻.checkStats()
                IUnitTypes.蚀虻Middle.speed *= fg
                IUnitTypes.蚀虻Middle.stats = Stats()
                IUnitTypes.蚀虻Middle.checkStats()
                IUnitTypes.蚀虻End.speed *= fg
                IUnitTypes.蚀虻End.stats = Stats()
                IUnitTypes.蚀虻End.checkStats()
            }
            uninstallFun {
                IUnitTypes.蚀虻.speed /= fg
                IUnitTypes.蚀虻.stats = Stats()
                IUnitTypes.蚀虻.checkStats()
                IUnitTypes.蚀虻Middle.speed /= fg
                IUnitTypes.蚀虻Middle.stats = Stats()
                IUnitTypes.蚀虻Middle.checkStats()
                IUnitTypes.蚀虻End.speed /= fg
                IUnitTypes.蚀虻End.stats = Stats()
                IUnitTypes.蚀虻End.checkStats()
            }
        }
        Remains("心跳鼓") {
            color = IceColor.r2
            setDescription("带有奇异弹性的心肌隔膜,沉稳的节拍能让你的心跳同步")
            effect = "使状态[${IStatus.回响.localizedName}]的影响提升[20%]"
            installFun {
                IStatus.回响.speedMultiplier += 0.2f
                IStatus.回响.stats = Stats()
                IStatus.回响.checkStats()
            }
            uninstallFun {
                IStatus.回响.speedMultiplier -= 0.2f
                IStatus.回响.stats = Stats()
                IStatus.回响.checkStats()
            }
        }
        Remains("玄岩板") {
            setDescription("由奇异,沉重的玄武岩打磨而成")
            effect = "[碳控熔炉]所需燃料减少[1]"
            installFun {
                IBlocks.碳控熔炉.formulas.formulas.forEach {
                    it.inputs?.forEach { cons ->
                        if (cons is ConsumeItems) {
                            cons.items.forEach { itemStack ->
                                if (itemStack.item == IItems.生煤) {
                                    itemStack.amount -= 1
                                }
                            }
                        }
                    }
                }
                IBlocks.碳控熔炉.stats = Stats()
                IBlocks.碳控熔炉.checkStats()
            }
            uninstallFun {
                IBlocks.碳控熔炉.formulas.formulas.forEach {
                    it.inputs?.forEach { cons ->
                        if (cons is ConsumeItems) {
                            cons.items.forEach { itemStack ->
                                if (itemStack.item == IItems.生煤) {
                                    itemStack.amount += 1
                                }
                            }
                        }
                    }
                }
                IBlocks.碳控熔炉.stats = Stats()
                IBlocks.碳控熔炉.checkStats()
            }
        }
        Remains("谐振探针"){
            setDescription("一种用于探测矿物谐振频率的装置,可以显示隐藏矿层")
            effect = "矿物地板不再[隐藏]"
            installFun {
                Vars.content.blocks().forEach {
                    if (it is IceOreBlock) {
                        it.display = true
                    }
                }
                Vars.renderer.blocks.floor.reload()
            }
            uninstallFun {
                Vars.content.blocks().forEach {
                    if (it is IceOreBlock) {
                        it.display = false
                    }
                }
                Vars.renderer.blocks.floor.reload()
            }
        }
        tempRemain = if (enableSeq.isEmpty) remainsSeq.first() else enableSeq.first()
    }

   override fun build() {
        cont.iTable {
            var t = tempRemain
            fun flun() {
                it.clearChildren()
                it.image(tempRemain.icon).size(90f).pad(10f).row()
                it.add(tempRemain.name).pad(5f).color(tempRemain.color).row()
                it.add(tempRemain.customTable).row()
                it.add("效果: ${tempRemain.effect}").color(tempRemain.color)
            }
            flun()
            it.update {
                if (t != tempRemain) {
                    t = tempRemain
                    flun()
                }
            }
        }.pad(50f).padTop(100f).minHeight(360f).row()

        cont.iTable { ta ->
            ta.add(Label { "正在生效:[${enableSeq.size} / $slotPos]" }).color(IceColor.b4).pad(10f).row()
            ta.iTableG {
                it.setRowsize(5)
                enableTable = it
                flunEnableSeq()
            }
        }.pad(30f).row()
        cont.image(IStyles.whiteui).color(IceColor.b1).height(3f).growX().row()
        cont.iTableG {
            it.top()
            it.add("已拥有:").color(IceColor.b4).pad(10f).row()
            it.icePane { ip ->
                ip.setRowsize(10)
                remainsTable = ip
                flunRemainsSeq()
            }
        }
    }

    private fun flunRemainsSeq() {
        remainsTable.clearChildren()
        remainsSeq.forEach { item ->
            remainsTable.button(item.icon, IStyles.button) {
                if (enableSeq.size < slotPos && enableSeq.addUnique(item)) {
                    item.setEnabled(true)
                }
            }.disabled {
                Vars.state.isGame
            }.size(60f).pad(10f).get().hovered {
                tempRemain = item
            }
        }
    }

    private fun flunEnableSeq() {
        enableTable.clearChildren()
        enableSeq.forEach { item ->
            enableTable.button(item.icon, item.buttonStyle) {
                item.setEnabled(false)
            }.disabled {
                Vars.state.isGame
            }.size(60f).pad(10f).get().hovered {
                tempRemain = item
            }
        }
        (1..(slotPos - enableSeq.size)).forEach { i ->
            enableTable.add(Image(IStyles.button.up)).size(60f).pad(10f)
        }
    }

    class Remains(
        val name: String, applys: Remains.() -> Unit = {}
    ) {
        var localizedName: String = ""
        var effect = ""
        var icon = if (IFiles.hasIcePng(name)) TextureRegionDrawable(
            IFiles.findIcePng(name)) else TextureRegionDrawable(IItems.红冰.uiIcon)
        var color = IceColor.b4
        private var install = {}
        private var uninstall = {}
        var customTable = Table()
        var buttonStyle = IStyles.button5

        init {
            applys(this)
            val bool = Core.settings.getBool(Ice.name + "-remains-" + name, false)
            if (bool) {
                enableSeq.add(this)
                install()
            } else {
                remainsSeq.add(this)
            }
        }

        fun setDescriptionTable(table: Cons<Table>) {
            table.get(customTable)
        }

        fun setDescription(desc: String) {
            customTable.add(desc).pad(5f).color(color).row()
        }

        fun installFun(install: () -> Unit) {
            this.install = install
        }

        fun uninstallFun(uninstall: () -> Unit) {
            this.uninstall = uninstall
        }

        fun setEnabled(enabled: Boolean) {
            if (enabled) {
                remainsSeq.remove(this)
                ISounds.remainInstall.play()
                install()
            } else {
                enableSeq.remove(this)
                remainsSeq.addUnique(this)
                ISounds.remainUninstall.play(UI.sfxVolume + 1)
                uninstall()
            }
            Core.settings.put(Ice.name + "-remains-" + name, enabled)
            flunRemainsSeq()
            flunEnableSeq()
        }

    }
}
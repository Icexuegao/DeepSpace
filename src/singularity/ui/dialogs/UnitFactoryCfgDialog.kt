package singularity.ui.dialogs

import arc.Core
import arc.func.*
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.input.KeyCode
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.scene.Element
import arc.scene.actions.Actions
import arc.scene.event.InputEvent
import arc.scene.event.InputListener
import arc.scene.event.Touchable
import arc.scene.style.Drawable
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.*
import arc.scene.ui.layout.Collapser
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import arc.struct.IntSeq
import arc.struct.Seq
import arc.util.*
import mindustry.Vars
import mindustry.core.UI
import mindustry.entities.Units
import mindustry.gen.Icon
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.type.UnitType
import mindustry.ui.Bar
import mindustry.ui.Fonts
import mindustry.ui.Styles
import mindustry.ui.dialogs.BaseDialog
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.modules.ItemModule.ItemConsumer
import singularity.graphic.SglDrawConst
import singularity.ui.SglStyles
import singularity.world.blocks.product.SglUnitFactory
import singularity.world.blocks.product.SglUnitFactory.*
import singularity.world.consumers.SglConsumeEnergy
import singularity.world.consumers.SglConsumeType
import singularity.world.consumers.SglConsumers
import singularity.world.meta.SglStat
import singularity.world.meta.SglStatUnit
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.ConsumeItemBase
import universecore.world.consumers.ConsumePower
import universecore.world.consumers.ConsumeType
import universecore.world.producers.BaseProducers
import universecore.world.producers.ProducePayload
import universecore.world.producers.ProduceType
import java.lang.Float
import java.text.Collator
import kotlin.Boolean
import kotlin.Comparator
import kotlin.Int
import kotlin.String
import kotlin.Throwable
import kotlin.arrayOfNulls
import kotlin.floatArrayOf
import kotlin.intArrayOf
import kotlin.math.max
import kotlin.run

class UnitFactoryCfgDialog : BaseDialog(Core.bundle.get("dialog.unitFactor.title")) {
    var currConfig: SglUnitFactoryBuild? = null
    var taskQueue: Table? = null
    var status: Table? = null
    var sideButton: Table? = null
    var commandCfg: Table? = null
    var commandCfgTab: Table? = null
    var commandConfiguring: Boolean = false
    var configuringPos: Vec2 = Vec2()
    var configCmdTask: BuildTask? = null
    var curr: BuildTask? = null
    var pri: Int = 0
    var fold: Boolean = false
    var makeTask: Dialog = object : BaseDialog(Core.bundle.get("dialog.unitFactor.makeTask")) {
        inner class Sorter(val name: String?, val sort: Comparator<UnitType?>, val icon: Prov<Drawable?>)

        private val compare: Collator = Collator.getInstance(Core.bundle.getLocale())
        private val sorts: Seq<Sorter?> = Seq.with<Sorter?>(
            Sorter("default", Comparator { t1: UnitType?, t2: UnitType? -> 0 }, Prov { Icon.list }),
            Sorter("name", Comparator { t1: UnitType?, t2: UnitType? -> compare.compare(t2!!.localizedName, t1!!.localizedName) }, Prov { Icon.edit }),
            Sorter("size", Comparator { t1: UnitType?, t2: UnitType? -> Float.compare(t1!!.hitSize, t2!!.hitSize) }, Prov { Icon.resize }),
            Sorter("health", Comparator { t1: UnitType?, t2: UnitType? -> Float.compare(t1!!.health, t2!!.health) }, Prov { Icon.add }),
            Sorter("cost", Comparator { t1: UnitType?, t2: UnitType? -> Float.compare(t1!!.getBuildTime(), t2!!.getBuildTime()) }, Prov { Icon.hammer }),
            Sorter("strength", Comparator { t1: UnitType?, t2: UnitType? -> Float.compare(t1!!.estimateDps(), t2!!.estimateDps()) }, Prov { Icon.power })
        )
        private val tmpList = Seq<BaseProducers>()
        var searching: String = ""
        var showLocking: Boolean = false
        var reverse: Boolean = false
        var rebuild: Runnable? = null
        var sort: Int = 0

        init {
            addCloseButton()

            cont.table(Cons { t: Table ->
                t.top().table(Cons { top: Table ->
                    top.image(Icon.zoom).size(40f)
                    top.field("", Cons { tex: String ->
                        searching = tex
                        rebuild!!.run()
                    }).growX()
                    top.button(Cons { b: Button? -> b!!.image().size(38f).scaling(Scaling.fit).update(Cons { i: Image? -> i!!.setDrawable(if (showLocking) Icon.lock else Icon.lockOpen) }) }, Styles.clearNonei, Runnable {
                        showLocking = !showLocking
                        rebuild!!.run()
                    }).size(50f)
                    top.add("").update(Cons { l: Label? -> l!!.setText(Core.bundle.format("dialog.unitFactor.sort", Core.bundle.get("dialog.unitFactor.sort_" + sorts.get(sort)!!.name))) })
                    top.button(Cons { b: Button? -> b!!.image().size(38f).scaling(Scaling.fit).update(Cons { i: Image? -> i!!.setDrawable(sorts.get(sort)!!.icon.get()) }) }, Styles.clearNonei, Runnable {
                        sort = (sort + 1) % sorts.size
                        rebuild!!.run()
                    }).size(50f)
                    top.button(Cons { b: Button? -> b!!.image().size(38f).scaling(Scaling.fit).update(Cons { i: Image? -> i!!.setDrawable(if (reverse) Icon.up else Icon.down) }) }, Styles.clearNonei, Runnable {
                        reverse = !reverse
                        rebuild!!.run()
                    }).size(50f)
                    top.add("").update(Cons { l: Label? -> l!!.setText(Core.bundle.get(if (reverse) "dialog.unitFactor.reverse" else "dialog.unitFactor.order")) }).color(Pal.accent)
                }).growX().padLeft(120f).padRight(120f).fillY()
                t.row()
                t.pane(Cons { list: Table ->
                    rebuild = Runnable {
                        list.clearChildren()
                        list.defaults().growX().fillY().pad(4f)

                        if (currConfig == null) return@Runnable

                        tmpList.clear()
                        tmpList.add((currConfig!!.block as SglUnitFactory).producers!!.select(Boolf { e: BaseProducers? ->
                            e!!.cons!!.selectable.get() == BaseConsumers.Visibility.usable || (showLocking && e.cons!!.selectable.get() == BaseConsumers.Visibility.unusable)
                        }))
                        val s = sorts.get(sort)!!.sort
                        val fs = if (reverse) s else Comparator { a: UnitType?, b: UnitType? -> s.compare(b, a) }
                        tmpList.sort(Comparator { a: BaseProducers?, b: BaseProducers? ->
                            val payloadsA = if (a!!.get<ProducePayload<*>>(ProduceType.payload) == null) null else a.get<ProducePayload<*>>(ProduceType.payload)!!.payloads[0]
                            val payloadsB = if (b!!.get<ProducePayload<*>>(ProduceType.payload) == null) null else b.get<ProducePayload<*>>(ProduceType.payload)!!.payloads[0]

                            if (payloadsA == null || payloadsB == null) return@Comparator 0
                            val uA = payloadsA.item as UnitType?
                            val uB = payloadsB.item as UnitType?
                            fs.compare(uA, uB)
                        })
                        for (i in 0..<tmpList.size) {
                            val prod = tmpList.get(i)
                            val cons = prod.cons
                            val payloads = if (prod.get<ProducePayload<*>>(ProduceType.payload) == null) null else prod.get<ProducePayload<*>>(ProduceType.payload)!!.payloads
                            if (payloads != null && payloads.size == 1 && payloads[0]!!.item is UnitType) {
                                var item = payloads[0]!!.item as UnitType
                                if (!searching.trim { it <= ' ' }.isEmpty() && !(item.name.contains(searching.trim { it <= ' ' }) || item.localizedName.contains(searching.trim { it <= ' ' }))) continue
                                val index = (currConfig!!.block as SglUnitFactory).consumers.indexOf(cons)
                                val button: Button = object : Button() {
                                    init {
                                        setStyle(Styles.grayt)

                                        clicked(Runnable {
                                            object : BaseDialog("", SglStyles.transparentBack) {
                                                var amount: Int = 1
                                                var tip: Table? = null

                                                init {
                                                    addCloseButton()
                                                    val rebuild = Runnable {
                                                        cont.clearChildren()
                                                        cont.table(SglDrawConst.grayUIAlpha, Cons { t: Table ->
                                                            t.left().defaults().left().growX().pad(4f)
                                                            t.top().table(Cons { top: Table ->
                                                                top.top().defaults().left()
                                                                top.image(item.fullIcon).size(325f).scaling(Scaling.fit).pad(6f)

                                                                if (Core.graphics.isPortrait()) top.row()
                                                                top.table(Cons { info: Table ->
                                                                    info.top().defaults().left().growX().pad(4f)
                                                                    info.add(item.localizedName).color(Pal.accent)
                                                                    info.row()
                                                                    info.add(item.description).wrap().color(Pal.lightishGray)
                                                                    info.row()
                                                                    info.table(Cons { req: Table ->
                                                                        req.left().defaults().left().fill().pad(4f)
                                                                        req.add(Stat.buildCost.localized() + ":")
                                                                        val ci = cons!!.get<ConsumeItemBase<*>>(ConsumeType.item)
                                                                        val cp = cons.get<ConsumePower<*>>(ConsumeType.power)
                                                                        val cn = cons.get<SglConsumeEnergy<*>>(SglConsumeType.energy)

                                                                        if (ci != null) {
                                                                            req.row()
                                                                            req.table(Cons { li: Table ->
                                                                                li.left().defaults().left().size(84f, 48f)
                                                                                var i = 0
                                                                                for (stack in ci.consItems!!) {
                                                                                    li.table(Cons { it: Table ->
                                                                                        it.left().defaults().left()
                                                                                        it.image(stack.item.fullIcon).scaling(Scaling.fit)
                                                                                        it.add(UI.formatAmount(stack.amount.toLong())).padLeft(3f)
                                                                                    })
                                                                                    i++
                                                                                    if (i % 4 == 0) li.row()
                                                                                }
                                                                            })
                                                                        }

                                                                        for (consume in cons.all()) {
                                                                            if (consume === ci || consume === cp || consume === cn) continue
                                                                            req.row()
                                                                            req.table(Cons { ln: Table ->
                                                                                ln.left().defaults().left()
                                                                                consume.buildIcons(ln)
                                                                            })
                                                                        }

                                                                        if (cp != null) {
                                                                            req.row()
                                                                            req.add(Stat.powerUse.localized() + ": " + cp.usage * 60 + StatUnit.perSecond.localized())
                                                                        }
                                                                        if (cn != null) {
                                                                            req.row()
                                                                            req.add(SglStat.consumeEnergy.localized() + ": " + cn.usage * 60 + SglStatUnit.neutronFluxSecond.localized())
                                                                        }
                                                                    })
                                                                    info.row()
                                                                    info.add(Stat.buildTime.localized() + ": " + timeFormat(cons!!.craftTime)).color(Color.gray)
                                                                }).grow().pad(4f)
                                                            })

                                                            t.row()
                                                            t.image().color(Color.lightGray).height(4f).pad(0f).padTop(4f).padBottom(4f).growX()
                                                            t.row()
                                                            t.table(Cons { am: Table ->
                                                                am.defaults().padLeft(5f).padRight(5f)
                                                                am.button(Icon.up, Styles.cleari, Runnable { amount++ }).size(48f).disabled(Boolf { i: ImageButton? -> amount >= Units.getCap(currConfig!!.team) })
                                                                am.button(Icon.down, Styles.cleari, Runnable { amount-- }).size(48f).disabled(Boolf { i: ImageButton? -> amount <= 1 })
                                                                am.add("").update(Cons { l: Label? -> l!!.setText(Core.bundle.format("dialog.unitFactor.createAmount", amount)) }).growX()
                                                            })
                                                            t.row()
                                                            t.table(Cons { button: Table ->
                                                                button.defaults().height(48f).pad(5f)
                                                                button.button(Core.bundle.get("misc.details"), Icon.info, Styles.grayt, 32f, Runnable {
                                                                    Vars.ui.content.show(item)
                                                                }).growX()
                                                                button.button(Core.bundle.get("misc.add"), Icon.add, Styles.grayt, 32f, Runnable {
                                                                    if (currConfig!!.taskCount() >= (currConfig!!.block as SglUnitFactory).maxTasks) {
                                                                        tip!!.clearActions()
                                                                        tip!!.actions(
                                                                            Actions.alpha(1f, 0.3f), Actions.delay(1.5f), Actions.alpha(0f, 0.8f)
                                                                        )
                                                                    } else {
                                                                        currConfig!!.configure(cfgArgs(ConfigCmd.ADD_TASK, item.id.toInt(), amount, index))
                                                                        rebuild(currConfig!!)

                                                                        hide()
                                                                    }
                                                                }).growX()
                                                            })
                                                        }).fill()
                                                        cont.row()
                                                        tip = cont.table(SglDrawConst.grayUIAlpha, Cons { t: Table -> t.add(Core.bundle.get("dialog.unitFactor.addFaid")).color(Color.red) }).margin(6f).fill().get()
                                                        tip!!.color.a = 0f
                                                    }

                                                    resized(rebuild)
                                                    shown(rebuild)
                                                }
                                            }.show()
                                        })

                                        left().defaults().left()
                                        image(item.uiIcon).size(80f).scaling(Scaling.fit).pad(5f)
                                        table(Cons { inf: Table ->
                                            inf.defaults().fill().left().pad(4f)
                                            inf.add(item.localizedName).color(Pal.accent)
                                            inf.row()
                                            inf.table(Cons { req: Table ->
                                                req.left().defaults().left().padRight(2f)
                                                req.add(Stat.buildCost.localized() + ":").padRight(4f)
                                                val tmp = Table()
                                                for (consume in cons!!.all()) {
                                                    consume.buildIcons(tmp)
                                                }
                                                val seq = tmp.getChildren()
                                                val items = seq.begin()
                                                val s = if (Core.graphics.isPortrait()) 8 else 6
                                                run {
                                                    var i = 0
                                                    val n = seq.size
                                                    while (i < n) {
                                                        val item = items[i]

                                                        if (i > s) {
                                                            req.add(Core.bundle.format("infos.andMore", n - s))
                                                            break
                                                        }
                                                        req.add<Element?>(item)
                                                        i++
                                                    }
                                                }
                                                seq.end()
                                            })
                                            inf.row()
                                            inf.add(Stat.buildTime.localized() + ": " + timeFormat(cons!!.craftTime)).color(Color.gray)
                                        })
                                    }

                                    override fun draw() {
                                        super.draw()

                                        if (cons!!.selectable.get() == BaseConsumers.Visibility.unusable) {
                                            Draw.color(Pal.darkerGray)
                                            Draw.alpha(0.8f * parentAlpha)

                                            Fill.rect(x + width / 2, y + height / 2, width, height)

                                            Draw.color(Color.lightGray, parentAlpha)
                                            Icon.lock.draw(x + width / 2 - 16, y + height / 2 + 8, 32f, 32f)

                                            Draw.color(Color.gray, parentAlpha)

                                            Fonts.outline.draw(Core.bundle.get("dialog.unitFactor.unresearch"), x + width / 2, y + height / 2 - 8, Tmp.c1.set(Pal.lightishGray).a(parentAlpha), 1f, false, Align.center)
                                        }
                                    }
                                }
                                list.add<Button?>(button).disabled(Boolf { b: Button? -> cons!!.selectable.get() != BaseConsumers.Visibility.usable })

                                if (Core.graphics.isPortrait() || i % 2 == 1) list.row()
                            }
                        }
                    }
                }).padLeft(60f).padRight(60f).growX().fillY().top()
            }).grow()

            resized(rebuild)

            shown(rebuild)
        }
    }

    init {
        val rebuildButtons = Runnable {
            buttons.clearChildren()
            buttons.defaults().reset()
            val portrait = Core.graphics.isPortrait()
            if (portrait) {
                buttons.defaults().size(480f, 64f).pad(4f)
            } else buttons.defaults().size(210f, 64f).pad(3f)
            var ta = if (portrait) buttons.table(Cons { t: Table -> t.defaults().grow() }).get() else buttons
            ta.button("@back", Icon.left, Styles.grayt, Runnable { this.hide() }).margin(6f)
            addCloseListener()
            ta.button(Core.bundle.get("misc.add"), Icon.add, Styles.grayt, Runnable {
                makeTask.show()
            }).margin(6f)

            if (portrait) {
                buttons.row()
                ta = buttons.table(Cons { t: Table -> t.defaults().grow() }).get()
            }
            val play = Cons { tab: Table? ->
                tab!!.button("", object : TextureRegionDrawable(Icon.play) {
                    override fun draw(x: kotlin.Float, y: kotlin.Float, width: kotlin.Float, height: kotlin.Float) {
                        if (currConfig != null && currConfig!!.activity) {
                            Icon.cancel.draw(x, y, width, height)
                        } else super.draw(x, y, width, height)
                    }

                    override fun draw(x: kotlin.Float, y: kotlin.Float, originX: kotlin.Float, originY: kotlin.Float, width: kotlin.Float, height: kotlin.Float, scaleX: kotlin.Float, scaleY: kotlin.Float, rotation: kotlin.Float) {
                        if (currConfig != null && currConfig!!.activity) {
                            Icon.cancel.draw(x, y, originX, originY, width, height, scaleX, scaleY, rotation)
                        } else super.draw(x, y, originX, originY, width, height, scaleX, scaleY, rotation)
                    }
                }, Styles.grayt, Runnable {
                    if (currConfig == null) return@Runnable
                    currConfig!!.configure(cfgArgs(ConfigCmd.ACTIVITY, if (currConfig!!.activity) -1 else 1))
                }).update(Cons { t: TextButton? ->
                    if (currConfig == null) return@Cons
                    t!!.setText(Core.bundle.get(if (currConfig!!.activity) "misc.stop" else "misc.execute"))
                }).margin(6f)
            }

            if (!portrait) play.get(ta)

            ta.button(Core.bundle.get("misc.import"), Icon.download, Styles.grayt, Runnable {
                object : BaseDialog("") {
                    init {
                        setStyle(SglStyles.transparentBack)

                        cont.table(SglDrawConst.grayUIAlpha, Cons { t: Table ->
                            t.defaults().size(320f, 58f)
                            t.add(Core.bundle.get("dialog.unitFactor.imports")).padBottom(12f).center().labelAlign(Align.center)
                            t.row()
                            val load = Boolc { b: Boolean ->
                                hide()
                                try {
                                    currConfig!!.deserializeTask(Core.app.getClipboardText().replace("\r\n", "\n"), b)
                                    rebuild(currConfig!!)
                                } catch (e: Throwable) {
                                    Vars.ui.showException(e)
                                }
                            }
                            t.button(Core.bundle.get("infos.override"), Icon.download, Styles.flatt, Runnable { load.get(false) }).margin(6f)
                            t.row()
                            t.button(Core.bundle.get("infos.append"), Icon.downOpen, Styles.flatt, Runnable { load.get(true) }).margin(6f)
                            t.row()
                            t.button(Core.bundle.get("misc.cancel"), Icon.cancel, Styles.flatt, Runnable { this.hide() }).margin(6f)
                        }).fill().margin(8f)
                    }
                }.show()
            }).margin(6f)

            ta.button(Core.bundle.get("misc.export"), Icon.upload, Styles.grayt, Runnable {
                val str = currConfig!!.serializeTasks()
                Core.app.setClipboardText(str)
                Vars.ui.showInfoFade(Core.bundle.get("dialog.unitFactor.exported"), 3f)
            }).margin(6f)
            if (portrait) {
                buttons.row()
                play.get(buttons)
            }
        }

        resized(rebuildButtons)
        shown(rebuildButtons)
    }

    fun build() {
        Vars.ui.hudGroup.fill(Cons { t: Table ->
            t.add(object : Element() {
                override fun draw() {
                    if (!commandConfiguring) return
                    val viewPos = Core.camera.project(configuringPos.x, configuringPos.y)
                    Draw.color(Pal.darkerGray)
                    Lines.stroke(12f)
                    Lines.square(viewPos.x, viewPos.y, Scl.scl(28f), 45f)
                    Draw.color(Pal.accent)
                    Lines.stroke(6f)
                    Lines.square(viewPos.x, viewPos.y, Scl.scl(28f), 45f)
                    val lerp = (Time.time % 120f) / 120f
                    Lines.stroke(6 * (1 - lerp))
                    Lines.square(viewPos.x, viewPos.y, Scl.scl(28 + 85 * lerp), 45f)
                    super.draw()
                }
            })
            val buttons = Table(SglDrawConst.grayUIAlpha, Cons { tab: Table? ->
                tab!!.table(Cons { cmds: Table -> commandCfgTab = cmds }).pad(5f).fill()
                tab.button(Icon.ok, Styles.clearNonei, Runnable {
                    currConfig!!.configure(
                        cfgArgs(
                            ConfigCmd.COMMAND, currConfig!!.indexOfTask(configCmdTask), (configuringPos.x * 1000).toInt(), (configuringPos.y * 1000).toInt() // UnitCommand.all.indexOf(configCmdTask.command)
                        )
                    )
                    t.visible = false
                    configCmdTask = null
                    commandConfiguring = false
                    configuringPos.set(kotlin.Float.Companion.MIN_VALUE, kotlin.Float.Companion.MIN_VALUE)
                    show()
                }).size(50f).pad(5f)
                tab.setTransform(true)
            })

            t.add<Table?>(buttons)

            commandCfg = t

            t.visible(Boolp { commandConfiguring && Vars.state.isGame() })
            t.touchable = Touchable.enabled
            val touchedPos = Vec2()
            val time = floatArrayOf(0f)
            t.addListener(object : InputListener() {
                override fun touchDown(event: InputEvent?, x: kotlin.Float, y: kotlin.Float, pointer: Int, button: KeyCode?): Boolean {
                    val hit = t.hit(x, y, true)
                    if (Core.input.useKeyboard() || hit != null && hit !== t) return false
                    val wx: kotlin.Float
                    val wy: kotlin.Float
                    val v = Core.camera.unproject(x, y)
                    wx = v.x
                    wy = v.y

                    touchedPos.set(wx, wy)
                    time[0] = Time.time

                    return false
                }
            })
            t.update(Runnable {
                if (Core.input.useKeyboard()) return@Runnable
                val viewPos = Core.camera.project(configuringPos.x, configuringPos.y)
                buttons.setPosition(viewPos.x, viewPos.y - 38, Align.top)
                if (configCmdTask != null && !Core.input.isTouched() && Time.time - time[0] <= 30) {
                    configCmdTask!!.targetPos = Vec2(touchedPos)
                    configuringPos.set(touchedPos)

                    time[0] = 0f
                }
            })
        })
        val rebuildLayout = Runnable {
            cont.clearChildren()
            cont.table(Cons { root: Table ->
                root.table(SglDrawConst.grayUIAlpha, Cons { pa: Table ->
                    pa.top().pane(Cons { list: Table -> taskQueue = list }).growX().fillY().top()
                }).grow()
                if (Core.graphics.isPortrait()) root.row()
                val cell = root.table(SglDrawConst.grayUIAlpha, Cons { side: Table ->
                    var side = side
                    val t = side
                    if (Core.graphics.isPortrait()) {
                        side = Table()
                        val coll = Collapser(side, true)
                        coll.setDuration(0.6f)

                        t.add<Collapser?>(coll).fillY().growX()
                        t.row()
                        t.button(Icon.up, Styles.clearNonei, 32f, Runnable {
                            coll.setCollapsed(!coll.isCollapsed(), true)
                        }).growX().height(40f).update(Cons { i: ImageButton? -> i!!.getStyle().imageUp = if (coll.isCollapsed()) Icon.upOpen else Icon.downOpen })
                    }
                    side.top().pane(Styles.noBarPane, Cons { info: Table -> status = info }).grow().top().pad(4f).get().setScrollingDisabledX(true)
                    side = t

                    side.row()
                    side.image().color(Pal.lightishGray).growX().pad(0f).padBottom(4f).height(4f)
                    side.row()
                    side.table(Cons { buttons: Table -> sideButton = buttons }).growX().fillY().top().pad(4f)
                })
                if (Core.graphics.isPortrait()) {
                    cell.growX().fillY().padTop(6f)
                } else cell.width(280f).padLeft(6f).growY()
            }).grow().padLeft((if (Core.graphics.isPortrait()) 20 else 80).toFloat()).padRight((if (Core.graphics.isPortrait()) 20 else 80).toFloat())

            cont.row()
            cont.image().color(Color.darkGray).height(4f).growX().pad(-1f).padTop(3f).padBottom(3f)
            if (currConfig != null) rebuild(currConfig!!)
        }

        resized(true, rebuildLayout)
        shown(rebuildLayout)
    }

    private fun rebuildCmds(task: BuildTask?) {
        if (task != null) {
            commandCfgTab!!.clearChildren()
            for (command in task.buildUnit!!.commands) {
                commandCfgTab!!.button(Icon.icons.get(command.icon, Icon.cancel), Styles.clearNoneTogglei, Runnable {
                    task.command = command
                }).checked(Boolf { i: ImageButton? -> task.command === command }).size(50f).tooltip(command.localized())
            }
        }
    }

    fun rebuild(factory: SglUnitFactoryBuild) {
        taskQueue!!.clear()

        currConfig = factory
        curr = factory.currentTask

        taskQueue!!.defaults().growX().left().pad(4f)

        taskQueue!!.add(Core.bundle.get("dialog.unitFactor.executing")).color(Pal.accent)
        taskQueue!!.row()
        taskQueue!!.image().color(Pal.accent).height(3f).pad(0f).padBottom(4f)
        taskQueue!!.row()

        buildTaskItem(taskQueue!!, factory.currentTask, true)

        taskQueue!!.add(Core.bundle.get("dialog.unitFactor.queue")).color(Pal.accent)
        taskQueue!!.row()
        taskQueue!!.image().color(Pal.accent).height(3f).pad(0f).padBottom(4f)
        taskQueue!!.row()

        if (factory.currentTask != null) {
            for (task in factory.currentTask) {
                if (task === factory.currentTask) continue

                buildTaskItem(taskQueue!!, task, false)
            }
        }

        status!!.clearChildren()
        status!!.top().defaults().left().growX().pad(4f).fillY()
        status!!.add("").update(Cons { l: Label? -> l!!.setText("> " + Core.bundle.format("dialog.unitFactor.status", factory.statusText())) })
        status!!.row()
        status!!.add("").update(Cons { l: Label? -> l!!.setText(Core.bundle.format("dialog.unitFactor.taskRemaining", factory.taskCount(), (factory.block as SglUnitFactory).maxTasks)) })
        status!!.row()
        status!!.add("").update(Cons { l: Label? ->
            var time = 0f
            if (factory.currentTask != null && factory.currentTask!!.factoryIndex != -1) {
                var cons = (factory.block as SglUnitFactory).consumers.get(factory.currentTask!!.factoryIndex) as SglConsumers
                time += factory.currentTask!!.queueAmount * cons.craftTime - (factory.progress() + factory.buildCount()) * cons.craftTime

                for (task in factory.currentTask) {
                    if (task === factory.currentTask) continue

                    cons = (factory.block as SglUnitFactory).consumers.get(task.factoryIndex) as SglConsumers
                    time += cons.craftTime * task.queueAmount
                }
            }
            l!!.setText(Core.bundle.format("dialog.unitFactor.timeRemaining", if (factory.queueMode) Core.bundle.get("misc.loop") else timeFormat(time / max(factory.workEfficiency(), 0.00001f))))
        })
        status!!.row()
        status!!.add("").update(Cons { l: Label? ->
            l!!.setText(
                Core.bundle.format(
                    "dialog.unitFactor.matrixNetLinking", Core.bundle.get(
                        if (factory.distributor!!.network.core == null) "infos.offline" else if (factory.distributor!!.network.netValid()) "infos.connected" else "infos.netInvalid"
                    )
                )
            )
        })
        status!!.row()
        pri = factory.priority
        status!!.table(Cons { t: Table ->
            t.defaults().left()
            t.add(Core.bundle.get("misc.priority"))
            t.field(pri.toString(), TextField.TextFieldFilter.digitsOnly, Cons { num: String? -> pri = if (num!!.isEmpty()) 0 else num.toInt() }).growX()
        })
        status!!.row()
        status!!.button(Core.bundle.get("misc.sure"), Icon.ok, Styles.grayt, Runnable { factory.configure(cfgArgs(ConfigCmd.SET_PRIORITY, pri)) }).height(38f).growX().disabled(Boolf { b: TextButton? -> pri == factory.priority }).update(Cons { b: TextButton? ->
            if (pri != factory.priority) {
                if (fold) {
                    b!!.clearActions()
                    b.actions(Actions.alpha(1f, 0.5f))
                    fold = false
                }
            } else {
                if (!fold) {
                    b!!.clearActions()
                    b.actions(Actions.alpha(0f, 0.5f))
                    fold = true
                }
            }
        }).margin(6f).get().color.a = 0f
        if ((factory.block as SglUnitFactory).hasPower) {
            status!!.row()
            status!!.add("").update(Cons { l: Label? ->
                val pow = factory.power.graph.getPowerBalance() * 60
                l!!.setText(
                    Core.bundle.format(
                        "dialog.unitFactor.power", UI.formatAmount(factory.power.graph.getBatteryStored().toLong()), UI.formatAmount(factory.power.graph.getBatteryCapacity().toLong()), (if (pow > 0) "[accent]+" else "[red]-") + UI.formatAmount(pow.toLong()), UI.formatAmount((if (factory.consumer == null || factory.consumer!!.current == null) 0f else factory.consumer!!.powerUsage * 60).toLong())
                    )
                )
            })
        }
        if ((factory.block as SglUnitFactory).hasEnergy) {
            status!!.row()
            status!!.add("").update(Cons { l: Label? ->
                val cons = if (factory.consumer!!.current == null) null else factory.consumer!!.current!!.get(SglConsumeType.energy)
                l!!.setText(
                    Core.bundle.format(
                        "dialog.unitFactor.energy", if (cons == null) "0" else {
                            cons as BaseConsume<ConsumerBuildComp>
                            Strings.autoFixed(cons.usage * cons!!.multiple(factory) * 60 * factory.consEfficiency(), 1)
                        }
                    )
                )
            })
            status!!.row()
            status!!.add<Bar?>(Bar(Prov { Core.bundle.format("fragment.bars.nuclearContain", factory.energy, factory.energyCapacity(), factory.energy()!!.displayAdding) }, Prov { SglDrawConst.matrixNetDark }, Floatp { factory.getEnergy() / factory.energyCapacity() })).height(24f).update(Cons { b: Bar? -> factory.energy()!!.updateFlow() })
        }
        status!!.row()
        status!!.add<Bar?>(Bar(Prov { Core.bundle.format("bar.efficiency", Mathf.round(factory.workEfficiency() * 100)) }, Prov { Pal.lightOrange }, Floatp { factory.workEfficiency() })).height(24f)
        status!!.row()
        status!!.add(Core.bundle.get("infos.storage"))
        status!!.row()
        status!!.table(Cons { req: Table ->
            val timer = Interval()
            val rebuild = Runnable {
                req.clearChildren()
                req.left().defaults().size(85f, 48f).left()
                val i = intArrayOf(0)
                val mod = if (Core.graphics.isPortrait()) 5 else 3
                factory.items.each(ItemConsumer { item: Item?, a: Int ->
                    req.table(Cons { it: Table ->
                        it.left().defaults().left()
                        it.image(item!!.fullIcon).scaling(Scaling.fit)
                        it.add("").padLeft(3f).update(Cons { l: Label? -> l!!.setText(UI.formatAmount(factory.items.get(item).toLong())) })
                    })
                    i[0]++
                    if (i[0] % mod == 0) req.row()
                })
            }
            req.update(Runnable {
                if (timer.get(30f)) rebuild.run()
            })
        }).fillY()

        sideButton!!.clearChildren()
        sideButton!!.left().defaults().left().growX().pad(4f)
        sideButton!!.button(Core.bundle.get("misc.clear"), Icon.trash, Styles.grayt, Runnable {
            var delays = 0f
            for (child in taskQueue!!.getChildren()) {
                if (child is Table) {
                    child.clearActions()
                    child.actions(
                        Actions.delay(delays), Actions.parallel(
                            Actions.moveBy(-180f, 0f, 0.6f, Interp.pow2Out), Actions.alpha(0f, 0.5f)
                        )
                    )
                    delays += 0.1f
                }
            }
            Time.run(delays * 60 + 36f, Runnable {
                factory.configure(cfgArgs(ConfigCmd.CLEAR_TASK))
                rebuild(factory)
            })
        }).margin(6f).disabled(Boolf { b: TextButton? -> factory.currentTask == null })
        sideButton!!.row()
        sideButton!!.check("", factory.queueMode, Boolc { b: Boolean -> factory.configure(cfgArgs(ConfigCmd.QUEUE_MODE, if (b) 1 else -1)) }).update(Cons { l: CheckBox? -> l!!.setText(Core.bundle.get(if (factory.queueMode) "dialog.unitFactor.queueMode" else "dialog.unitFactor.stackMode")) }).get().left()
        sideButton!!.row()
        sideButton!!.check(Core.bundle.get("dialog.unitFactor.skipBlocked"), factory.skipBlockedTask, Boolc { b: Boolean -> factory.configure(cfgArgs(ConfigCmd.SKIP_BLOCKED, if (b) 1 else -1)) }).get().left()
    }

    fun show(factory: SglUnitFactoryBuild) {
        rebuild(factory)
        show()
    }

    private fun buildTaskItem(table: Table, task: BuildTask?, executing: Boolean) {
        val mark = arrayOfNulls<Table>(1)
        mark[0] = table.table(SglDrawConst.grayUIAlpha, Cons { ta: Table ->
            ta.defaults().left().pad(6f)
            if (task == null) {
                ta.add("no task executing!").center().height(80f)
                return@Cons
            }
            val cons = (currConfig!!.block as SglUnitFactory).consumers.get(task.factoryIndex) as SglConsumers

            ta.image(task.buildUnit!!.uiIcon).size(80f).scaling(Scaling.fit)
            ta.table(Cons { tab: Table ->
                tab.table(Cons { inf: Table ->
                    inf.add(task.buildUnit!!.localizedName).color(Pal.accent).left()
                    inf.add("").update(Cons { t: Label? ->
                        t!!.setText(
                            Core.bundle.format(
                                "dialog.unitFactor.movePos", if (task.targetPos == null) "--" else Mathf.round(task.targetPos!!.x), if (task.targetPos == null) "--" else Mathf.round(task.targetPos!!.y)
                            )
                        )
                    }).color(Color.gray).left().growX().padLeft(4f)
                    inf.table(Cons { top: Table ->
                        if (executing) {
                            top.add("").left().update(Cons { l: Label? -> l!!.setText(Core.bundle.format("dialog.unitFactor.executed", currConfig!!.buildCount(), task.queueAmount)) })
                        } else top.add(Core.bundle.format("dialog.unitFactor.createAmount", task.queueAmount)).left()
                        top.table(Cons { button: Table ->
                            button.defaults().size(42f)
                            button.button(Icon.upOpen, Styles.clearNonei, 28f, Runnable {
                                currConfig!!.configure(cfgArgs(ConfigCmd.RISE_TASK, currConfig!!.indexOfTask(task)))
                                rebuild(currConfig!!)
                            }).disabled(Boolf { b: ImageButton? -> task.pre == null || (currConfig!!.activity && task.pre == currConfig!!.currentTask) })
                            button.button(Icon.downOpen, Styles.clearNonei, 28f, Runnable {
                                currConfig!!.configure(cfgArgs(ConfigCmd.DOWN_TASK, currConfig!!.indexOfTask(task)))
                                rebuild(currConfig!!)
                            }).disabled(Boolf { b: ImageButton? -> task.next == null || (currConfig!!.activity && task == currConfig!!.currentTask) })
                            button.button(Icon.settings, Styles.clearNonei, 28f, Runnable {
                                hide()
                                configuringPos.set(currConfig!!.x, currConfig!!.y)
                                configCmdTask = task
                                commandConfiguring = true
                                commandCfg!!.visible = true
                                rebuildCmds(task)
                            })
                            button.button(Icon.cancel, Styles.clearNonei, 28f, Runnable {
                                if (executing) {
                                    currConfig!!.configure(cfgArgs(ConfigCmd.REMOVE_TASK, currConfig!!.indexOfTask(task)))
                                } else {
                                    mark[0]!!.clearActions()
                                    mark[0]!!.actions(
                                        Actions.parallel(
                                            Actions.moveBy(-180f, 0f, 0.6f, Interp.pow2Out), Actions.alpha(0f, 0.5f)
                                        ), Actions.run(Runnable {
                                            currConfig!!.removeTask(task)
                                            rebuild(currConfig!!)
                                        })
                                    )
                                }
                            })
                        }).padLeft(4f).padTop(-4f).padRight(-4f)
                    }).right().fillX()
                }).growX()
                tab.row()

                tab.table(Cons { req: Table ->
                    req.table(Cons { r: Table ->
                        r.defaults().left()
                        req.add(Stat.buildCost.localized() + ":").padRight(4f)
                        for (consume in cons.all()) {
                            consume.buildIcons(req)
                        }
                    }).left().growX()
                    req.add(Core.bundle.get("misc.command") + ":")
                    req.image().update(Cons { i: Image? -> i!!.setDrawable(Icon.icons.get(task.command!!.icon, Icon.cancel)) }).scaling(Scaling.fit).size(38f)
                    req.add("").update(Cons { l: Label? -> l!!.setText(task.command!!.localized()) }).color(Color.gray).padRight(4f)
                    if (executing) {
                        req.add("").right().update(Cons { l: Label? -> l!!.setText(timeFormat((task.queueAmount * cons.craftTime - cons.craftTime * (currConfig!!.buildCount() + currConfig!!.progress())) / max(currConfig!!.workEfficiency(), 0.00001f))) })
                    } else req.add(timeFormat(cons.craftTime * task.queueAmount)).right()
                }).left().growX()

                tab.row()
                tab.add<Bar?>(Bar(if (executing) Prov { Core.bundle.format("bar.numprogress", Strings.autoFixed(currConfig!!.progress() * 100, 2)) } else Prov { Core.bundle.get("infos.waiting") }, Prov { Pal.powerBar }, if (executing) Floatp { currConfig!!.progress() } else Floatp { 0F })).height(25f).growX()
            }).growX().padBottom(4f).padTop(4f)
        }).fillY().growX().get()
        table.row()

        if (executing) {
            mark[0]!!.update(Runnable {
                if (currConfig!!.currentTask != curr) {
                    curr = currConfig!!.currentTask

                    mark[0]!!.clearActions()
                    mark[0]!!.actions(
                        Actions.parallel(
                            Actions.moveBy(-180f, 0f, 0.6f, Interp.pow2Out), Actions.alpha(0f, 0.5f)
                        ), Actions.run(Runnable { rebuild(currConfig!!) })
                    )
                }
            })
        }
    }

    companion object {
        private fun cfgArgs(cmd: ConfigCmd, vararg args: Int): IntSeq {
            val res = IntSeq.with(cmd.ordinal)
            res.addAll(*args)
            return res
        }

        fun timeFormat(ticks: kotlin.Float): String {
            var ticks = ticks
            val hor = (ticks / 216000).toInt()
            if (hor > 99) return "xx:xx:xx"

            ticks %= 216000f
            val min = (ticks / 3600).toInt()
            ticks %= 3600f
            val sec = (ticks / 60).toInt()
            val builder = StringBuilder()
            if (hor < 10) builder.append("0")
            builder.append(hor).append(":")
            if (min < 10) builder.append("0")
            builder.append(min).append(":")
            if (sec < 10) builder.append("0")
            builder.append(sec)

            return builder.toString()
        }
    }
}
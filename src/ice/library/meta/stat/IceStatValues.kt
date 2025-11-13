package ice.library.meta.stat

import arc.Core
import arc.func.Boolf
import arc.graphics.Color
import arc.math.Mathf
import arc.scene.Element
import arc.scene.event.HandCursorListener
import arc.scene.ui.Tooltip.Tooltips
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Collapser
import arc.scene.ui.layout.Table
import arc.struct.ObjectFloatMap
import arc.struct.ObjectMap
import arc.util.Scaling
import arc.util.Strings
import ice.library.content.blocks.crafting.multipleCrafter.Formula
import ice.library.content.blocks.crafting.multipleCrafter.FormulaStack
import ice.library.entities.bullet.IceBasicBulletType
import ice.library.scene.tex.IStyles
import mindustry.Vars
import mindustry.content.StatusEffects
import mindustry.ctype.UnlockableContent
import mindustry.entities.bullet.BulletType
import mindustry.gen.Icon
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.type.UnitType
import mindustry.ui.Styles
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.Turret
import mindustry.world.meta.StatUnit
import mindustry.world.meta.StatValue
import mindustry.world.meta.StatValues
import mindustry.world.meta.Stats
import kotlin.math.max

object IceStatValues {
    fun funString(str: () -> String): StatValue {
        return StatValue {
            it.add(arc.scene.ui.Label{str()})
        }
    }

    fun drillables(
        drillTime: Float,
        drillMultiplier: Float,
        size: Float,
        multipliers: ObjectFloatMap<Item>?,
        filter: Boolf<Block>
    ): StatValue {
        return StatValue { table ->
            table.row()
            table.table { c ->
                var i = 0
                for (block in Vars.content.blocks()) {
                    if (!filter.get(block)) continue
                    c.table(Styles.grayPanel) { b ->
                        b.image(block.uiIcon).size(40f).pad(10f).left().scaling(Scaling.fit)
                        b.table { info ->
                            info.left()
                            info.add(block.localizedName).left().row()
                            info.image(block.itemDrop.uiIcon).size(18f, 21f)
                                .with { l -> StatValues.withTooltip(l, block.itemDrop) }.left()
                        }.grow()
                        if (multipliers != null) {
                            b.add(Strings.autoFixed(60f / (max(drillTime + drillMultiplier * block.itemDrop.hardness,
                                drillTime) / multipliers.get(block.itemDrop, 1f)) * size,
                                2) + StatUnit.perSecond.localized())
                                .right().pad(10f).padRight(15f).color(Color.lightGray)
                        }
                    }.growX().pad(5f)
                    if (++i % 2 == 0) c.row()
                }
            }.growX().colspan(table.columns)
        }
    }

    fun formulas(formulas: FormulaStack, block: Block): StatValue {
        return StatValue { t: Table ->
            t.table { table: Table ->
                table.left()
                formulas.formulas.forEach { f ->
                    table.table(IStyles.background121) { tab: Table ->
                        tab.left()
                        formula(f, block).display(tab)
                    }.growX().pad(5f).row()
                }
            }.left()
        }
    }

    fun formula(formula: Formula, block: Block): StatValue {
        return StatValue { t: Table ->
            t.table { table: Table ->
                val stats = Stats()
                formula.display(stats, block)
                displayStats(stats, table)
            }.left().margin(10f)
            t.row()
        }
    }

    fun displayStats(stats: Stats, table: Table) {
        val toMap = stats.toMap()
        for (cat in toMap.keys()) {
            val map = toMap[cat]

            if (map.size == 0) continue

            if (stats.useCategories) {
                table.add("@category." + cat.name).color(Pal.accent).fillX()
                table.row()
            }

            for (stat in map.keys()) {
                table.table { inset: Table ->
                    inset.left()
                    inset.add("[lightgray]" + stat.localized() + ":[] ").left().top()
                    val arr = map[stat]
                    for (value in arr) {
                        value.display(inset)
                        inset.add().size(10f)
                    }
                }.fillX()
                table.row()
            }
        }
    }

    fun <T : UnlockableContent> ammo(
        map: ObjectMap<T, BulletType>,
        nested: Boolean = false,
        showUnit: Boolean = false
    ): StatValue {
        return StatValue { table ->
            table.row()
            val orderedKeys = map.keys().toSeq().sort()
            for (t in orderedKeys) {
                val compact = t is UnitType && !showUnit || nested
                val type = map.get(t)

                if (type.spawnUnit != null && type.spawnUnit.weapons.size > 0) {
                    ammo(
                        ObjectMap.of(t, type.spawnUnit.weapons.first().bullet), nested,
                        false).display(table)
                    continue
                }

                table.table(Styles.grayPanel) { bt ->
                    bt.left().top().defaults().padRight(3f).left()
                    //显示两次单位图标是没有意义的
                    if (!compact && t !is Turret) {
                        bt.table { title ->
                            title.image(t.uiIcon).size((3 * 8).toFloat()).padRight(4f).right()
                                .scaling(Scaling.fit).top().with { i -> StatValues.withTooltip(i, t, false) }
                            title.add(t.localizedName).padRight(10f).left().top()
                            if (type.displayAmmoMultiplier && type.statLiquidConsumed > 0f) {
                                title.add("[stat]" + StatValues.fixValue(
                                    type.statLiquidConsumed / type.ammoMultiplier * 60f) + " [lightgray]" + StatUnit.perSecond.localized())
                            }
                        }
                        bt.row()
                    }
                    if (type is IceBasicBulletType) {
                        type.setStats(bt, compact, t)
                    } else {
                        if (type.damage > 0 && (type.collides || type.splashDamage <= 0)) {
                            if (type.continuousDamage() > 0) {
                                bt.add(Core.bundle.format("bullet.damage",
                                    type.continuousDamage()) + StatUnit.perSecond.localized())
                            } else {
                                bt.add(Core.bundle.format("bullet.damage", type.damage))
                            }
                        }

                        if (type.buildingDamageMultiplier != 1f) {
                            sep(bt, Core.bundle.format("bullet.buildingdamage",
                                ammoStat((type.buildingDamageMultiplier * 100 - 100).toInt().toFloat())))
                        }

                        if (type.rangeChange != 0f && !compact) {
                            sep(bt,
                                Core.bundle.format("bullet.range", ammoStat(type.rangeChange / Vars.tilesize)))
                        }

                        if (type.shieldDamageMultiplier != 1f) {
                            sep(bt, Core.bundle.format("bullet.shielddamage",
                                ammoStat((type.shieldDamageMultiplier * 100 - 100).toInt().toFloat())))
                        }

                        if (type.splashDamage > 0) {
                            sep(bt, Core.bundle.format("bullet.splashdamage", type.splashDamage.toInt(),
                                Strings.fixed(type.splashDamageRadius / Vars.tilesize, 1)))
                        }

                        if (type.statLiquidConsumed <= 0f && !compact && !Mathf.equal(type.ammoMultiplier,
                                1f) && type.displayAmmoMultiplier && (t !is Turret || t.displayAmmoMultiplier)
                        ) {
                            sep(bt, Core.bundle.format("bullet.multiplier", type.ammoMultiplier.toInt()))
                        }

                        if (!compact && !Mathf.equal(type.reloadMultiplier, 1f)) {
                            val `val` = (type.reloadMultiplier * 100 - 100).toInt()
                            sep(bt, Core.bundle.format("bullet.reload", ammoStat(`val`.toFloat())))
                        }

                        if (type.knockback > 0) {
                            sep(bt, Core.bundle.format("bullet.knockback", Strings.autoFixed(type.knockback, 2)))
                        }

                        if (type.healPercent > 0f) {
                            sep(bt,
                                Core.bundle.format("bullet.healpercent", Strings.autoFixed(type.healPercent, 2)))
                        }

                        if (type.healAmount > 0f) {
                            sep(bt,
                                Core.bundle.format("bullet.healamount", Strings.autoFixed(type.healAmount, 2)))
                        }

                        if (type.pierce || type.pierceCap != -1) {
                            sep(bt,
                                if (type.pierceCap == -1) "@bullet.infinitepierce" else Core.bundle.format(
                                    "bullet.pierce",
                                    type.pierceCap))
                        }

                        if (type.incendAmount > 0) {
                            sep(bt, "@bullet.incendiary")
                        }

                        if (type.homingPower > 0.01f) {
                            sep(bt, "@bullet.homing")
                        }

                        if (type.lightning > 0) {
                            sep(bt, Core.bundle.format("bullet.lightning", type.lightning,
                                if (type.lightningDamage < 0) type.damage else type.lightningDamage))
                        }

                        if (type.pierceArmor) {
                            sep(bt, "@bullet.armorpierce")
                        }

                        if (type.maxDamageFraction > 0) {
                            sep(bt,
                                Core.bundle.format("bullet.maxdamagefraction", (type.maxDamageFraction * 100).toInt()))
                        }

                        if (type.suppressionRange > 0) {
                            sep(bt, Core.bundle.format("bullet.suppression",
                                Strings.autoFixed(type.suppressionDuration / 60f, 2),
                                Strings.fixed(type.suppressionRange / Vars.tilesize, 1)))
                        }

                        if (type.status !== StatusEffects.none) {
                            sep(bt,
                                (if (type.status.hasEmoji()) type.status.emoji() else "") + "[stat]" + type.status.localizedName + (if (type.status.reactive) "" else ("[lightgray] ~ [stat]" +
                                        Strings.autoFixed(type.statusDuration / 60f,
                                            1) + "[lightgray] " + Core.bundle.get(
                                    "unit.seconds")))).with { c: Element -> withTooltip(c, type.status) }
                        }

                        if (!type.targetMissiles) {
                            sep(bt, "@bullet.notargetsmissiles")
                        }

                        if (!type.targetBlocks) {
                            sep(bt, "@bullet.notargetsbuildings")
                        }

                        if (type.intervalBullet != null) {
                            bt.row()
                            val ic = Table()
                            ammo<UnlockableContent>(ObjectMap.of(t, type.intervalBullet), true).display(ic)
                            val coll = Collapser(ic, true)
                            coll.setDuration(0.1f)

                            bt.table { it: Table ->
                                it.left().defaults().left()
                                it.add(Core.bundle.format("bullet.interval",
                                    Strings.autoFixed(type.intervalBullets / type.bulletInterval * 60, 2)))
                                it.button(Icon.downOpen, Styles.emptyi) { coll.toggle(false) }
                                    .update { i -> i.style.imageUp = (if (!coll.isCollapsed) Icon.upOpen else Icon.downOpen) }
                                    .size(8f).padLeft(16f).expandX()
                            }
                            bt.row()
                            bt.add(coll)
                        }
                        if (type.fragBullet != null) {
                            bt.row()
                            val fc = Table()
                            ammo(ObjectMap.of(t, type.fragBullet), true).display(fc)
                            val coll = Collapser(fc, true)
                            coll.setDuration(0.1f)

                            bt.table { ft ->
                                ft.left().defaults().left()
                                ft.add(Core.bundle.format("bullet.frags", type.fragBullets))
                                ft.button(Icon.downOpen, Styles.emptyi) { coll.toggle(false) }
                                    .update { i -> i.style.imageUp = (if (!coll.isCollapsed) Icon.upOpen else Icon.downOpen) }
                                    .size(8f).padLeft(16f).expandX()
                            }
                            bt.row()
                            bt.add(coll)
                        }
                    }
                }.padLeft(5f).padTop(5f).padBottom((if (compact) 0 else 5).toFloat()).growX()
                    .margin((if (compact) 0 else 10).toFloat())
                table.row()
            }
        }
    }

    //for AmmoListValue
    fun sep(table: Table, text: String): Cell<*> {
        table.row()
        return table.add(text)
    }

    //for AmmoListValue
    fun ammoStat(amout: Float): String {
        return (if (amout > 0) "[stat]+" else "[negstat]") + Strings.autoFixed(amout, 1)
    }

    fun <T : Element> withTooltip(element: T, content: UnlockableContent?, tooltip: Boolean = false): T {
        if (content != null) {
            if (!Vars.mobile) {
                if (tooltip) {
                    element.addListener(Tooltips.getInstance().create(content.localizedName, Vars.mobile))
                }
                element.addListener(HandCursorListener({ !content.isHidden }, true))
            }
            element.clicked {
                if (!content.isHidden) {
                    Vars.ui.content.show(content)
                }
            }
        }
        return element
    }
}

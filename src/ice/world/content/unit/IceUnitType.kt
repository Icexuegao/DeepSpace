package ice.world.content.unit

import arc.Core
import arc.func.Cons
import arc.graphics.Color
import arc.graphics.Pixmap
import arc.graphics.Pixmaps
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.Rand
import arc.math.Scaled
import arc.math.geom.Vec2
import arc.struct.Seq
import arc.util.Time
import arc.util.Tmp
import ice.content.IItems
import ice.content.IPlanets
import ice.entities.IceRegister
import ice.graphics.IceColor
import ice.library.IFiles
import ice.world.content.unit.ability.UnitTypeFun
import ice.world.content.unit.entity.base.Entity
import ice.world.content.unit.weapon.IceWeapon
import mindustry.Vars
import mindustry.ai.ControlPathfinder
import mindustry.ai.Pathfinder
import mindustry.ai.UnitCommand
import mindustry.ai.UnitStance
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.core.Renderer
import mindustry.entities.Effect
import mindustry.entities.Effect.EffectContainer
import mindustry.entities.part.DrawPart
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.MultiPacker
import mindustry.graphics.Pal
import mindustry.type.ItemStack
import mindustry.type.UnitType
import mindustry.type.Weapon
import mindustry.world.meta.Env
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.full.primaryConstructor

open class IceUnitType(name: String, applys: IceUnitType.() -> kotlin.Unit) : UnitType(name), UnitTypeFun {
    companion object {
        var imineLaserRegion = IFiles.findIcePng("minelaser")
        var imineLaserEndRegion = IFiles.findIcePng("minelaser-end")
        val legOffsetIce = Vec2()
        val rand: Rand = Rand()
    }

    private var statsFun = {}
    private var unitDamageEventFun: ((Unit, Bullet) -> kotlin.Unit)? = null
    private var requirements: Array<ItemStack> = arrayOf(ItemStack(IItems.低碳钢, 100))

    init {
        constructor = IceRegister.getPutUnit<Entity>()
        applys(this)
    }

    fun requirements(vararg req: Any) {
        requirements = ItemStack.with(*req)
    }

    override fun getRequirements(prevReturn: Array<UnitType?>?, timeReturn: FloatArray?): Array<ItemStack>? {
        if (totalRequirements != null) return totalRequirements

        totalRequirements = requirements
        buildTime = 0f
        if (prevReturn != null) prevReturn[0] = null

        for (stack in requirements) {
            buildTime += stack.item.cost * stack.amount
        }
        if (timeReturn != null) timeReturn[0] = buildTime

        return requirements
    }

    override fun createIcons(packer: MultiPacker) {
        if (!Core.atlas.has("$name-full") && Core.atlas.has("$name-treads")) {
            val treadRegion = Pixmaps.outline(Core.atlas.getPixmap(treadRegion), outlineColor, outlineRadius)
            val region = Pixmaps.outline(Core.atlas.getPixmap(region), outlineColor, outlineRadius)
            val pixmap = Pixmap(region.width, region.height)
            pixmap.each { x, y ->
                val color = treadRegion.getRaw(x, y)
                if (color != 0) {
                    pixmap.set(x, y, color)
                }
                val color1 = region.getRaw(x, y)
                if (color1 != 0) {
                    pixmap.set(x, y, color1)
                }
            }

            weapons.forEach { weapon ->
                val wearegion = Pixmaps.outline(Core.atlas.getPixmap(weapon.region), outlineColor, outlineRadius)
                pixmap.each { x, y ->
                    val color = wearegion.get(x, y)
                    if (color != 0) {
                        pixmap.set(x, y, color)
                    }
                }
            }
            packer.add(MultiPacker.PageType.main, "$name-full", pixmap)
            pixmap.dispose()
        }
        super.createIcons(packer)
    }

    override fun load() {
        super.load()
        val name1 = IFiles.getRepName(name) + 1
        if (IFiles.hasPng(name1)) region = IFiles.findPng(name1)
        shadowRegion = region
    }

    fun setUnitDamageEvent(event: (Unit, Bullet) -> kotlin.Unit) {
        unitDamageEventFun = event
    }

    fun damageBulletEvent(unit: Unit, bullet: Bullet) {
        unitDamageEventFun?.invoke(unit, bullet)
    }

    override fun init() {
        val example: Unit = constructor.get()

        checkEntityMapping(example)
        //  allowLegStep = example is Legsc || example is Crawlc
        //water preset
        databaseTabs.add(IPlanets.阿德里)
        stats.useCategories = true
        if (example is WaterMovec || example is WaterCrawlc) {
            naval = true
            canDrown = false
            emitWalkSound = false
            omniMovement = false
            immunities.add(StatusEffects.wet)
            if (shadowElevation < 0f) {
                shadowElevation = 0.11f
            }
        }

        if (flowfieldPathType == -1) {
            flowfieldPathType = if (naval) Pathfinder.costNaval else if (allowLegStep) Pathfinder.costLegs else if (flying) Pathfinder.costNone else if (hovering) Pathfinder.costHover else Pathfinder.costGround
        }

        if (pathCost == null) {
            pathCost = if (naval) ControlPathfinder.costNaval else if (allowLegStep) ControlPathfinder.costLegs else if (hovering) ControlPathfinder.costHover else ControlPathfinder.costGround
        }

        pathCostId = ControlPathfinder.costTypes.indexOf(pathCost)
        if (pathCostId == -1) pathCostId = 0

        if (flying) {
            envEnabled = envEnabled or Env.space
        }

        if (lightRadius == -1f) {
            lightRadius = max(60f, hitSize * 2.3f)
        }
        //if a status effects slows a unit when firing, don't shoot while moving.
        if (autoFindTarget) {
            autoFindTarget = !weapons.contains { w: Weapon? -> w!!.shootStatus.speedMultiplier < 0.99f } || alwaysShootWhenMoving
        }

        if (flyingLayer < 0) flyingLayer = if (lowAltitude) Layer.flyingUnitLow else Layer.flyingUnit
        clipSize = max(clipSize, lightRadius * 1.1f)
        singleTarget = weapons.size <= 1 && !forceMultiTarget

        if (itemCapacity < 0) {
            itemCapacity = max(Mathf.round((hitSize * 4f).toInt(), 10), 10)
        }
        //assume slight range margin
        val margin = 4f
        //set up default range
        if (range < 0) {
            range = Float.MAX_VALUE
            for (weapon in weapons) {
                if (!weapon.useAttackRange) continue

                range = min(range, weapon.range() - margin)
                maxRange = max(maxRange, weapon.range() - margin)
            }
        }

        if (maxRange < 0) {
            maxRange = max(0f, range)

            for (weapon in weapons) {
                if (!weapon.useAttackRange) continue

                maxRange = max(maxRange, weapon.range() - margin)
            }
        }

        if (fogRadius < 0) {
            //TODO depend on range?
            fogRadius = max(58f * 3f, hitSize * 2f) / 8f
        }

        if (!weapons.contains { w: Weapon? -> w!!.useAttackRange }) {
            if (range < 0 || range == Float.MAX_VALUE) range = mineRange
            if (maxRange < 0 || maxRange == Float.MAX_VALUE) maxRange = mineRange
        }

        if (mechStride < 0) {
            mechStride = 4f + (hitSize - 8f) / 2.1f
        }

        if (segmentSpacing < 0) {
            segmentSpacing = hitSize
        }
        if (aimDst < 0) {
            aimDst = if (weapons.contains { w: Weapon? -> !w!!.rotate }) hitSize * 2f else hitSize / 2f
        }

        if (stepShake < 0) {
            stepShake = Mathf.round((hitSize - 11f) / 9f).toFloat()
            mechStepParticles = hitSize > 15f
        }

        if (engineSize > 0) {
            engines.add(UnitEngine(0f, -engineOffset, engineSize, -90f))
        }

        if (treadEffect == null) {
            treadEffect = Effect(50f) { e: EffectContainer? ->
                Draw.color(Tmp.c1.set(e!!.color).mul(1.5f))
                Fx.rand.setSeed(e.id.toLong())
                repeat(3) {
                    Fx.v.trns(e.rotation + Fx.rand.range(40f), Fx.rand.random(6f * e.finpow()))
                    Fill.circle(
                        e.x + Fx.v.x + Fx.rand.range(4f), e.y + Fx.v.y + Fx.rand.range(4f), min(e.fout(), e.fin() * e.lifetime / 8f) * hitSize / 28f * 3f * Fx.rand.random(
                            0.8f, 1.1f
                        ) + 0.3f
                    )
                }
            }.layer(Layer.debris)
        }

        if (mineBeamOffset == Float.NEGATIVE_INFINITY) mineBeamOffset = hitSize / 2

        for (ab in abilities) {
            ab.init(this)
        }
        //add mirrored weapon variants
        val mapped = Seq<Weapon>()
        for (w in weapons) {
            if (w.recoilTime < 0) w.recoilTime = w.reload
            mapped.add(w)
            //mirrors are copies with X values negated
            if (w.mirror) {
                val copy = w.copy()
                copy.flip()
                mapped.add(copy)
                //since there are now two weapons, the reload and recoil time must be doubled
                w.recoilTime *= 2f
                copy.recoilTime *= 2f
                w.reload *= 2f
                copy.reload *= 2f

                w.otherSide = mapped.size - 1
                copy.otherSide = mapped.size - 2
            }
        }
        this.weapons = mapped

        weapons.each(Cons { obj: Weapon? -> obj!!.init() })

        canHeal = weapons.contains { w: Weapon? -> w!!.bullet.heals() }

        canAttack = weapons.contains { w: Weapon? -> !w!!.noAttack }
        //assign default commands.
        if (commands.size == 0) {
            commands.add(UnitCommand.moveCommand, UnitCommand.enterPayloadCommand)

            if (canBoost) {
                commands.add(UnitCommand.boostCommand)

                if (buildSpeed > 0f) {
                    commands.add(UnitCommand.rebuildCommand, UnitCommand.assistCommand)
                }
                if (mineTier > 0) {
                    commands.add(UnitCommand.mineCommand)
                }
            }
            //healing, mining and building is only supported for flying units; pathfinding to ambiguously reachable locations is hard.
            if (flying) {
                if (canHeal) {
                    commands.add(UnitCommand.repairCommand)
                }

                if (buildSpeed > 0) {
                    commands.add(UnitCommand.rebuildCommand, UnitCommand.assistCommand)
                }

                if (mineTier > 0) {
                    commands.add(UnitCommand.mineCommand)
                }
                if (example is Payloadc) {
                    commands.addAll(
                        UnitCommand.loadUnitsCommand, UnitCommand.loadBlocksCommand, UnitCommand.unloadPayloadCommand, UnitCommand.loopPayloadCommand
                    )
                }
            }
        }

        if (defaultCommand == null && commands.size > 0) {
            defaultCommand = commands.first()
        }

        if (stances.size == 0) {
            if (canAttack) {
                stances.addAll(UnitStance.stop, UnitStance.holdFire, UnitStance.pursueTarget, UnitStance.patrol)
                if (!flying) {
                    stances.add(UnitStance.ram)
                }
            } else {
                stances.addAll(UnitStance.stop, UnitStance.patrol)
            }
        }
        //dynamically create ammo capacity based on firing rate
        if (ammoCapacity < 0) {
            val shotsPerSecond = weapons.sumf { w: Weapon? -> if (w!!.useAmmo) 60f / w.reload else 0f }
            //duration of continuous fire without reload
            val targetSeconds = 35f

            ammoCapacity = max(1, (shotsPerSecond * targetSeconds).toInt())
        }

        estimateDps()
        //only do this after everything else was initialized
        sample = constructor.get()
    }

    open fun setWeapon(weaponName: String = "", configurator: IceWeapon.() -> kotlin.Unit): IceWeapon {
        return IceWeapon(
            if (weaponName.isEmpty()) "" else {
                "$name-$weaponName"
            }
        ).apply(configurator).also(weapons::add)
    }

    inline fun <reified T : IceWeapon> setWeaponT(weaponName: String = "", configurator: T.() -> kotlin.Unit): T {
        val primaryConstructor = T::class.primaryConstructor!!
        val call = primaryConstructor.call(if (weaponName.isEmpty()) "" else "$name-$weaponName")

        return call.apply(configurator).also(weapons::add)
    }

    open fun IceWeapon.copyAdd(configurator: Weapon.() -> kotlin.Unit) {
        val copy = copy()
        configurator(copy)
        weapons.add(copy)
    }

    override fun setStats() {
        super.setStats()
        statsFun.invoke()
    }

    fun statsFun(stats: () -> kotlin.Unit) {
        statsFun = stats
    }

    override fun drawMiningBeam(unit: Unit, px: Float, py: Float) {
        if (!unit.mining()) return
        val swingScl = 12f
        val swingMag = Vars.tilesize / 8f
        val flashScl = 0.3f
        val ex = unit.mineTile.worldx() + Mathf.sin(Time.time + 48, swingScl, swingMag)
        val ey = unit.mineTile.worldy() + Mathf.sin(Time.time + 48, swingScl + 2f, swingMag)

        Draw.z(Layer.flyingUnit + 0.1f)

        Draw.color(IceColor.b4, Color.white, 1f - flashScl + Mathf.absin(Time.time, 0.5f, flashScl))

        Draw.alpha(Renderer.unitLaserOpacity)
        Drawf.laser(imineLaserRegion, imineLaserEndRegion, px, py, ex, ey, 0.75f)

        if (unit.isLocal) {
            Lines.stroke(1f, Pal.accent)
            Lines.poly(unit.mineTile.worldx(), unit.mineTile.worldy(), 4, Vars.tilesize / 2f * Mathf.sqrt2, Time.time)
        }

        Draw.color()
    }

    override fun draw(unit: Unit) {
        val scl = Draw.xscl
        if (unit.inFogTo(Vars.player.team())) return

        if (buildSpeed > 0f) unit.drawBuilding()

        if (unit.mining()) drawMining(unit)
        val isPayload = !unit.isAdded
        val mech: Mechc? = unit as? Mechc
        val seg: Segmentc? = unit as? Segmentc
        val z = if (isPayload) Draw.z() else  //死亡的飞行单位被假定为正在坠落，并且为了防止黑暗“雾”出现奇怪的剪裁问题，它们总是画在雾上方
            if (unit.elevation > 0.5f || (flying && unit.dead)) (flyingLayer) else if (seg != null) groundLayer + seg.segmentIndex() / 4000f * Mathf.sign(
                segmentLayerOrder
            ) + (if (!segmentLayerOrder) 0.01f else 0f) else groundLayer + Mathf.clamp(
                hitSize / 4000f, 0f, 0.01f
            )

        if (unit.isAdded && (unit.isFlying || shadowElevation > 0)) {
            Draw.z(min(Layer.darkness, z - 1f))
            if (unit is Entity) unit.drawShadow() else drawShadow(unit)
        }

        Draw.z(z - 0.02f)

        if (mech != null) {
            drawMech(mech)
            //side
            legOffsetIce.trns(
                mech.baseRotation(), 0f, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 2f / Mathf.PI, 1f) * mechSideSway, 0f, unit.elevation)
            )
            //front
            legOffsetIce.add(
                Tmp.v1.trns(
                    mech.baseRotation() + 90, 0f, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 1f / Mathf.PI, 1f) * mechFrontSway, 0f, unit.elevation)
                )
            )

            unit.trns(legOffsetIce.x, legOffsetIce.y)
        }

        if (treadRegion.found() && unit is Entity) {
            unit.drawTank()
        }

        if (unit is Legsc && unit.isAdded) {
            if (unit is Entity && legRegion.found()) {
                unit.drawLegs()
            } else drawLegs(unit)
        }

        Draw.z(min(z - 0.01f, Layer.bullet - 1f))

        if (unit is Payloadc) drawPayload(unit)

        if (drawSoftShadow) drawSoftShadow(unit)

        Draw.z(z)

        if (unit is Crawlc) drawCrawl(unit)

        if (drawBody) drawOutline(unit)
        drawWeaponOutlines(unit)
        if (engineLayer > 0) Draw.z(engineLayer)
        if (trailLength > 0 && !naval && (unit.isFlying || !useEngineElevation)) {
            drawTrail(unit)
        }
        if (engines.size > 0) drawEngines(unit)
        Draw.z(z)
        if (drawBody) {
            if (unit is Entity) {
                unit.drawBody()
            } else drawBody(unit)
        }
        if (drawCell && unit !is Crawlc) drawCell(unit)
        Draw.scl(scl) //TODO this is a hack for neoplasm turrets
        drawWeapons(unit)
        if (drawItems) drawItems(unit)
        if (unit.isAdded) drawLight(unit)

        if (unit.shieldAlpha > 0 && drawShields) {
            drawShield(unit)
        }
        //TODO 我该如何/在哪里画画？
        if (parts.size > 0) {
            for (i in 0..<parts.size) {
                val part = parts.get(i)
                val mount = if (unit.mounts.size > part.weaponIndex) unit.mounts[part.weaponIndex] else null
                if (mount != null) {
                    DrawPart.params.set(
                        mount.warmup, mount.reload / mount.weapon.reload, mount.smoothReload, mount.heat, mount.recoil, mount.charge, unit.x, unit.y, unit.rotation
                    )
                } else {
                    DrawPart.params.set(0f, 0f, 0f, 0f, 0f, 0f, unit.x, unit.y, unit.rotation)
                }

                if (unit is Scaled) {
                    DrawPart.params.life = unit.fin()
                }

                applyColor(unit)
                part.draw(DrawPart.params)
            }
        }

        if (unit.isAdded) {
            for (a in unit.abilities) {
                Draw.reset()
                a.draw(unit)
            }
        }

        if (mech != null) {
            unit.trns(-legOffsetIce.x, -legOffsetIce.y)
        }

        Draw.reset()
    }

    inner class IUnitEngine(x: Float, y: Float, radius: Float, rotate: Float, var width: Float = 8f) : UnitEngine(x, y, radius, rotate) {
        override fun draw(unit: Unit) {
            val type = unit.type
            val scale = if (type.useEngineElevation) unit.elevation else 1f
            if (scale <= 0.0001f) return
            val rot = unit.rotation - 90
            val color = if (type.engineColor == null) unit.team.color else type.engineColor
            val absin = Mathf.absin(Time.time, 3f, 0.2f)

            Tmp.v1.set(x, y - absin * 3).rotate(rot)
            val ex = Tmp.v1.x
            val ey = Tmp.v1.y

            Tmp.v2.set(x, y + 5).rotate(rot)
            val ex2 = Tmp.v2.x
            val ey2 = Tmp.v2.y

            Draw.color(color)
            Drawf.flame(unit.x + ex, unit.y + ey, 20, unit.rotation, 30f + absin * 8, width, 0.5f - absin * 0.8f)

            Draw.color(type.engineColorInner)
            Drawf.flame(unit.x + ex2, unit.y + ey2, 15, unit.rotation, 30f, 3.5f, 0.4f - absin)/* Fill.circle(
             unit.x + ex - Angles.trnsx(rot + rotation, 1f),
             unit.y + ey - Angles.trnsy(rot + rotation, 1f),
             (radius + Mathf.absin(Time.time, 2f, radius / 4f)) / 2f * scale
         )*/
        }
    }
}



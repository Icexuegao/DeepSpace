package singularity.world.blocks.turrets

import arc.Core
import arc.audio.Sound
import arc.func.*
import arc.graphics.Color
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.scene.ui.layout.Table
import arc.struct.EnumSet
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.Strings
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.UnitTypes
import mindustry.core.World
import mindustry.entities.*
import mindustry.entities.Units.Sortf
import mindustry.entities.bullet.BulletType
import mindustry.entities.pattern.ShootPattern
import mindustry.game.Team
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.logic.LAccess
import mindustry.logic.Ranged
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.world.blocks.ControlBlock
import mindustry.world.blocks.defense.turrets.Turret
import mindustry.world.meta.*
import singularity.graphic.SglDrawConst
import singularity.ui.UIUtils
import singularity.world.blocks.SglBlock
import singularity.world.consumers.SglConsumers
import singularity.world.draw.DrawSglTurret
import universecore.world.consumers.*
import kotlin.math.max

open class SglTurret(name: String) : SglBlock(name) {
    private val timerTarget = timers++

    /**炮塔的索敌范围 */
    var range: Float = 80f

    /**是否根据敌人的移动提前修正弹道 */
    var accurateDelay: Boolean = true

    /**是否根据敌人的移动提前修正弹道 */
    var accurateSpeed: Boolean = true

    /**是否攻击空中目标 */
    var targetAir: Boolean = true

    /**是否攻击地面目标 */
    var targetGround: Boolean = true

    /**是否瞄准生命值未满的友方 */
    var targetHealing: Boolean = false

    /**瞄准右方时是否瞄准单位 */
    var targetHealUnit: Boolean = true

    /**单位目标选择过滤器 */
    var unitFilter: Boolf<Unit?> = Boolf { u: Unit? -> true }

    /**建筑目标选择过滤器 */
    var buildingFilter: Boolf<Building?> = Boolf { b: Building? -> !b!!.block.underBullets }

    /**单位索敌排序准则，默认为最近目标 */
    var unitSort: Sortf? = UnitSorts.closest

    /**能否由玩家控制 */
    var playerControllable: Boolean = true

    /**索敌时间间隔，以刻为单位 */
    var targetInterval: Float = 20f

    /**预热速度 */
    var warmupSpeed: Float = 0.1f

    /**是否为线性预热过程 */
    var linearWarmup: Boolean = true

    /**开火预热阈值，需要达到阈值才能开火 */
    var fireWarmupThreshold: Float = 0f

    /**开火音效 */
    var shootSound: Sound = Sounds.shoot

    /**开火音效音调 */
    var shootSoundPitch: Float = 1f
    var shootSoundVolume: Float = 1f
    var chargeSound: Sound = Sounds.none

    /**充能音效音调 */
    var chargeSoundPitch: Float = 1f
    var chargeSoundVolume: Float = 1f

    /**音效音量范围 */
    var soundPitchRange: Float = 0.05f

    /**开火特效 */
    var shootEffect: Effect? = null

    /**烟雾特效 */
    var smokeEffect: Effect? = null

    /**弹药使用特效（例如抛壳） */
    var ammoUseEffect: Effect = Fx.none

    /**在炮塔冷却过程中显示的特效 */
    var coolEffect: Effect = Fx.fuelburn

    /**炮管红热时的光效遮罩层颜色 */
    var heatColor: Color? = Pal.turretHeat

    /**弹药出膛的偏移位置 */
    var shootX: Float = 0f
    var shootY: Float = Float.NEGATIVE_INFINITY

    /**子弹消耗特效产生的偏移位置 */
    var ammoEjectBack: Float = 1f

    /**开火抖动 */
    var shake: Float = 0f

    /**子弹最小开火范围，用于跨射武器 */
    var minRange: Float = 0f

    /**弹药出膛位置的横向平移范围 */
    var xRand: Float = 0f

    /**子弹弹道的散布角度范围 */
    var inaccuracy: Float = 0f

    /**子弹速度的随机偏差量 */
    var velocityRnd: Float = 0f

    /**炮塔的高光角度 */
    var elevation: Float = -1f

    /**射击模式 */
    var shoot: ShootPattern = ShootPattern()

    /**炮管冷却时间，这仅用于绘制热量 */
    var cooldownTime: Float = 20f

    /**后座力复位时间，默认使用当前弹药的装载时长 */
    var recoilTime: Float = -1f

    /**后座偏移插值的幂，参考[arc.math.Interp] */
    var recoilPow: Float = 1.8f

    /**炮塔尝试对目标开火的最小直线偏差角度 */
    var shootCone: Float = 8f

    /**每次射击后座力最大平移距离 */
    var recoil: Float = 1f

    /**转向速度 */
    var rotateSpeed: Float = 5f

    /**炮台在充能时能否转向 */
    var moveWhileCharging: Boolean = true

    /**炮塔充能时是否保持预热状态 */
    var chargingWarm: Boolean = true
    var ammoTypes: ObjectMap<BaseConsumers, AmmoDataEntry> = ObjectMap<BaseConsumers, AmmoDataEntry>()

    init {
        canOverdrive = false
        update = true
        solid = true
        autoSelect = true
        canSelect = false
        outlinedIcon = 1
        quickRotate = false
        outlineIcon = true
        attacks = true
        priority = TargetPriority.turret
        group = BlockGroup.turrets
        flags = EnumSet.of(BlockFlag.turret)
        draw = DrawSglTurret()
        buildType = Prov(::SglTurretBuild)
    }

    override fun init() {
        oneOfOptionCons = false

        if (shootY == Float.NEGATIVE_INFINITY) shootY = size * Vars.tilesize / 2f
        if (elevation < 0) elevation = size / 2f

        for (consumer in consumers) {
            for (baseConsume in consumer.all()) {
                if (baseConsume is ConsumePower<*>) baseConsume.showIcon = true

                if (baseConsume !is ConsumeItemBase<*> && baseConsume !is ConsumePayload<*>) {
                    val old: Floatf<Any>? = baseConsume.consMultiplier as Floatf<Any>?
                    baseConsume.setMultiple(if (old == null) Floatf { e: Any? -> ((e as SglTurretBuild).coolantScl) } else (Floatf { e: Any -> old!!.get(e) * (e as SglTurretBuild).coolantScl }))
                }
            }
        }

        super.init()
    }

    fun newAmmo(ammoType: BulletType, value: Cons2<Table, BulletType>): AmmoDataEntry {
        return newAmmo(ammoType, false, value)
    }

    fun newAmmo(ammoType: BulletType, override: Boolean = false, value: Cons2<Table, BulletType> = Cons2 { t: Table?, p: BulletType? -> }): AmmoDataEntry {
        consume = object : SglConsumers(false) {
            init {
                showTime = false
            }

            override fun time(time: Float): BaseConsumers {
                showTime = false
                craftTime = time
                return this
            }

            override fun items(vararg items: ItemStack): ConsumeItems<*> {
                val add1 = add(ConsumeItems(items))
                add1.showPerSecond = false
                return add1
            }
        }
        consumers.add(consume)
        val res: AmmoDataEntry
        ammoTypes.put(consume, AmmoDataEntry(ammoType, override).also { res = it })
        res.display(value)

        return res
    }

    fun newCoolant(scl: Float, duration: Float) {
        newOptionalConsume({ e: SglTurretBuild?, c: BaseConsumers? ->
            e!!.applyCoolant(c, scl, duration)
        }, { s: Stats?, c: BaseConsumers? ->
            s!!.add(Stat.booster) { t: Table? ->
                t!!.table { req: Table? ->
                    req!!.left().defaults().left().padLeft(3f)
                    for (co in c!!.all()) {
                        co.buildIcons(req)
                    }
                }.left().padRight(40f)
                t.add(Core.bundle.format("bullet.reload", Strings.autoFixed(scl * 100, 1))).growX().right()
            }
        })
        val c: BaseConsumers? = consume
        consume!!.customDisplayOnly = true
        consume!!.optionalAlwaysValid = false
        consume!!.consValidCondition { t: SglTurretBuild? -> t!!.consumer!!.current != null && t.reloadCounter < t.consumer!!.current!!.craftTime && (t.currCoolant == null || t.currCoolant === c) }
    }

    /**使用默认的冷却模式，与原版的冷却稍有不同，液体的温度和热容共同确定冷却力，热容同时影响液体消耗倍率 */
    fun newCoolant(baseCoolantScl: Float, attributeMultiplier: Float, filter: Boolf<Liquid?>, usageBase: Float, duration: Float) {
        newCoolant(
            { liquid: Liquid? -> baseCoolantScl + (liquid!!.heatCapacity * 1.2f - (liquid.temperature - 0.35f) * 0.6f) * attributeMultiplier }, { liquid: Liquid? -> !liquid!!.gas && liquid.coolant && filter.get(liquid) }, usageBase, { liquid: Liquid? -> usageBase / (liquid!!.heatCapacity * 0.7f) }, duration
        )
        val c: BaseConsumers? = consume
        consume!!.optionalAlwaysValid = false
        consume!!.consValidCondition { t: SglTurretBuild? -> (t!!.currCoolant == null || t.currCoolant === c) }
    }

    fun newCoolant(coolEff: Floatf<Liquid?>, filters: Boolf<Liquid?>?, usageBase: Float, usageMult: Floatf<Liquid?>, duration: Float) {
        newOptionalConsume({ e: SglTurretBuild?, c: BaseConsumers? ->
            var cl: ConsumeLiquidCond<SglTurretBuild>? = null
            if (((c!!.get(ConsumeType.liquid) as ConsumeLiquidCond<SglTurretBuild>).also { cl = it }) != null) {
                val curr = cl!!.getCurrCons(e)
                if (curr != null) e!!.applyCoolant(c, coolEff.get(curr), duration)
            }
        }, { s: Stats?, c: BaseConsumers? ->
            s!!.add(Stat.booster) { t: Table? ->
                t!!.defaults().left().padTop(4f)
                t.row()
                val get = c!!.get(ConsumeType.liquid)
                if (get is ConsumeLiquidCond<*>) {
                    for (stack in (get as ConsumeLiquidCond<*>).cons) {
                        val liquid: Liquid = stack.liquid

                        t.add(StatValues.displayLiquid(liquid, usageBase * usageMult.get(liquid) * 60, true)).padRight(40f).left().top().height(50f)
                        t.table(Tex.underline) { tb: Table? ->
                            tb!!.right().add(Core.bundle.format("bullet.reload", Strings.autoFixed(coolEff.get(liquid) * 100, 1))).growX().right()
                        }.height(50f).growX().right()
                        t.row()
                    }
                }
            }
        })
        consume!!.optionalAlwaysValid = false
        consume!!.add(object : ConsumeLiquidCond<SglTurretBuild>() {
            init {
                filter = filters
                usage = usageBase
                usageMultiplier = usageMult

                maxFlammability = 0.1f
            }

            override fun display(stats: Stats) {}
        })
        val c: BaseConsumers? = consume
        consume!!.consValidCondition { t: SglTurretBuild? -> t!!.consumer!!.current != null && t.reloadCounter < t.consumer!!.current!!.craftTime && (t.currCoolant == null || t.currCoolant === c) }
    }

    override fun setStats() {
        super.setStats()

        stats.add(Stat.shootRange, range / Vars.tilesize, StatUnit.blocks)
        stats.add(Stat.inaccuracy, inaccuracy.toInt().toFloat(), StatUnit.degrees)
        stats.add(Stat.targetsAir, targetAir)
        stats.add(Stat.targetsGround, targetGround)

        stats.add(Stat.ammo) { table ->
            table!!.defaults().padLeft(15f)
            for (entry in ammoTypes) {
                table.row()
                table.table(SglDrawConst.grayUIAlpha, Cons { t ->
                    t!!.left().defaults().left().growX()
                    t.table { st ->
                        st!!.left().defaults().left()
                        st.table { c ->
                            c!!.left().defaults().left()
                            for (consume in entry.key!!.all()) {
                                c.table { cons: Table? ->
                                    cons!!.left().defaults().left().padLeft(3f).fill()
                                    consume.buildIcons(cons)
                                }.fill()
                            }
                        }.fill()

                        st.row()
                        st.add(Stat.reload.localized() + ":" + Strings.autoFixed(60f / entry.key!!.craftTime * shoot.shots, 1) + StatUnit.perSecond.localized())
                        if (entry.value!!.reloadAmount > 1) {
                            st.row()
                            st.add(Core.bundle.format("bullet.multiplier", entry.value!!.reloadAmount))
                        }
                    }
                    t.row()
                    val ammoEntry: AmmoDataEntry = entry.value!!
                    val type = ammoEntry.bulletType

                    t.left().defaults().padRight(3f).left()

                    if (type.spawnUnit != null && type.spawnUnit.weapons.size > 0) {
                        UIUtils.buildAmmo(t, type.spawnUnit.weapons.first().bullet)
                        return@Cons
                    }
                    t.table { bt: Table? ->
                        bt!!.defaults().left()
                        if (!ammoEntry.override) {
                            UIUtils.buildAmmo(bt, type)
                        }
                        for (value in ammoEntry.statValues) {
                            value.get(bt, type)
                        }
                    }.left()
                }).fillY().growX().margin(10f).pad(5f)
            }
        }
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        super.drawPlace(x, y, rotation, valid)
        Drawf.dashCircle(x * Vars.tilesize + offset, y * Vars.tilesize + offset, range, Pal.placing)
    }

    open inner class SglTurretBuild : SglBuilding(), ControlBlock, Ranged {
        var recoilOffset: Vec2 = Vec2()
        var charge: Float = 0f
        var reloadCounter: Float = 0f
        var coolantScl: Float = 0f
        var coolantSclTimer: Float = 0f
        var warmup: Float = 0f
        var target: Posc? = null
        var rotationu: Float = 90f
        var targetPos: Vec2 = Vec2()
        var unit: BlockUnitc = UnitTypes.block.create(team) as BlockUnitc
        var curRecoil: Float = 0f
        var heat: Float = 0f
        var wasShooting: Boolean = false
        var logicShooting: Boolean = false
        var currentAmmo: AmmoDataEntry? = null
        var shotStack: Int = 0
        var totalShots: Int = 0
        var queuedBullets: Int = 0
        var logicControlTime: Float = 0f
        var currCoolant: BaseConsumers? = null

        fun applyCoolant(consing: BaseConsumers?, scl: Float, duration: Float) {
            coolantSclTimer = max(coolantSclTimer, duration)
            currCoolant = consing
            coolantScl = scl
        }

        fun logicControlled(): Boolean {
            return logicControlTime > 0
        }


        override fun updateTile() {
            wasShooting = false
            if (consumer.current == null) return

            if (!ammoTypes.containsKey(consumer!!.current)) throw RuntimeException("unknown ammo recipe")

            currentAmmo = ammoTypes.get(consumer!!.current)

            curRecoil = Mathf.approachDelta(curRecoil, 0f, 1 / (if (recoilTime > 0) recoilTime else consumer!!.current!!.craftTime))
            heat = Mathf.approachDelta(heat, 0f, 1 / cooldownTime * coolantScl)
            charge = if (charging()) Mathf.approachDelta(charge, 1f, 1 / shoot.firstShotDelay) else 0f

            unit.tile(this)
            unit.rotation(rotationu)
            unit.team(team)
            recoilOffset.trns(rotationu, -Mathf.pow(curRecoil, recoilPow) * recoil)

            updateTarget()

            if (!isControlled) {
                unit.aimX(targetPos.x)
                unit.aimY(targetPos.y)
            }

            if (logicControlTime > 0) {
                logicControlTime -= Time.delta
            }
            val tarValid = validateTarget()
            val targetRot = angleTo(targetPos)

            if (tarValid && (shootValid() || isControlled) && (moveWhileCharging || !charging())) {
                turnToTarget(targetRot)
            }

            if (wasShooting() && shootValid()) {
                if (canShoot() && tarValid) {
                    warmup = if (linearWarmup) Mathf.approachDelta(warmup, 1f, warmupSpeed * consEfficiency()) else Mathf.lerpDelta(warmup, 1f, warmupSpeed * consEfficiency())
                    wasShooting = true

                    if (!charging() && warmup >= fireWarmupThreshold) {
                        if (reloadCounter >= consumer!!.current!!.craftTime) {
                            if (Angles.angleDist(rotationu, targetRot) < shootCone) {
                                doShoot(currentAmmo!!.bulletType)
                            }
                        }
                    }
                }
            } else if (!chargingWarm || !charging()) {
                warmup = if (linearWarmup) Mathf.approachDelta(warmup, 0f, warmupSpeed) else Mathf.lerpDelta(warmup, 0f, warmupSpeed)
            }

            if (canShoot() && shootValid() && !charging() && reloadCounter < consumer!!.current!!.craftTime) {
                reloadCounter += consEfficiency() * delta() * coolantScl
                if (coolantSclTimer > 0) {
                    val c = consumer!!.optionalCurr?.get(ConsumeType.liquid) as ConsumeLiquidBase<SglTurretBuild?>?
                    var usage = 0f
                    if (c is ConsumeLiquidCond<*>) {
                        val l = (c as ConsumeLiquidCond<*>).getCurrCons(this)
                        if (l != null) usage = c.usageMultiplier.get(l)
                    } else if (c is ConsumeLiquids<*>) {
                        for (liquid in c.consLiquids!!) {
                            usage += liquid.amount
                        }
                    }
                    if (Mathf.chance(0.06 * usage)) {
                        coolEffect.at(x + Mathf.range(size * Vars.tilesize / 2f), y + Mathf.range(size * Vars.tilesize / 2f))
                    }
                }
            }

            if (coolantSclTimer > 0) coolantSclTimer -= Time.delta
            else {
                currCoolant = null
                coolantScl = 1f
            }
        }

        fun shootValid(): Boolean {
            return consumeValid() || shotStack > 0
        }

        open fun canShoot(): Boolean {
            return true
        }

        open fun turnToTarget(targetRot: Float) {
            rotationu = Angles.moveToward(rotationu, targetRot, rotateSpeed * delta())
        }

        protected fun validateTarget(): Boolean {
            return !Units.invalidateTarget(target, if (canHeal()) Team.derelict else team, x, y) || isControlled || logicControlled()
        }

        override fun shouldConsume(): Boolean {
            return super.shouldConsume() && !charging() && consumer!!.current != null && reloadCounter < consumer!!.current!!.craftTime
        }

        open fun doShoot(type: BulletType) {
            val bulletX = x + Angles.trnsx(rotationu - 90, shootX, shootY)
            val bulletY = y + Angles.trnsy(rotationu - 90, shootX, shootY)

            if (shoot.firstShotDelay > 0) {
                chargeSound.at(bulletX, bulletY, max(chargeSoundPitch + Mathf.random(-soundPitchRange, soundPitchRange), 0.01f), chargeSoundVolume)
                type.chargeEffect.at(bulletX, bulletY, rotationu, type.lightColor)
            }

            shoot.shoot(totalShots) { xOffset: Float, yOffset: Float, angle: Float, delay: Float, mover: Mover? ->
              queuedBullets++
              if (delay > 0f) {
                Time.run(delay) { bullet(type, xOffset, yOffset, angle, mover) }
              } else {
                bullet(type, xOffset, yOffset, angle, mover)
              }
              totalShots++
            }

          reloadCounter %= consumer!!.current!!.craftTime

            if (shotStack <= 0) {
                consumer.trigger()
                shotStack = currentAmmo!!.reloadAmount
            }
            if (shotStack > 0) {
                shotStack--
            }
        }

        protected fun bullet(type: BulletType, xOffset: Float, yOffset: Float, angleOffset: Float, mover: Mover?) {
            queuedBullets--

            if (dead) return
            val xSpread = Mathf.range(xRand)
            val bulletX = x + Angles.trnsx(rotationu - 90, shootX + xOffset + xSpread, shootY + yOffset)
            val bulletY = y + Angles.trnsy(rotationu - 90, shootX + xOffset + xSpread, shootY + yOffset)
            val shootAngle = rotationu + angleOffset + Mathf.range(inaccuracy)
            val lifeScl = if (type.scaleLife) Mathf.clamp(Mathf.dst(bulletX, bulletY, targetPos.x, targetPos.y) / type.range, minRange / type.range, range() / type.range) else 1f

            handleBullet(type.create(this, team, bulletX, bulletY, shootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y), xOffset, yOffset, shootAngle - rotationu)

            (if (shootEffect == null) type.shootEffect else shootEffect)!!.at(bulletX, bulletY, rotationu + angleOffset, type.hitColor)
            (if (smokeEffect == null) type.smokeEffect else smokeEffect)!!.at(bulletX, bulletY, rotationu + angleOffset, type.hitColor)
            shootSound.at(bulletX, bulletY, max(shootSoundPitch + Mathf.random(-soundPitchRange, soundPitchRange), 0.01f), shootSoundVolume)

            ammoUseEffect.at(
                x - Angles.trnsx(rotationu, ammoEjectBack), y - Angles.trnsy(rotationu, ammoEjectBack), rotationu * Mathf.sign(xOffset)
            )

            if (shake > 0) {
                Effect.shake(shake, shake, this)
            }

            curRecoil = 1f
            heat = 1f
        }

        override fun canControl(): Boolean {
            return playerControllable
        }

        override fun control(type: LAccess?, p1: Double, p2: Double, p3: Double, p4: Double) {
            if (type == LAccess.shoot && !unit!!.isPlayer) {
                targetPos.set(World.unconv(p1.toFloat()), World.unconv(p2.toFloat()))
                logicControlTime = Turret.logicControlCooldown
                logicShooting = !Mathf.zero(p3)
            }

            super.control(type, p1, p2, p3, p4)
        }

        override fun control(type: LAccess?, p1: Any?, p2: Double, p3: Double, p4: Double) {
            if (type == LAccess.shootp && (unit == null || !unit!!.isPlayer)) {
                logicControlTime = Turret.logicControlCooldown
                logicShooting = !Mathf.zero(p2)

                if (p1 is Posc) {
                    targetPosition(p1)
                }
            }

            super.control(type, p1, p2, p3, p4)
        }

        override fun sense(sensor: LAccess): Double {
            return when (sensor) {
                LAccess.ammo -> items.total()
                LAccess.ammoCapacity -> itemCapacity
                LAccess.rotation -> rotationu
                LAccess.shootX -> World.conv(targetPos.x)
                LAccess.shootY -> World.conv(targetPos.y)
                LAccess.shooting -> if (wasShooting()) 1 else 0
                LAccess.progress -> progress()
                else -> super.sense(sensor)
            }.toDouble()
        }

        override fun progress(): Float {
            return if (consumer.current == null) 0f else Mathf.clamp(reloadCounter / consumer!!.current!!.craftTime)
        }

        protected open fun handleBullet(bullet: Bullet?, offsetX: Float, offsetY: Float, angleOffset: Float) {}

        fun charging(): Boolean {
            return queuedBullets > 0 && shoot.firstShotDelay > 0
        }

        fun wasShooting(): Boolean {
            return if (isControlled) unit.isShooting else if (logicControlled()) logicShooting else target != null
        }

        fun updateTarget() {
            if (timer(timerTarget, targetInterval)) {
                findTarget()
            }

            if (isControlled) {
                targetPos.set(unit.aimX(), unit.aimY())
            } else {
                targetPosition(target)
            }
        }

        fun targetPosition(pos: Posc?) {
            if (!shootValid() || pos == null || currentAmmo == null) return
            val offset = Tmp.v1.setZero()

            if (accurateDelay && pos is Hitboxc) {
                offset.set(pos.deltaX(), pos.deltaY()).scl(shoot.firstShotDelay / Time.delta)
            }

            if (accurateSpeed) {
                targetPos.set(Predict.intercept(this, pos, offset.x, offset.y, if (currentAmmo!!.bulletType.speed <= 0.01f) 1.0E8f else currentAmmo!!.bulletType.speed))
            } else targetPos.set(pos)

            if (targetPos.isZero) {
                targetPos.set(pos)
            }
        }

        override fun range(): Float {
            if (currentAmmo != null) {
                return range + currentAmmo!!.bulletType.rangeChange
            }
            return range
        }

        fun findTarget() {
            val range = range()

            if (targetAir && !targetGround) {
                target = Units.bestEnemy(team, x, y, range, Boolf { e: Unit? -> !e!!.dead() && !e.isGrounded && unitFilter.get(e) }, unitSort)
            } else {
                val heal = canHeal()

                target = Units.bestTarget(
                    null, x, y, range, Boolf { e: Unit? -> (e!!.team !== team || (heal && targetHealUnit && e.damaged())) && !e.dead() && unitFilter.get(e) && (e.isGrounded || targetAir) && (!e.isGrounded || targetGround) }, Boolf { b: Building? -> (b!!.team !== team || (heal && b.damaged())) && targetGround && buildingFilter.get(b) }, unitSort
                )
            }
        }

        protected fun canHeal(): Boolean {
            return targetHealing && shootValid() && currentAmmo!!.bulletType.collidesTeam && currentAmmo!!.bulletType.heals()
        }

        override fun drawSelect() {
            super.drawSelect()

            Drawf.dashCircle(x, y, range, Pal.placing)
        }

        override fun warmup(): Float {
            return warmup
        }

        override fun drawrot(): Float {
            return rotationu - 90f
        }

        override fun shouldConsumeOptions(): Boolean {
            return consumer.current != null && reloadCounter < consumer.current!!.craftTime
        }

        override fun unit(): Unit? {
            unit.tile(this)
            unit.team(team)
            return unit as Unit?
        }

        override fun write(write: Writes) {
            super.write(write)
            write.f(reloadCounter)
            write.f(warmup)
            write.f(rotationu)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            reloadCounter = read.f()
            warmup = read.f()
            rotationu = read.f()
        }
    }

    class AmmoDataEntry(val bulletType: BulletType, val override: Boolean) {
        var reloadAmount: Int = 1
        val statValues = Seq<Cons2<Table, BulletType>>()

        fun display(statValue: Cons2<Table, BulletType>): AmmoDataEntry {
            statValues.add(statValue)
            return this
        }

        fun setReloadAmount(amount: Int): AmmoDataEntry {
            reloadAmount = amount
            return this
        }
    }
}
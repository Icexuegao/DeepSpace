package singularity.contents

import arc.func.Cons2
import arc.func.Intf
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.scene.style.TextureRegionDrawable
import arc.struct.ObjectSet
import arc.util.Time
import arc.util.Tmp
import arc.util.pooling.Pool.Poolable
import arc.util.pooling.Pools
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.UnitSorts
import mindustry.entities.bullet.BulletType
import mindustry.entities.bullet.LaserBulletType
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Building
import mindustry.gen.Bullet
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.defense.Wall
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.Stats
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.SglUnitSorts
import singularity.world.blocks.defence.GameOfLife
import singularity.world.blocks.defence.PhasedRadar
import singularity.world.blocks.defence.SglWall
import singularity.world.draw.DrawDirSpliceBlock
import singularity.world.meta.SglStat
import universecore.world.consumers.BaseConsumers
import universecore.world.lightnings.LightningContainer
import universecore.world.lightnings.generator.VectorLightningGenerator

class DefenceBlocks : ContentList {
    override fun load() {
        phased_radar = object : PhasedRadar("phased_radar") {
            init {
                requirements(Category.effect, ItemStack.with())

                newConsume()
                consume!!.power(1f)

                draw = DrawMulti(
                    DrawDefault(),
                    object : DrawDirSpliceBlock<PhasedRadarBuild?>() {
                        init {
                            simpleSpliceRegion = true
                            spliceBits = Intf { e: PhasedRadarBuild? -> e!!.spliceDirBit }
                            layerRec = false
                        }
                    },
                    object : DrawRegion("_rotator") {
                        init {
                            layer = Layer.blockOver
                            rotateSpeed = 0.4f
                        }
                    }
                )
            }
        }

        strengthening_alloy_wall = object : Wall("strengthening_alloy_wall") {
            init {
                requirements(Category.defense, ItemStack.with(SglItems.strengthening_alloy, 8))
                health = 900
            }
        }

        strengthening_alloy_wall_large = object : Wall("strengthening_alloy_wall_large") {
            init {
                requirements(Category.defense, ItemStack.with(SglItems.strengthening_alloy, 32))
                size = 2
                health = 900 * 4
            }
        }

        neutron_polymer_wall = object : SglWall("neutron_polymer_wall") {
            init {
                requirements(Category.defense, ItemStack.with(SglItems.degenerate_neutron_polymer, 8, SglItems.strengthening_alloy, 4))
                health = 2400
                density = 1024f
                damageFilter = 72f
                absorbLasers = true
            }
        }

        neutron_polymer_wall_large = object : SglWall("neutron_polymer_wall_large") {
            init {
                requirements(Category.defense, ItemStack.with(SglItems.degenerate_neutron_polymer, 32, SglItems.strengthening_alloy, 16, SglItems.aerogel, 8))
                size = 2
                health = 2400 * 4
                density = 1024f
                damageFilter = 95f
                absorbLasers = true
            }
        }

        attack_matrix = object : GameOfLife("attack_matrix") {
            init {
                requirements(Category.defense, ItemStack.with())
                size = 8
                health = 5200

                hasItems = true
                itemCapacity = 64

                energyCapacity = 16384f
                basicPotentialEnergy = 8192f

                newConsume()
                consume!!.power(160f)
                consume!!.energy(20f)

                launchCons.time(600f)
                launchCons.energy(12f)
                launchCons.item(SglItems.anti_metter, 1)
                launchCons.display = Cons2 { s: Stats?, c: BaseConsumers? ->
                    s!!.add(SglStat.multiple, 4.toString() + "*cells")
                }

                draw = DrawMulti(
                    DrawDefault(),
                    object : DrawBlock() {
                        override fun draw(build: Building?) {
                            if (build is GameOfLifeBuild) {


                                Draw.z(Layer.effect)
                                Draw.color(Color.white)

                                Fill.square(build.x, build.y, 6 * build.depoly, 45f)

                                Lines.stroke(1.5f * build.depoly)
                                Lines.square(build.x, build.y, 8 + 8 * build.depoly, -Time.time * 2)
                                Lines.stroke(2 * build.depoly)
                                Lines.square(build.x, build.y, 10 + 18 * build.depoly, Time.time)

                                Lines.stroke(3 * build.depoly)
                                Lines.square(build.x, build.y, 40 * build.depoly, 45f)

                                if (build.depoly >= 0.99f && !build.launched && build.activity) {
                                    Fill.square(build.x, build.y, 30 * build.warmup, 45f)
                                }

                                for (p in Geometry.d4) {
                                    Tmp.v1.set(p.x.toFloat(), p.y.toFloat()).scl(50 + Mathf.absin(6f, 4f)).rotate(Time.time * 0.6f)
                                    Draw.rect((SglDrawConst.matrixArrow as TextureRegionDrawable).getRegion(), build.x + Tmp.v1.x, build.y + Tmp.v1.y, 16 * build.warmup, 16 * build.warmup, Tmp.v1.angle() + 90)
                                }
                            }
                        }
                    }
                )

                addDeathTrigger(0, effectEnemy(StatusEffects.electrified, cellSize / 2 * 3.2f, 45f))
                addDeathTrigger(1, 2, damage(260f, cellSize / 2 * 3.2f, 2f))
                addDeathTrigger(1, 2, effectEnemy(StatusEffects.sapped, cellSize / 2 * 3.2f, 60f))

                addDeathTrigger(3, shootBullet(object : BulletType() {
                    init {
                        damage = 420f
                        speed = 6f
                        lifetime = 72f
                        homingRange = 300f
                        homingPower = 0.12f

                        pierceArmor = true

                        status = StatusEffects.slow
                        statusDuration = 60f

                        despawnHit = true
                        hitEffect = SglFx.spreadDiamondSmall
                        hitColor = Color.white

                        trailEffect = Fx.trailFade
                        trailColor = Color.black
                        trailLength = 30
                        trailWidth = 2.5f
                        layer = Layer.bullet - 1
                    }

                    override fun draw(b: Bullet) {
                        super.draw(b)

                        Draw.color(Color.black)
                        Fill.square(b.x, b.y, 5f, Time.time * 5)

                        Draw.color(Color.white)
                        Lines.stroke(2f)
                        Lines.square(b.x, b.y, 10f, -Time.time * 5)
                    }
                }, object : ShootPattern() {
                    init {
                        shots = 4
                    }

                    override fun shoot(totalShots: Int, handler: BulletHandler) {
                        for (i in 0..<shots) {
                            handler.shoot(0f, 0f, (45 + i * 90).toFloat(), 0f)
                        }
                    }
                }))

                addDeathTrigger(4, 5, fx(SglFx.crossLight))
                addDeathTrigger(4, shootBullet(object : BulletType() {
                    init {
                        damage = 285f
                        speed = 4.5f
                        lifetime = 70f
                        homingRange = 360f
                        homingPower = 0.25f

                        pierceArmor = true

                        hitEffect = SglFx.spreadDiamondSmall
                        hitColor = Color.white

                        trailEffect = Fx.trailFade
                        trailColor = Color.black
                        trailLength = 24
                        trailWidth = 2.5f
                        layer = Layer.bullet - 1

                        fragBullets = 1
                        fragOnAbsorb = true
                        fragAngle = 0f
                        fragSpread = 0f
                        fragRandomSpread = 0f
                        fragBullet = object : LaserBulletType() {
                            init {
                                length = 280f
                                damage = 280f
                                layer = Layer.bullet - 1
                                colors = arrayOf<Color?>(Color.black.cpy().a(0.4f), Color.black, Color.white)
                            }
                        }
                    }

                    override fun draw(b: Bullet) {
                        super.draw(b)

                        Draw.color(Color.black)
                        Fill.square(b.x, b.y, 5f, Time.time * 5)

                        Draw.color(Color.white)
                        Lines.stroke(1.5f)
                        Lines.square(b.x, b.y, 8f, -Time.time * 5)
                        Lines.stroke(2.5f)
                        Lines.square(b.x, b.y, 14f, Time.time * 5)
                    }
                }, object : ShootPattern() {
                    init {
                        shots = 4
                    }

                    override fun shoot(totalShots: Int, handler: BulletHandler) {
                        for (i in 0..<shots) {
                            handler.shoot(0f, 0f, (i * 90).toFloat(), 0f)
                        }
                    }
                }))

                addDeathTrigger(5, shootBullet(object : BulletType() {
                    init {
                        damage = 180f
                        clipSize = 200f
                        speed = 0f
                        homingRange = 320f
                        homingPower = 0f
                        homingDelay = 240f
                        lifetime = 240f
                        hittable = false
                        collides = false
                        absorbable = false
                        reflectable = false
                        layer = Layer.bullet - 1
                        pierce = true
                        pierceCap = -1
                        pierceBuilding = true
                        removeAfterPierce = false

                        status = StatusEffects.shocked
                        statusDuration = 30f

                        trailEffect = SglFx.spreadDiamond
                        trailColor = Color.white
                        trailInterval = 60f
                        trailWidth = 2.5f
                        trailLength = 26

                        despawnEffect = SglFx.spreadField

                        hitEffect = SglFx.spreadDiamondSmall
                        hitColor = Color.white
                    }

                    val gen: VectorLightningGenerator = VectorLightningGenerator().apply {

                            minInterval = 12f
                            maxInterval = 26f
                            maxSpread = 14f

                    }

                    override fun init(b: Bullet) {
                        super.init(b)
                        b.data = Pools.obtain(Data::class.java, Prov { Data() })
                    }

                    override fun removed(b: Bullet) {
                        if (b.data is Data) {
                            val data = b.data
                            Pools.free(data)
                        }
                        super.removed(b)
                    }

                    override fun update(b: Bullet) {
                        super.update(b)
                        val data = b.data
                        if (data is Data) {
                            data.container.update()

                            if (b.timer(2, 6f)) {
                                data.related.clear()
                                data.aim = null

                                for (unit in SglUnitSorts.findEnemies(3, b, 320f, UnitSorts.farthest)) {
                                    if (unit == null) continue

                                    if (data.aim == null) data.aim = unit

                                    if (!unit.within(b.x, b.y, 200f) || data.related.size >= 3) return

                                    if (data.related.add(unit)) {
                                        unit.damage(damage)
                                        unit.apply(status, statusDuration)
                                        hitEffect.at(unit.x, unit.y, hitColor)

                                        gen.vector.set(unit.x - b.x, unit.y - b.y)
                                        data.container.create(gen)
                                    }
                                }
                            }

                            if (data.aim != null) {
                                b.vel.lerpDelta(Tmp.v1.set(data.aim!!.x - b.x, data.aim!!.y - b.y).setLength(2f), 0.05f)
                            }
                        }
                    }

                    override fun draw(b: Bullet) {
                        super.draw(b)

                        Draw.color(Color.black)
                        Fill.square(b.x, b.y, 4f, Time.time * 2)

                        Draw.z(Layer.bullet)
                        Draw.color(Color.white)
                        Lines.stroke(1.5f)
                        Lines.square(b.x, b.y, 8f, -Time.time * 3)
                        Lines.stroke(2.5f)
                        Lines.square(b.x, b.y, 16f, Time.time * 4)
                        Lines.stroke(3f)
                        Lines.square(b.x, b.y, 24f, -Time.time * 5)
                        val data = b.data
                        if (data is Data) {
                            data.container.draw(b.x, b.y)
                            for (other in data.related) {
                                Draw.z(Layer.bullet - 1)
                                Lines.stroke(5f, Color.black)
                                Lines.line(b.x, b.y, other.x, other.y)
                                Draw.z(Layer.bullet)
                                Lines.stroke(3.25f, Color.white)
                                Lines.line(b.x, b.y, other.x, other.y)
                            }
                        }
                    }

                    override fun continuousDamage(): Float {
                        return damage * 10
                    }


                }, object : ShootPattern() {
                    init {
                        shots = 3
                    }

                    override fun shoot(totalShots: Int, handler: BulletHandler) {
                        handler.shoot(0f, 0f, 0f, 0f)
                    }
                }))
            }
        }
    }
    class Data : Poolable {
        val container: LightningContainer = object : LightningContainer() {
            init {
                maxWidth = 6f
                minWidth = 4f

                time = 0f
                lifeTime = 18f
            }
        }
        val related: ObjectSet<Unit> = ObjectSet<Unit>()
        var aim: Unit? = null

        override fun reset() {
            related.clear()
            aim = null
        }
    }
    companion object {
        /**相控雷达 */
        var phased_radar: Block? = null

        /**强化合金墙 */
        var strengthening_alloy_wall: Block? = null

        /**大型强化合金墙 */
        var strengthening_alloy_wall_large: Block? = null

        /**简并态中子聚合物墙 */
        var neutron_polymer_wall: Block? = null

        /**大型简并态中子聚合物墙 */
        var neutron_polymer_wall_large: Block? = null

        /**生命游戏-混沌矩阵 */
        var attack_matrix: Block? = null
    }
}
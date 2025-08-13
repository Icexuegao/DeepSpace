package ice.library.baseContent.blocks.distribution

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.struct.Seq
import arc.util.Eachable
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.IFiles
import ice.library.baseContent.blocks.abstractBlocks.IceBlock
import ice.library.scene.tex.Colors
import mindustry.Vars
import mindustry.entities.Effect
import mindustry.entities.Effect.EffectContainer
import mindustry.entities.TargetPriority
import mindustry.entities.effect.WaveEffect
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.gen.Sounds
import mindustry.gen.Teamc
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.Autotiler
import mindustry.world.blocks.Autotiler.SliceMode
import mindustry.world.blocks.distribution.Conveyor.ConveyorBuild
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import kotlin.math.min

open class IceStackConveyor(name: String) : IceBlock(name), Autotiler {
    companion object {
        val stateMove = 0
        val stateLoad = 1
        val stateUnload = 2
    }

    var speed = 0f

    /** 填满一条线路所需的（最小）装卸码头数量。 */
    var recharge = 2f
    var baseEfficiency = 0f
    var outputRouter = true
    var edgeRegion = IFiles.findPng("$name-edge")
    var stackRegion = IFiles.findPng("$name-stack")
    var regions = Array(3) {
        IFiles.findPng("$name-$it")
    }
    var loadEffect: Effect = WaveEffect().apply {
        lifetime = 20f
        sides = 3
        sizeTo = 6f
        sizeFrom = 0f
        strokeTo = 0f
        strokeFrom = 3f
        colorFrom = Colors.b4
        colorTo = Colors.b4
    }
    var unloadEffect = Effect(35.0f) { e: EffectContainer ->
        Draw.color(Pal.plasticBurn, Colors.b4, e.fin())
        Angles.randLenVectors(
            e.id.toLong(), 4, 3.0f + e.fin() * 4.0f
        ) { x: Float, y: Float ->
            Fill.circle(e.x + x, e.y + y, e.fout() * 1.11f)
        }
    }
    var drawLastItems = true
    var differentItem = false

    init {
        rotate = true
        update = true
        hasItems = true
        itemCapacity = 10
        underBullets = true
        conveyorPlacement = true
        ambientSoundVolume = 0.004f
        ambientSound = Sounds.conveyor
        group = BlockGroup.transportation
        priority = TargetPriority.transport
        buildType = Prov(::IceStackConveyorBuild)
    }

    override fun setStats() {
        super.setStats()
        stats.add(Stat.itemsMoved, Mathf.round(itemCapacity * speed * 60).toFloat(), StatUnit.itemsSecond)
    }

    override fun blends(
        tile: Tile, rotation: Int, otherx: Int, othery: Int, otherrot: Int, otherblock: Block
    ): Boolean {
        val b = tile.build
        if (b is IceStackConveyorBuild) {
            val state: Int = b.state
            if (state == stateLoad) { //standard conveyor mode
                return otherblock.outputsItems() && lookingAtEither(
                    tile, rotation, otherx, othery, otherrot, otherblock
                )
            } else if (state == stateUnload && !outputRouter) { //router mode
                val b1 = otherblock.acceptsItems && (!otherblock.noSideBlend || lookingAtEither(
                    tile, rotation, otherx, othery, otherrot, otherblock
                ))
                val b2 = notLookingAt(
                    tile, rotation, otherx, othery, otherrot, otherblock
                ) || (otherblock is IceStackConveyor && facing(
                    otherx, othery, otherrot, tile.x.toInt(), tile.y.toInt()
                ))
                val b3 = run {
                    val build = Vars.world.build(
                        otherx, othery
                    )

                    !(build is IceStackConveyorBuild && build.state == stateUnload)
                }
                val b4 = run {
                    val build = Vars.world.build(
                        otherx, othery
                    )
                    !(build is IceStackConveyorBuild && build.state == stateMove && !facing(
                        otherx, othery, otherrot, tile.x.toInt(), tile.y.toInt()
                    ))
                }

                return b1 && b2 && b3 && b4
            }
        }
        return otherblock.outputsItems() && blendsArmored(
            tile, rotation, otherx, othery, otherrot, otherblock
        ) && otherblock is IceStackConveyor
    }

    override fun drawPlanRegion(plan: BuildPlan, list: Eachable<BuildPlan>) {
        val bits = getTiling(plan, list) ?: return
        val region = regions[0]
        Draw.rect(region, plan.drawx(), plan.drawy(), (plan.rotation * 90).toFloat())

        for (i in 0..3) {
            if ((bits[3] and (1 shl i)) == 0) {
                Draw.rect(edgeRegion, plan.drawx(), plan.drawy(), ((plan.rotation - i) * 90).toFloat())
            }
        }
    }

    override fun rotatedOutput(x: Int, y: Int): Boolean {
        val tile = Vars.world.build(x, y)
        if (tile is IceStackConveyorBuild) {
            return tile.state != stateUnload
        }
        return super.rotatedOutput(x, y)
    }

    inner class IceStackConveyorBuild : IceBuild() {
        var state: Int = 0
        var blendprox: Int = 0
        var link: Int = -1
        var cooldown: Float = 0f
        var lastItem: Item? = null
        var proxUpdating: Boolean = false
        override fun draw() {
            Draw.z(Layer.block - 0.2f)

            Draw.rect(regions[state], x, y, rotdeg())

            for (i in 0..3) {
                if ((blendprox and (1 shl i)) == 0) {
                    Draw.rect(edgeRegion, x, y, ((rotation - i) * 90f))
                }
            }
            //draw inputs
            if (state == stateLoad) {
                for (i in 0..3) {
                    val dir = rotation - i
                    val near = nearby(dir)
                    if ((blendprox and (1 shl i)) != 0 && i != 0 && near != null && !near.block.squareSprite) {
                        Draw.rect(
                            sliced(regions[0], SliceMode.bottom),
                            x + Geometry.d4x(dir) * Vars.tilesize * 0.75f,
                            y + Geometry.d4y(dir) * Vars.tilesize * 0.75f,
                            (dir * 90).toFloat()
                        )
                    }
                }
            } else if (state == stateUnload) { //front unload
                //TOOD hacky front check
                if ((blendprox and (1)) != 0 && !front().block.squareSprite) {
                    Draw.rect(
                        sliced(regions[0], SliceMode.top),
                        x + Geometry.d4x(rotation) * Vars.tilesize * 0.75f,
                        y + Geometry.d4y(rotation) * Vars.tilesize * 0.75f,
                        rotation * 90f
                    )
                }
            }

            Draw.z(Layer.block - 0.1f)
            val from = Vars.world.tile(link)


            if (link == -1 || from == null || lastItem == null) return
            val fromRot = if (from.build == null) rotation else from.build.rotation
            //offset
            Tmp.v1[from.worldx()] = from.worldy()
            Tmp.v2[x] = y
            Tmp.v1.interpolate(Tmp.v2, 1f - cooldown, Interp.linear)
            //rotation
            var a = ((fromRot % 4) * 90).toFloat()
            val b = ((rotation % 4) * 90).toFloat()
            if ((fromRot % 4) == 3 && (rotation % 4) == 0) a = (-1 * 90).toFloat()
            if ((fromRot % 4) == 0 && (rotation % 4) == 3) a = (4 * 90).toFloat()

            if (drawLastItems) {
                //stack
                Draw.rect(
                    stackRegion,
                    Tmp.v1.x,
                    Tmp.v1.y,
                    Mathf.lerp(a, b, Interp.smooth.apply(1f - Mathf.clamp(cooldown * 2, 0f, 1f)))
                )
                //item
                val size = Vars.itemSize * Mathf.lerp(
                    min((items.total().toFloat() / itemCapacity).toDouble(), 1.0).toFloat(), 1f, 0.4f
                )
                Drawf.shadow(Tmp.v1.x, Tmp.v1.y, size * 1.2f)
                Draw.rect(lastItem!!.fullIcon, Tmp.v1.x, Tmp.v1.y, size, size, 0f)
            } else {
                //stack
                val size = 2 + Vars.itemSize * Mathf.lerp(
                    min((items.total().toFloat() / itemCapacity), 1.0f), 1f, 0.4f
                )
                Draw.rect(
                    stackRegion,
                    Tmp.v1.x,
                    Tmp.v1.y,
                    size,
                    size,
                    Mathf.lerp(a, b, Interp.smooth.apply(1f - Mathf.clamp(cooldown * 2, 0f, 1f)))
                )
            }
        }

        override fun drawCracks() {
            Draw.z(Layer.block - 0.15f)
            super.drawCracks()
        }

        override fun payloadDraw() {
            Draw.rect(block.fullIcon, x, y)
        }

        override fun onProximityUpdate() {
            super.onProximityUpdate()
            val lastState = state

            state = stateMove
            val bits = buildBlending(tile, rotation, null, true)
            if (bits[0] == 0 && blends(tile, rotation, 0) && (!blends(
                    tile, rotation, 2
                ) || back() is IceStackConveyorBuild && (back() as IceStackConveyorBuild).state == stateUnload)
            ) {
                state = stateLoad // a 0 that faces into a conveyor with none behind it
            }

            if (outputRouter && bits[0] == 0 && !blends(tile, rotation, 0) && blends(tile, rotation, 2)) state =
                stateUnload // a 0 that faces into none with a conveyor behind it
            if (!outputRouter && front() !is IceStackConveyorBuild) state =
                stateUnload // a 0 that faces into none with a conveyor behind it
            if (!Vars.headless) {
                blendprox = 0

                for (i in 0..3) {
                    if (blends(
                            tile, rotation, i
                        ) && (state != stateUnload || outputRouter || i == 0 || nearby(
                            Mathf.mod(
                                rotation - i, 4
                            )
                        ) is IceStackConveyorBuild)
                    ) {
                        blendprox = blendprox or (1 shl i)
                    }
                }
            }
            //面对时无法加载
            if (state == stateLoad) {
                for (near in proximity) {
                    if (near is IceStackConveyorBuild && near.front() === this) {
                        state = stateMove
                        break
                    }
                }
            }
            //Update Other Conveyor 状态 当此 Conveyor 的状态发生变化时
            if (state != lastState) {
                proxUpdating = true
                for (near in proximity) {
                    if (!(near is IceStackConveyorBuild && near.proxUpdating && near.state != stateUnload)) {
                        near.onProximityUpdate()
                    }
                }
                proxUpdating = false
            }
        }

        override fun canUnload(): Boolean {
            return state != stateLoad
        }

        override fun updateTile() {
            val eff = if (enabled) (efficiency + baseEfficiency) else 0f
            //reel in crater
            if (cooldown > 0f) cooldown = Mathf.clamp(cooldown - speed * eff * delta(), 0f, recharge)
            //表示空状态
            if (link == -1) return
            //Crater 需要居中
            if (cooldown > 0f) return
            //获取当前物品
            if (lastItem == null || !items.has(lastItem)) {
                lastItem = items.first()
            }
            //如果禁用，则不继续, 仍允许卷入一个以防止视觉堆叠
            if (!enabled) return

            if (state == stateUnload) { //卸载
                while (lastItem != null && (if (!outputRouter) moveForward(lastItem) else dump(lastItem))) {
                    if (!outputRouter) {
                        items.remove(lastItem, 1)
                    }

                    if (items.empty()) {
                        poofOut()
                        lastItem = null
                    }
                    if (lastItem !== items.first()) break
                }
            } else { //transfer
                if (state != stateLoad || (items.total() >= getMaximumAccepted(lastItem))) {
                    if (front() is IceStackConveyorBuild && (front() as IceStackConveyorBuild).team === team) {
                        val e = (front() as IceStackConveyorBuild)
                        //sleep if its occupied
                        if (e.link == -1) {
                            e.items.add(items)
                            e.lastItem = lastItem
                            e.link = tile.pos()
                            //▲ to | from ▼
                            link = -1
                            items.clear()

                            cooldown = recharge
                            e.cooldown = 1f
                        }
                    }
                }
            }
        }

        override fun overwrote(builds: Seq<Building>) {
            val build = builds.first()
            if (build is ConveyorBuild) {
                val item: Item? = build.items.first()
                if (item != null) {
                    handleStack(item, build.items.get(item), null)
                }
            }
        }

        override fun shouldAmbientSound(): Boolean {
            return false //has no moving parts;
        }

        private fun poofIn() {
            link = tile.pos()
            loadEffect.at(this, rotdeg())
        }

        private fun poofOut() {
            unloadEffect.at(this)
            link = -1
        }

        override fun acceptStack(item: Item, amount: Int, source: Teamc): Int {
            if (items.any() && !items.has(item)) return 0
            return super.acceptStack(item, amount, source)
        }

        override fun handleItem(source: Building, item: Item) {
            if (items.empty() && tile != null) poofIn()
            super.handleItem(source, item)
            lastItem = item
        }

        override fun handleStack(item: Item, amount: Int, source: Teamc?) {
            if (amount <= 0) return
            if (items.empty() && tile != null) poofIn()
            super.handleStack(item, amount, source)
            lastItem = item
        }

        override fun removeStack(item: Item, amount: Int): Int {
            try {
                return super.removeStack(item, amount)
            } finally {
                if (items.empty()) poofOut()
            }
        }

        override fun itemTaken(item: Item) {
            if (items.empty()) poofOut()
        }

        override fun acceptItem(source: Building, item: Item): Boolean {
            if (this === source) return items.total() < itemCapacity && (!items.any() || items.has(item)) //玩家投掷物品
            if (cooldown > recharge - 1f) return false //仍在冷却
            if (items.total() >= getMaximumAccepted(item)) return false//已满员
            if (front() === source) return false

            if (!differentItem && items.any() && !items.has(item)) return false//不兼容的项目
            if (state != stateLoad) return false//不是装卸码头
            return true
        }

        override fun write(write: Writes) {
            super.write(write)

            write.i(link)
            write.f(cooldown)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)

            link = read.i()
            cooldown = read.f()
        }
    }
}

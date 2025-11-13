package ice.library.content.unit.entity

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.util.Interval
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.ILiquids
import ice.content.IUnitTypes
import ice.library.IFiles
import ice.library.content.unit.entity.base.FleshEntity
import ice.library.meta.IceEffects
import mindustry.Vars
import mindustry.entities.Puddles
import mindustry.gen.Puddle
import kotlin.math.min

class Schizovegeta : FleshEntity() {
    companion object {
        val bags: Array<TextureRegion> by lazy {
            Array(3) {
                Core.atlas.find(IFiles.getNormName("schizovegeta-bag${it + 1}"))
            }
        }
        val scorchs: Array<TextureRegion> by lazy {
            Array(2) {
                Core.atlas.find(IFiles.getNormName("schizovegeta-scorch-${it + 1}"))
            }
        }
    }

    val interval = Interval(2)
    var index = 0
    override fun drawBodyRegion(rotation: Float) {
        super.drawBodyRegion(rotation)
        Draw.rect(bags[index % 3], x, y, rotation)
    }

    override fun update() {
        super.update()
        if (interval.get(30f)) {
            index++
            if (index > 1000) index = 0
        }
        if (!interval.get(1, IceEffects.rand.random(20f, 40f))) return
        val solid = tileOn()?.floor()?.solid ?: true
        if (solid) return
        val p = Puddles.get(tileOn())
        if (!Vars.net.client() && p == null) {
            //do not create puddles clientside as that destroys syncing
            val puddle = Puddle.create()
            puddle.tile = tileOn()
            puddle.liquid = ILiquids.浓稠血浆
            puddle.amount = min(IceEffects.rand.random(20f), Puddles.maxLiquid)
            puddle.set(x + IceEffects.rand.random(-hitSize(), hitSize()),
                y + IceEffects.rand.random(-hitSize(), hitSize()))
            Puddles.register(puddle)
            puddle.add()
        }

    }

    override fun destroy() {
        (1..4).forEach { _ ->
            val x1 = IceEffects.rand.random(10f, 40f).run {
                if (IceEffects.rand.nextInt(2) > 0) this else -this
            }
            val y1 = IceEffects.rand.random(10f, 40f).run {
                if (IceEffects.rand.nextInt(2) > 0) this else -this
            }
            val create = IUnitTypes.丰穰之瘤.spawn(team, x, y, IceEffects.rand.random(0f, 360f))
            create.set(x + x1, y + y1)
            val puddle = Puddle.create()
            puddle.tile = Vars.world.tileWorld(create.x, create.y)
            puddle.liquid = ILiquids.浓稠血浆
            puddle.amount = IceEffects.rand.random(20f, 30f)
            puddle.set(create.x, create.y)
            Puddles.register(puddle)
            puddle.add()
        }
        super.destroy()
    }

    override fun read(read: Reads) {
        super.read(read)
        index = read.i()
    }

    override fun write(write: Writes) {
        super.write(write)
        write.i(index)
    }
}
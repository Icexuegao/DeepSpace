package ice.type

import arc.Events
import arc.graphics.g2d.Draw
import arc.struct.Seq
import arc.util.Interval
import arc.util.Time
import ice.library.IFiles
import ice.world.meta.IceEffects
import mindustry.game.EventType
import mindustry.world.Tile

class Dup(val tiles: Tile) {
    companion object {
        var array = Seq<Dup>()
        val foors = Seq<Tile>()
    }

    init {
        Events.on(EventType.ResetEvent::class.java) {
            array.clear()
            foors.clear()
        }
        Events.run(EventType.Trigger.draw) {
            array.forEach {
                it.draw()
            }
        }
        Events.run(EventType.Trigger.update) {
            array.forEach {
                it.update()
            }
            if (array.size <= foors.size / 5) {
                foors.random()?.let {
                    Dup(it)
                }
            }
        }
    }

    init {
        array.add(this)
    }

    var offxTime = IceEffects.rand.random(60f)
    var indx = 1
    var inty = Interval(1)
    val offx = IceEffects.rand.random(-4f, 4f)
    val offy = IceEffects.rand.random(-4f, 4f)
    var i = 0f
    val indxtexs = IceEffects.rand.random(1, 3)
    fun update() {
        i += Time.delta
        if (i > offxTime && inty[10f]) {
            if (indx + 1 != 13) indx++ else array.remove(this)
        }
    }

    fun draw() {
        Draw.rect(IFiles.findModPng("thickBloodHubble$indxtexs-$indx"), tiles.drawx() + offx, tiles.drawy() + offy)
    }
}
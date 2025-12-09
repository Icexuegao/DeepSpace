package ice.world.content.unit.entity

import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.block.Environment
import ice.world.content.unit.entity.base.Entity
import mindustry.Vars
import mindustry.content.Fx

class RichTumor : Entity() {
    var time = {
        Fx.rand.setSeed(this.id.toLong())
        Fx.rand.random(6f, 10f) * 60
    }.invoke()
    var timer = 0f
    override fun update() {
        super.update()
        timer += Time.delta
        if (timer > time) {
            Vars.world.tileWorld(x, y)?.setFloor(Environment.肿瘤地)
            kill()
        }
    }

    override fun write(write: Writes) {
        super.write(write)
        write.f(time)
        write.f(timer)
    }

    override fun read(read: Reads) {
        super.read(read)
        time = read.f()
        timer = read.f()
    }
}
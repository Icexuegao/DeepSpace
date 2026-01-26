package ice.async

import arc.struct.Seq
import ice.library.util.accessField
import ice.world.content.blocks.distribution.conveyor.PackStack
import mindustry.Vars
import mindustry.async.AsyncProcess
import mindustry.async.PhysicsProcess
import mindustry.async.PhysicsProcess.PhysicRef
import mindustry.async.PhysicsProcess.PhysicsWorld.PhysicsBody
import mindustry.gen.Groups

object ParcelProcess: AsyncProcess {
    // private PhysicsWorld physics;
    //    private Seq<PhysicRef> refs = new Seq<>(false);
    var PhysicsProcess.physics: PhysicsProcess.PhysicsWorld? by accessField("physics")
    var PhysicsProcess.refs: Seq<PhysicRef> by accessField("refs")
    var process= Vars.asyncCore.processes.get(0) as PhysicsProcess
    override fun begin() {
        if (process.physics == null) return
        val local = !Vars.net.client()


        //remove stale entities
       /* process.refs.removeAll(Boolf { ref: PhysicRef? ->
            if (!ref!!.entity.isAdded()) {
                process.physics.remove(ref.body)
                ref.entity.physref(null)
                return@removeAll true
            }
            false
        })*/


        //find Units without bodies and assign them
        for (entity in Groups.all) {
            if (entity !is PackStack) continue

            if (entity.physref == null) {
                val body = PhysicsBody()
                body.x = entity.x
                body.y = entity.y
                body.mass = entity.mass()
                body.radius = entity.hitSize * Vars.unitCollisionRadiusScale

                val ref = PhysicRef(entity, body)
                process.refs.add(ref)

                entity.physref = ref

                process.physics!!.add(body)
            }

            //save last position
            val ref: PhysicRef = entity.physref!!

            ref.body.layer = PhysicsProcess.layerGround
            ref.x = entity.x
            ref.y = entity.y
            ref.body.local = local || entity.isLocal()
        }
    }

  override fun reset() {
  }

  override fun init() {

  }

  override fun end() {

  }

  override fun process() {
  }

  override fun shouldProcess(): Boolean {
    return true
  }


}
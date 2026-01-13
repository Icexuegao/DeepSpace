package singularity.world;

import arc.math.geom.Mat3D
import arc.math.geom.Quat
import arc.math.geom.Vec3
import arc.struct.Seq

interface Transform {
    companion object {
        val tmpStack: Seq<Transform> = Seq()
    }

    fun parent(): Transform?

    fun parentTrans(): Mat3D

    var parTransformed: Boolean

    fun updateParentTransform() {
        if (!parTransformed) {
            val stack: Seq<Transform> = tmpStack.clear()
            var curr: Transform? = this
            while (curr != null) {
                stack.add(curr)

                curr = curr.parent()
            }

            for (i in stack.size - 1 downTo 0) {
                val obj = stack.get(i)
                val par = obj.parent()
                if (par == null) obj.parentTrans().idt()
                else {
                    val objTrn = obj.parentTrans()
                    val parTrn = par.parentTrans()

                    par.getTransform(objTrn).translate(obj.x, obj.y, obj.z).mulLeft(parTrn)
                }
                obj.parTransformed = true
            }
        }
    }

    var tmpQuat: Quat
    var x: Float
    var y: Float
    var z: Float

    fun getPos(result: Vec3): Vec3? {
        return result.set(this.x, this.y, this.z)
    }

    fun setPosition(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun setPosition(vec3: Vec3) {
        setPosition(vec3.x, vec3.y, vec3.z)
    }

    fun transform(x: Float, y: Float, z: Float) {
        setPosition(this.x + x, this.y + y, this.z + z)
    }

    fun transform(vec3: Vec3) {
        transform(vec3.x, vec3.y, vec3.z)
    }

    var eulerX: Float
    var eulerY: Float
    var eulerZ: Float

    fun getEuler(result: Quat): Quat {
        return getRotation(result)
    }

    fun setEuler(x: Float, y: Float, z: Float) {
        this.eulerX = x
        this.eulerY = y
        this.eulerZ = z
    }

    fun setEuler(vec3: Vec3) {
        setEuler(vec3.x, vec3.y, vec3.z)
    }

    fun rotate(x: Float, y: Float, z: Float) {
        setEuler(this.eulerX + x, this.eulerY + y, this.eulerZ + z)
    }

    fun rotate(vec3: Vec3) {
        rotate(vec3.x, vec3.y, vec3.z)
    }

    //init 1
    var scaleX: Float

    //init 1
    var scaleY: Float

    //init 1
    var scaleZ: Float
    fun getScale(result: Vec3): Vec3 {
        return result.set(this.scaleX, this.scaleY, this.scaleZ)
    }

    fun setScale(x: Float, y: Float, z: Float) {
        this.scaleX = x
        this.scaleY = y
        this.scaleZ = z
    }

    fun setScale(vec3: Vec3) {
        setScale(vec3.x, vec3.y, vec3.z)
    }

    fun getRotation(result: Quat): Quat {
        return result.setEulerAngles(this.eulerY, this.eulerX, this.eulerZ)
    }

    fun setRotation(quat: Quat) {
        setEuler(quat.getPitch(), quat.getYaw(), quat.getRoll())
    }

    fun getTransform(result: Mat3D): Mat3D {
        val q = getRotation(tmpQuat)

        return result.set(this.x, this.y, this.z, q.x, q.y, q.z, q.w, this.scaleX, this.scaleY, this.scaleZ)
    }

    fun getAbsTransform(result: Mat3D): Mat3D {
        if (parent() == null) return getTransform(result)
        val q = getRotation(tmpQuat)

        return result.set(this.x, this.y, this.z, q.x, q.y, q.z, q.w, this.scaleX, this.scaleY, this.scaleZ).mulLeft(parentTrans())
    }
}

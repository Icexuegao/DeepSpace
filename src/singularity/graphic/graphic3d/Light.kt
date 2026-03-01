package singularity.graphic.graphic3d

import arc.graphics.Color
import arc.graphics.Mesh
import arc.math.geom.BoundingBox
import arc.math.geom.Mat3D
import arc.math.geom.Quat

open class Light : LightSource, RendererObject {
    private var mesh: Mesh? = null
    var material: Material? = null
    var parentObj: RendererObject? = null

    override fun material(): Material? {
        return material
    }

    override var boundBox= BoundingBox()
    override var renderValid=false

    override fun mesh(): Mesh? {
        return mesh
    }

    fun setMesh(mesh: Mesh) {
        material()!!.shader.checkMesh(mesh)
        this.mesh = mesh
    }

    override fun parent(): RendererObject? {
        return parentObj
    }

    override fun parentTrans()=Mat3D()
    override var parTransformed=false
    override var tmpQuat=Quat()
    override var x=0f
    override var y=0f
    override var z=0f
    override var eulerX=0f
    override var eulerY=0f
    override var eulerZ=0f
    override var scaleX =1f
    override var scaleY=1f
    override var scaleZ=1f
    override var entityID=0

    override fun update() {}
    override var lightColor: Color =Color.white
    override var lightRadius=400f
    override var lightAttenuation=3.8f
}

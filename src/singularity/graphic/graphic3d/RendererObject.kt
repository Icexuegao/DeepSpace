package singularity.graphic.graphic3d;

import arc.graphics.Gl
import arc.graphics.Mesh
import arc.math.geom.BoundingBox
import arc.math.geom.Mat3D
import singularity.world.GameObject

interface RendererObject : GameObject {
    fun mesh(): Mesh?
    fun material(): Material?

    var boundBox: BoundingBox
    fun bounds(): BoundingBox? {
        return boundBox
    }

    fun getTransformedBounds(result: BoundingBox, tmp: Mat3D): BoundingBox {
        result.set(bounds())
        val trn = getAbsTransform(tmp)
        Mat3D.prj(result.min, trn)
        Mat3D.prj(result.max, trn)
        return result
    }
    var renderValid: Boolean
    fun renderValid(): Boolean {
        return renderValid
    }
    fun renderValid(valid: Boolean) {
        renderValid=valid
    }

    fun renderer() {
        val shaderProgram = material()!!.shader
        material()!!.setupData()
        shaderProgram.drawObject(this)
    }

    fun meshOffset(): Int {
        return 0
    }

    fun meshCount(): Int {
        val mesh = mesh()
        return if (mesh!!.indices.max() > 0) mesh.getNumIndices() else mesh.getNumVertices()
    }

    fun verticesPrimitive(): Int {
        return Gl.triangles
    }
}

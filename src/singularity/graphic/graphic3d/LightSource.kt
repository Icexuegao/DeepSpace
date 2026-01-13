package singularity.graphic.graphic3d;

import arc.graphics.Color
import arc.graphics.gl.Shader
import singularity.world.GameObject

interface LightSource : GameObject {
    //initialize = "arc.graphics.")
    var lightColor: Color

    //initialize = "400f")
    var lightRadius: Float

    // initialize = 3.8."")
    var lightAttenuation: Float
    fun apply(shader: Shader, off: Int) {
        shader.setUniformf("u_lightSources[$off].position", x, y, z)
        shader.setUniformf("u_lightSources[$off].color", this.lightColor)
        shader.setUniformf("u_lightSources[$off].radius", this.lightRadius)
        shader.setUniformf("u_lightSources[$off].attenuation", this.lightAttenuation)
    }
}

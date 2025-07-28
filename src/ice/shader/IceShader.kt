package ice.shader

import arc.Core
import arc.func.Cons
import arc.graphics.Texture
import arc.graphics.gl.Shader
import arc.scene.ui.layout.Scl
import arc.util.Time
import ice.library.IFiles
import mindustry.Vars
import mindustry.graphics.CacheLayer
import mindustry.graphics.CacheLayer.ShaderLayer

object IceShader {
    val shieldShader = ShieldShader()
    val ichorPoolCache: CacheLayer = getCacheLayer(SurfaceShader("ichorPool"))
    val softRedIceCache: CacheLayer = getCacheLayer(SurfaceShader("softRedIce"))
    val bloodNeoplasma: CacheLayer= getCacheLayer(SurfaceShader("bloodNeoplasma"))
    fun getCacheLayer(sursha: SurfaceShader): CacheLayer {
        return ShaderLayer(sursha).also {
            CacheLayer.add(0, it)
        }
    }

    fun findFi(name: String) = IFiles.findShader(name)
    class SurfaceShader : Shader {
        constructor(frag: String) : super(findFi("screenspace.vert"), findFi(("$frag.frag"))) {
            Core.assets.load("sprites/" + textureName() + ".png", Texture::class.java).loaded = Cons { t: Texture ->
                t.setFilter(Texture.TextureFilter.linear)
                t.setWrap(Texture.TextureWrap.repeat)
            }
        }

        var noiseTex: Texture? = null
        fun textureName(): String {
            return "noise"
        }

        override fun apply() {
            setUniformf("u_resolution", Core.camera.width, Core.camera.height)
            setUniformf("u_time", Time.time)
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2,
                Core.camera.position.y - Core.camera.height / 2)
            if (hasUniform("u_noise")) {
                if (noiseTex == null) {
                    noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture::class.java)
                }
                noiseTex!!.bind(1)
                Vars.renderer.effectBuffer.texture.bind(0)
                setUniformi("u_noise", 1)
            }
        }
    }

    class ShieldShader : LoadShader("buildbeam", "screenspace") {
        override fun apply() {
            setUniformf("u_dp", Scl.scl(1f))
            setUniformf("u_time", Time.time / Scl.scl(1f))
            setUniformf("u_offset",
                Core.camera.position.x - Core.camera.width / 2,
                Core.camera.position.y - Core.camera.height / 2)
            setUniformf("u_texsize", Core.camera.width, Core.camera.height)
            setUniformf("u_invsize", 1f / Core.camera.width, 1f / Core.camera.height)
        }
    }

    open class LoadShader(frag: String, vert: String) : Shader(findFi("$vert.vert"), findFi("$frag.frag"))
}



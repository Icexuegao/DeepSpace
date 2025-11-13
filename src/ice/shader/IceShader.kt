package ice.shader

import arc.Core
import arc.files.Fi
import arc.func.Cons
import arc.graphics.Color
import arc.graphics.Texture
import arc.graphics.gl.Shader
import arc.util.Nullable
import arc.util.Time
import ice.library.IFiles
import mindustry.Vars
import mindustry.graphics.CacheLayer

object IceShader {
    var ichor: CacheLayer = getCacheLayers("ichor")
    val thickBlood: CacheLayer = getCacheLayers("thickBlood")
    val softRedIceCache: CacheLayer = getCacheLayers("softRedIce")
    fun getCacheLayers(name: String): CacheLayer {
        val shaderLayer = CacheLayer.ShaderLayer(getCacheLayer(name))
        CacheLayer.add(shaderLayer)
        return shaderLayer
    }

    fun getCacheLayer(name: String): Shader {
        return object : Shader(getShaderFi("screenspace.vert"), findFi("$name.frag")) {
            var noiseTex1: Texture? = null
            var noiseTex2: Texture? = null

            init {
                loadNoise()
            }

            fun getTexture(): Texture? {
                return null
            }

            fun textureName(): String {
                return "noise"
            }

            fun loadNoise() {
                Core.assets.load("sprites/" + textureName() + ".png",
                    Texture::class.java).loaded = Cons { t: Texture? ->
                    t!!.setFilter(Texture.TextureFilter.linear)
                    t.setWrap(Texture.TextureWrap.repeat)
                }
            }

            override fun apply() {
                setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2,
                    Core.camera.position.y - Core.camera.height / 2)
                setUniformf("u_resolution", Core.camera.width, Core.camera.height)
                setUniformf("u_time", Time.time)

                if (hasUniform("u_noise")) {
                    if (noiseTex1 == null) {
                        noiseTex1 = if (getTexture() == null) Core.assets.get<Texture?>(
                            "sprites/" + textureName() + ".png", Texture::class.java) else getTexture()
                    }

                    noiseTex1!!.bind(1)
                    Vars.renderer.effectBuffer.getTexture().bind(0)

                    setUniformi("u_noise", 1)
                }

                if (hasUniform("u_noise_2")) {
                    if (noiseTex2 == null) {
                        noiseTex2 = Core.assets.get<Texture?>("sprites/" + "noise" + ".png", Texture::class.java)
                    }

                    noiseTex2!!.bind(1)
                    Vars.renderer.effectBuffer.getTexture().bind(0)

                    setUniformi("u_noise_2", 1)
                }
            }

        }
    }

    fun findFi(name: String) = IFiles.findShader(name)
    fun getShaderFi(file: String?): Fi? {
        return Vars.tree.get("shaders/$file")
    }

    class SurfaceShader : Shader {
        constructor(frag: String) : super(getShaderFi("screenspace.vert"), findFi(("$frag.frag"))) {
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

    class ShaderLayer @JvmOverloads constructor(@Nullable shader: Shader?, liquid: Boolean = true) : CacheLayer() {
        @Nullable
        var shader: Shader?

        init {
            this.liquid = liquid
            this.shader = shader
        }

        override fun begin() {
            if (!Vars.renderer.animateWater) return

            Vars.renderer.effectBuffer.begin()
            Core.graphics.clear(Color.clear)
            Vars.renderer.blocks.floor.beginDraw()
        }

        override fun end() {
            if (!Vars.renderer.animateWater) return

            Vars.renderer.effectBuffer.end()
            Vars.renderer.effectBuffer.blit(shader)
            Vars.renderer.blocks.floor.beginDraw()
        }
    }

    open class LoadShader(frag: String, vert: String) : Shader(findFi("$vert.vert"), findFi("$frag.frag"))
}



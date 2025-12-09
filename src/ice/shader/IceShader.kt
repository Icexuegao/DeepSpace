package ice.shader

import ice.library.IFiles
import mindustry.graphics.CacheLayer
import mindustry.graphics.Shaders

object IceShader {
    var ichor: CacheLayer = getCacheLayers("ichor")
    val softRedIceCache: CacheLayer = getCacheLayers("softRedIce")
    val bloodShallow: CacheLayer = getCacheLayers("bloodShallow")
    val thickBlood: CacheLayer = getCacheLayers("thickBlood")
    fun getCacheLayers(name: String): CacheLayer {
        val surfaceShader = Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), findFi("$name.frag").readString())
        val shaderLayer = CacheLayer.ShaderLayer(surfaceShader)
        CacheLayer.add(shaderLayer)
        return shaderLayer
    }

    fun findFi(name: String) = IFiles.findShader(name)
}



package ice.shader

import ice.library.IFiles
import mindustry.graphics.CacheLayer
import mindustry.graphics.Shaders

object IceShader {
    var ichor: CacheLayer.ShaderLayer = getCacheLayers("ichor")
    val softRedIceCache: CacheLayer.ShaderLayer =  getCacheLayers("softRedIce")
    val bloodShallow: CacheLayer.ShaderLayer = getCacheLayers("bloodShallow")
    val thickBlood: CacheLayer.ShaderLayer = getCacheLayers("thickBlood")
    fun getCacheLayers(name: String): CacheLayer.ShaderLayer {
        val surfaceShader = Shaders.SurfaceShader(Shaders.getShaderFi("screenspace.vert").readString(), findFi("$name.frag").readString())
        val shaderLayer = CacheLayer.ShaderLayer(surfaceShader)
        CacheLayer.add(shaderLayer)
        return shaderLayer
    }

    fun findFi(name: String) = IFiles.findShader(name)
}



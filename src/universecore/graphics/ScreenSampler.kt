package universecore.graphics

import arc.Core
import arc.Events
import arc.graphics.Color
import arc.graphics.GL30
import arc.graphics.Gl
import arc.graphics.g2d.Draw
import arc.graphics.gl.FrameBuffer
import arc.graphics.gl.GLFrameBuffer
import arc.graphics.gl.Shader
import mindustry.game.EventType

object ScreenSampler {
  private val currentBoundBuffer = GLFrameBuffer::class.java.getDeclaredField("currentBoundFramebuffer")
    .also { it.isAccessible = true }
  private val screenSwapBuffer: FrameBuffer = FrameBuffer()
  private val baseScreen: Shader = Shader(
    """
    attribute vec4 a_position;
    attribute vec2 a_texCoord0;
    
    varying vec2 v_texCoords;
    
    void main(){
        v_texCoords = a_texCoord0;
        gl_Position = a_position;
    }
    """.trimIndent(),
    """
    uniform sampler2D u_texture;

    varying vec2 v_texCoords;

    void main() {
        gl_FragColor.rgb = texture2D(u_texture, v_texCoords).rgb;
        gl_FragColor.a = 1.0;
    }
    """.trimIndent()
  )

  init {
    screenSwapBuffer.resize(Core.graphics.width, Core.graphics.height)
    Events.on(EventType.ResizeEvent::class.java) { event ->
      screenSwapBuffer.resize(Core.graphics.width, Core.graphics.height)
    }
  }

  @JvmStatic
  fun toBuffer(target: FrameBuffer,clear: Boolean =false) {
    val buffer = currentBoundBuffer.get(null) as? GLFrameBuffer<*>

    buffer?.run {
      blitBuffer(buffer, target,clear)
    }?: run {
      if (screenSwapBuffer.width == target.width && screenSwapBuffer.height == target.height) {
        Draw.flush()

        copyPixels(target)
      }
      else {
        Draw.flush()

        copyPixels(screenSwapBuffer)

        blitBuffer(screenSwapBuffer, target,clear)
      }
    }
  }

  private fun copyPixels(target: GLFrameBuffer<*>) {
    Core.gl30?.run {
      Gl.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0)
      Gl.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, target.framebufferHandle)
      glReadBuffer(Gl.back)
      glBlitFramebuffer(
        0, 0, Core.graphics.width, Core.graphics.height,
        0, 0, target.width, target.height,
        Gl.colorBufferBit, Gl.nearest
      )
      Gl.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0)
      Gl.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0)
    } ?:
    Core.gl20?.run {
      target.texture.bind()
      Gl.copyTexImage2D(
        Gl.texture2d, 0,
        Gl.rgba, 0, 0,
        target.texture.width, target.texture.height,
        0
      )
      Gl.bindTexture(Gl.texture2d, 0)
    }
  }

  private fun blitBuffer(source: GLFrameBuffer<*>, target: GLFrameBuffer<*>, clear: Boolean) {
    Core.gl30?.run {
      Gl.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER, source.framebufferHandle)
      Gl.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, target.framebufferHandle)
      glBlitFramebuffer(
        0, 0, source.width, source.height,
        0, 0, target.width, target.height,
        Gl.colorBufferBit, Gl.nearest
      )
      Gl.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0)
      Gl.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0)
    } ?:
    Core.gl20.run {
      if (clear)target.begin(Color.clear) else target.begin()
      source.texture.bind(0)
      Draw.blit(baseScreen)
      Gl.bindTexture(Gl.texture2d, 0)
      target.end()
    }
  }
}
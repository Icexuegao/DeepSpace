package ice.ui.scene.element

import arc.Core
import arc.func.Cons
import arc.func.Floatp
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.GlyphLayout
import arc.math.Mathf
import arc.scene.Element
import arc.scene.ui.layout.Scl
import arc.util.pooling.Pools
import ice.ui.tex.IceTex
import mindustry.ui.Fonts


class MusicsBar(private var fraction: Floatp) : Element() {
    //private val scissor = Rect()
    private var value = 0f
    private var lastValue = 0f
    private var blink = 0f
    private var outlineRadius = 0f
    private val blinkColor = Color()
    private val outlineColor = Color()

    constructor(name: String, color: Color, fraction: Floatp) : this(fraction) {
        this.fraction = fraction
        val s = Core.bundle[name, name]
        this.name = s
        blinkColor.set(color)
        value = fraction.get()
        lastValue = value
        setColor(color)
    }

    fun reset(value: Float) {
        blink = value
        lastValue = blink
        this.value = lastValue
    }

    operator fun set(name: Prov<String>, fraction: Floatp, color: Color) {
        this.fraction = fraction
        lastValue = fraction.get()
        blinkColor.set(color)
        setColor(color)
        update { this.name = name.get() }
    }

    fun snap() {
        value = fraction.get()
        lastValue = value
    }

    fun outline(color: Color, stroke: Float): MusicsBar {
        outlineColor.set(color)
        outlineRadius = Scl.scl(stroke)
        return this
    }

    fun flash() {
        blink = 1f
    }

    override fun update(r: Runnable): MusicsBar {
        super.update(r)
        return this
    }

    fun update(b: Cons<MusicsBar>): MusicsBar {
        super.update {
            b.get(this)
        }
        return this
    }

    fun blink(color: Color): MusicsBar {
        blinkColor.set(color)
        return this
    }

    override fun draw() {
        var computed = Mathf.clamp(fraction.get())
        if (lastValue > computed) {
            blink = 1f
            lastValue = computed
        }
        if (java.lang.Float.isNaN(lastValue)) lastValue = 0f
        if (java.lang.Float.isInfinite(lastValue)) lastValue = 1f
        if (java.lang.Float.isNaN(value)) value = 0f
        if (java.lang.Float.isInfinite(value)) value = 1f
        if (java.lang.Float.isNaN(computed)) computed = 0f
        if (java.lang.Float.isInfinite(computed)) computed = 1f
        blink = Mathf.lerpDelta(blink, 0f, 0.2f)
        value = Mathf.lerpDelta(value, computed, 0.15f)

        Draw.color(color, blinkColor, blink)
        Draw.alpha(color.a*parentAlpha)
        val top = IceTex.barTop
        val topWidth = width * value
        top.draw(x, y, topWidth, height)
        Draw.color()
        Draw.alpha(color.a*parentAlpha)
        IceTex.barBackground.scale = 4f
        Draw.rect(IceTex.barBackground, x + width / 2, y + height / 2)
        Draw.color()
        val font = Fonts.outline
        val lay = Pools.obtain(GlyphLayout::class.java) { GlyphLayout() }
        lay.setText(font, name)
        font.setColor(1f, 1f, 1f, 1f)
        font.cache.clear()
        font.cache.addText(name, x + width / 2f - lay.width / 2f, y + height / 2f + lay.height / 2f + 1)
        font.cache.draw(parentAlpha)
        Pools.free(lay)
    }

}

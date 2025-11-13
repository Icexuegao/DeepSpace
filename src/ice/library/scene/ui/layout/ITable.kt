package ice.library.scene.ui.layout

import arc.Core
import arc.graphics.g2d.ScissorStack
import arc.math.geom.Rect
import arc.scene.Element
import arc.scene.style.Drawable
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Table

class ITable(back: Drawable? = null) : Table(back) {
    private var rowsiz = 0
    fun setRowsize(int: Int) {
        rowsiz = int
    }


    override fun <T : Element> add(element: T): Cell<T> {
        val size = cells.size
        if (rowsiz != 0 && size % rowsiz == 0) {
            row()
        }
        return super.add(element)
    }

}
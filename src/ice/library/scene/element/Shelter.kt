package ice.library.scene.element

import arc.Core
import arc.graphics.g2d.Draw
import arc.math.Interp
import arc.scene.Element
import arc.scene.actions.Actions
import ice.library.scene.action.IceActions.moveToAlphaAction
import mindustry.ui.Styles

class Shelter {
    class ShelterUp(
        mx: Float,
        my: Float,
        private val dWidth: Float,
        private val dHeight: Float,
        mTime: Float,
        dTime: Float,
    ) : Element() {
        init {
            color.a = 0f
            actions(
                moveToAlphaAction(x + mx, y + my, mTime, 1f, Interp.pow3Out),
                Actions.delay(dTime),
                moveToAlphaAction(x - mx, y - my, mTime, 0f, Interp.pow3In),
                Actions.remove()
            )
        }

        override fun draw() {
            super.draw()
            Draw.alpha(color.a)
            Styles.black.draw(x, y - dHeight, dWidth, dHeight)
        }
    }

    class ShelterDown(
        mx: Float,
        my: Float,
        private val dWidth: Float,
        private val dHeight: Float,
        mTime: Float,
        dTime: Float,
    ) : Element() {
        init {
            color.a = 0f
            actions(
                moveToAlphaAction(x + mx, y - my, mTime, 1f, Interp.pow3Out),
                Actions.delay(dTime),
                moveToAlphaAction(x - mx, y + my, mTime, 0f, Interp.pow3In),
                Actions.remove()
            )
        }

        override fun draw() {
            super.draw()
            Draw.alpha(color.a)
            Styles.black.draw(x, y+ Core.graphics.height.toFloat(), dWidth, dHeight)
        }
    }
}

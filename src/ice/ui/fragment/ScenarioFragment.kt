package ice.ui.fragment

import arc.func.Boolp
import arc.scene.Group
import arc.scene.event.Touchable
import arc.scene.ui.layout.WidgetGroup

object ScenarioFragment {
    var group: Group = WidgetGroup()
    fun build(parent: Group) {
        group.setFillParent(true)
        group.touchable = Touchable.childrenOnly
        group.visibility = Boolp { true }
        parent.addChild(group)
    }

}
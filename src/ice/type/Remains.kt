package ice.type

import arc.Core
import arc.func.Cons
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.layout.Table
import ice.Ice
import ice.audio.ISounds
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.IFiles
import ice.ui.UI
import ice.ui.menusDialog.RemainsDialog
import mindustry.Vars

open class Remains(val name: String) {
    var localizedName: String = ""
    var effect = ""
    var icon = TextureRegionDrawable(IFiles.findModPng(name))
    var color = IceColor.b4
    var install = {}
    var uninstall = {}
    var disabled: () -> Boolean = { Vars.state.isGame }
    var customTable = Table()
    var buttonStyle = IStyles.button5

    init {
        RemainsDialog.remainsSeq.add(this)
    }

    fun setDescriptionTable(table: Cons<Table>) {
        table.get(customTable)
    }

    fun setDescription(desc: String) {
        customTable.add(desc).pad(5f).color(color).row()
    }

    fun setEnabled(enabled: Boolean) {
        if (enabled) {
            RemainsDialog.enableSeq.addUnique(this)
            RemainsDialog.remainsSeq.remove(this)
            install()
        } else {
            RemainsDialog.enableSeq.remove(this)
            RemainsDialog.remainsSeq.addUnique(this)
            uninstall()
        }
        Core.settings.put("${Ice.name}-remains-$name-enabled", enabled)
    }

    fun rebuildEnableRemains(table: Table) {
        table.button(icon, buttonStyle) {
            setEnabled(false)
            ISounds.remainUninstall.play(UI.sfxVolume + 1)
            RemainsDialog.flunRemains()
        }.disabled {
            disabled()
        }.size(60f).pad(10f).get().hovered {
            if (RemainsDialog.tempRemain != this) {
                RemainsDialog.tempRemain = this
                RemainsDialog.flunRemains()
            }
        }
    }

    fun rebuildRemains(table: Table) {
        table.button(icon, IStyles.button) {
            if (RemainsDialog.enableSeq.size < RemainsDialog.slotPos) {
                setEnabled(true)
                ISounds.remainInstall.play()
                RemainsDialog.flunRemains()
            }
        }.disabled {
            disabled()
        }.size(60f).pad(10f).get().hovered {
            if (RemainsDialog.tempRemain != this) {
                RemainsDialog.tempRemain = this
                RemainsDialog.flunRemains()
            }
        }
    }
}
package ice.ui.bundle

import mindustry.ctype.UnlockableContent

fun UnlockableContent.localization(block: LocalizationMap.() -> Unit) {
  LocalizationManager.registerTarget(this, LocalizationMap().apply(block))
}

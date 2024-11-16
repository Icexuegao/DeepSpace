package ice.Alon.ui.dialogs;

import arc.Events;
import ice.Alon.game.EventType;
import mindustry.ui.dialogs.PausedDialog;

public class IcePausedDialog extends PausedDialog {
    @Override
    public void runExitSave() {
        Events.fire(new EventType.SaveExitEvent());
        super.runExitSave();
    }
}

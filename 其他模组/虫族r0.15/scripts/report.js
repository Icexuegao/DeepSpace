let mod = Vars.modDirectory.child("zerg").child("main.txt");
mod.exists() || mod.writeString("");
let hidden = Boolean(mod.readString());

Events.on(EventType.ClientLoadEvent, () => {
    const remind = new BaseDialog("Welcome to the Greavar");

    remind.cont.pane(table => {
        table.image(Core.atlas.find("zerg-greavar")).size(64, 64).pad(3).row();
        table.add(Core.bundle.get("thanking")).left().growX().wrap().width(420).maxWidth(420).pad(4).labelAlign(Align.left).row();

        table.add(Core.bundle.get("remind")).pad(4).labelAlign(Align.center).row();
        table.image(Tex.whiteui, Pal.accent).growX().height(3).pad(4).row();
        table.add(Core.bundle.get("remind.sub")).left().growX().wrap().width(420).maxWidth(420).pad(4).labelAlign(Align.left).row();
    });
    let time = 15 * 60;
    remind.buttons.button("", run(() => {
        remind.hide();
        mod.writeString("true")
    })).size(128, 64).disabled(b => time > 0).update(b => {
        if (time > 0) {
            time -= Time.delta
            b.setText(Core.bundle.get("close") + "(" + parseInt(time / 60) + "s)")
        } else {
            b.setText("@close")
            b.update(null)
        }
    })
    remind.buttons.button(Core.bundle.get("removeMod"), run(() => {
        Vars.mods.removeMod(Vars.mods.getMod("zerg"));
        Core.app.exit();
    })).size(128, 64)

    if (!hidden) remind.show();
})
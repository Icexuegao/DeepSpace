let tex = "选择之后将无法切换科技，请慎重选择";

Events.on(EventType.ClientLoadEvent, cons(e => {
    let dialog = new BaseDialog("mod名字");
    dialog.buttons.defaults().size(320, 64);
    dialog.cont.image(Core.atlas.find("logo")).row();
    dialog.cont.pane((() => {
        let table = new Table();
        table.add("介绍").center().width(600).maxWidth(600).labelAlign(Align.center).row();
        table.button("[red]选择科技树", Styles.grayt, () => {
            let dialog = new BaseDialog("[red]选择科技树");
            dialog.cont.pane((() => {
                let t = new Table();
                info(t, "科技树1", tex);
                info(t, "科技树2", tex);
                info(t, "科技树3", tex);
                info(t, "科技树4", tex);
                info(t, "科技树5", tex);
                t.row();
                info(t, "科技树6", tex);
                info(t, "科技树7", tex);
                info(t, "科技树8", tex);
                info(t, "科技树9", tex);
                info(t, "科技树10", tex);
                return t;
            })()).grow().center().maxWidth(800);
            close(dialog);
            dialog.show();
        }).bottom().size(210, 64);
        return table;
    })()).grow().center().maxWidth(600).maxHeight(500);
    close(dialog);
    dialog.show();
}))

function info(table, title, text) {
    let shown = false;
    table.button(title, Styles.grayt, () => {
        let dialog = new BaseDialog(title);
        dialog.cont.pane(t => {
            t.add(text).row();
        });
        dialog.buttons.button("@ok", Styles.grayt, () => {
            dialog.hide();
            shown = !shown
        }).size(210, 64);
        close(dialog);
        dialog.show();
    }).size(160, 80).disabled(b => shown);
}

function close(dialog) {
    dialog.buttons.button("@close", Styles.grayt, () => {
        dialog.hide();
    }).size(210, 64);
}

function coolapser(table, text, lambda) {
    let shown = false;
    table.button(Icon.downOpen, Styles.flati, () => shown = !shown).size(size, 45).disabled(b => !shown).update(b => {
        b.getStyle().imageUp = shown ? Icon.upOpen : Icon.downOpen;
    }).get().add(text);
    table.collapser(lambda, true, () => shown).row();
};
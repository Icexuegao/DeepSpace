Events.on(ClientLoadEvent, e => {
    let unsafeField = Packages.sun.misc.Unsafe.__javaObject__.getDeclaredField("theUnsafe");
    unsafeField.setAccessible(true);
    let unsafe = unsafeField.get(null);

    let field = MapObjectivesCanvas.__javaObject__.getDeclaredField("unitSize");

    let offset = unsafe.objectFieldOffset(field);

    let infoDialog = Reflect.get(Vars.ui.editor, "infoDialog");
    let objectives = Reflect.get(infoDialog, "objectives");
    let canvas = objectives.canvas;

    objectives.buttons.button("+", () => addSize(5));
    objectives.buttons.button("-", () => addSize(-5));

    function addSize(amount) {
        let size = canvas.unitSize;
        unsafe.putFloat(canvas, offset, size + Scl.scl(amount));
        rebuild();
    }

    function rebuild() {
        let all = Vars.state.rules.objectives.all;
        objectives.hide();
        objectives.show(all, arr => all.set(arr));
    }
})
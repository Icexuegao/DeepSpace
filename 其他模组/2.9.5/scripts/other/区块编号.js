var shown = true, planetDialog;
Events.on(ClientLoadEvent, e => {
    let planets, state;
    shown = Core.settings.getBool("planet-sector-id", true);
    Vars.ui.planet = extend(PlanetDialog, {
        renderProjections(planet) {
            this.super$renderProjections(planet);
            if (!shown) return;
            let alpha = state.uiAlpha;
            if (alpha < 0.0001) return;
            planet.sectors.each(sec => {
                planets.drawPlane(sec, () => {
                    Fonts.outline.draw("" + sec.id, 0, 0, Color.white, 0.6 / Scl.scl(), true, Align.center);
                });
            });
        },
    });
    planetDialog = Vars.ui.planet;
    planets = planetDialog.planets;
    state = planetDialog.state;
    planetDialog.shown(() => rebuildButton());
});

function rebuildButton() {
    let table = planetDialog.getChildren().get(0).getChildren().get(3);
    table.row();
    table.table(Styles.black6, t => {
        t.button("显示星球区块编号", Styles.flatTogglet, () => {
            shown = !shown;
            Core.settings.put("planet-sector-id", shown);
        }).height(48).growX().checked(b => shown)
    }).fillX();
}
let table = new Table(), num = 10, size = 45 * num, fire = 0;
Events.on(ClientLoadEvent, cons(e => {
    setup();
    Vars.ui.settings.game.checkPref("作弊菜单", Core.settings.getBool("作弊菜单"));
    Vars.ui.hudGroup.fill(cons(t => {
        t.visibility = boolp(() => Core.settings.getBool("作弊菜单") && (Vars.state == null || Vars.state.rules.editor || Vars.state.rules.infiniteResources));
        t.bottom().add(table);
        /*let lastX = 0, lastY = 0;
        t.addListener(extend(InputListener, {
            touchDown(event, x, y, pointer, button) {
                let v = t.localToParentCoordinates(Tmp.v1.set(x, y));
                lastX = v.x;
                lastY = v.y;
                return true
            },
            touchDragged(event, x, y, pointer) {
                let v = t.localToParentCoordinates(Tmp.v1.set(x, y));
                t.translation.add(v.x - lastX, v.y - lastY);
                lastX = v.x;
                lastY = v.y
            }
        }))*/
    }))
}))

function TRD(region) {
    return new TextureRegionDrawable(region);
}

function ut(t) {
    if (t == 0) return Team.derelict;
    if (t == 1) return Team.sharded;
    if (t == 2) return Team.crux;
    if (t == 3) return Team.malis;
    if (!t) return Vars.player.team();
}

function setup() {
    let i = 0;

    function row(table) {
        i++
        if (i % num == 0) table.row();
    };

    function coolapser(table, text, lambda) {
        let shown = false;
        table.button(Icon.downOpen, Styles.flati, () => shown = !shown).size(size, 45).update(b => {
            b.getStyle().imageUp = shown ? Icon.upOpen : Icon.downOpen;
        }).get().add(text);
        table.row();
        table.collapser(lambda, true, () => shown).row();
    };
    table.clear();
    table.visibility = () => Vars.ui.hudfrag.shown && !Vars.ui.minimapfrag.shown();
    table.background(Styles.black6);
    table.table(Tex.windowEmpty, cons(t => {
        coolapser(t, "单位", ue => {
            ue.pane(e => {
                let amount = 1, team = 1, x = 0, y = 0;
                ue.table(Tex.windowEmpty, cons(e => {
                    e.add("数量: ");
                    e.field(amount, cons(text => amount = text)).size(size / 2 - 45, 45);
                    e.add("队伍: ");
                    e.field(team, cons(text => team = text)).size(size / 2 - 45, 45).row();
                })).row();
                ue.table(Tex.windowEmpty, cons(e => {
                    e.add("x: ");
                    let ux = e.field(x, cons(text => x = text)).size(size / 2 - 45, 45);
                    e.add("y: ");
                    let uy = e.field(y, cons(text => y = text)).size(size / 2 - 45, 45);
                    e.button(TRD(UnitTypes.gamma.uiIcon), Styles.flati, 40, () => {
                        let p = Vars.player.unit();
                        x = p.x / 8, y = p.y / 8;
                        ux.update(t => t.setText(Math.round(x)));
                        uy.update(t => t.setText(Math.round(y)));
                    }).size(45).row();
                })).row();
                Vars.content.units().each(unit => {
                    if (!unit.internal) {
                        e.button(TRD(unit.uiIcon), Styles.flati, 40, () => {
                            for (let i = 0; i < amount; i++) {
                                unit.spawn(ut(team), x * 8, y * 8);
                            }
                        }).size(45);
                        row(e);
                    }
                })
                i = 0;
            })
        });
        coolapser(t, "物品", ie => {
            ie.pane(e => {
                Vars.content.items().each(item => {
                    e.button(TRD(item.uiIcon), Styles.flati, 40, () => {
                        fire = Vars.state.rules.coreIncinerates ? 20 : 1;
                        Vars.player.core().items.set(item, Vars.player.core().getMaximumAccepted(item) / fire);
                    }).size(45);
                    row(e);
                });
                e.button(TRD(Icon.add), Styles.flati, 40, () => {
                    fire = Vars.state.rules.coreIncinerates ? 20 : 1;
                    Vars.content.items().each(item => {
                        Vars.player.core().items.set(item, Vars.player.core().getMaximumAccepted(item) / fire);
                    });
                }).size(45);
            })
        });
    }))
}


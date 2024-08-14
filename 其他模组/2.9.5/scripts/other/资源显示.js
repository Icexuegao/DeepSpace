let table = new Table(), usedItems = new ObjectSet();
let scale = 0.8, player = null;

function setup() {
    table.background(Styles.black3);
    let iconSize = Vars.iconSmall * scale;
    table.table(null, itemsTable => {
        let rebuild = () => {
            itemsTable.clear();
            let i = 0;
            Vars.content.items().each((item) => {
                if (!usedItems.contains(item)) return;
                itemsTable.image(item.uiIcon).size(iconSize);
                itemsTable.label(() => "" + UI.formatAmount(player.core() == null ? 0 : player.core().items.get(item))).padRight(5).minWidth(iconSize + 5).get().setFontScale(scale);
                if (++i % 5 == 0) itemsTable.row();
            });
        };
        itemsTable.update(t => {
            Vars.content.items().each(item => {
                if (player.core() != null && player.core().items.get(item) > 0 && usedItems.add(item)) {
                    rebuild();
                }
            });
        });
    });
    table.row();
    table.table(null, info => {
        let addInfo = (icon, l) => {
            info.image(icon).size(iconSize).growX();
            info.label(l).padRight(5).get().setFontScale(scale);
        }
        addInfo(Blocks.coreNucleus.uiIcon, () => player.team().cores().size + "");
        addInfo(UnitTypes.gamma.uiIcon, () => {
            let team = player.team();
            return "[#" + team.color + "]" + team.data().players.size + "[]/[accent]" + Groups.player.size();
        });
    }).growX();
}

Events.on(ResetEvent, e => {
    usedItems.clear();
});

Events.on(ClientLoadEvent, e => {
    Vars.ui.settings.game.checkPref("资源显示", Core.settings.getBool("资源显示"));
    //Vars.ui.hudGroup.addChild(uiGroup);
    player = Vars.player;
    setup();
    Vars.ui.hudGroup.fill(cons(t => {
        t.visibility = boolp(() => Core.settings.getBool("资源显示"));
        t.left().add(table);
        // 可拖动的ui
        let lastX = 0, lastY = 0;
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
        }));
    }));
});
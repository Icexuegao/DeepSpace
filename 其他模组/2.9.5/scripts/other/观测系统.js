const my = require("base/物品");

exports.观测系统 = extend(Block, "观测系统", {
    destructible: false,
    setStats() {
        this.super$setStats();
        this.stats.remove(Stat.size);
    }
});

let mod = Vars.modDirectory.child("data.json");
mod.writeString("", true);
let base = mod.readString();
let data = Jval.read(base).asObject();
let sate = data.get("satellite");

Events.on(ClientLoadEvent, e => {
    Vars.renderer.minZoom = 1.5 - 0.45 * sate;
    Vars.renderer.maxZoom = 6 + 18 * sate;
})
Events.on(UnlockEvent, e => {
    if (!Vars.headless && e.content == exports.观测系统) {
        sate++
        Vars.renderer.minZoom = 1.5 - 0.45 * sate;
        Vars.renderer.maxZoom = 6 + 18 * sate;
        mod.writeString("satellite: " + sate);
        if (sate < 3) {
            let node = TechTree.all.find(boolf(t => t.content.name.equals(e.content.name)));
            exports.观测系统.clearUnlock();
            if (sate == 1) node.setupRequirements(ItemStack.with(
                Items.lead, 4600,
                Items.graphite, 5200,
                Items.thorium, 7400,
                Items.silicon, 9200,
                Items.plastanium, 4600,
                Items.surgeAlloy, 3400
            ));
            if (sate == 2) node.setupRequirements(ItemStack.with(
                my.铱板, 9200,
                Items.graphite, 10400,
                my.导能回路, 12200,
                Items.silicon, 18400,
                my.陶钢, 9400,
                Items.surgeAlloy, 8200
            ));
        }
    }
});
Events.on(WorldLoadEvent, e => {
    if (!Vars.headless && !(Vars.state == null || Vars.state.isCampaign())) {
        Vars.renderer.minZoom = 0.15;
        Vars.renderer.maxZoom = 60;
    }
});
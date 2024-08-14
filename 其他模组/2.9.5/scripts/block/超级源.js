let AllSource = extend(PowerNode, "超级源", {
    setStats() {
        this.super$setStats();
        this.stats.add(Stat.output, this.heatSize, StatUnit.heatUnits);
    }
})

Object.assign(AllSource, {
    heatSize: 9999,
    maxNodes: 100,
    envEnabled: -1,
    update: true,
    outputsPower: true,
    consumesPower: false,
    category: Category.effect,
    buildVisibility: BuildVisibility.sandboxOnly
});
let heatSize = AllSource.heatSize;

AllSource.buildType = () => new JavaAdapter(PowerNode.PowerNodeBuild, HeatBlock, {
    updateTile() {
        this.super$updateTile();
        this.proximity.each(p => {
            Vars.content.items().each(a => {
                if (!p.block.hasItems || p.items == null) {
                    return;
                }
                if (p.acceptItem(this, a)) {
                    p.handleItem(p, a);
                }
            })
            Vars.content.liquids().each(a => {
                if (!p.block.hasLiquids || p.liquids == null) {
                    return;
                }
                if (p.acceptLiquid(this, a) && p.liquids.currentAmount() < p.block.liquidCapacity) {
                    p.handleLiquid(p, a, 100);
                }
            })
            if (p instanceof HeatProducer.HeatProducerBuild) {
                p.heat = Mathf.approachDelta(p.heat, heatSize * this.efficiency, heatSize * this.delta());
            }
            if (p instanceof HeatCrafter.HeatCrafterBuild) {
                p.heat = Mathf.approachDelta(p.heat, heatSize * this.efficiency, heatSize * this.delta());
            }
        })
    },
    getPowerProduction() {
        return this.enabled ? 1000000 / 6 : 0;
    },
    draw() {
        this.super$draw();
        let RGB = new Color(1, 1, 1, 1);
        RGB.fromHsv((Time.globalTime * 3) % 360, 1, 1);
        Draw.color(RGB);
        Draw.rect("center", this.x, this.y);
        Draw.color();
        AllSource.laserColor1 = RGB;
        AllSource.laserColor2 = RGB;
    },
    heat() {
        return this.enabled ? heatSize : 0;
    },
    heatFrac() {
        return 1;
    }
}, AllSource);
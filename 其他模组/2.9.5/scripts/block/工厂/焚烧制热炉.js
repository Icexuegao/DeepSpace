let fs = extend(HeatProducer, "焚烧制热炉", {
    setBars() {
        this.super$setBars();
        this.removeBar("heat");
        this.addBar("heat", func(e => new Bar(
            prov(() => "热量：" + (e.heat * e.itemHeat()).toFixed(2)),
            prov(() => Pal.lightOrange),
            floatp(() => e.heat * e.itemHeat() / e.block.heatOutput)
        )));
    }
});
fs.buildType = prov(() => extend(HeatProducer.HeatProducerBuild, fs, {
    itemHeat() {
        return this.items.first() ? this.items.first().flammability : 0;
    },
    heat() {
        return this.heat * this.itemHeat();
    }
}));
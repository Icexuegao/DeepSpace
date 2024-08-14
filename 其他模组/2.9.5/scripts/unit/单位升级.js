const {
    空雷1,
    空雷2,
    空雷3,
    空雷4,
    空雷5,
    空炸1,
    空炸2,
    空炸3,
    空炸4,
    空炸5,
    陆战1,
    陆战2,
    陆战3,
    陆战4,
    陆战5
} = require("unit/units");

Blocks.airFactory.plans.add(
    new UnitFactory.UnitPlan(
        空雷1, 60 * 20,
        ItemStack.with(
            Items.silicon, 30,
            Items.lead, 15,
            Items.graphite, 10
        )
    )
);
Blocks.additiveReconstructor.addUpgrade(空雷1, 空雷2);
Blocks.multiplicativeReconstructor.addUpgrade(空雷2, 空雷3);
Blocks.exponentialReconstructor.addUpgrade(空雷3, 空雷4);
Blocks.tetrativeReconstructor.addUpgrade(空雷4, 空雷5);
Blocks.airFactory.plans.add(
    new UnitFactory.UnitPlan(
        空炸1, 60 * 35,
        ItemStack.with(
            Items.graphite, 15,
            Items.titanium, 20,
            Items.silicon, 30
        )
    )
);
Blocks.additiveReconstructor.addUpgrade(空炸1, 空炸2);
Blocks.multiplicativeReconstructor.addUpgrade(空炸2, 空炸3);
Blocks.exponentialReconstructor.addUpgrade(空炸3, 空炸4);
Blocks.tetrativeReconstructor.addUpgrade(空炸4, 空炸5);
Blocks.groundFactory.plans.add(
    new UnitFactory.UnitPlan(
        陆战1, 60 * 30,
        ItemStack.with(
            Items.lead, 30,
            Items.titanium, 30,
            Items.silicon, 20
        )
    )
);
Blocks.additiveReconstructor.addUpgrade(陆战1, 陆战2);
Blocks.multiplicativeReconstructor.addUpgrade(陆战2, 陆战3);
Blocks.exponentialReconstructor.addUpgrade(陆战3, 陆战4);
Blocks.tetrativeReconstructor.addUpgrade(陆战4, 陆战5);
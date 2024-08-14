const lib = require("base/coflib");

function bloodBuild(type, building, name, amount, percent) {
    let b = extend(type, name, {
        update: true,
        setStats() {
            this.super$setStats();
            if (percent) this.stats.add(lib.damageReduction, lib.bundle("damageReduction", (1 - percent) * 100));
            this.stats.add(lib.regenAmount, lib.bundle("regenAmount", amount));
        }
    })
    lib.setBuilding(building, b, {
        updateTile() {
            this.super$updateTile();
            if (this.healthf() < 1) this.heal(amount / 60);
        },
        handleDamage(amount) {
            if (percent) {
                amount = Math.min(amount * 10, amount + this.block.armor);
                return Damage.applyArmor(amount * percent, this.block.armor);
            } else return amount
        }
    })
    exports[name] = b;
}

bloodBuild(StackConveyor, StackConveyor.StackConveyorBuild, "生物钢传送带", 10);
bloodBuild(Conveyor, Conveyor.ConveyorBuild, "血肉装甲传送带", 30);
bloodBuild(ItemBridge, ItemBridge.ItemBridgeBuild, "增生传送带桥", 60);
bloodBuild(Conduit, Conduit.ConduitBuild, "动脉导管", 30);
bloodBuild(LiquidBridge, LiquidBridge.LiquidBridgeBuild, "动脉导管桥", 60);
bloodBuild(PowerNode, PowerNode.PowerNodeBuild, "节点", 5);
bloodBuild(PowerNode, PowerNode.PowerNodeBuild, "节点大", 20);
bloodBuild(LiquidRouter, LiquidRouter.LiquidRouterBuild, "装甲储液罐", 120);

bloodBuild(Wall, Wall.WallBuild, "生物钢墙", 200, 0.8);
bloodBuild(Wall, Wall.WallBuild, "生物钢墙大", 800, 0.8);
bloodBuild(StorageBlock, StorageBlock.StorageBuild, "晶格数据矩阵", 400, 0.6);
const lib = require("base/coflib");
let jc = new Wall("失稳晶簇");
jc.size = 2;
jc.health = 5000;
jc.update = true;
jc.breakable = false;
jc.createRubble = false;
jc.customShadow = true;
jc.drawTeamOverlay = false;
jc.buildVisibility = BuildVisibility.editorOnly;
lib.setBuilding(Wall.WallBuild, jc, {
    updateTile() {
        if (this.team != Team.derelict) this.team = Team.derelict;
    },
    afterDestroyed() {
        let range = 8 * 20;
        let {x, y} = this;
        let num = round(12);
        if (num == 0) {
            Vars.indexer.eachBlock(null, x, y, range, b => b.block.canOverdrive, b => {
                b.applyBoost(rand(4), rand(900));
                Fx.chainEmp.at(x, y, 0, Pal.accent, b);
            });
        } else if (num == 1) {
            Vars.indexer.eachBlock(null, x, y, range, b => b.power != null && b.power.graph.getLastPowerProduced() > 0, b => {
                b.applySlowdown(rand(1), rand(900));
                Fx.chainEmp.at(x, y, 0, lib.FF5845, b);
            });
        } else if (num == 2) {
            Vars.indexer.eachBlock(null, x, y, range, b => b.damaged() && !b.isHealSuppressed(), b => {
                b.heal(b.maxHealth * rand(5) / 100);
                b.recentlyHealed();
                Fx.healBlockFull.at(b.x, b.y, b.block.size, lib.Color("84F491"), b.block);
            });
            Units.nearby(null, x, y, range, u => u.heal(u.maxHealth * rand(5) / 100));
        } else if (num == 3) {
            let id = round(Vars.content.statusEffects().size);
            Damage.status(null, x, y, range, Vars.content.getByID(ContentType.status, id), rand(900), true, true);
        } else if (num == 4) {
            let spawner = Vars.spawner.getSpawns();
            if (spawner.size > 0) {
                Units.nearby(null, x, y, range, u => {
                    let random = Mathf.random(0, spawner.size - 1);
                    u.x = spawner.get(random).x * 8 + Mathf.range(2 * 8);
                    u.y = spawner.get(random).y * 8 + Mathf.range(2 * 8);
                    Fx.chainLightning.at(this.x, this.y, 0, Pal.gray, u);
                })
                Sounds.spark.at(this);
            }
        } else if (num == 5) {
            Units.nearby(null, x, y, range, u => {
                u.x = x + Mathf.range(2 * 8);
                u.y = y + Mathf.range(2 * 8);
                Fx.chainLightning.at(this.x, this.y, 0, Pal.gray, u);
            })
        } else if (num == 6) {
            Damage.damage(x, y, range, rand(500));
        } else if (num == 7) {
            Damage.damage(x, y, range, rand(500, 1000) * rand(2, 5));
        } else if (num == 8) {
            //Units.nearby(null, x, y, range, u => u.type.spawn(Team.crux, u.x, u.y));
        } else if (num == 9) {
            //Units.nearby(null, x, y, range, u => u.type.spawn(u.team, u.x, u.y));
        } else if (num == 10) {
            this.tile.setBlock(jc);
        } else if (num == 11) {
            Groups.bullet.intersect(this.x - range, this.y - range, range * 2, range * 2, b => {
                if (this.within(b, range)) b.vel.scl(rand(1));
            });
        } else if (num == 12) {
            Groups.bullet.intersect(this.x - range, this.y - range, range * 2, range * 2, b => {
                if (this.within(b, range)) b.vel.setAngle(b.rotation() + rand(-180, 180));
            });
        }
    }
});

function rand(num, num2) {
    if (num2) return Mathf.random(num, num2);
    return Mathf.random(num);
}

function round(num) {
    return Math.round(rand(num));
}
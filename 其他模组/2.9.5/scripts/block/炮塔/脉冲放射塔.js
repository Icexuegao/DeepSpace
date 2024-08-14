const lib = require("base/coflib");
let pulse = new PowerTurret("脉冲放射塔");
lib.setBuilding(PowerTurret.PowerTurretBuild, pulse, {
    setStats() {
        this.super$setStats();
        this.stats.remove(Stat.reload);
        this.stats.remove(Stat.inaccuracy);
    },
    findTarget() {
        this.target = this;
    },
    validateTarget() {
        return true
    }
})
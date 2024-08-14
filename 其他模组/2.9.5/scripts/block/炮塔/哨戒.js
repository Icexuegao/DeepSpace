let 哨戒 = new PowerTurret("哨戒");
Object.assign(哨戒, {
    health: 2340,
    armor: 11,
    reload: 8,
    recoil: 0.5,
    range: 190,
    playerControllable: false,
    ammoUseEffect: Fx.casing1,
    shootType: Blocks.salvo.ammoTypes.get(Items.thorium)
})

哨戒.buildType = prov(() => {
    let time = 900;
    return extend(PowerTurret.PowerTurretBuild, 哨戒, {
        updateTile() {
            this.super$updateTile();
            time--
            if (time == 0) this.kill();
        }
    })
})
exports.哨戒 = 哨戒;
function Pay(block) {
    return new BuildPayload(block, Team.derelict)
}

function CoreTurret(build, block, ammo) {
    build.buildType = prov(() => {
        const p = Pay(block);
        return extend(CoreBlock.CoreBuild, build, {
            updateTile() {
                this.super$updateTile();
                if (p.build.team != this.team) {
                    p.build.team = this.team;
                }
                p.update(null, this);
                for (let i of ammo) {
                    if (this.team.core().items.get(i) >= 1) {
                        if (p.build.acceptItem(this, i)) {
                            this.team.core().items.remove(i, 1)
                            p.build.handleItem(this, i);
                        }
                        break;
                    }
                }
                p.set(this.x, this.y, p.build.payloadRotation);
            },
            draw() {
                this.super$draw();
                p.draw();
            },
            drawSelect() {
                this.super$drawSelect();
                Drawf.dashCircle(this.x, this.y, block.range, Pal.accent);
            }
        })
    });
}

CoreTurret(core, turret, [Items.copper, Items.lead])
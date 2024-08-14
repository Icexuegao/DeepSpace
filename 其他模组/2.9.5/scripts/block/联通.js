let chainedDrill = Object.assign(new Wall("联通墙"), {
    size: 2,
    health: 10000,
    update: true,
});

chainedDrill.setupRequirements(
    Category.production,
    BuildVisibility.shown,
    ItemStack.with(
        Items.copper, 200,
        Items.lead, 150
    )
);

var block = chainedDrill;

function ChainedGroup() {
    this.health = 0;
    this.builds = new ObjectSet();

    this.addBuild = function (build) {
        let added = this.builds.add(build);
        if (added) {
            build.setGroup(this);
            this.health += build.health;
        }
    }

    this.addGroup = function (group) {
        group.builds.each(b => this.addBuild(b));
    }

    this.getHealth = function () {
        return this.health / this.builds.size;
    }

    this.damage = function (damage) {
        this.health -= damage;

        if (this.health <= 0) {
            this.builds.each(b => !b.dead, b => b.kill());
        }
    }
}

var tmpSeq = new Seq();
chainedDrill.buildType = () => {
    let size, proximity;
    var group = new ChainedGroup();

    let build = extend(Wall.WallBuild, chainedDrill, {
        created() {
            size = block.size;
            proximity = build.proximity;
            group.addBuild(build);
        },

        damage(team, damage) {
            group.damage(damage);
        },

        updateTile() {
            this.health = group.getHealth();
        },

        onProximityUpdate() {
            this.super$onProximityUpdate();
        },

        onProximityAdded() {
            this.super$onProximityAdded();
            this.updateChained();
        },

        onProximityRemoved() {
            this.super$onProximityRemoved();
            let buildSize = group.builds.size;
            proximity.each(b => b.block == block, b => {
                b.proximity.remove(this);
                let chained = b.getChained();
                let newGroup = new ChainedGroup();
                chained.each(b => {
                    newGroup.addBuild(b);
                    //newGroup.health = parseInt(amount * f);
                })
            })
        },

        updateChained() {
            proximity.each(b => b.block == block && b.getGroup() != group, b => {
                b.group.addGroup(group);
            });
        },

        getChained() {
            tmpSeq.clear();
            this.getChainedBuilds(tmpSeq);
            return tmpSeq;
        },

        getChainedBuilds(seq) {
            seq.add(this);
            proximity.each(b => b.block == block && !seq.contains(b), b => {
                b.getChainedBuilds(seq);
            });
        },

        drawSelect() {
            let chained = group.builds;
            if (chained.isEmpty()) return;
            Lines.stroke(1, Pal.accent);
            chained.each(b => {
                for (let i = 0; i < 4; i++) {
                    let p = Geometry.d8edge[i];
                    let offset = -Math.max(b.block.size - 1, 0) / 2 * Vars.tilesize;
                    Draw.rect("block-select", b.x + offset * p.x, b.y + offset * p.y, i * 90);
                }
            });
            Draw.reset();
        },

        getGroup() {
            return group;
        },

        setGroup(newGroup) {
            group = newGroup;
        },
    });

    return build;
}
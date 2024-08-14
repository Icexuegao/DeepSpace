const lib = require("base/coflib");
let range = 320, max = 5;
const dian = Object.assign(lib.newBlock("定向加速器", {
    drawPlace(x, y, rotation, valid) {
        this.super$drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * 8 + this.offset, y * 8 + this.offset, range, Pal.accent);
    }
}), {
    size: 3,
    canOverdrive: false,
    requirements: ItemStack.with()
})

function stack() {
    this.content = [];
    this.subtract = (b) => {
        this.content.splice(this.content.indexOf(b), 1)
    };
    this.add = (b) => {
        this.content.push(b)
    };
    this.size = () => {
        return this.content.length
    };
    this.reset = () => {
        this.content = []
    };
    return this
};

dian.buildType = prov(() => {
    let links = new stack();
    return extend(Building, {
        tf(block) {
            return block instanceof PayloadSource.PayloadSourceBuild
        },
        draw() {
            this.super$draw();
            if (this.timer.get(15)) for (let i of links.content) {
                let chance = Math.min(Math.pow(i.block.size, 2) / 50, 0.8);
                if (Mathf.chance(chance)) {
                    let rad = i.block.size * 3;
                    let ax = Math.sin(Time.time / (10 + rad * 3));
                    let ay = Math.cos(Time.time / (10 + rad * 3));
                    let sin = Math.sin(Time.time / (10 + rad / 3) * Mathf.range(1));
                    Fx.chainLightning.at(i.x + ax * rad * sin, i.y + ay * rad * sin, 0, lib.FF5845, this);
                }
            }
        },
        updateTile() {
            for (let i of links.content) {
                if (this.timer.get(60)) i.applyBoost(this.efficiency * 9, 61);
                if (!this.linkValid(i)) links.subtract(i);
            }
        },
        linkValid(pos) {
            if (pos === undefined || pos === null || pos == -1 || !pos.isValid()) return false
            else return true
        },
        onConfigureBuildTapped(other) {
            if (other != this && links.size() < max && this.dst(other) <= range && !links.content.includes(other) && !this.tf(other) && other.block.canOverdrive) {
                links.add(other);
                return false
            } else if (links.content.includes(other)) {
                links.subtract(other)
                return false
            } else if (links.size() >= max) {
                return false
            }
            return true
        },
        drawConfigure() {
            let c = Pal.accent, sin = Mathf.absin(Time.time, 4, 1);
            Lines.stroke(1, c);
            Drawf.circles(this.x, this.y, this.block.size / 2 * Vars.tilesize + sin, Pal.accent);
            for (let i of links.content) {
                Drawf.square(i.x, i.y, this.tf(i) ? 3 * 8 : i.block.size * 4, Pal.place);
            }
            Drawf.dashCircle(this.x, this.y, range, c);
        },
        /*write(write) {
            this.super$write(write);
            write.s(links.content.length);
            for (let i of links.content) {
                let ix = i.x, iy = i.y;
                write.f(ix);
                write.f(iy);
            }
        },
        read(read, revision) {
            this.super$read(read, revision);
            let linkSize = read.s();
            for (let i = 0; i < linkSize; i++) {
                let ix = read.f(), iy = read.f();
                let build = Vars.world.tile(ix, iy).build();
                print("ix: "+ix+", "+"iy: "+iy)
                print(build)
                links.add(build);
            }
        }*/
    })
});
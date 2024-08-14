const lib = require("base/coflib");
const status = require("base/status");

function ceriumExtractor(name) {
    let b = new GenericCrafter(name);
    b.buildType = prov(() => {
        let size = 0;
        return extend(GenericCrafter.GenericCrafterBuild, b, {
            range() {
                return this.block.size * 8 * 1.5;
            },
            updateTile() {
                this.super$updateTile();
                size = (this.timeScale > 1 ? Math.min((this.timeScale - 1) / 2 + 1, 2.5) : this.timeScale) + lib.float(3.14 * 3, 0.1) * this.efficiency;
                Damage.status(null, this.x, this.y, this.range() * this.warmup * size, status.辐射, 300, true, true);
            },
            draw() {
                this.super$draw();
                Draw.z(Layer.shields);
                Draw.color(Color.valueOf("F9A3C7"));
                Draw.alpha(0.4 + lib.float(3.14 * 5, 0.4) * this.efficiency);
                Fill.poly(this.x, this.y, 16, this.range() * this.warmup * size);
            }
        })
    });
}

ceriumExtractor("铈提取器");
ceriumExtractor("铈提取器大");
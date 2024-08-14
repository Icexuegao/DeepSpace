const my = require("base/物品");
const lib = require("base/coflib");
const FX = require("base/Effect/fightFx");
const {过热} = require("base/status");
let range = 960;

let PayloadLaunchPad = extend(PayloadBlock, "载荷发射台", {
    size: 8,
    solid: true,
    itemCapacity: 80,
    configurable: true,
    commandable: true,
    acceptsPayload: true,
    group: BlockGroup.units,
    category: Category.units,
    priority: TargetPriority.turret,
    buildVisibility: BuildVisibility.shown,
    drawPlace(x, y, rotation, valid) {
        this.super$drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * 8 + this.offset, y * 8 + this.offset, range, Pal.accent);
    },
    setStats() {
        this.super$setStats();
        this.stats.add(Stat.range, range / 8, StatUnit.blocks);
    },
    setBars() {
        this.super$setBars();
        /*this.addBar("Power", func(e => new Bar(
            prov(() => lib.bundle("bar.ownPower", Strings.fixed(e.getMP() * 60 * 10, 0))),
            prov(() => Pal.powerBar),
            floatp(() => e.getMP())
        )));*/
    }
});

PayloadLaunchPad.consumeItems(ItemStack.with(my.铱板, 80, my.铈凝块, 80));

PayloadLaunchPad.buildType = prov(() => {
    let commandPos = new Vec2(), loadUnit = null;
    return extend(PayloadBlock.PayloadBlockBuild, PayloadLaunchPad, {
        buildConfiguration(table) {
            table.button(new TextureRegionDrawable(this.payload ? Icon.upload : Icon.none), Styles.flati, run(() => {
                if (!this.payload) {
                    Vars.ui.showLabel("发射仓内未检测到有效载荷，无法进行发射！", 3, this.x, this.y);
                    return
                } else if (this.needItem(my.铱板) && this.needItem(my.铈凝块)) {
                    Vars.ui.showLabel("发射仓材料不足，无法进行发射！", 3, this.x, this.y);
                    return
                } else if (!this.moveInPayload()) {
                    Vars.ui.showLabel("单位尚未就位，无法进行发射！", 3, this.x, this.y);
                    return
                }
                this.items.clear();
                loadUnit = this.payload.unit.type;
                this.payload = null;
                Fx.launchPod.at(this);
                FX.launchUp.at(this.x, this.y, 0, this.team.color, {
                    region: loadUnit.fullIcon,
                    timer: new Interval()
                });
                let x = commandPos.x, y = commandPos.y;
                let distance = Mathf.dst(this.x, this.y, x, y) / range;
                if (distance > 1) {
                    let ang = Angles.angle(this.x, this.y, x, y);
                    let xy = lib.AngleTrns(ang, range);
                    x = this.x + xy.x;
                    y = this.y + xy.y;
                }
                Time.run(FX.launchUp.lifetime + 300 * distance, () => {
                    FX.launchDown.at(x, y, 0, this.team.color, {
                        region: loadUnit.fullIcon,
                        timer: new Interval()
                    });
                    Time.run(FX.launchDown.lifetime, () => {
                        let u = loadUnit.spawn(this.team, x, y);
                        u.rotation = 90;
                        u.apply(过热, Math.pow(loadUnit.hitSize, 1.5));
                        Damage.damage(null, x, y, loadUnit.hitSize, Math.pow(loadUnit.hitSize, 2));
                        Fx.titanSmoke.at(x, y, this.team.color);
                        Effect.shake(8, 8, x, y);
                    });
                });
            })).size(64);
        },
        updateTile() {
            this.super$draw();
            this.moveInPayload();
        },
        draw() {
            this.super$draw();
            for (let i = 0; i < 4; i++) {
                if (this.blends(i)) Draw.rect(Core.atlas.find(this.block.name + "-in"), this.x, this.y, (i * 90) - 180);
            }
            if (this.payload) this.payload.draw();
        },
        drawSelect() {
            this.super$drawSelect();
            Drawf.dashCircle(this.x, this.y, range, Pal.accent);
        },
        needItem(item) {
            return this.items.get(item) < this.getMaximumAccepted(item);
        },
        acceptPayload(source, payload) {
            if (!(this.payload == null && (this.enabled || source == this) && this.relativeTo(source) != this.rotation && payload instanceof UnitPayload)) return false;
            return !this.payload && Math.pow(payload.unit.type.hitSize, 2) <= Math.pow(this.block.size, 2) * 64;
        },
        acceptUnitPayload(unit) {
            return !this.payload && Math.pow(unit.type.hitSize, 2) <= Math.pow(this.block.size, 2) * 64;
        },
        getCommandPosition() {
            return commandPos;
        },
        onCommand(target) {
            commandPos = target;
        },
        /*status() {
            return this.power.status > 0 ? BlockStatus.active : BlockStatus.noInput;
        },*/
        write(write) {
            this.super$write(write);
            TypeIO.writeVecNullable(write, commandPos);
        },
        read(read, revision) {
            this.super$read(read, revision);
            commandPos = TypeIO.readVecNullable(read);
        }
    })
})
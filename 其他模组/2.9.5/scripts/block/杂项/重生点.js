const lib = require("base/coflib");
const my = require("base/物品");

let RemakePoint = extend(ShockMine, "重生点", {})
let shockBuild = new Seq(); //存储临时重生点
let unitReviveCore = new Seq(); //存储临时可重生单位
let ReviveRecordCore = new Seq(); //存储临时已重生单位

//每一次加载地图，对两个临时存储集进行清除
Events.run(WorldLoadEvent, () => {
    shockBuild.clear();
    unitReviveCore.clear();
    ReviveRecordCore.clear();
});
//重生
Events.on(UnitDestroyEvent, e => {
    var unit = e.unit;
    //循环执行临时重生点
    for (var i = 0; i < shockBuild.size; i++) {
        var b = shockBuild.get(i);
        //设置重生位置
        let sx = b.x + Mathf.range(b.hitSize());
        let sy = b.y + Mathf.range(b.hitSize());
        //遍历
        b.getunitRevive().each(boolf(unit => unit == unit), cons(unit => {
            Fx.spawn.at(sx, sy); //特效
            var unitE = unit.type.spawn(unit.team, sx, sy); //生成
            b.getunitRevive().remove(unit); //清除重生点的可重生单位
            b.getReviveRecord().add(unitE); //为重生点记录已重生的单位
            ReviveRecordCore.add(unitE); //记录临时已重生的单位
        }));
    }
});
/*loadIcon() { //设置连接贴图
	this.super$loadIcon();
	drawLaserA = lib.region("拾取-laser");
	drawLaserB = lib.region("拾取-laser-end");
}*/

RemakePoint.buildType = prov(() => {
    let unitRevive = new Seq(); //存储需要可重生单位
    let ReviveRecord = new Seq(); //存储已重生不能再重生的单位
    return extend(ShockMine.ShockMineBuild, RemakePoint, {
        /*draw() {
            this.super$draw();
            //动画，连接重生单位
            unitRevive.each(id => {
                var unit = Groups.unit.getByID(id);
                if (unit != null) {
                    let x1 = unit.x, y1 = unit.y, x2 = this.x, y2 = this.y
                    let size1 = unit.hitSize, size2 = this.hitSize();
                    let angle1 = Angles.angle(x1, y1, x2, y2);
                    let vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1);
                    let len1 = size1 / 2f - 1.5f, len2 = size2 / 2f - 1.5f;
                    Draw.color(Color.white);
                    Drawf.laser(drawLaserA, drawLaserB, x1 + vx * len1, y1 + vy * len1, x2 - vx * len2, y2 - vy * len2, 0.25f);
                }
            });
            Draw.reset();
        },*/
        getunitRevive() {
            return unitRevive;
        },
        getReviveRecord() {
            return ReviveRecord;
        },
        unitOn(unit) {
            if (!shockBuild.contains(this)) {
                shockBuild.add(this); //在临时重生点里加入本重生点
            }
            if (this.enabled && unit.team == this.team) { //设置
                if (!ReviveRecord.contains(unit) && !unitRevive.contains(unit) && !unitReviveCore.contains(unit) && !ReviveRecordCore.contains(unit)) {
                    unitRevive.add(unit); //加入可重生集
                    unitReviveCore.add(unit); //加入临时重生集
                }
            }
        },
        write(write) { //数据保存(用于地图保存)
            this.super$write(write);
            write.i(unitRevive.size);
            for (var i = 0; i < unitRevive.size; i++) {
                write.i(unitRevive.get(i).id);
            }
            write.i(ReviveRecord.size);
            for (var i = 0; i < ReviveRecord.size; i++) {
                write.i(ReviveRecord.get(i).id);
            }
        },
        read(read, revision) { //数据读取(用于地图加载)
            this.super$read(read, revision);
            unitRevive.clear();
            var UR = read.i();
            for (var i = 0; i < UR; i++) {
                var id = read.i();

                var unit = Groups.unit.getByID(id);

                unitRevive.add(unit);
                unitReviveCore.add(unit);
            }
            ReviveRecord.clear();
            var RR = read.i();
            for (var i = 0; i < RR; i++) {
                var id = read.i();

                var unit = Groups.unit.getByID(id);

                ReviveRecord.add(unit);
                ReviveRecordCore.add(unit);
            }
        }
    })
})
RemakePoint.health = 300;
RemakePoint.size = 3;
RemakePoint.armor = 8;
RemakePoint.hasShadow = false;
RemakePoint.setupRequirements(
    Category.effect,
    BuildVisibility.shown,
    ItemStack.with(
        my.铱板, 150,
        my.导能回路, 55,
        Items.phaseFabric, 25
    )
);
/**
 项目: 液物热发电机 [20230607]
 作者: miner
 */

const ConsumeGeneratorBuild = ConsumeGenerator.ConsumeGeneratorBuild;

var heatRequirement = 5; // 需热
var overheatScale = 1; // 过热的效率
var maxEfficiency = 2; // 最大效率

let heatGenerator = extend(ConsumeGenerator, "heat-generator", {
    size: 2,
    powerProduction: 3,

    setBars(){
        this.super$setBars();

        this.addBar("heat", e => new Bar(
            () => Core.bundle.format("bar.heatpercent", parseInt(e.heat()), parseInt(e.efficiencyScale() * 100)),
            () => Pal.lightOrange,
            () => parseInt(e.heat()) / heatRequirement));
    },
});

/** setupRequirements: 三个愿望 一次满足(误
 * 参数1: 设置category
 * 参数2: 设置buildVisibility
 * 参数3: 设置requirements
 */
heatGenerator.setupRequirements(
    Category.power,
    BuildVisibility.shown,
    ItemStack.with(Items.copper, 200, Items.lead, 150)
);

heatGenerator.consumeLiquid(Liquids.water, 0.3);

var block = heatGenerator;

heatGenerator.buildType = () => {
    let sideHeat = new Array(4);
    let heat = 0;

    /** JavaAdapter:
     * 主类(ConsumeGeneratorBuild),
     * 接口(HeatConsumer),
     * 实现({...}),
     * 构造函数的参数(heatGenerator)
     *
     * 注意: extend函数其实也是用JavaAdapter 其区别在于:
     * extend无法实现接口,只能继承主类(这也是anuke给extend的本意
     */
    let build = new JavaAdapter(ConsumeGeneratorBuild, HeatConsumer, {
        /** 这里是HeatConsumer需要实现的方法
         * float[] sideHeat();
         * float heatRequirement();
         */
        sideHeat(){
            return sideHeat;
        },

        heatRequirement(){
            return heatRequirement;
        },

        updateTile(){
            heat = this.calculateHeat(sideHeat);

            this.super$updateTile();

            this.productionEfficiency *= this.efficiencyScale();
        },

        warmupTarget(){
            return Mathf.clamp(heat / heatRequirement);
        },

        efficiencyScale(){
            let over = Math.max(heat - heatRequirement, 0);
            return Math.min(Mathf.clamp(heat / heatRequirement) + over / heatRequirement * overheatScale, maxEfficiency);
        },

        // 获取热量
        heat(){
            return heat;
        },

    }, block);

    return build;
}
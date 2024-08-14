const lib = require("base/coflib");
const mode = new UnitAssemblerModule("高级装配模块");
mode.configurable = true;
mode.buildVisibility = BuildVisibility.sandboxOnly;
mode.buildType = prov(() => {
    let LV = 1;
    return extend(UnitAssemblerModule.UnitAssemblerModuleBuild, mode, {
        buildConfiguration(table) {
            table.button(Icon.refresh, Styles.cleari, run(() => {
                LV = 1;
                Vars.ui.showLabel(LV, 1, this.x, this.y);
            })).size(45);
            table.button(Icon.add, Styles.cleari, run(() => {
                if (LV < 3) {
                    LV += 1;
                    Vars.ui.showLabel(LV, 1, this.x, this.y);
                }
            })).size(45);
        },
        tier() {
            return LV;
        }
    })
})
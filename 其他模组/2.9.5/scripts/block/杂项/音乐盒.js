const lib = require("base/coflib");
let musicBox = lib.newBlock("音乐盒");
musicBox.buildVisibility = BuildVisibility.sandboxOnly;

lib.setBuilding(Building, musicBox, {
    mumber: 1,
    play: false,
    updateTile() {
        Vars.ui.showLabel(this.mumber, 0.01, this.x, this.y);
    },
    buildConfiguration(table) {
        table.button(Icon.add, Styles.cleari, run(() => {
            if (this.mumber <= 3) this.mumber += 1;
            if (this.mumber > 3) this.mumber = 1;
        })).size(45).update(b => {
            b.setDisabled(this.play);
        });
        table.button(Icon.play, Styles.cleari, run(() => {
            //Vars.tree.loadSound("sound" + this.mumber).play();
            //Vars.tree.loadSound("芜！").at(this);
            this.play = true;
        })).size(45).update(b => {
            b.setDisabled(this.play);
        });
        table.button(Icon.pause, Styles.cleari, run(() => {
            //Vars.tree.loadSound("sound" + this.mumber).stop();
            //Vars.tree.loadSound("芜！").at(this);
            this.play = false;
        })).size(45).update(b => {
            b.setDisabled(!this.play);
        });
    }
})
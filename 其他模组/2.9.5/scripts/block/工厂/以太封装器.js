const lib = require("base/coflib");

let b = new GenericCrafter("以太封装器");
lib.setBuilding(GenericCrafter.GenericCrafterBuild, b, {
    craft() {
        this.super$craft();
        this.applyBoost(5, 240);
    }
})
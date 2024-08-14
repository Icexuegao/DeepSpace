const lib = require("base/coflib");
let yf = new Incinerator("液体焚化炉");
lib.setBuilding(Incinerator.IncineratorBuild, yf, {
    acceptItem(source, item) {
        return false
    }
});
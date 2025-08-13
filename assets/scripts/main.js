// const TopLevel=Packages.rhino.TopLevel
//
// let scope = new TopLevel();
// new Packages.rhino.ClassCache().associate(scope)
// let n = new Packages.rhino.NativeJavaObject(scope, StatusEffects.burning, StatusEffect, true);
// n.affinity(StatusEffects.boss, (u, t, s) => {
//
// })

/*const statusLib = require("StatusLib");
StatusEffects.burning.init(() => {
    statusLib.affinity(StatusEffects.burning, StatusEffects.blasted, (unit, entry, time) => {

    })
    statusLib.opposite(StatusEffects.burning,StatusEffects.sapped)
})*/

/*
let transitionHandler = (fn) => new StatusEffect.TransitionHandler()
{
    handle:fn
}

Reflect.invoke(StatusEffect, StatusEffects.burning, "affinity", [StatusEffects.blasted, transitionHandler((unit, entry, time) => {

})], StatusEffect, StatusEffect.TransitionHandler);*/
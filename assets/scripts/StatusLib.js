/**
 * @author < i..hope >
 * @author < miner >
 * @author < Alon >
 * @version 150
 * //text
 * const statusLib = require("StatusLib");
 * StatusEffects.burning.init(() => {
 *     statusLib.affinity(StatusEffects.burning, StatusEffects.blasted, (unit, entry, time) => {
 *
 *     })
 *     statusLib.opposite(StatusEffects.burning, StatusEffects.sapped)
 * })
 */

function affinity(from, to, transitionHandler) {
    let spreadElements = new StatusEffect.TransitionHandler()
    {
        transitionHandler
    }
    Reflect.invoke(StatusEffect, from, "affinity", [to, spreadElements], StatusEffect, StatusEffect.TransitionHandler);
}

exports.affinity = affinity;

function opposite(from, to) {
    let forName = Packages.java.lang.Class.forName("[Lmindustry.type.StatusEffect;");

    var  method = from.getClass().getSuperclass().getDeclaredMethod("opposite",forName);
    method.setAccessible(true);

// 调用方法
    let d = [StatusEffects.sapped];
    let asObject = new Packages.java.lang.Object({ value: d });

// 然后使用
    method.invoke(from, asObject.value);


   // Reflect.invoke(StatusEffects.burning.getClass().getSuperclass(), from, "opposite", [to], forName);
}

exports.opposite = opposite;
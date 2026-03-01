package singularity.world.meta;

import arc.struct.Seq;
import ice.world.meta.IceStatCats;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import universecore.util.handler.FieldHandler;

public class SglStat{
  public static final Stat
      componentBelongs = create("componentBelongs", IceStatCats.INSTANCE.get结构()),
      maxChildrenNodes = create("maxChildrenNodes", IceStatCats.INSTANCE.get结构()),
      linkDirections = create("linkDirections", IceStatCats.INSTANCE.get结构()),

      energyCapacity = create("energyCapacity", SglStatCat.neutron),
      energyResident = create("energyResident", SglStatCat.neutron),
      basicPotentialEnergy = create("basicPotentialEnergy", SglStatCat.neutron),
      maxEnergyPressure = create("maxEnergyPressure", SglStatCat.neutron),
      minEnergyPotential = create("minEnergyPotential", SglStatCat.neutron),
      maxEnergyPotential = create("maxEnergyPotential", SglStatCat.neutron),
      consumeEnergy = create("consumeEnergy", SglStatCat.neutron),
      productEnergy = create("productEnergy", SglStatCat.neutron),

      matrixEnergyUse = create("matrixEnergyUse", SglStatCat.matrix),
      matrixEnergyCapacity = create("matrixEnergyCapacity", SglStatCat.matrix),
      topologyUse = create("topologyUse", SglStatCat.matrix),
      maxMatrixLinks = create("maxMatrixLinks", SglStatCat.matrix),

      bufferSize = create("bufferSize", SglStatCat.matrix),
      computingPower = create("computingPower", SglStatCat.matrix),
      topologyCapacity = create("topologyCapacity", SglStatCat.matrix),
      drillSize = create("drillSize", SglStatCat.matrix),
      drillAngle = create("drillAngle", SglStatCat.matrix),
      pierceBuild = create("pierceBuild", SglStatCat.matrix),
      matrixEnergyUseMulti = create("matrixEnergyUseMulti", SglStatCat.matrix),
      drillMoveMulti = create("drillMoveMulti", SglStatCat.matrix),

      heatProduct = create("heatProduct", SglStatCat.heat),
      maxHeat = create("maxHeat", SglStatCat.heat),

      consume = create("consume", SglStatCat.reaction),
      product = create("product", SglStatCat.reaction),

      empHealth = create("empHealth", StatCat.general),
      empArmor = create("empArmor", StatCat.general),
      empRepair = create("empRepair", StatCat.general),

      bulletCoating = create("bulletCoating", StatCat.function),
      coatingTime = create("coatingTime", StatCat.function),
      exShieldDamage = create("exShieldDamage", StatCat.function),
      exDamageMultiplier = create("exDamageMultiplier", StatCat.function),
      damagedMultiplier = create("damagedMultiplier", StatCat.function),
      damageProbably = create("damageProbably", StatCat.function),
      exPierce = create("exPierce", StatCat.function),
      maxCoatingBuffer = create("maxcoatingbuffer", StatCat.function),
      flushTime = create("flushtime", StatCat.function),
      maxCellYears = create("maxcellyears", StatCat.function),
      gridSize = create("gridsize", StatCat.function),
      launchTime = create("launchtime", StatCat.function),
      launchConsume = create("launchconsume", 53, StatCat.function),
      maxTarget = create("maxTarget", StatCat.function),

      multiple = create("multiple", StatCat.crafting),
      autoSelect = create("autoSelect", 46, StatCat.crafting),
      controllable = create("controllable", 47, StatCat.crafting),
      recipes = create("recipes", 48, StatCat.crafting),
      special = create("special", 51, StatCat.crafting),

      sizeLimit = create("sizeLimit", StatCat.crafting),
      healthLimit = create("healthLimit", StatCat.crafting),
      buildLevel = create("buildLevel", StatCat.crafting),

      effect = create("effect", StatCat.function),

      fieldStrength = create("fieldStrength", StatCat.function),
      albedo = create("albedo", StatCat.function);

  private static Stat create(String name, StatCat cat){
    return create(name, Stat.all.size, cat);
  }

  private static Stat create(String name, int index, StatCat cat){
    Seq<Stat> all = Stat.all;
    Stat res = new Stat(name, cat);

    all.remove(res);
    all.insert(index, res);

    for(int i = 0; i < all.size; i++){
      FieldHandler.setValueDefault(all.get(i), "id", i);
    }

    return res;
  }
}

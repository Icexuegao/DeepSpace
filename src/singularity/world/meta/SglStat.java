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

      energyCapacity = create("energyCapacity", IceStatCats.INSTANCE.getNeutron()),
      energyResident = create("energyResident", IceStatCats.INSTANCE.getNeutron()),
      basicPotentialEnergy = create("basicPotentialEnergy", IceStatCats.INSTANCE.getNeutron()),
      maxEnergyPressure = create("maxEnergyPressure", IceStatCats.INSTANCE.getNeutron()),
      minEnergyPotential = create("minEnergyPotential", IceStatCats.INSTANCE.getNeutron()),
      maxEnergyPotential = create("maxEnergyPotential", IceStatCats.INSTANCE.getNeutron()),
      consumeEnergy = create("consumeEnergy", IceStatCats.INSTANCE.getNeutron()),
      productEnergy = create("productEnergy", IceStatCats.INSTANCE.getNeutron()),

      matrixEnergyUse = create("matrixEnergyUse", IceStatCats.INSTANCE.getMatrix()),
      matrixEnergyCapacity = create("matrixEnergyCapacity", IceStatCats.INSTANCE.getMatrix()),
      topologyUse = create("topologyUse", IceStatCats.INSTANCE.getMatrix()),
      maxMatrixLinks = create("maxMatrixLinks", IceStatCats.INSTANCE.getMatrix()),

      bufferSize = create("bufferSize", IceStatCats.INSTANCE.getMatrix()),
      computingPower = create("computingPower", IceStatCats.INSTANCE.getMatrix()),
      topologyCapacity = create("topologyCapacity", IceStatCats.INSTANCE.getMatrix()),
      drillSize = create("drillSize", IceStatCats.INSTANCE.getMatrix()),
      drillAngle = create("drillAngle", IceStatCats.INSTANCE.getMatrix()),
      pierceBuild = create("pierceBuild", IceStatCats.INSTANCE.getMatrix()),
      matrixEnergyUseMulti = create("matrixEnergyUseMulti", IceStatCats.INSTANCE.getMatrix()),
      drillMoveMulti = create("drillMoveMulti", IceStatCats.INSTANCE.getMatrix()),

      heatProduct = create("heatProduct", IceStatCats.INSTANCE.getHeat()),
      maxHeat = create("maxHeat", IceStatCats.INSTANCE.getHeat()),

      consume = create("consume", IceStatCats.INSTANCE.getReaction()),
      product = create("product", IceStatCats.INSTANCE.getReaction()),

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

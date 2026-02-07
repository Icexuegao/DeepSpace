package singularity

import arc.func.Cons
import arc.math.Mathf
import arc.util.Strings
import mindustry.core.UI
import mindustry.ctype.UnlockableContent
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.blocks.environment.Floor
import mindustry.world.meta.StatUnit
import singularity.recipes.EnergyMarker
import singularity.recipes.RecipeParsers
import singularity.recipes.RecipeParsers.ProducerParser
import singularity.world.consumers.SglConsumeEnergy
import singularity.world.consumers.SglConsumeFloor
import singularity.world.consumers.SglConsumeType
import singularity.world.consumers.SglConsumeType.Companion.floor
import singularity.world.meta.SglStatUnit
import singularity.world.products.ProduceEnergy
import tmi.RecipeEntry
import tmi.TooManyItems
import tmi.recipe.Recipe
import tmi.recipe.RecipeItemStack
import tmi.recipe.RecipeType
import tmi.recipe.types.PowerMark
import universecore.world.consumers.*
import universecore.world.producers.*

class Recipes : RecipeEntry {
  init {
    //items
    RecipeParsers.registerConsumeParser(ConsumeType.item) { b: Block?, r: Recipe?, c: ConsumeItemBase<*>?, h: Cons<RecipeItemStack?>? ->
      if (c is ConsumeItemCond<*>) {
        for (stack in c.consItems!!) {
          h!!.get(r!!.addMaterialInteger(TooManyItems.itemsManager.getItem<Item?>(stack.item), stack.amount).setAttribute(c).setMaxAttr())
        }
      } else {
        for (stack in c!!.consItems!!) {
          h!!.get(r!!.addMaterialInteger(TooManyItems.itemsManager.getItem<Item?>(stack.item), stack.amount))
        }
      }
    }
    RecipeParsers.registerProduceParser(ProduceType.item) { b: Block?, r: Recipe?, p: ProduceItems<*>?, h: Cons<RecipeItemStack?>? ->
      if (p!!.random) {
        var n = 0f
        for (stack in p.items) {
          n += stack.amount.toFloat()
        }

        val l = n
        for (item in p.items) {
          h!!.get(r!!.addProductionFloat(TooManyItems.itemsManager.getItem<Item?>(item.item), item.amount / l / r.craftTime).setFormat { f: Float -> Mathf.round(item.amount / l * 100).toString() + "%" }.setAltFormat { f ->
            (if (f * 60 > 1000) UI.formatAmount((f * 60).toLong())
            else Strings.autoFixed(f * 60, 2)) + "/" + StatUnit.seconds.localized()
          })
        }
      } else {
        for (stack in p.items) {
          h!!.get(r!!.addProductionInteger(TooManyItems.itemsManager.getItem<Item?>(stack.item), stack.amount))
        }
      }
    }

    //liquids
    RecipeParsers.registerConsumeParser(ConsumeType.liquid) { b: Block?, r: Recipe?, c: ConsumeLiquidBase<*>?, h: Cons<RecipeItemStack?>? ->
      when (c) {
        is ConsumeLiquidCond<*> -> {
          for (stack in c.consLiquids!!) {
            h!!.get(r!!.addMaterialPersec(TooManyItems.itemsManager.getItem<Liquid?>(stack.liquid), stack.amount).setAttribute(c).setMaxAttr())
          }
        }

        is ConsumeLiquids<*> if c.portion -> {
          for (stack in c.consLiquids!!) {
            h!!.get(r!!.addMaterialFloat(TooManyItems.itemsManager.getItem<Liquid?>(stack.liquid), stack.amount))
          }
        }

        else -> {
          for (stack in c!!.consLiquids!!) {
            h!!.get(r!!.addMaterialPersec(TooManyItems.itemsManager.getItem<Liquid?>(stack.liquid), stack.amount))
          }
        }
      }
    }
    RecipeParsers.registerProduceParser(ProduceType.liquid) { b: Block?, r: Recipe?, p: ProduceLiquids<*>?, h: Cons<RecipeItemStack?>? ->
      if (p!!.portion) {
        for (liquid in p.liquids) {
          h!!.get(r!!.addProductionFloat(TooManyItems.itemsManager.getItem<Liquid?>(liquid.liquid), liquid.amount))
        }
      }
      for (liquid in p.liquids) {
        h!!.get(r!!.addProductionPersec(TooManyItems.itemsManager.getItem<Liquid?>(liquid.liquid), liquid.amount))
      }
    }

    //power
    RecipeParsers.registerConsumeParser(ConsumeType.power) { b: Block?, r: Recipe?, c: ConsumePower<*>?, h: Cons<RecipeItemStack?>? -> h!!.get(r!!.addMaterialPersec(PowerMark, c!!.usage)) }
    RecipeParsers.registerProduceParser(ProduceType.power) { b: Block?, r: Recipe?, p: ProducePower<*>?, h: Cons<RecipeItemStack?>? -> h!!.get(r!!.addProductionPersec(PowerMark, p!!.powerProduction)) }

    //neutron energy
    RecipeParsers.registerConsumeParser(SglConsumeType.energy) { b: Block?, r: Recipe?, c: SglConsumeEnergy<*>?, h: Cons<RecipeItemStack?>? -> h!!.get(r!!.addMaterialPersec(EnergyMarker.INSTANCE, c!!.usage).setFormat { f: Float -> (if (f * 60.0f > 1000.0f) UI.formatAmount((f * 60.0f).toLong()) else Strings.autoFixed(f * 60.0f, 2)) + SglStatUnit.neutronFluxSecond.localized() }) }
    RecipeParsers.registerProduceParser(ProduceType.energy) { b: Block?, r: Recipe?, p: ProduceEnergy<*>, h: Cons<RecipeItemStack?>? -> h!!.get(r!!.addProductionPersec(EnergyMarker.INSTANCE, p!!.product).setFormat { f: Float -> (if (f * 60.0f > 1000.0f) UI.formatAmount((f * 60.0f).toLong()) else Strings.autoFixed(f * 60.0f, 2)) + SglStatUnit.neutronFluxSecond.localized() }) }

    //payloads
    RecipeParsers.registerConsumeParser(ConsumeType.payload) { b: Block?, r: Recipe?, c: ConsumePayload<*>?, h: Cons<RecipeItemStack?>? ->
      for (stack in c!!.payloads) {
        if (stack.amount > 1) h!!.get(r!!.addMaterialInteger(TooManyItems.itemsManager.getItem<UnlockableContent?>(stack.item), stack.amount))
        else h!!.get(r!!.addMaterialInteger(TooManyItems.itemsManager.getItem<UnlockableContent?>(stack.item), stack.amount).emptyFormat())
      }
    }
    RecipeParsers.registerProduceParser(ProduceType.payload) { b: Block?, r: Recipe?, p: ProducePayload<*>?, h: Cons<RecipeItemStack?>? ->
      for (stack in p!!.payloads) {
        if (stack.amount > 1) h!!.get(r!!.addProductionInteger(TooManyItems.itemsManager.getItem<UnlockableContent?>(stack.item), stack.amount))
        else h!!.get(r!!.addProductionInteger(TooManyItems.itemsManager.getItem<UnlockableContent?>(stack.item), stack.amount).emptyFormat())
      }
    }

    //floors
    RecipeParsers.registerConsumeParser(floor) { b: Block?, r: Recipe?, c: SglConsumeFloor<*>?, h: Cons<RecipeItemStack?>? ->
      for (entry in c!!.floorEff) {
        val eff = entry.value * b!!.size * b.size
        h!!.get(r!!.addMaterialInteger(TooManyItems.itemsManager.getItem<Floor?>(entry.key), b.size * b.size).setOptional(c.baseEfficiency > 0).setEff(c.baseEfficiency + eff).setAttribute(c).setAttribute())
      }
    }
  }

  override fun init() {
    RecipeType.generator.addPower(EnergyMarker.INSTANCE)

    TooManyItems.recipesManager.registerParser(ProducerParser())
  }

  override fun afterInit() {
  }
}
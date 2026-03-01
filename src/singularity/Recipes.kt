package singularity

import arc.func.Cons
import arc.math.Mathf
import arc.util.Strings
import mindustry.core.UI
import mindustry.ctype.UnlockableContent
import mindustry.type.Item
import mindustry.world.Block
import mindustry.world.blocks.environment.Floor
import mindustry.world.meta.StatUnit
import singularity.recipes.EnergyMarker
import singularity.recipes.NormalCrafterParser
import universecore.world.consumers.cons.SglConsumeEnergy
import universecore.world.consumers.cons.SglConsumeFloor

import singularity.world.meta.SglStatUnit
import singularity.world.products.ProduceEnergy
import tmi.RecipeEntry
import tmi.TooManyItems
import tmi.recipe.Recipe
import tmi.recipe.RecipeItemGroup
import tmi.recipe.RecipeItemStack
import tmi.recipe.types.PowerMark
import universecore.world.consumers.*
import universecore.world.consumers.cons.ConsumeItemBase
import universecore.world.consumers.cons.ConsumeItemCond
import universecore.world.consumers.cons.ConsumeLiquidBase
import universecore.world.consumers.cons.ConsumeLiquidCond
import universecore.world.consumers.cons.ConsumeLiquids
import universecore.world.consumers.cons.ConsumePayload
import universecore.world.consumers.cons.ConsumePower
import universecore.world.producers.*

class Recipes : RecipeEntry {
  init {
    //items
    NormalCrafterParser.registerConsumeParser(ConsumeType.item) { b: Block?, r: Recipe?, c: ConsumeItemBase<*>?, h: Cons<RecipeItemStack<*>> ->
      var grou = RecipeItemGroup()
      if (c is ConsumeItemCond<*>) {
        for (stack in c.consItems!!) {
          val addMaterialInteger: RecipeItemStack<out Any?> = r!!.addMaterialInteger(TooManyItems.itemsManager.getItem<Item?>(stack.item), stack.amount)
          addMaterialInteger.setGroup(grou)
          h.get(addMaterialInteger/*addMaterialInteger.setAttribute(c).setMaxAttr()*/)
        }
      } else {
        for (stack in c!!.consItems!!) {
          h.get(r!!.addMaterialInteger(TooManyItems.itemsManager.getItem<Item?>(stack.item), stack.amount))
        }
      }
    }
    NormalCrafterParser.registerProduceParser(ProduceType.item) { b: Block?, r: Recipe?, p: ProduceItems<*>?, h: Cons<RecipeItemStack<*>>? ->
      if (p!!.random) {
        var n = 0f
        for (stack in p.items) {
          n += stack.amount.toFloat()
        }

        val l = n
        for (item in p.items) {
          h!!.get(r!!.addProductionFloat(TooManyItems.itemsManager.getItem(item.item), item.amount / l / r.craftTime).setFormat { f: Float -> Mathf.round(item.amount / l * 100).toString() + "%" }.setAltFormat { f ->
            (if (f * 60 > 1000) UI.formatAmount((f * 60).toLong())
            else Strings.autoFixed(f * 60, 2)) + "/" + StatUnit.seconds.localized()
          })
        }
      } else {
        for (stack in p.items) {
          h!!.get(r!!.addProductionInteger(TooManyItems.itemsManager.getItem(stack.item), stack.amount))
        }
      }
    }

    //liquids
    NormalCrafterParser.registerConsumeParser(ConsumeType.liquid) { b: Block?, r: Recipe?, c: ConsumeLiquidBase<*>?, h: Cons<RecipeItemStack<*>?>? ->
      when (c) {
        is ConsumeLiquidCond<*> -> {
          for (stack in c.consLiquids!!) {
            h!!.get(r!!.addMaterialPersec(TooManyItems.itemsManager.getItem(stack.liquid), stack.amount)/*.setAttribute(c).setMaxAttr()*/)
          }
        }

        is ConsumeLiquids<*> if c.portion -> {
          for (stack in c.consLiquids!!) {
            h!!.get(r!!.addMaterialFloat(TooManyItems.itemsManager.getItem(stack.liquid), stack.amount))
          }
        }

        else -> {
          for (stack in c!!.consLiquids!!) {
            h!!.get(r!!.addMaterialPersec(TooManyItems.itemsManager.getItem(stack.liquid), stack.amount))
          }
        }
      }
    }
    NormalCrafterParser.registerProduceParser(ProduceType.liquid) { b: Block?, r: Recipe?, p: ProduceLiquids<*>?, h: Cons<RecipeItemStack<*>>? ->
      if (p!!.portion) {
        for (liquid in p.liquids) {
          h!!.get(r!!.addProductionFloat(TooManyItems.itemsManager.getItem(liquid.liquid), liquid.amount))
        }
      }
      for (liquid in p.liquids) {
        h!!.get(r!!.addProductionPersec(TooManyItems.itemsManager.getItem(liquid.liquid), liquid.amount))
      }
    }

    //power
    NormalCrafterParser.registerConsumeParser(ConsumeType.power) { b: Block?, r: Recipe?, c: ConsumePower<*>?, h: Cons<RecipeItemStack<*>>? -> h!!.get(r!!.addMaterialPersec(PowerMark, c!!.usage)) }
    NormalCrafterParser.registerProduceParser(ProduceType.power) { b: Block?, r: Recipe?, p: ProducePower<*>?, h: Cons<RecipeItemStack<*>>? -> h!!.get(r!!.addProductionPersec(PowerMark, p!!.powerProduction)) }

    //neutron energy
    NormalCrafterParser.registerConsumeParser(ConsumeType.energy) { b: Block?, r: Recipe?, c: SglConsumeEnergy<*>?, h: Cons<RecipeItemStack<*>>? -> h!!.get(r!!.addMaterialPersec(EnergyMarker.INSTANCE, c!!.usage).setFormat { f: Float -> (if (f * 60.0f > 1000.0f) UI.formatAmount((f * 60.0f).toLong()) else Strings.autoFixed(f * 60.0f, 2)) + SglStatUnit.neutronFluxSecond.localized() }) }
    NormalCrafterParser.registerProduceParser(ProduceType.energy) { b: Block?, r: Recipe?, p: ProduceEnergy<*>, h: Cons<RecipeItemStack<*>>? -> h!!.get(r!!.addProductionPersec(EnergyMarker.INSTANCE, p.product).setFormat { f: Float -> (if (f * 60.0f > 1000.0f) UI.formatAmount((f * 60.0f).toLong()) else Strings.autoFixed(f * 60.0f, 2)) + SglStatUnit.neutronFluxSecond.localized() }) }

    //payloads
    NormalCrafterParser.registerConsumeParser(ConsumeType.payload) { b: Block?, r: Recipe?, c: ConsumePayload<*>?, h: Cons<RecipeItemStack<*>>? ->
      for (stack in c!!.payloads) {
        if (stack.amount > 1) h!!.get(r!!.addMaterialInteger(TooManyItems.itemsManager.getItem<UnlockableContent?>(stack.item), stack.amount))
        else h!!.get(r!!.addMaterialInteger(TooManyItems.itemsManager.getItem<UnlockableContent?>(stack.item), stack.amount).emptyFormat())
      }
    }
    NormalCrafterParser.registerProduceParser(ProduceType.payload) { b: Block?, r: Recipe?, p: ProducePayload<*>?, h: Cons<RecipeItemStack<*>>? ->
      for (stack in p!!.payloads) {
        if (stack.amount > 1) h!!.get(r!!.addProductionInteger(TooManyItems.itemsManager.getItem<UnlockableContent?>(stack.item), stack.amount))
        else h!!.get(r!!.addProductionInteger(TooManyItems.itemsManager.getItem<UnlockableContent?>(stack.item), stack.amount).emptyFormat())
      }
    }

    //floors
    NormalCrafterParser.registerConsumeParser(ConsumeType.floor) { b: Block?, r: Recipe?, c: SglConsumeFloor<*>?, h: Cons<RecipeItemStack<*>>? ->
      for (entry in c!!.floorEff) {
        val eff = entry.value * b!!.size * b.size
        h!!.get(r!!.addMaterialInteger(TooManyItems.itemsManager.getItem<Floor?>(entry.key), b.size * b.size).setOptional(c.baseEfficiency > 0).setEff(c.baseEfficiency + eff)/*.setAttribute(c).setAttribute()*/)
      }
    }
  }

  override fun init() {
    // RecipeType.generator.addPower(EnergyMarker.INSTANCE)

    TooManyItems.recipesManager.registerParser(NormalCrafterParser())
    // TooManyItems.recipesManager.registerParser(MyParser())
  }

  override fun afterInit() {
  }
}
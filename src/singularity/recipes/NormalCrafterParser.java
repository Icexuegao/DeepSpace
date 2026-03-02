package singularity.recipes;

import arc.Core;
import arc.func.Cons;
import arc.func.Cons4;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.world.Block;
import org.jetbrains.annotations.NotNull;
import singularity.world.blocks.product.NormalCrafter;
import tmi.recipe.*;
import universecore.components.blockcomp.ConsumerBuildComp;
import universecore.world.consumers.BaseConsume;
import universecore.world.consumers.BaseConsumers;
import universecore.world.consumers.ConsumeType;
import universecore.world.consumers.cons.SglConsumeFloor;
import universecore.world.producers.BaseProduce;
import universecore.world.producers.BaseProducers;
import universecore.world.producers.ProduceType;

public class NormalCrafterParser extends RecipeParser<NormalCrafter> {

  static final ObjectMap<ConsumeType<?>, Cons4<Block, Recipe, BaseConsume<?>, Cons<RecipeItemStack<?>>>> consParsers = new ObjectMap<>();
  static final ObjectMap<ProduceType<?>, Cons4<Block, Recipe, BaseProduce<?>, Cons<RecipeItemStack<?>>>> prodParsers = new ObjectMap<>();

  @SuppressWarnings("unchecked")
  public static <T extends BaseConsume<?>> void registerConsumeParser(ConsumeType<T> type, Cons4<Block, Recipe, T, Cons<RecipeItemStack<?>>> parser) {
    consParsers.put(type, (Cons4<Block, Recipe, BaseConsume<?>, Cons<RecipeItemStack<?>>>) parser);
  }

  @SuppressWarnings("unchecked")
  public static <T extends BaseProduce<?>> void registerProduceParser(ProduceType<T> type, Cons4<Block, Recipe, T, Cons<RecipeItemStack<?>>> parser) {
    prodParsers.put(type, (Cons4<Block, Recipe, BaseProduce<?>, Cons<RecipeItemStack<?>>>) parser);
  }

  @Override
  public boolean isTarget(@NotNull Block block) {
    return block instanceof NormalCrafter;
  }

  @NotNull
  @Override
  public Seq<Recipe> parse(NormalCrafter normalCrafter) {
    Seq<Recipe> recipes = new Seq<>();

    for (BaseProducers crafter : normalCrafter.getProducers()) {
      boolean isGenerator = false;

      for (BaseProduce<?> produce : crafter.all()) {
        if (produce.type() == ProduceType.Companion.getPower() || produce.type() == ProduceType.Companion.getEnergy()) {
          isGenerator = true;
          break;
        }
      }

      Recipe recipe = new Recipe(isGenerator ? RecipeType.generator : RecipeType.factory, getWrap(normalCrafter), crafter.getCons().getCraftTime());

      for (BaseConsume<?> consume : crafter.getCons().all()) {
        if (consParsers.containsKey(consume.type())) {
          consParsers.get(consume.type()).get(normalCrafter, recipe, consume, s -> {
          });
          if (consume instanceof SglConsumeFloor<?> cf) {
            recipe.setEff(new Recipe.EffFunc() {
              @Override
              public float calculateMultiple(@NotNull Recipe recipe, @NotNull InputTable envParameter) {
                return 0;
              }

              @Override
              public float calculateEff(@NotNull Recipe recipe, @NotNull InputTable envParameter, float v) {
                return cf.getBaseEfficiency();
              }
            });
          }
        }
      }

      for (BaseConsumers consumers : normalCrafter.getOptionalCons()) {
        if (normalCrafter.getOptionalProducts().containsKey(consumers)) continue;

        for (BaseConsume<? extends ConsumerBuildComp> consume : consumers.all()) {
          if (consParsers.containsKey(consume.type()))
            consParsers.get(consume.type()).get(normalCrafter, recipe, consume, recipeItemStack -> {
              AmountFormatter old = recipeItemStack.getAmountFormat();
              float eff = normalCrafter.getBoosts().get(consumers, 1f) * recipeItemStack.getEfficiency();
              if (eff == 1 && recipeItemStack.getGroup() == null) return;
              recipeItemStack.setOptional().setEff(eff).setFormat(f -> old.format(f) + "\n[#98ffa9]" + Mathf.round(eff * 100) + "%");
            });
        }
      }

      for (BaseProduce<?> produce : crafter.all()) {
        if (prodParsers.containsKey(produce.type()))
          prodParsers.get(produce.type()).get(normalCrafter, recipe, produce, s -> {
          });
      }

      NormalCrafter.Byproduct byproduct = normalCrafter.getByproducts().get(crafter.getCons());
      if (byproduct != null) {
        recipe.addProduction(getWrap(byproduct.getItem()), byproduct.getBase() + byproduct.getChance()).setOptional();
      }

      recipes.add(recipe);
    }

    for (ObjectMap.Entry<BaseConsumers, BaseProducers> product : normalCrafter.getOptionalProducts()) {
      Recipe recipe = new Recipe(RecipeType.factory, getWrap(normalCrafter), product.key.getCraftTime());
      recipe.setSubInfo(t -> {
        t.add(Core.bundle.get("[accent]附加的次要生产项"));
        if (!product.key.getOptionalAlwaysValid())
          t.row().add(Core.bundle.get("[lightgray]需主要的生产处于工作状态")).padTop(4);
      });

      for (BaseProduce<?> produce : product.value.all()) {
        if (prodParsers.containsKey(produce.type()))
          prodParsers.get(produce.type()).get(normalCrafter, recipe, produce, s -> {
          });
      }

      for (BaseConsume<?> consume : product.key.all()) {
        if (consParsers.containsKey(consume.type()))
          consParsers.get(consume.type()).get(normalCrafter, recipe, consume, s -> {
          });
      }

      recipes.add(recipe);
    }

    return recipes;
  }
}
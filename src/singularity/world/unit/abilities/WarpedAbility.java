package singularity.world.unit.abilities;

import arc.Events;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import mindustry.entities.abilities.Ability;
import mindustry.game.EventType;
import mindustry.gen.Unit;

public class WarpedAbility extends Ability implements Pool.Poolable {
  public final Seq<Ability> warpedAbilities = new Seq<>(Ability.class);
  public Ability original;

  public static void deploy(){
    // 使用事件监听器替代 EntityAspect
    Events.on(EventType.UnitCreateEvent.class, event -> {
      Unit e = event.unit;
      if (e.abilities.length > 0){
        WarpedAbility wrap = Pools.obtain(WarpedAbility.class, WarpedAbility::new);
        wrap.original = e.abilities[0];
        e.abilities[0] = wrap;
      }
    });
    
    Events.on(EventType.UnitDestroyEvent.class, event -> {
      Unit e = event.unit;
      if (e.abilities.length > 0 && e.abilities[0] instanceof WarpedAbility wrap){
        Pools.free(wrap);
      }
    });
  }

  @Override
  public void update(Unit unit) {
    original.update(unit);
    data = original.data;

    for (Ability ability : warpedAbilities) {
      ability.update(unit);
    }
  }

  @Override
  public void draw(Unit unit) {
    original.draw(unit);
    for (Ability ability : warpedAbilities) {
      ability.draw(unit);
    }
  }

  @Override
  public void death(Unit unit) {
    original.death(unit);
    for (Ability ability : warpedAbilities) {
      ability.death(unit);
    }
  }

  @Override
  public void displayBars(Unit unit, Table bars) {
    original.displayBars(unit, bars);
    for (Ability ability : warpedAbilities) {
      ability.displayBars(unit, bars);
    }
  }

  @Override
  public Ability copy() {
    WarpedAbility res = Pools.obtain(WarpedAbility.class, WarpedAbility::new);
    res.original = original.copy();
    for (int i = 0; i < res.warpedAbilities.items.length; i++) {
      res.warpedAbilities.set(i, warpedAbilities.items[i].copy());
    }

    return res;
  }

  @Override
  public void reset() {
    original = null;
    warpedAbilities.clear();
  }
}

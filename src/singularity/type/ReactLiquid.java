package singularity.type;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.entities.Puddles;
import mindustry.gen.Puddle;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import universecore.util.Empties;

public class ReactLiquid extends Liquid {
  private final ObjectMap<Liquid, Seq<ReactHandler>> reacts = new ObjectMap<>();
  private final Seq<ReactHandler> anyReact = new Seq<>();

  public Runnable init = () -> {};

  public ReactLiquid(String name) {
    super(name);
  }

  public ReactLiquid(String name, Color color) {
    super(name, color);
  }

  public void reactWith(Liquid other, ReactHandler reacting){
    reacts.get(other, Seq::new).add(reacting);
  }

  public void reactAny(ReactHandler reacting){
    anyReact.add(reacting);
  }

  public static ReactHandler effectWith(Effect fx, float delScl){
    return effectWith(fx, 0.16f, delScl);
  }

  public static ReactHandler effectWith(Effect fx, float chance, float delScl){
    return effectWith(fx, chance, Color.white, delScl);
  }

  public static ReactHandler effectWith(Effect fx, float chance, Color color, float delScl){
    return  (liquid, amount, x, y) -> {
      if (Mathf.chanceDelta(chance*Mathf.clamp(amount*2f, 0f, 2f))) fx.at(x, y, color);
      return amount*delScl;
    };
  }

  @Override
  public void init() {
    super.init();
    init.run();
  }

  @Override
  public float react(Liquid other, float amount, Tile tile, float x, float y) {
    float res = 0;
    for (ReactHandler react : reacts.get(other, Empties.nilSeq())) {
      res += react.reactWith(other, amount, x, y);
    }

    for (ReactHandler react : anyReact) {
      res += react.reactWith(other, amount, x, y);
    }

    return res;
  }

  @Override
  public void update(Puddle puddle) {
    Tile tile = puddle.tile;
    for (Point2 p : Geometry.d4) {
      Tile other = Vars.world.tile(tile.x + p.x, tile.y + p.y);
      if (other == null) continue;
      Puddle otherPuddle = Puddles.get(other);
      if (otherPuddle == null) continue;
      
      float x = (puddle.x + otherPuddle.x) / 2f;
      float y = (puddle.y + otherPuddle.y) / 2f;
      
      for(ReactHandler cons: reacts.get(otherPuddle.liquid, Empties.nilSeq())) {
        puddle.amount += cons.reactWith(otherPuddle.liquid, puddle.accepting, x, y);
      }

      for (ReactHandler func : anyReact) {
        puddle.amount += func.reactWith(otherPuddle.liquid, puddle.accepting, x, y);
      }
    }
  }
  
  @FunctionalInterface
  public interface ReactHandler{
    float reactWith(Liquid other, float amount, float x, float y);
  }
}

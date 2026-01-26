package singularity.world.components;

import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.ObjectSet;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.Stats;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public interface EdgeLinkerComp{
  ObjectSet<EdgeLinkerBuildComp> tmpSeq = new ObjectSet<>();
  
  //@Annotations.BindField("linkLength")
  default int linkLength(){
    return 0;
  }

 // @Annotations.BindField("linkOffset")
  default float linkOffset(){
    return 0;
  }
  
 // @Annotations.BindField("linkRegion")
  default TextureRegion linkRegion(){
    return null;
  }

//  @Annotations.BindField("linkCapRegion")
  default TextureRegion linkCapRegion(){
    return null;
  }
  
//  @Annotations.MethodEntry(entryMethod = "drawPlace", paramTypes = {"int -> x", "int -> y", "int -> rotation", "boolean -> valid"})
  default void drawPlacing(int x, int y, int rotation, boolean valid){
    Tmp.v1.set(1, 0);
    for(int i = 0; i < 4; i++){
      float dx = x*tilesize + getBlock().offset + Geometry.d4x(i)*getBlock().size*tilesize/2f;
      float dy = y*tilesize + getBlock().offset + Geometry.d4y(i)*getBlock().size*tilesize/2f;
      
      Drawf.dashLine(
          Pal.accent,
          dx,
          dy,
          dx + Geometry.d4x(i)*linkLength()*tilesize,
          dy + Geometry.d4y(i)*linkLength()*tilesize
      );

      for(int d = 1; d <= linkLength(); d++){
        Tmp.v1.setLength(d);
        Building t = world.build(x + (int) Tmp.v1.x, y + (int) Tmp.v1.y);
        if(t instanceof EdgeLinkerBuildComp e && canLink(world.tile(x, y), this, t.tile, e.getEdgeBlock())){
          Drawf.select(t.x, t.y, t.block.size * tilesize / 2f + 2f + Mathf.absin(Time.time, 4f, 1f), Pal.breakInvalid);
        }
      }
      Tmp.v1.rotate90(1);
    }
  }

 // @Annotations.MethodEntry(entryMethod = "setStats", context = {"stats -> stats"})
  default void setEdgeLinkerStats(Stats stats){
    stats.add(Stat.linkRange, linkLength(), StatUnit.blocks);
  }
  
  default void drawConfiguring(EdgeLinkerBuildComp origin){
    Drawf.square(origin.getBuilding().x, origin.getBuilding().y, origin.getBlock().size*tilesize, Pal.accent);
    if(origin.perEdge() != null){
      Tmp.v2.set(
          Tmp.v1.set(origin.getBuilding().x, origin.getBuilding().y)
              .sub(origin.perEdge().getBuilding().x, origin.perEdge().getBuilding().y)
              .setLength(origin.perEdge().getBlock().size*tilesize/2f))
          .setLength(origin.getBlock().size*tilesize/2f);
      
      Drawf.square(
          origin.perEdge().getBuilding().x,
          origin.perEdge().getBuilding().y,
          origin.perEdge().getBlock().size*tilesize,
          45,
          Pal.place
      );
      Drawf.dashLine(
          Pal.accent,
          origin.perEdge().getBuilding().x + Tmp.v1.x,
          origin.perEdge().getBuilding().y + Tmp.v1.y,
          origin.getBuilding().x - Tmp.v2.x,
          origin.getBuilding().y - Tmp.v2.y
      );
    }
    
    if(origin.nextEdge() != null){
      Tmp.v2.set(
          Tmp.v1.set(origin.nextEdge().getBuilding().x, origin.nextEdge().getBuilding().y)
              .sub(origin.getBuilding().x, origin.getBuilding().y)
              .setLength(origin.getBlock().size*tilesize/2f))
          .setLength(origin.nextEdge().getBlock().size*tilesize/2f);
  
      Drawf.square(
          origin.nextEdge().getBuilding().x,
          origin.nextEdge().getBuilding().y,
          origin.nextEdge().getBlock().size*tilesize,
          45,
          Pal.accent
      );
      Lines.stroke(3, Pal.gray);
      Lines.line(
          origin.getBuilding().x + Tmp.v1.x,
          origin.getBuilding().y + Tmp.v1.y,
          origin.nextEdge().getBuilding().x - Tmp.v2.x,
          origin.nextEdge().getBuilding().y - Tmp.v2.y
      );
      Lines.stroke(1, Pal.accent);
      Lines.line(
          origin.getBuilding().x + Tmp.v1.x,
          origin.getBuilding().y + Tmp.v1.y,
          origin.nextEdge().getBuilding().x - Tmp.v2.x,
          origin.nextEdge().getBuilding().y - Tmp.v2.y
      );
    }
    
    for(EdgeLinkerBuildComp other : getLinkable(origin)){
      if(other == origin.nextEdge() || other == origin.perEdge()) continue;
      Drawf.select(other.getBuilding().x, other.getBuilding().y,
          other.getBlock().size * tilesize / 2f + 2f + Mathf.absin(Time.time, 4f, 1f), Pal.breakInvalid);
    }
  }
  
  default ObjectSet<EdgeLinkerBuildComp> getLinkable(EdgeLinkerBuildComp origin){
    tmpSeq.clear();
    for(int i = 0; i < 4; i++){
      int dx = Geometry.d4x(i);
      int dy = Geometry.d4y(i);
      for(int l = getBlock().size/2; l < linkLength() + getBlock().size/2; l++){
        Tile tile = origin.tile().nearby(dx*l, dy*l);
        if(tile == null) continue;
        Building build = tile.build;
        if(build instanceof EdgeLinkerBuildComp && canLink(origin, (EdgeLinkerBuildComp) build)) tmpSeq.add((EdgeLinkerBuildComp) build);
      }
    }
    
    return tmpSeq;
  }
  
  default Block getBlock(){
    return (Block) this;
  }
  
  default void link(EdgeLinkerBuildComp entity, Integer pos){
    Building build = Vars.world.build(pos);
    
    if(build instanceof EdgeLinkerBuildComp){
      if(entity.nextEdge() == build){
        entity.delink((EdgeLinkerBuildComp) build);
      }
      else entity.link((EdgeLinkerBuildComp) build);
    }
  }

  default boolean canLink(EdgeLinkerBuildComp origin, EdgeLinkerBuildComp other){
    return canLink(origin.tile(), origin.getEdgeBlock(), other.tile(), other.getEdgeBlock());
  }
  
  default boolean canLink(Tile origin, EdgeLinkerComp originBlock, Tile other, EdgeLinkerComp otherBlock){
    if(!originBlock.linkable(otherBlock) || !otherBlock.linkable(this)) return false;

    int xDistance = Math.abs(origin.x - other.x),
        yDistance = Math.abs(origin.y - other.y);

    int linkLength = Math.min(originBlock.linkLength(), otherBlock.linkLength());
    
    return (yDistance < linkLength + getBlock().size/2f + getBlock().offset && origin.x == other.x && origin.y != other.y)
        || (xDistance < linkLength + getBlock().size/2f + getBlock().offset && origin.x != other.x && origin.y == other.y);
  }

  boolean linkable(EdgeLinkerComp other);
}

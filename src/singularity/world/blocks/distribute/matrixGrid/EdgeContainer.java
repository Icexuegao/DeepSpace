package singularity.world.blocks.distribute.matrixGrid;

import arc.math.geom.Polygon;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import mindustry.world.Tile;
import singularity.world.components.EdgeLinkerBuildComp;
import universecore.util.Empties;

public class EdgeContainer{
  private static final ObjectSet<EdgeLinkerBuildComp> flowed = new ObjectSet<>();
  
  public Seq<EdgeLinkerBuildComp> all = new Seq<>();
  
  private Polygon poly;
  private boolean closure;
  
  public boolean inLerp(Tile tile){
    return closure && poly != null && poly.contains(tile.drawx(), tile.drawy());
  }
  
  public void add(EdgeLinkerBuildComp other){
    all.add(other);
    other.setEdges(this);
  }
  
  public void flow(EdgeLinkerBuildComp source){
    flow(source, Empties.nilSeq());
  }
  
  public void flow(EdgeLinkerBuildComp source, Seq<EdgeLinkerBuildComp> exclude){
    flowed.clear();
    EdgeLinkerBuildComp curr = source;
    Seq<EdgeLinkerBuildComp> temp = new Seq<>();
    while(curr != null && flowed.add(curr) && !exclude.contains(curr)){
      temp.add(curr);
      curr = curr.perEdge();
    }
    for(int i = temp.size - 1; i >= 0; i--){
      add(temp.get(i));
    }
    if(curr != null && !exclude.contains(curr)){
      closure = true;
      updatePoly();

      for(EdgeLinkerBuildComp edge: all){
        edge.edgeUpdated();
      }
      return;
    }
    
    curr = source;
    while(curr != null && flowed.add(curr) && !exclude.contains(curr)){
      add(curr);
      curr = curr.nextEdge();
    }
    poly = null;
    closure = false;

    for(EdgeLinkerBuildComp edge: all){
      edge.edgeUpdated();
    }
  }
  
  public boolean isClosure(){
    return closure;
  }
  
  public Polygon getPoly(){
    return poly;
  }
  
  private void updatePoly(){
    float[] vertexArr = new float[all.size*2];
    for(int i = 0; i < all.size; i++){
      vertexArr[i*2] = all.get(i).getBuilding().x();
      vertexArr[i*2 + 1] = all.get(i).getBuilding().y();
    }
    poly = new Polygon(vertexArr);
  }
  
  public void remove(EdgeLinkerBuildComp remove){
    if(remove.nextEdge() != null){
      new EdgeContainer().flow(remove.nextEdge(), Seq.with(remove));
    }
    if(remove.perEdge() != null && remove.getEdges() == this){
      new EdgeContainer().flow(remove.perEdge(), Seq.with(remove));
    }
  }
}

package singularity.world.blocks.defence;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.*;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import universecore.math.gravity.GravityField;
import universecore.util.path.BFSPathFinder;
import universecore.util.path.IPath;
import universecore.util.path.PathFindFunc;

import static mindustry.Vars.tilesize;


public class SglWall extends Wall{
  /**一格方块的质量，mass不指定的情况下用size计算质量，设为0禁用*/
  public float density = 0;
  public float mass = 0;
  
  public float damageFilter = -1;
  public float healMultiplier = 1f/3;
  public Effect absorbEffect = Fx.healBlockFull;
  public Color absorbEffColor = Color.valueOf("84f491");

  public SglWall(String name){
    super(name);
    update = true;
  }

  @Override
  public void init(){
    super.init();
    if(mass == 0 && density != 0){
      mass = size*size*density;
    }
  }

  public class SglWallBuild extends WallBuild{
    protected GravityGroup gravGroup;
    private final OrderedSet<SglWallBuild> proximityGrav = new OrderedSet<>();



    @Override
    public Building init(Tile tile, Team team, boolean shouldAdd, int rotation){
      super.init(tile, team, shouldAdd, rotation);
      if(mass != 0) new GravityGroup().add(this);

      return this;
    }

    @Override
    public void onProximityAdded(){
      super.onProximityAdded();

      proximityGrav.clear();
      for(Building building: proximity){
        if(building instanceof SglWallBuild w && w.gravGroup != null){
          proximityGrav.add(w);
        }
      }

      if(gravGroup != null){
        for(SglWallBuild build: proximityGrav){
          gravGroup.add(build.gravGroup);
        }
        gravGroup.clip(16);
      }
    }

    @Override
    public void onRemoved(){
      if(gravGroup != null) gravGroup.remove(this);
    }

    @Override
    public void updateTile(){
      super.updateTile();

      if(gravGroup != null) gravGroup.update();

      float rad = size*tilesize/1.44f;
      for(Bullet bullet: Groups.bullet.intersect(x - rad/2, y - rad/2, rad, rad)){
        if(bullet.team != team && !bullet.type.collides && bullet.type.absorbable && bullet.damage <= damageFilter){
          absorbed(bullet);
        }
      }
    }

    public boolean gravitable(Bullet e){
      return e.team != team && (e.type.collides || e.type.absorbable || e.type.hittable);
    }

    @Override
    public boolean collision(Bullet bullet){
      if(bullet.type.absorbable && bullet.damage < damageFilter){
        absorbed(bullet);

        return true;
      }
      return super.collision(bullet);
    }

    public void absorbed(Bullet bullet){
      heal(bullet.damage*bullet.type.buildingDamageMultiplier*healMultiplier);
      absorbEffect.at(x, y, 0, absorbEffColor, block);

      bullet.remove();
    }

    public float mass(){
      return mass;
    }
  }

  protected static class GravityGroup implements BFSPathFinder<SglWallBuild>{
    private static final ObjectSet<SglWallBuild> added = new ObjectSet<>();
    private static final Queue<SglWallBuild> queue = new Queue<>();

    private static final IntMap<OrderedSet<SglWallBuild>> clips = new IntMap<>();

    OrderedSet<SglWallBuild> child = new OrderedSet<>();
    Seq<ClipGravitySystem> childGroup = new Seq<>();

    long frameID;

    public void clip(int clipStep){
      clips.clear();

      for(SglWallBuild build: child){
        int pos = Point2.pack(build.tileX()/clipStep, build.tileY()/clipStep);
        OrderedSet<SglWallBuild> clip = clips.get(pos, OrderedSet::new);
        clip.add(build);
      }

      for(ClipGravitySystem clip: childGroup){
        Pools.free(clip);
      }
      childGroup.clear();

      added.clear();

      ClipGravitySystem clip;
      for(OrderedSet<SglWallBuild> value: clips.values()){
        for(SglWallBuild build: value){
          if(added.contains(build)) break;
          clip = ClipGravitySystem.make();

          queue.clear();

          queue.addFirst(build);
          added.add(build);
          clip.add(build);

          while(!queue.isEmpty()){
            for(SglWallBuild vertex: queue.removeFirst().proximityGrav){
              if(!value.contains(vertex)) continue;

              if(added.add(vertex)){
                queue.addFirst(vertex);
                clip.add(vertex);
              }
            }
          }

          childGroup.add(clip);
        }
      }
    }

    public void update(){
      if(frameID == Core.graphics.getFrameId()) return;
      frameID = Core.graphics.getFrameId();

      for(ClipGravitySystem system: childGroup){
        system.update();
      }
    }

    public void add(GravityGroup other){
      if(other == this) return;

      for(SglWallBuild build: other.child){
        add(build);
      }
    }

    public void add(SglWallBuild build){
      child.add(build);
      build.gravGroup = this;
    }

    public void remove(SglWallBuild build){
      for(SglWallBuild other: build.proximityGrav){
        if(other.gravGroup != this) continue;
        other.proximityGrav.remove(build);

        new GravityGroup(){{
          eachVertices(other, this::add);
        }};
        other.gravGroup.clip(16);
      }
    }

    @Override
    public void reset(){
      added.clear();
      queue.clear();
    }

    @Override
    public boolean relateToPointer(SglWallBuild sglWallBuild, PathPointer<SglWallBuild> pathPointer){
      return added.add(sglWallBuild);
    }

    @Override
    public PathPointer<SglWallBuild> getPointer(SglWallBuild sglWallBuild){
      return null;
    }

    @Override
    public SglWallBuild queueNext(){
      return queue.isEmpty()? null: queue.removeFirst();
    }

    @Override
    public void queueAdd(SglWallBuild sglWallBuild){
      queue.addFirst(sglWallBuild);
    }

    @Override
    public IPath<SglWallBuild> createPath(){
      return null;
    }

    @Override
    public Iterable<SglWallBuild> getLinkVertices(SglWallBuild sglWallBuild){
      return sglWallBuild.proximityGrav;
    }

    @Override
    public boolean isDestination(SglWallBuild sglWallBuild, SglWallBuild vert1){
      return false;
    }

    @Override
    public void findPath(SglWallBuild origin, PathFindFunc.PathAcceptor<SglWallBuild> pathConsumer){
      throw new UnsupportedOperationException();
    }
  }

  protected static class ClipGravitySystem implements Pool.Poolable{
    float mass;
    float argX, argY;
    Vec2 position = new Vec2();

    SglWallBuild mark;

    public static ClipGravitySystem make(){
      return Pools.obtain(ClipGravitySystem.class, ClipGravitySystem::new);
    }

    public void add(SglWallBuild build){
      if(mark == null) mark = build;

      mass += build.mass();
      argX += build.mass()*build.x;
      argY += build.mass()*build.y;

      position.set(argX/mass, argY/mass);
    }

    public void update(){
      for(Bullet bullet: Groups.bullet){
        if(mark != null && mark.gravitable(bullet)){
          Tmp.v1.set(position)
              .sub(bullet.x, bullet.y)
              .setLength(Math.min(64/(bullet.damage + bullet.type.splashDamage), 2.5f)*mass/(Mathf.pow(Tmp.v1.len(), 2)*GravityField.GRAV_CONST))
              .clamp(0, 32)
              .scl(Time.delta/60);

          bullet.vel.add(Tmp.v1);
        }
      }
    }

    @Override
    public void reset(){
      mass = 0;
      position.setZero();
      argX = argY = 0;
      mark = null;
    }
  }
}

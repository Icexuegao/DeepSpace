package universecore.world.lightnings.generator;

import arc.func.Cons;
import arc.func.Func2;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import universecore.util.funcs.Floatp2;
import universecore.world.lightnings.Lightning;
import universecore.world.lightnings.LightningVertex;

import java.util.Iterator;

/**闪电生成器基类，同时实现了Iterator和Iterable接口，可以使用for-each循环形式逐个产生顶点，每一次获取迭代器都将返回生成器自身，并重置迭代状态
 * 注意，任何在迭代器运作外的时机变更生成器属性，都会直接影响迭代产生的顶点分布情况，而生成器是可复用的，每次迭代都会产生互不相关的一组顶点
 * <p>警告：这个方法不是线程安全的，任何时候要避免同时迭代此对象
 *
 * @since 1.5
 * @author EBwilson
 * 移动到图形模块中*/

public abstract class LightningGenerator implements Iterable<LightningVertex>, Iterator<LightningVertex>{
  public Rand seed = new Rand();

  /**顶点基准间距最小值*/
  public float minInterval = 6;
  /**顶点基准位置最大值*/
  public float maxInterval = 18;

  /**闪电顶点离散程度，越高则顶点偏移越远*/
  public float maxSpread = 12.25f;

  /**产生分支的几率（每一个顶点）*/
  public float branchChance;
  /**最小分支强度*/
  public float minBranchStrength = 0.3f;
  /**最大分支强度*/
  public float maxBranchStrength = 0.8f;
  /**分支创建器，传入分支所在的顶点以及分支的强度，需要返回一个闪电生成器，注意，任何生成器对象都可以被传入，请不要new创建生成器*/
  public Func2<LightningVertex, Float, LightningGenerator> branchMaker;

  public Cons<Lightning> branchCreated;
  public Floatp2<LightningVertex, LightningVertex> blockNow;

  protected Lightning curr;

  protected LightningVertex last;
  protected boolean isEnding;

  private float offsetX, offsetY;

  public static final Pool<LightningVertex> vertexPool;

  static {
    Pools.set(LightningVertex.class, vertexPool = new Pool<>(8192, 65536){
      @Override
      protected LightningVertex newObject(){
        return new LightningVertex();
      }
    });
  }

  public void setCurrentGen(Lightning curr){
    this.curr = curr;
  }

  public void branched(Cons<Lightning> branchCreated){
    this.branchCreated = branchCreated;
  }

  /**使用当前的分支生成器对顶点创建一条分支闪电*/
  public void createBranch(LightningVertex vertex){
    float strength = Mathf.clamp(Mathf.random(minBranchStrength, maxBranchStrength));
    LightningGenerator gen = branchMaker.get(vertex, strength);
    gen.setOffset(vertex.x, vertex.y);
    Floatp2<LightningVertex, LightningVertex> old = gen.blockNow;
    gen.blockNow = (l, v) -> old != null? old.get(l, v): blockNow != null? blockNow.get(l, v): -1;
    vertex.branchOther = Lightning.create(
        gen,
        curr.width*strength,
        curr.lifeTime,
        curr.fadeTime,
        curr.lerp,
        curr.time,
        curr.fade,
        curr.backFade,
        curr.trigger
    );
    gen.blockNow = old;
    gen.resetOffset();

    vertex.branchOther.vertices.first().isStart = false;

    if(branchCreated != null) branchCreated.get(vertex.branchOther);
  }

  /**此类同时实现了可迭代和迭代器接口，即可以进行for-each循环来逐个产生顶点，这个方法不是线程安全的*/
  @Override
  public synchronized Iterator<LightningVertex> iterator(){
    reset();
    return this;
  }

  public void reset(){
    last = null;
    isEnding = false;
  }

  /**迭代器通过这个方法获取下一个顶点*/
  @Override
  public LightningVertex next(){
    LightningVertex vertex = Pools.obtain(LightningVertex.class, null);
    handleVertex(vertex);
    offsetVertex(vertex);
    afterHandle(vertex);

    float blockLen;
    if(blockNow != null && last != null && (blockLen = blockNow.get(last, vertex)) > 0){
      isEnding = true;
      vertex.isEnd = true;
      float angle = Mathf.angle(vertex.x - last.x, vertex.y - last.y);
      vertex.x = last.x + Angles.trnsx(angle, blockLen);
      vertex.y = last.y + Angles.trnsy(angle, blockLen);
      offsetVertex(vertex);
      afterHandle(vertex);
      return vertex;
    }

    if(!vertex.isStart && !vertex.isEnd && branchChance > 0 && Mathf.chance(branchChance)){
      createBranch(vertex);
    }
    last = vertex;
    return vertex;
  }

  @Override
  public boolean hasNext(){
    return !isEnding;
  }

  /**在顶点处理之后调用*/
  public void afterHandle(LightningVertex vertex){
    if(last == null) return;
    vertex.angle = Mathf.angle(vertex.x - last.x, vertex.y - last.y);
  }

  public void offsetVertex(LightningVertex vertex){
    vertex.x += offsetX;
    vertex.y += offsetY;
  }

  public void setOffset(float dx, float dy){
    offsetX = dx;
    offsetY = dy;
  }

  public void resetOffset(){
    offsetY = offsetX = 0;
  }

  public boolean isEnclosed(){
    return false;
  }

  /**顶点处理，实现以为顶点分配属性，如坐标等*/
  protected abstract void handleVertex(LightningVertex vertex);

  /**返回当前闪电的裁剪大小，此大小应当能够完整绘制闪电*/
  public abstract float clipSize();
}

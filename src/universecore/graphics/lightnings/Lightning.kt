package universecore.graphics.lightnings;

import arc.func.Cons2;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import mindustry.graphics.Drawf;
import universecore.graphics.lightnings.generator.LightningGenerator;

/**
 * 单条闪电的存储容器，保存了闪电的起始时间还有闪电的顶点信息
 * 此类实例大量，应当复用
 *
 * @since 2.3
 * @author EBwilson
 */
public class Lightning implements Pool.Poolable{
  private static final Vec2 last = new Vec2(), self = new Vec2(), next = new Vec2();

  public final Seq<LightningVertex> vertices = new Seq<>();
  /**闪电的持续时间*/
  public float lifeTime;
  /**闪电消逝的过渡时间*/
  public float fadeTime;
  /**闪电是否随淡出过程从起点开始消失*/
  public boolean backFade = false;
  /**闪电整体的宽度是否随闪电的持续时间淡出*/
  public boolean fade = true;
  /**闪电被创建时的时间*/
  public float startTime;
  /**这道闪电的剪切尺寸，用于绘制时的画面裁切*/
  public float clipSize;
  /**闪电的宽度*/
  public float width;

  /**闪电的宽度插值函数*/
  public Interp lerp = Interp.linear;
  /**闪电的每一段的触发器，在任意一段闪电的部分生成完成时会各自调用一次，传入当前顶点和前一个顶点*/
  public Cons2<LightningVertex, LightningVertex> trigger;

  /**闪电由产生到完全显现的时间*/
  public float time;
  public float counter, lengthMargin;

  public boolean headClose, endClose;

  float totalLength;

  int cursor;

  boolean enclosed;

  public static Lightning create(LightningGenerator generator, float width, float lifeTime, Interp lerp, float time, Cons2<LightningVertex, LightningVertex> trigger){
    return create(generator, width, lifeTime, lifeTime, lerp, time, true, false, trigger);
  }

  public static Lightning create(LightningGenerator generator, float width, float lifeTime, float fadeTime, Interp lerp, float time, boolean fade, boolean backFade, Cons2<LightningVertex, LightningVertex> trigger){
    Lightning result = Pools.obtain(Lightning.class, Lightning::new);
    result.width = width;
    result.time = time;
    result.startTime = Time.time;
    result.lifeTime = lifeTime;
    result.fadeTime = fadeTime;
    result.lerp = lerp;
    result.fade = fade;
    result.backFade = backFade;
    result.trigger = trigger;

    generator.setCurrentGen(result);

    LightningVertex last = null;
    for(LightningVertex vertex: generator){
      result.vertices.add(vertex);
      if(last != null){
        result.totalLength += Mathf.len(vertex.x - last.x, vertex.y - last.y);
      }
      last = vertex;
    }
    result.enclosed = generator.isEnclosed();
    result.clipSize = generator.clipSize();

    return result;
  }
  
  private Lightning(){}

  /**更新一次闪电状态*/
  public void update(){
    if(time == 0 && cursor < vertices.size){
      LightningVertex per = null;
      for(LightningVertex vertex: vertices){
        if(per != null){
          per.progress = 1;
          vertex.valid = true;
          if(trigger != null) trigger.get(per, vertex);
        }
        per = vertex;
      }
      cursor = vertices.size;
    }
    else{
      float increase = vertices.size/time*Time.delta;

      while(increase > 0){
        if(cursor == 0){
          cursor++;
        }

        if(cursor >= vertices.size) break;

        LightningVertex per = vertices.get(cursor - 1), curr = vertices.get(cursor);
        float delta = Math.min(increase, 1 - per.progress);
        per.progress += delta;
        increase -= delta;

        if(per.progress >= 1){
          curr.valid = true;
          if(trigger != null) trigger.get(per, curr);
          cursor++;
        }
      }
    }

    for(LightningVertex vertex: vertices){
      if(!vertex.isEnd && !vertex.isStart && vertex.valid) vertex.update();
    }
  }

  /**绘制这道闪电
   *
   * @param x 绘制闪电的原点x坐标
   * @param y 绘制闪电的原点y坐标
   * */
  @SuppressWarnings("DuplicatedCode")
  public void draw(float x, float y){
    float lerp = Mathf.clamp(this.lerp.apply(Mathf.clamp((lifeTime - (Time.time - startTime))/fadeTime)));
    float del = backFade? (1 - lerp)*vertices.size: 0;

    if (!fade) lerp = 1;

    for(int i = 2; i <= vertices.size; i++){
      LightningVertex v1 = i - 3 >= 0? vertices.get(i - 3): enclosed? vertices.get(Mathf.mod(i - 3, vertices.size)): null,
          v2 = vertices.get(i - 2),
          v3 = vertices.get(i - 1),
          v4 = i < vertices.size? vertices.get(i): enclosed? vertices.get(Mathf.mod(i, vertices.size)): null;

      float lastOffX, lastOffY;
      float nextOffX, nextOffY;

      float fade = Math.min(del, 1);
      del -= fade;
      if(!v2.valid) break;

      self.set(v3.x, v3.y).sub(v2.x, v2.y);

      if(v1 != null){
        last.set(v2.x, v2.y).sub(v1.x, v1.y);

        float aveAngle = (last.angle() + self.angle())/2;
        float off = width/2*lerp/Mathf.cosDeg(aveAngle - last.angle());

        lastOffX = Angles.trnsx(aveAngle + 90, off);
        lastOffY = Angles.trnsy(aveAngle + 90, off);
      }
      else{
        Tmp.v1.set(self).rotate90(1).setLength(width/2*lerp);
        lastOffX = Tmp.v1.x;
        lastOffY = Tmp.v1.y;
      }

      if(v4 != null){
        next.set(v4.x, v4.y).sub(v3.x, v3.y);
        float aveAngle = (self.angle() + next.angle())/2;
        float off = width/2*lerp/Mathf.cosDeg(aveAngle - self.angle());

        nextOffX = Angles.trnsx(aveAngle + 90, off);
        nextOffY = Angles.trnsy(aveAngle + 90, off);
      }
      else{
        Tmp.v1.set(self).rotate90(1).setLength(width/2*lerp);
        nextOffX = Tmp.v1.x;
        nextOffY = Tmp.v1.y;
      }

      lastOffX *= lerp;
      lastOffY *= lerp;
      nextOffX *= lerp;
      nextOffY *= lerp;

      float orgX = x + v2.x, orgY = y + v2.y;
      float fadX = Tmp.v1.x*fade, fadY = Tmp.v1.y*fade;

      Tmp.v1.set(self).scl(v2.progress);
      if((v2.isStart && !headClose) || (v3.isEnd && !endClose)){
        float l = v2.isStart? v2.progress: 1 - v2.progress;
        float f = v2.isStart? fade: 1 - fade;
        Fill.quad(
            orgX + fadX + lastOffX*f,
            orgY + fadY + lastOffY*f,
            orgX + fadX - lastOffX*f,
            orgY + fadY - lastOffY*f,
            orgX + Tmp.v1.x - nextOffX*l,
            orgY + Tmp.v1.y - nextOffY*l,
            orgX + Tmp.v1.x + nextOffX*l,
            orgY + Tmp.v1.y + nextOffY*l
        );
      }
      else{
        Fill.quad(
            orgX + fadX + lastOffX,
            orgY + fadY + lastOffY,
            orgX + fadX - lastOffX,
            orgY + fadY - lastOffY,
            orgX + Tmp.v1.x - nextOffX,
            orgY + Tmp.v1.y - nextOffY,
            orgX + Tmp.v1.x + nextOffX,
            orgY + Tmp.v1.y + nextOffY
        );
      }

      Drawf.light(
          orgX, orgY,
          orgX + Tmp.v1.x, orgY + Tmp.v1.y,
          width*32,
          Draw.getColor(),
          Draw.getColor().a
      );

      v2.draw(x, y);
    }
  }

  @Override
  public void reset(){
    for(LightningVertex vertex: vertices){
      Pools.free(vertex);
    }
    vertices.clear();
    counter = 0;
    width = 0;
    time = 0;
    cursor = 0;
    lifeTime = 0;
    enclosed = false;
    lerp = null;
    lengthMargin = 0;
    startTime = 0;
    clipSize = 0;
    trigger = null;
  }
}

package universecore.world.lightnings;

import arc.util.pooling.Pool;
import arc.util.pooling.Pools;

/**闪电的顶点容器，保存了一个顶点的必要信息和绘制进度计时器
 * 此类实例大量，应当复用
 *
 * @since 1.5
 * @author EBwilson
 *  移动到图形模块中*/
public class LightningVertex implements Pool.Poolable{
  public float x, y;
  public float angle;

  public boolean isStart;
  public boolean isEnd;

  public boolean valid;
  public float progress;

  public Lightning branchOther;

  protected void draw(float x, float y){
    if(branchOther != null) branchOther.draw(x, y);
  }

  public void update(){
    if(branchOther != null) branchOther.update();
  }

  @Override
  public void reset(){
    if(branchOther != null) Pools.free(branchOther);

    valid = false;
    progress = 0;
    x = y = 0;
    angle = 0;
    branchOther = null;
    isStart = false;
    isEnd = false;
  }
}

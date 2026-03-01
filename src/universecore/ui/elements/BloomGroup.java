package universecore.ui.elements;

import arc.Core;
import arc.graphics.g2d.Bloom;
import arc.scene.Group;
import arc.scene.ui.layout.Table;

import static arc.Core.settings;

/**泛光特效容器，位于此容器内的元素绘制会经过泛光过滤处理，即赋予光效效果，泛光参数默认来自游戏设置。
 *
 * <p>该组件与{@link arc.graphics.g2d.ScissorStack}或{@link arc.graphics.Gl#scissor(int, int, int, int)}相冲突，
 * 即此容器不应存放在任何会进行裁切的父级当中，例如{@link arc.scene.ui.ScrollPane}等。
 * 但是此容器内部支持进行裁切，应当将此容器覆盖于裁切元素上方，并将此容器的clip设为true*/
public class BloomGroup extends Group {
  private final Bloom bloom = new Bloom(true);
  private boolean clip = false;

  public boolean bloomEnabled = settings.getBool("bloom", true);
  public float bloomIntensity = settings.getInt("bloomintensity", 6) / 4f + 1f;
  public int bloomBlur = settings.getInt("bloomblur", 1);

  /**@see Table#getClip()*/
  public boolean getClip(){
    return clip;
  }

  /**@see Table#setClip(boolean)*/
  public void setClip(boolean clip){
    this.clip = clip;
  }

  @Override
  protected void drawChildren() {
    if (bloomEnabled){
      bloom.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
      bloom.setBloomIntensity(bloomIntensity);
      bloom.blurPasses = bloomBlur;

      bloom.capture();
    }

    if (clip){
      boolean applied = clipBegin();
      super.drawChildren();
      if (applied) clipEnd();
    }
    else super.drawChildren();

    if (bloomEnabled) bloom.render();
  }
}

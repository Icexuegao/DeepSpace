package universecore.ui.elements.chart;

import arc.func.Floatf;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.Element;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Nullable;
import arc.util.pooling.Pools;
import mindustry.ui.Fonts;

import java.util.Arrays;
import java.util.Iterator;

/**统计图的基类，默认绘制一个基本的网格，统计表包含一系列的数据集，对各组数据的呈现和绘制需要子类实现*/
public abstract class Chart<Stat extends Chart.StatGroup> extends Element{
  private static final int[] STEP_BASE = {2, 5, 10};

  private final int maxValuesCount;

  protected Seq<Stat> data = new Seq<>();

  protected int viewLength;
  protected int viewOffset;

  protected float originX, originY;
  protected float verticalLength, horizonLength;
  protected float padTop = 0, padBottom = 0, padLeft = 0,  padRight = 0;

  public float valueBound;
  public float valueStep;

  public Func<Integer, String> horizonScaleMapping;

  public boolean displayValuesScale = true;
  public float lerpTime = 1;

  @Nullable public String horizontal, vertical;

  public int valueSteps = 10;

  public float axisStroke = 5;

  public float maxValue, minValue;

  public Chart(int maxValuesCount){
    this.maxValuesCount = maxValuesCount;
    this.viewLength = maxValuesCount;
  }

  public Chart(int maxValuesCount, int defaultViewLength){
    this.maxValuesCount = maxValuesCount;
    this.viewLength = defaultViewLength;
  }

  public void setHorizontal(String horizontal){
    this.horizontal = horizontal;
  }

  public void setVertical(String vertical){
    this.vertical = vertical;
  }

  public void updateValueBound(){
    for(Stat datum: data){
      if(!datum.show) continue;
      for(float value: datum.values){
        maxValue = Float.isNaN(value)? maxValue: Math.max(maxValue, value);
        minValue = Float.isNaN(value)? minValue: Math.min(minValue, value);
      }
    }
  }

  public Stat newStatGroup(){
    Stat group = (Stat) new StatGroup();
    group.color = new Color(1, 1, 1, 1);

    data.add(group);
    return group;
  }

  public void remove(Stat group){
    data.remove(group);
  }

  public void clear(){
    data.clear();
  }

  @Override
  public void act(float delta){
    super.act(delta);
    lerpTime = Mathf.clamp(lerpTime);
    for(Stat group: data){
      group.update();
    }
  }

  @Override
  public void draw(){
    super.draw();
    Draw.alpha(parentAlpha);

    padTop = padBottom = padLeft = padRight = 0;

    Font font = Fonts.outline;
    Color fontC = font.getColor();
    font.setColor(Draw.getColor());
    GlyphLayout lay = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
    if(horizontal != null){
      lay.setText(font, horizontal);

      if(horizonScaleMapping != null){
        padBottom = lay.height/2;
        padRight = lay.width*2 + 1;
      }
      else{
        padBottom = lay.height;
        font.draw(horizontal, x + width - lay.width, y + lay.height);
      }
    }
    if(vertical != null){
      lay.setText(font, vertical);
      if(displayValuesScale){
        padLeft = lay.width/2;
        padTop = lay.height*2f + 1;
      }
      else{
        padLeft = lay.width;
        font.draw(vertical, x + lay.width, y + height - lay.height/2);
      }
    }

    valueBound = calculateValueBound(maxValue - minValue);
    valueStep = valueBound/valueSteps;

    if(valueStep == 0) return;

    maxValue = (float) (Math.ceil(maxValue/valueStep)*valueStep);
    minValue = (float) (Math.floor(minValue/valueStep)*valueStep);

    valueBound = maxValue - minValue;//再次确认边界值，尽管通常不会有变动

    float valuesWidth = 0, horHeight = 0;
    if(displayValuesScale){
      for(int i = 0; i <= valueSteps; i++){
        lay.setText(font, Float.toString(i*valueStep));
        valuesWidth = Math.max(lay.width, valuesWidth);
      }
      padLeft = Math.max(padLeft, valuesWidth + 6);

      padBottom += lay.height + 2;
    }
    horizonLength = width - padLeft - padRight;
    originX = x + padLeft;
    if(displayValuesScale && vertical != null){
      lay.setText(font, vertical);
      font.draw(vertical, originX, y + height - lay.height/2);
    }

    if(horizonScaleMapping != null){
      float horInterval = horizonLength/viewLength;
      for(int i = viewOffset, end = viewOffset + viewLength; i < end; i++){
        String str = horizonScaleMapping.get(i);
        lay.setText(font, str, font.getColor(), horInterval - 6, Align.center, true);
        horHeight = Math.max(horHeight, lay.height);
      }
      padBottom = Math.max(padBottom, horHeight + 4);
    }
    verticalLength = height - padBottom - padTop;
    originY = y + padBottom;
    if(horizonScaleMapping != null && horizontal != null){
      lay.setText(font, horizontal);
      font.draw(horizontal, x + width - lay.width/2, originY);
    }

    float offset = 0;
    if(minValue < 0 && maxValue > 0){
      offset = (-minValue)/valueBound*verticalLength;
    }
    else if(maxValue < 0){
      offset = verticalLength;
    }
    originY += offset;

    drawAsis();

    drawValueScale(valuesWidth);
    drawHorizonScale(horHeight);

    Pools.free(lay);
    font.setColor(fontC);
  }

  protected int calculateValueBound(float value){
    value = Math.abs(value);

    int max = 1;
    while(max < value){
      for(int base: STEP_BASE){
        if(max*base > value){
          return max*base;
        }
      }

      max *= 10;
    }
    return max;
  }

  public void drawAsis(){
    float dx = x + padLeft, dy = y + padBottom;

    Lines.stroke(axisStroke, Color.lightGray);
    Draw.alpha(parentAlpha);
    Lines.line(dx, dy, dx + horizonLength, dy);
    Lines.line(dx, dy, dx, dy + verticalLength);

    Draw.color(Color.darkGray);
    Draw.alpha(parentAlpha);
    if(valueStep < 1) return;
    for(float i = minValue; i <= maxValue; i += valueStep){
      float subAxisY = originY + verticalLength/valueBound*i;
      if(Math.abs(subAxisY - dy) < 0.001f) continue;
      Lines.line(dx, subAxisY, dx + horizonLength, subAxisY);
    }
  }

  protected abstract void drawValueScale(float valuesWidth);

  protected abstract void drawHorizonScale(float horHeight);

  public class StatGroup{
    protected final float[] values;
    private int cursor;

    protected boolean show = true;

    protected final float[] displayValues;

    public Color color;

    public StatGroup(){
      this.values = new float[maxValuesCount];
      displayValues = new float[maxValuesCount];
      Arrays.fill(values, Float.NaN);
      Arrays.fill(displayValues, Float.NaN);
    }

    public boolean isShown(){
      return show;
    }

    public void show(){
      show = true;
    }

    public void hide(){
      show = false;
    }

    public void update(){
      for(int i = 0; i < values.length; i++){
        if(Float.isNaN(displayValues[i]) && !Float.isNaN(values[i])) displayValues[i] = values[i];
        displayValues[i] = lerpTime >= 1? values[i]: Mathf.lerpDelta(displayValues[i], values[i], lerpTime);
      }
    }

    public void setValues(Iterable<Float> values){
      Iterator<Float> itr = values.iterator();
      int i = 0;
      while(itr.hasNext() && i < this.values.length){
        this.values[i] = itr.next();
      }
    }

    public <T> void setValues(Iterable<T> sources, Floatf<T> func){
      Iterator<T> itr = sources.iterator();
      int i = 0;
      while(itr.hasNext() && i < values.length){
        this.values[i] = func.get(itr.next());
      }
    }

    public void back(){
      System.arraycopy(values, 1, values, 0, values.length - 1);
      syncDisplay();
    }

    public void putValue(float value){
      if(cursor > values.length - 1){
        System.arraycopy(values, 1, values, 0, values.length - 1);
        values[cursor - 1] = value;
      }
      else{
        values[cursor++] = value;
      }
    }

    public void insertValue(int index, float value){
      checkIndex(index);

      if(index == values.length - 1){
        values[index] = value;
        return;
      }
      System.arraycopy(values, index, values, index + 1, values.length - index);
      values[index] = value;
    }

    public void setValue(int index, float value){
      checkIndex(index);
      values[index] = value;
      if(displayValues[index] == -1) displayValues[index] = 0;
    }

    public void clear(){
      Arrays.fill(values, Float.NaN);
      Arrays.fill(displayValues, Float.NaN);
    }

    public void syncDisplay(){
      System.arraycopy(values, 0, displayValues, 0, values.length);
    }

    private void checkIndex(int index){
      if(index > values.length || index < 0) throw new IndexOutOfBoundsException("index must be >= 0 and < " + values.length + ", given: " + index);
    }
  }
}

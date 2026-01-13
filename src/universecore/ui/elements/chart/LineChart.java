package universecore.ui.elements.chart;

import arc.func.Floatc2;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.util.Align;
import mindustry.ui.Fonts;

/**折线统计图实现，按折线方式绘制统计数据呈现*/
public class LineChart extends Chart<LineChart.LineChartStat>{
  public float lineStroke = 5;

  public boolean displayScale = true;

  public LineChart(int maxValuesCount){
    super(maxValuesCount);
  }

  public LineChart(int maxValuesCount, int defaultViewLength){
    super(maxValuesCount, defaultViewLength);
  }

  @Override
  public LineChartStat newStatGroup(){
    LineChartStat group = new LineChartStat();
    group.color = new Color(1, 1, 1, 1);

    data.add(group);
    return group;
  }

  @Override
  public void draw(){
    super.draw();

    for(LineChartStat group: data){
      if(!group.show) continue;
      Lines.stroke(lineStroke, group.color);
      Draw.alpha(parentAlpha);
      Lines.beginLine();

      float horInterval = horizonLength/viewLength;
      float heightStep = verticalLength/valueBound;

      for(int i = viewOffset, end = viewOffset + viewLength; i < end; i++){
        if(Float.isNaN(group.displayValues[i])) continue;
        Lines.linePoint(originX + horInterval*i, originY + group.displayValues[i]*heightStep);
        if(group.drawPoint != null) group.drawPoint.get(
            originX + horInterval*i,
            originY + group.displayValues[i]*heightStep
        );
      }
      Lines.endLine();
    }
  }

  @Override
  protected void drawValueScale(float valuesWidth){
    Draw.alpha(parentAlpha);
    if(displayValuesScale){
      float heightStep = verticalLength/valueBound;
      if(valueStep < 1) return;
      for(int i = (int) minValue; i <= maxValue; i += valueStep){
        Fonts.outline.draw(Integer.toString(i), x + valuesWidth, originY + i*heightStep, Align.right);
      }
    }
  }

  @Override
  protected void drawHorizonScale(float horHeight){
    float horInterval = horizonLength/viewLength;

    for(int i = 0, end = viewOffset + viewLength; i < end; i++){
      if(displayScale){
        float scaleX = originX + horInterval*i;
        Draw.color(Color.lightGray);
        Draw.alpha(parentAlpha);
        Lines.line(scaleX, y + padBottom, scaleX, y + padBottom + 2*axisStroke);
      }
      if(horizonScaleMapping != null) Fonts.outline.draw(horizonScaleMapping.get(i), originX + horInterval*i, y + horHeight/2, Align.right);
    }
  }

  public class LineChartStat extends StatGroup{
    public Floatc2 drawPoint;
  }
}

package universecore.ui.elements.chart;

public class ColumnChart extends Chart{//TODO: 完成这个
  public ColumnChart(int maxValuesCount){
    super(maxValuesCount);
  }

  public ColumnChart(int maxValuesCount, int defaultViewLength){
    super(maxValuesCount, defaultViewLength);
  }

  @Override
  public void draw(){
    super.draw();

  }

  @Override
  protected void drawValueScale(float valuesWidth){

  }

  @Override
  protected void drawHorizonScale(float horHeight){

  }
}

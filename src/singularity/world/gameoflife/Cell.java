package singularity.world.gameoflife;

import arc.struct.Seq;

public class Cell{
  public Seq<Cell> neighbour = new Seq<>();
  public int years;

  public boolean isLife;

  int cellsCount;

  public final int x, y;

  public Cell(int x, int y){
    this.x = x;
    this.y = y;
  }

  public void kill(){
    isLife = false;
  }

  public void born(){
    years = 0;
    isLife = true;
  }

  public void countFlush(){
    cellsCount = 0;
    for(Cell cell: neighbour){
      if(cell.isLife) cellsCount++;
    }
  }

  public int statusFlush(){
    if(isLife && (cellsCount >= 4 || cellsCount <= 1)){
      kill();
      return 2;
    }
    else if(!isLife && cellsCount == 3){
      born();
      return 1;
    }
    else if(isLife){
      years++;
    }
    return 0;
  }
}

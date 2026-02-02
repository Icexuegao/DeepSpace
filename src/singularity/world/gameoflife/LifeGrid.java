package singularity.world.gameoflife;

import arc.func.Cons;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;

import java.util.Iterator;

public class LifeGrid implements Iterable<Cell>{
  Cell[][] cells;

  public final int size;

  protected final int off;

  public final float offset;

  public int maxYears = 10;

  public LifeGrid(int size){
    this.size = size;

    off = size/2 - (size + 1)%2;

    offset = (size + 1)%2/2f;

    initCells();
  }

  public void initCells(){
    cells = new Cell[size][size];

    for(int i = 0; i < cells.length; i++){
      for(int j = 0; j < cells[i].length; j++){
        cells[i][j] = new Cell(i - off, j - off);
      }
    }

    for(int i = 0; i < cells.length; i++){
      for(int j = 0; j < cells[i].length; j++){
        for(Point2 p: Geometry.d8){
          if(i + p.x >= 0 && i + p.x < size && j + p.y >= 0 && j + p.y < size){
            cells[i][j].neighbour.add(cells[i + p.x][j + p.y]);
          }
        }
      }
    }
  }

  public void resetYears(){
    for(Cell cell: this){
      if(cell.isLife) cell.years = 0;
    }
  }

  public void flush(Cons<Cell> bornCallBack, Cons<Cell> killCallBack){
    for(Cell cell: this){
      cell.countFlush();
    }

    for(Cell cell: this){
      int stat = cell.statusFlush();
      if(stat == 1) bornCallBack.get(cell);
      if(stat == 2) killCallBack.get(cell);

      if(cell.years > maxYears) cell.years = maxYears;
    }
  }

  public Cell get(int x, int y){
    x = x + off;
    y = y + off;

    return getAbs(x, y);
  }

  public Cell getAbs(int x, int y){
    if(x < 0 || x >= size || y < 0 || y >= size) return null;
    return cells[x][y];
  }

  @Override
  public Iterator<Cell> iterator(){
    return new Itr();
  }

  class Itr implements Iterator<Cell>{
    int x, y;

    @Override
    public boolean hasNext(){
      return y < size;
    }

    @Override
    public Cell next(){
      Cell res = cells[x++][y];
      if(x >= size){
        x = 0;
        y++;
      }
      return res;
    }
  }
}

package universecore.ui.elements;

import arc.input.KeyCode;
import arc.math.Mathf;
import arc.scene.Action;
import arc.scene.Element;
import arc.scene.event.*;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.util.Align;

/**一个可缩放的列表视图，可直接对此布局的子元素进行缩放操作*/
public class ZoomableTable extends Table{
  public float maxZoom = 1.5f, minZoom = 0.5f;
  public float defX, defY;
  public boolean zoomable = true, movable = true;
  boolean defSetted = false;
  protected float lastZoom = 1;
  protected Table zoomCont = new Table(), elementChild;
  
  public ZoomableTable(Table cont){
    super();
    defaults().center();
    elementChild = cont == null? new Table(): cont;
    zoomCont.setTransform(true);
    zoomCont.add(elementChild);
    super.add(zoomCont);
    
    //scaling/drag input
    addListener(new InputListener(){
      @Override
      public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY){
        if(zoomable){
          zoomCont.setScale(Mathf.clamp(zoomCont.scaleX - amountY/10f*zoomCont.scaleX, minZoom, maxZoom));
          zoomCont.setOrigin(Align.center);
        }
        return true;
      }

      @Override
      public boolean mouseMoved(InputEvent event, float x, float y){
        if(movable){
          elementChild.requestScroll();
          if(!defSetted){
            defX = elementChild.x;
            defY = elementChild.y;
            defSetted = true;
          }
        }
        return super.mouseMoved(event, x, y);
      }
    });
  
    touchable = Touchable.enabled;
  
    addCaptureListener(new ElementGestureListener(){
      @Override
      public void zoom(InputEvent event, float initialDistance, float distance){
        if(zoomable){
          zoomCont.setScale(Mathf.clamp(distance/initialDistance*lastZoom, minZoom, maxZoom));
          zoomCont.setOrigin(Align.center);
        }
      }
    
      @Override
      public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button){
        lastZoom = zoomCont.scaleX;
      }
    
      @Override
      public void pan(InputEvent event, float x, float y, float deltaX, float deltaY){
        if(movable){
          if(!defSetted){
            defX = elementChild.x;
            defY = elementChild.y;
            defSetted = true;
          }
          elementChild.moveBy(deltaX/lastZoom, deltaY/lastZoom);
        }
      }
    });
  }
  
  public ZoomableTable(){
    this(null);
  }
  
  public void setValid(boolean b){
    movable = b;
    zoomable = b;
  }
  
  public void resetZoom(){
    lastZoom = 1;
    zoomCont.setScale(1);
    zoomCont.setOrigin(Align.center);
    zoomCont.setTransform(true);
  }
  
  @Override
  public void reset(){
    super.reset();
    resetZoom();
  }
  
  @Override
  public <T extends Element> Cell<T> add(T element){
    return elementChild.add(element);
  }
  
  @Override
  public boolean removeChild(Element element, boolean unfocus){
    return elementChild.removeChild(element, unfocus);
  }
  
  @Override
  public boolean addListener(EventListener listener){
    return elementChild.addListener(listener);
  }
  
  @Override
  public boolean removeListener(EventListener listener){
    return elementChild.removeListener(listener);
  }
  
  @Override
  public void clearChildren(){
    elementChild.clearChildren();
  }
  
  @Override
  public void clear(){
    elementChild.clear();
  }
  
  @Override
  public void clearActions(){
    elementChild.clearActions();
  }
  
  @Override
  public void clearListeners(){
    elementChild.clearListeners();
  }
  
  @Override
  public void addAction(Action action){
    elementChild.addAction(action);
  }
  
  @Override
  public void removeAction(Action action){
    elementChild.removeAction(action);
  }
}

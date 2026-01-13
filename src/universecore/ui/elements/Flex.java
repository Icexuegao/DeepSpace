package universecore.ui.elements;

import arc.func.*;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.Element;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.Elem;
import arc.struct.Seq;
import arc.util.Align;

import static arc.Core.scene;

public class Flex extends WidgetGroup {
  private final Seq<FlexCell<?>> cells = new Seq<>();
  private float prefWidth, prefHeight;

  @Override
  public void layout() {
    for (FlexCell<?> cell : cells) {

    }
  }

  public <E extends Element> FlexCell<E> add(E element){
    FlexCell<E> cell = new FlexCell<>();
    cell.element = element;

    addChild(element);
    return cell;
  }

  public void add(Element... elements){
    for(Element element : elements) add(element);
  }

  public FlexCell<Collapser> collapser(Cons<Table> cons, Boolp shown){
    return collapser(cons, false, shown);
  }

  public FlexCell<Collapser> collapser(Table table, Boolp shown){
    return collapser(table, false, shown);
  }

  public FlexCell<Collapser> collapser(Cons<Table> cons, boolean animate, Boolp shown){
    Collapser col = new Collapser(cons, !shown.get());
    col.setCollapsed(animate, () -> !shown.get());
    return add(col);
  }

  public FlexCell<Collapser> collapser(Table table, boolean animate, Boolp shown){
    Collapser col = new Collapser(table, !shown.get());
    col.setCollapsed(animate, () -> !shown.get());
    return add(col);
  }

  public FlexCell<Table> table(){
    return table((Drawable)null);
  }

  public FlexCell<Table> table(Drawable background){
    Table table = new Table(background);
    return add(table);
  }

  public FlexCell<Table> table(Cons<Table> cons){
    Table table = new Table();
    cons.get(table);
    return add(table);
  }

  public FlexCell<Table> table(Drawable background, Cons<Table> cons){
    return table(background, Align.center, cons);
  }

  public FlexCell<Table> table(Drawable background, int align, Cons<Table> cons){
    Table table = new Table(background);
    table.align(align);
    cons.get(table);
    return add(table);
  }

  public FlexCell<Label> label(Prov<CharSequence> text){
    return add(new Label(text));
  }

  public FlexCell<Label> labelWrap(Prov<CharSequence> text){
    Label label = new Label(text);
    label.setWrap(true);
    return add(label);
  }

  public FlexCell<Label> labelWrap(String text){
    Label label = new Label(text);
    label.setWrap(true);
    return add(label);
  }

  public FlexCell<ScrollPane> pane(Cons<Table> consumer){
    return pane(scene.getStyle(ScrollPane.ScrollPaneStyle.class), consumer);
  }

  public FlexCell<ScrollPane> pane(ScrollPane.ScrollPaneStyle style, Cons<Table> consumer){
    Table table = new Table();
    consumer.get(table);
    ScrollPane pane = new ScrollPane(table, style);
    return add(pane);
  }

  public FlexCell<ScrollPane> pane(ScrollPane.ScrollPaneStyle style, Element element){
    ScrollPane pane = new ScrollPane(element, style);
    return add(pane);
  }

  public FlexCell<ScrollPane> pane(Element element){
    return pane(scene.getStyle(ScrollPane.ScrollPaneStyle.class), element);
  }

  /** Adds a new cell with a label. */
  public FlexCell<Label> add(CharSequence text){
    return add(new Label(text));
  }

  /** Adds a new cell with a label. */
  public FlexCell<Label> add(CharSequence text, float scl){
    Label l = new Label(text);
    l.setFontScale(scl);
    return add(l);
  }

  /** Adds a new cell with a label. */
  public FlexCell<Label> add(CharSequence text, Label.LabelStyle labelStyle, float scl){
    Label l = new Label(text, labelStyle);
    l.setFontScale(scl);
    return add(l);
  }

  public FlexCell<Label> add(CharSequence text, Color color, float scl){
    Label l = new Label(text);
    l.setColor(color);
    l.setFontScale(scl);
    return add(l);
  }

  /** Adds a new cell with a label. */
  public FlexCell<Label> add(CharSequence text, Label.LabelStyle labelStyle){
    return add(new Label(text, labelStyle));
  }

  /** Adds a new cell with a label. */
  public FlexCell<Label> add(CharSequence text, Color color){
    return add(new Label(text, new Label.LabelStyle(scene.getStyle(Label.LabelStyle.class).font, color)));
  }

  /** Adds a cell without an element. */
  public FlexCell add(){
    return add((Element)null);
  }

  /**
   * Adds a new cell to the table with the specified elements in a {@link Stack}.
   * @param elements May be null to add a stack without any elements.
   */
  public FlexCell<Stack> stack(Element... elements){
    Stack stack = new Stack();
    if(elements != null){
      for(int i = 0, n = elements.length; i < n; i++)
        stack.addChild(elements[i]);
    }
    return add(stack);
  }

  public FlexCell<Image> image(Prov<TextureRegion> reg){
    return add(new Image(reg.get())).update(i -> {
      ((TextureRegionDrawable)i.getDrawable()).setRegion(reg.get());
      i.layout();
    });
  }

  public FlexCell<Image> imageDraw(Prov<Drawable> reg){
    return add(new Image(reg.get())).update(i -> {
      i.setDrawable(reg.get());
      i.layout();
    });
  }

  public FlexCell<Image> image(){
    return add(new Image());
  }

  public FlexCell<Image> image(Drawable name){
    return add(new Image(name));
  }

  public FlexCell<Image> image(Drawable name, Color color){
    Image image = new Image(name);
    image.setColor(color);
    return add(image);
  }

  public FlexCell<Image> image(TextureRegion region){
    return add(new Image(region));
  }

  public FlexCell<CheckBox> check(String text, Boolc listener){
    CheckBox button = Elem.newCheck(text, listener);
    return add(button);
  }

  public FlexCell<CheckBox> check(String text, boolean checked, Boolc listener){
    CheckBox button = Elem.newCheck(text, listener);
    button.setChecked(checked);
    return add(button);
  }

  public FlexCell<CheckBox> check(String text, float imagesize, boolean checked, Boolc listener){
    CheckBox button = Elem.newCheck(text, listener);
    button.getImageCell().size(imagesize);
    button.setChecked(checked);
    return add(button);
  }

  public FlexCell<Button> button(Cons<Button> cons, Runnable listener){
    Button button = new Button();
    button.clearChildren();
    button.clicked(listener);
    cons.get(button);
    return add(button);
  }

  public FlexCell<Button> button(Cons<Button> cons, Button.ButtonStyle style, Runnable listener){
    Button button = new Button(style);
    button.clearChildren();
    button.clicked(listener);
    cons.get(button);
    return add(button);
  }

  public FlexCell<TextButton> button(String text, Runnable listener){
    TextButton button = Elem.newButton(text, listener);
    return add(button);
  }

  public FlexCell<TextButton> button(String text, TextButton.TextButtonStyle style, Runnable listener){
    TextButton button = Elem.newButton(text, style, listener);
    return add(button);
  }

  public FlexCell<ImageButton> button(Drawable icon, Runnable listener){
    ImageButton button = Elem.newImageButton(icon, listener);
    return add(button);
  }

  public FlexCell<ImageButton> button(Drawable icon, float isize, Runnable listener){
    ImageButton button = Elem.newImageButton(icon, listener);
    button.resizeImage(isize);
    return add(button);
  }

  public FlexCell<ImageButton> button(Drawable icon, ImageButton.ImageButtonStyle style, float isize, Runnable listener){
    ImageButton button = new ImageButton(icon, style);
    button.clicked(listener);
    button.resizeImage(isize);
    return add(button);
  }

  public FlexCell<ImageButton> button(Drawable icon, ImageButton.ImageButtonStyle style, Runnable listener){
    ImageButton button = new ImageButton(icon, style);
    button.clicked(listener);
    button.resizeImage(icon.imageSize());
    return add(button);
  }

  public FlexCell<TextField> field(String text, Cons<String> listener){
    TextField field = Elem.newField(text, listener);
    return add(field);
  }

  public FlexCell<TextArea> area(String text, Cons<String> listener){
    TextArea area = new TextArea(text);
    area.changed(() -> listener.get(area.getText()));
    return add(area);
  }

  public FlexCell<TextArea> area(String text, TextField.TextFieldStyle style, Cons<String> listener){
    TextArea area = new TextArea(text, style);
    area.changed(() -> listener.get(area.getText()));
    return add(area);
  }

  public FlexCell<TextField> field(String text, TextField.TextFieldFilter filter, Cons<String> listener){
    TextField field = Elem.newField(text, listener);
    field.setFilter(filter);
    return add(field);
  }

  public FlexCell<TextField> field(String text, TextField.TextFieldStyle style, Cons<String> listener){
    TextField field = Elem.newField(text, listener);
    field.setStyle(style);
    return add(field);
  }

  public FlexCell<?> rect(Table.DrawRect draw){
    return add(new Element(){
      @Override
      public void draw(){
        draw.draw(x, y, getWidth(), getHeight());
      }
    });
  }

  public FlexCell<TextButton> buttonRow(String text, Drawable image, Runnable clicked){
    TextButton button = new TextButton(text);
    button.clearChildren();
    button.add(new Image(image)).update(i -> i.setColor(button.isDisabled() ? Color.gray : Color.white));
    button.row();
    button.add(button.getLabel()).padTop(4).padLeft(4).padRight(4).wrap().growX();
    button.clicked(clicked);
    return add(button);
  }

  public FlexCell<TextButton> button(String text, Drawable image, Runnable clicked){
    return button(text, image, image.imageSize() / Scl.scl(1f), clicked);
  }

  public FlexCell<TextButton> button(String text, Drawable image, float imagesize, Runnable clicked){
    return button(text, image, scene.getStyle(TextButton.TextButtonStyle.class), imagesize, clicked);
  }

  public FlexCell<TextButton> button(String text, Drawable image, TextButton.TextButtonStyle style, float imagesize, Runnable clicked){
    TextButton button = new TextButton(text, style);
    button.add(new Image(image)).size(imagesize);
    button.getCells().reverse();
    button.clicked(clicked);
    return add(button);
  }

  public FlexCell<TextButton> button(String text, Drawable image, TextButton.TextButtonStyle style, Runnable clicked){
    return button(text, image, style, image.imageSize() / Scl.scl(1f), clicked);
  }

  public FlexCell<TextButton> buttonCenter(String text, Drawable image, float imagesize, Runnable clicked){
    return buttonCenter(text, image, scene.getStyle(TextButton.TextButtonStyle.class), imagesize, clicked);
  }

  public FlexCell<TextButton> buttonCenter(String text, Drawable image, Runnable clicked){
    return buttonCenter(text, image, scene.getStyle(TextButton.TextButtonStyle.class), image.imageSize(), clicked);
  }

  public FlexCell<TextButton> buttonCenter(String text, Drawable image, TextButton.TextButtonStyle style, float imagesize, Runnable clicked){
    TextButton button = new TextButton(text, style);
    button.add(new Image(image)).size(imagesize);
    button.getCells().reverse();
    button.clicked(clicked);
    button.getLabelCell().padLeft(-imagesize);
    return add(button);
  }

  public FlexCell<Slider> slider(float min, float max, float step, Floatc listener){
    return slider(min, max, step, 0f, listener);
  }

  public FlexCell<Slider> slider(float min, float max, float step, float defvalue, Floatc listener){
    Slider slider = new Slider(min, max, step, false);
    slider.setValue(defvalue);
    if(listener != null)
      slider.moved(listener);
    return add(slider);
  }

  public FlexCell<Slider> slider(float min, float max, float step, float defvalue, boolean onUp, Floatc listener){
    Slider slider = new Slider(min, max, step, false);
    slider.setValue(defvalue);
    if(listener != null){
      if(!onUp){
        slider.moved(listener);
      }else{
        slider.released(() -> listener.get(slider.getValue()));
      }

    }
    return add(slider);
  }

  @Override
  public float getPrefWidth() {
    return prefWidth;
  }

  @Override
  public float getPrefHeight() {
    return prefHeight;
  }
}

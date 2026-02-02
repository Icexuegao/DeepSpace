package singularity.ui.dialogs;

import arc.Core;
import arc.scene.event.Touchable;
import arc.scene.style.Drawable;
import arc.scene.ui.Tooltip;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.dialogs.BaseDialog;
import singularity.Contribute;
import singularity.Contributors;
import singularity.Sgl;
import singularity.Singularity;
import singularity.graphic.SglDrawConst;
import universecore.ui.elements.ZoomableTable;

public class ContributorsDialog extends BaseDialog{
  protected ZoomableTable table = new ZoomableTable();
  
  public ContributorsDialog(){
    super("");
    margin(0f).marginBottom(8);
    
    titleTable.clearChildren();
    
    cont.add(table).size(Core.scene.getWidth(), Core.scene.getHeight());
  }
  
  public void build(){
    addCloseButton();
    
    touchable = Touchable.enabled;

    table.table(SglDrawConst.grayUIAlpha, table -> {
      table.defaults().pad(8);

      table.table(Tex.underline, t -> {
        t.left().defaults().left().fill();
        t.add(Core.bundle.get("dialog.contributors.info"));
        t.row();
        t.add(Core.bundle.get("dialog.contributors.special")).color(Pal.accent);
      }).growX();
      table.row();
      table.table(cons -> {
        cons.defaults().pad(8).padLeft(8).padRight(8);
        for(Contribute contribute: Contribute.values()){
          if (contribute == Contribute.author) continue;

          cons.table(Tex.buttonTrans, t -> {
            t.top().defaults().center().top().pad(16).padTop(12);
            t.image(Singularity.<Drawable>getModDrawable(contribute.name())).size(64);
            t.row();
            t.add(contribute.localize()).padTop(6);
            t.row();
            Seq<Contributors.Contributor> seq = Sgl.contributors.get(contribute);
            if(seq != null) for(Contributors.Contributor contributor: seq){
              t.add(new ContributorTable(contributor)).fillY();
              t.row();
            }
          }).fillY();
        }
      });
      table.row();
      table.left().add(Core.bundle.get("dialog.contributors.thanks")).fill().left();
    }).margin(8);
  }
  
  protected static class ContributorTable extends Table{
    static float tapTime;

    public ContributorTable(Contributors.Contributor contributor){
      image(contributor.avatar).size(180).get().addListener(new Tooltip(t -> t.table(Tex.paneLeft).get().add(Core.bundle.get("infos.clickToPage") + "\nhttps://github.com/" + contributor.name)));
      row();
      add(contributor.displayName).color(Pal.accent).fill();

      touchable = Touchable.enabled;

      tapped(() -> tapTime = Time.time);
      clicked(() -> {
        if (Time.time - tapTime < 30) Core.app.openURI("https://github.com/" + contributor.name);
      });
    }
  }
}

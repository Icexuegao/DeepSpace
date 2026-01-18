package singularity.ui.dialogs;

import arc.Core;
import arc.files.Fi;
import arc.func.Cons;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.style.Drawable;
import arc.scene.ui.layout.Table;
import arc.util.Http;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Streams;
import arc.util.serialization.Jval;
import ice.Ice;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import singularity.Sgl;
import singularity.graphic.SglDrawConst;

import java.io.OutputStream;
import java.util.regex.Pattern;

import static mindustry.Vars.*;

public class AboutModDialog extends BaseDialog {
  public static final Pattern UNC_RELEASE_FILE = Pattern.compile("^Singularity-\\w*-?\\d+\\.\\d+\\.\\d+\\.(jar|zip)$");

  @Nullable String newVersion;
  @Nullable String updateUrl;
  boolean checking;
  float downloadProgress;
  
  ButtonEntry[] modPages = new ButtonEntry[]{
      new ButtonEntry(Icon.githubSquare, t -> {
        t.add(Core.bundle.get("misc.github")).color(Pal.accent);
        t.row();
        t.add(Core.bundle.get("infos.openAddress"));
      }, () -> Pal.accent, () -> openUrl(Sgl.githubProject)),
      
      new ButtonEntry(Icon.discord, t -> {
        t.add(Core.bundle.get("misc.discord")).color(Pal.accent);
        t.row();
        t.add(Core.bundle.get("infos.discord"));
      }, () -> Pal.lightOrange, () -> openUrl(Sgl.discord)),

      new ButtonEntry(SglDrawConst.telegramIcon, t -> {
        t.add(Core.bundle.get("misc.telegram")).color(Pal.accent);
        t.row();
        t.add(Core.bundle.get("infos.telegram"));
      }, () -> Color.valueOf("7289da"), () -> {}/*openUrl(Sgl.telegramGroup)*/),

      new ButtonEntry(SglDrawConst.qqIcon, t -> {
        t.add(Core.bundle.get("misc.qq")).color(Pal.accent);
        t.row();
        t.add(Core.bundle.get("infos.qq"));
      }, () -> Pal.lightishGray, () -> openUrl(Ice.qqGrops)),
  };

  public AboutModDialog() {
    super(Core.bundle.get("dialog.aboutMod.title"));
    
    addCloseButton();
    shown(this::checkOrDoUpdate);
    hidden(() -> {
      checking = false;
      newVersion = null;
      updateUrl = null;
    });
  }
  
  public void build(){
    cont.clearChildren();
    cont.defaults().fillY().top();

    cont.table(main -> {
      main.table(SglDrawConst.grayUIAlpha, t -> {
        t.defaults().left().pad(5).growX().height(40);
        t.add(Core.bundle.get("mod.name")).color(Pal.accent);
        t.row();
        t.add(Core.bundle.get("misc.author")).color(Pal.accent);
        t.add(Core.bundle.get("mod.author"));
        t.button(Core.bundle.get("mod.contributor"), SglDrawConst.contributeIcon, Styles.nonet, 28, () -> Sgl.ui.contributors.show()).update(b -> b.setChecked(false)).width(230);
        t.row();
        t.add(Core.bundle.get("misc.version")).color(Pal.accent);
        t.add(Core.bundle.get("mod.version"));
        t.table(update -> {
          update.add(new Element(){
            @Override
            public void draw(){
              Draw.alpha(parentAlpha*color.a);

              if (checking){
                Draw.color(Pal.accent);
                Fill.square(x + width/2, y + height/2, 8, Time.time);
                Fill.square(x + width/2, y + height/2, 8, 45 + 2*Time.time);
              }
              else{
                if (newVersion == null) Draw.color(Pal.heal);
                else Draw.color(Pal.accent, Pal.heal, Mathf.absin(8, 1));

                Fill.square(x + width/2, y + height/2, 8);
                Fill.square(x + width/2, y + height/2, 8, 45);
              }
            }
          }).size(40);
          update.add("").update(l -> l.setText(checking? Core.bundle.get("infos.checkingUpgrade"): newVersion != null? Core.bundle.format("infos.hasUpdate", newVersion): Core.bundle.get("infos.newestVersion")));
        }).width(230);
        t.row();
        t.add(Core.bundle.get("infos.releaseDate")).color(Pal.accent);
        t.add(Core.bundle.get("mod.updateDate"));
        t.button("", Icon.upload, Styles.nonet, 28, this::checkOrDoUpdate)
            .update(b -> b.setText(newVersion != null? Core.bundle.get("misc.update"): Core.bundle.get("infos.checkUpdate"))).width(230);
      }).growX().fillY().padTop(40).margin(4);

      main.row();

      main.pane(t -> {
        t.defaults().growX().height(64).pad(0).padTop(10).margin(0);

        t.add(Core.bundle.get("infos.modPage")).color(Pal.accent).height(24).width(720);
        t.row();
        t.image().color(Pal.accent).width(740).height(4).pad(0).padTop(4);
        t.row();
        for(ButtonEntry item : modPages){
          t.table(Tex.underline, table -> {
            table.table(img -> {
              img.image().height(60).width(40f).update(i -> i.setColor(item.color.get()));
              img.row();
              img.image().height(4).width(40f).update(i -> i.setColor(item.color.get().cpy().mul(0.8f, 0.8f, 0.8f, 1f)));
            }).expandY();

            table.table(Tex.buttonEdge3, i -> i.image(item.drawable).size(32)).size(64);
            Table i = table.table().width(545).padLeft(10).get();
            i.defaults().growX().left();
            item.text.get(i);

            table.button(Icon.link, item.clicked).size(64).left().padLeft(12);
          }).width(710);

          t.row();
        }
      }).growX().padTop(20);
    });
  }

  private void checkOrDoUpdate() {
    if(newVersion != null){
      if (updateUrl == null) ui.showException("what? updateUrl was null!", new NullPointerException());
      else {
        downloadMod();
      }
    }
    else {
      checking = true;
      Http.get(Sgl.githubProjReleaseApi, res -> {
        Jval response = Jval.read(res.getResultAsString());

        if (!checking) return;

        if (isNewVersion(response.getString("tag_name"))) {
          newVersion = response.getString("tag_name");

          for (Jval asset : response.get("assets").asArray()) {
            if (asset.has("name") && UNC_RELEASE_FILE.matcher(asset.getString("name")).matches()) {
              updateUrl = asset.getString("browser_download_url");
            }
          }
        }

        Core.app.post(() -> checking = false);
      }, e -> Core.app.post(() -> {
        checking = false;
        ui.showInfoFade("\n\n" + Core.bundle.get("infos.checkFailed"));
      }));
    }
  }

  private void downloadMod(){
    downloadProgress = 0f;
    ui.loadfrag.show("@downloading");
    ui.loadfrag.setProgress(() -> downloadProgress);
    Http.get(updateUrl, result -> {
      try{
        Fi file = tmpDirectory.child("Singularity" + newVersion + ".jar");
        long len = result.getContentLength();

        try(OutputStream stream = file.write(false)){
          Streams.copyProgress(result.getResultAsStream(), stream, len, 4096,  len <= 0 ? f -> {} : p -> downloadProgress = p);
        }

        var mod = mods.importMod(file);
        mod.setRepo("EB-wilson/Singularity");
        file.delete();

        Core.app.post(() -> {
          ui.loadfrag.hide();
          ui.showConfirm("@mods.reloadexit", () -> {
            Log.info("Exiting to reload mods.");
            Core.app.exit();
          });
        });
      }catch(Throwable e){
        ui.showException(e);
        Log.err(e);
      }
    }, e -> {
      ui.showException(e);
      Log.err(e);
    });
  }

  private static boolean isNewVersion(String version) {
    String[] version_arr = version.split("\\.");
    String[] curr_version_arr = "11..1.11".split("\\.");

    boolean newestVersion = false;
    int n = Math.max(version_arr.length, curr_version_arr.length);
    for (int i = 0; i < n; i++) {
      if (i < version_arr.length && i < curr_version_arr.length){
        try {
          if (Integer.parseInt(curr_version_arr[i]) < Integer.parseInt(version_arr[i])){
            newestVersion = true;
            break;
          }
        } catch (NumberFormatException ignored){
          break; //忽略意外的版本号
        }
      }
      else if (i < version_arr.length){
        newestVersion = true;
        break;
      }
    }
    return newestVersion;
  }

  private static void openUrl(String url){
    if(!Core.app.openURI(url)){
      ui.showErrorMessage("@linkfail");
      Core.app.setClipboardText(url);
    }
  }
  
  private static class ButtonEntry{
    Drawable drawable;
    Cons<Table> text;
    Prov<Color> color;
    
    Runnable clicked;
    
    public ButtonEntry(Drawable drawable, Cons<Table> text, Prov<Color> color, Runnable clicked){
      this.drawable = drawable;
      this.text = text;
      this.color = color;
      this.clicked = clicked;
    }
    
    public ButtonEntry(Drawable drawable, String text, Color color, Runnable clicked){
      this(drawable, t -> t.add(text), () -> color, clicked);
    }
  }
}

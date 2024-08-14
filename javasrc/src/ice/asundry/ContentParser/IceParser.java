package ice.asundry.ContentParser;

import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.mod.Mod;
import mindustry.mod.Mods;
import mindustry.type.ErrorContent;

import java.util.Locale;

public class IceParser {
    private static final IceContentParser parser = new IceContentParser();

    public static void load(String contentName, Class<? extends Mod> clas) {
        Mods.LoadedMod mod = Vars.mods.getMod(clas);
        Fi fi = mod.root.child(contentName);
        Seq<LoadRun> runs = new Seq<>();
        if (fi.exists()) {
            Fi contentRoot = mod.root.child(contentName);
            for (ContentType type : ContentType.all) {
                String lower = type.name().toLowerCase(Locale.ROOT);
                Fi folder = contentRoot.child(lower + (lower.endsWith("s") ? "" : "s"));//units,items....
                if (folder.exists()) {
                    for (Fi file : folder.findAll(f -> f.extension().equals("json") || f.extension().equals("hjson"))) {
                        runs.add(new LoadRun(type, file, mod));
                    }
                }
            }
        }
        runs.sort();
        for (LoadRun l : runs) {
            Content current = Vars.content.getLastAdded();
            try {
                //这将绑定内容，但不会完全加载
                Content loaded = parser.parse(l.mod, l.file.nameWithoutExtension(), l.file.readString("UTF-8"), l.file, l.type);
                Log.debug("[@]加载'@'.", l.mod.meta.name, (loaded instanceof UnlockableContent u ? u.localizedName : loaded));
            } catch (Throwable e) {
                if (current != Vars.content.getLastAdded() && Vars.content.getLastAdded() != null) {
                    parser.markError(Vars.content.getLastAdded(), l.mod, l.file, e);
                } else {
                    ErrorContent error = new ErrorContent();
                    parser.markError(error, l.mod, l.file, e);
                }
            }
        }
        //这样就完成了对内容字段的解析
        parser.finishParsing();
    }

    public static class LoadRun implements Comparable<LoadRun> {
        final ContentType type;
        final Fi file;
        final Mods.LoadedMod mod;

        public LoadRun(ContentType type, Fi file, Mods.LoadedMod mod) {
            this.type = type;
            this.file = file;
            this.mod = mod;
        }

        @Override
        public int compareTo(LoadRun l) {
            int mod = this.mod.name.compareTo(l.mod.name);
            if (mod != 0) return mod;
            return this.file.name().compareTo(l.file.name());
        }
    }
}

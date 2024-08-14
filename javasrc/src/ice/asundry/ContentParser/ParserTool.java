package ice.asundry.ContentParser;

import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.mod.Mod;
import mindustry.mod.Mods;
import mindustry.type.Item;
import mindustry.type.ItemStack;

public class ParserTool {
    public static boolean isNumeric4(String str) {
        return str != null && str.chars().allMatch(Character::isDigit);
    }

    public static Object[] getObjectArr(String[] strings) {
        return getObjectArr(strings, null);
    }

    public static Object[] getObjectArr(String[] strings, Class<? extends Mod> modClass) {
        Mods.LoadedMod mod = Vars.mods.getMod(modClass);
        Object[] objects = new Object[strings.length];
        int index = 0;
        for (String s : strings) {
            if (s.contains("/")) {
                String[] split = s.split("/");
                if (split.length == 2) {
                    if (split[0].equals("i") || split[0].equals("item")) {
                        if (mod != null) {
                            objects[index] = Vars.content.item(mod.name + "-" + split[1]);
                        } else {
                            objects[index] = Vars.content.item(split[1]);
                        }
                        index++;
                    } else if (split[0].equals("b") || split[0].equals("block")) {
                        objects[index] = Vars.content.block(split[1]);
                        index++;
                    }
                } else if (split.length == 3) {
                    if (split[0].equals("i") || split[0].equals("item")) {
                        Item item = Vars.content.item(split[1]);

                        if (mod != null) {
                            item = Vars.content.item(mod.name + "-" + split[1]);
                        } else {
                            for (Mods.LoadedMod mod1 : Vars.mods.list()) {
                                item = Vars.content.item(mod1.name + "-" + split[1]);
                                if (item != null) break;
                            }
                        }

                        int amount = Integer.parseInt(split[2]);
                        objects[index] = new ItemStack(item, amount);
                        index++;
                    }
                }
            } else {
                if (isNumeric4(s)) {
                    objects[index] = Integer.parseInt(s);
                    index++;
                } else {
                    for (ContentType contentType : ContentType.all) {
                        Content byName = Vars.content.getByName(contentType, mod == null ? "" : mod.name + "-" + s);
                        if (byName != null) {
                            objects[index] = byName;
                            index++;
                            break;
                        }
                    }
                }
            }
        }
        return objects;
    }
}

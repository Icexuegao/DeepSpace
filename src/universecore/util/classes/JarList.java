package universecore.util.classes;

import arc.Core;
import arc.files.Fi;
import arc.util.Log;
import mindustry.Vars;
import universecore.util.mods.ModGetter;
import universecore.util.mods.ModInfo;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class JarList{
  public static final Fi jarFileCache = Core.settings.getDataDirectory().child("universecore").child("cache");

  private final static Fi listFile = jarFileCache.child("mod-generatedJars.lis");

  private static final JarList INSTANCE = new JarList();

  private final HashMap<String, InfoEntry> list = new HashMap<>();
  private boolean selfUpdate, recached;

  public static JarList inst(){
    return INSTANCE;
  }
  
  private JarList(){
    jarFileCache.mkdirs();
    if(!listFile.exists()) return;
    BufferedReader reader = new BufferedReader(listFile.reader());
    String line;
    InfoEntry entry;
    try{
      while((line = reader.readLine()) != null){
        line = line.trim();
        if(line.isEmpty()) continue;
        entry = InfoEntry.infoOf(line);
        list.put(entry.name, entry);
      }
    }catch(Exception ignored){
      Log.info("[UniverseCore] failed to load mod generated jars cache, regenerate all generate cache");
      list.clear();
      writeToList();
    }
  }

  public void update(ModInfo mod){
    InfoEntry entry = list.get(mod.getName());
    if(entry == null)
      throw new RuntimeException("unknown mod " + mod.getName());

    entry.md5 = getMd5(mod.getFile());
  }
  
  public boolean matched(ModInfo mod){
    String md5Code = getMd5(mod.getFile());
    InfoEntry entry = list.get(mod.getName());
    if(entry == null) return false;
    if(!mod.getVersion().equals(entry.version)) return false;
    return md5Code.equals(entry.getMd5());
  }
  
  public Fi loadCacheFile(ModInfo mod){
    if (!selfUpdate && mod.getName().equals("universe-core")){
      if (!matched(mod)){
        selfUpdate = true;
        Log.info("[UniverseCore] universe core updated, all cache will be regenerated");
        list.clear();
        writeToList();
      }
      else {
        for (Fi fi : Vars.modDirectory.list()) {
          ModGetter.INSTANCE.checkModFormat(fi);
          ModInfo info = new ModInfo(fi);

          if (!info.getName().equals("universe-code") && !matched(info)) {
            selfUpdate = true;
            Log.info("[UniverseCore] exist mod updated, universe core class cache updating");
            break;
          }
        }
      }
    }

    InfoEntry entry = list.get(mod.getName());
    if(entry == null){
      if (mod.getName().equals("universe-core")) recached = true;

      Log.info("[UniverseCore] new mod: " + mod.getName() + " installed, to generate class cache");
      entry = new InfoEntry();
      entry.name = mod.getName();
      entry.version = mod.getVersion();
      entry.file = jarFileCache.child("generated-" + mod.getName() + ".jar");
      entry.file.delete();
      entry.md5 = getMd5(mod.getFile());
      list.put(mod.getName(), entry);
      writeToList();
    }
    else if((selfUpdate && mod.getName().equals("universe-core") && !recached) || !matched(mod)){
      if (mod.getName().equals("universe-core")) recached = true;

      Log.info("[UniverseCore] source mod: " + mod.getName() + " is updated, regenerate class cache");
      entry.version = mod.getVersion();
      entry.file.delete();
      entry.md5 = getMd5(mod.getFile());
      writeToList();
    }
    else Log.info("[UniverseCore] loading mod: " + mod.getName() + " class cache");

    return entry.file;
  }

  public void deleteCacheFile(ModInfo mod) {
    InfoEntry entry = list.get(mod.getName());
    if(entry == null) return;
    entry.file.delete();
    list.remove(mod.getName());
    writeToList();
  }
  
  private void writeToList(){
    try(BufferedWriter writer = new BufferedWriter(listFile.writer(false))){
      for(InfoEntry entry : list.values()){
        writer.write(entry.toString());
        writer.newLine();
      }
    }catch(IOException e){
      throw new RuntimeException(e);
    }
  }

  public static class InfoEntry{
    private String name;
    private String version;
    private String md5;
    private Fi file;
    
    public String getName(){
      return name;
    }
    
    public String getVersion(){
      return version;
    }
    
    public String getMd5(){
      return md5;
    }
    
    public Fi getFile(){
      return file;
    }
    
    public String toString(){
      return "name=" + name + ";version=" + version + ";MD5=" + getMd5() + ";file=" + file.name() + ";";
    }
    
    public static InfoEntry infoOf(String str){
      InfoEntry result = new InfoEntry();
      StringBuilder keyBuffer = new StringBuilder();
      StringBuilder valueBuffer = new StringBuilder();
  
      BufferedReader reader = new BufferedReader(new StringReader(str));
      int character;
      try{
        String c;
        String key;
        String value;
        boolean inValue = false;
        while((character = reader.read()) != - 1){
          c = Character.toString((char)character);
          if(c.equals(";")){
            if(!inValue) throw new IllegalArgumentException("unexpected \";\"");
            key = keyBuffer.toString();
            value = valueBuffer.toString();

            switch(key){
              case "name" -> result.name = value;
              case "version" -> result.version = value;
              case "MD5" -> result.md5 = value;
              case "file" -> result.file = new Fi(jarFileCache.path()).child(value);
              default -> throw new IllegalArgumentException("unknown key: " + key);
            }
            
            inValue = false;
            keyBuffer = new StringBuilder();
            valueBuffer = new StringBuilder();
          }
          else if(c.equals("=")){
            inValue = true;
          }
          else{
            if(inValue){
              valueBuffer.append(c);
            }
            else keyBuffer.append(c);
          }
        }
        reader.close();
      }catch(IOException e){
        throw new RuntimeException(e);
      }
      
      return result;
    }
  }
  
  private static String getMd5(Fi file){
    MessageDigest md;
    byte[] buffer = new byte[8192];
    try{
      md = MessageDigest.getInstance("MD5");
      InputStream input = new FileInputStream(file.file());
      int data;
      while((data = input.read(buffer)) != - 1){
        md.update(buffer, 0, data);
      }
      input.close();
      return new BigInteger(1, md.digest()).toString(16);
    }catch(IOException | NoSuchAlgorithmException e){
      throw new RuntimeException(e);
    }
  }
}

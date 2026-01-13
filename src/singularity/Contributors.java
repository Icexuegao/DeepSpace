package singularity;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.serialization.Jval;
import universecore.util.UrlDownloader;

public class Contributors{
  private final static Jval contList = Jval.read(Sgl.modFile.child("contributors.hjson").reader());
  
  private final Seq<Contributor> allContributor = new Seq<>();
  private final ObjectMap<Contribute, Seq<Contributor>> contributors = new ObjectMap<>();
  
  public Contributors(){
    for(ObjectMap.Entry<String, Jval> entry : contList.asObject()){
      Contributor contributor = new Contributor(entry.key, Contribute.valueOf(entry.value.get("contribute").asString()));
      contributor.displayName = entry.value.has("name")? entry.value.get("name").asString(): entry.key;
      allContributor.add(contributor);
    }
    
    Seq<Contributor> c;
    for(Contributor cont: allContributor){
      if((c = contributors.get(cont.contribute)) == null){
        c = new Seq<>();
        contributors.put(cont.contribute, c);
      }
      c.add(cont);
    }
  }
  
  public Seq<Contributor> allContributors(){
    return allContributor;
  }
  
  public ObjectMap<Contribute, Seq<Contributor>> contributors(){
    return contributors;
  }
  
  public Seq<Contributor> get(Contribute cont){
    return contributors.get(cont);
  }
  
  public static class Contributor{
    public final String name;
    public final Contribute contribute;
    public final TextureRegion avatar;
    
    public String displayName;
    
    public Contributor(String name, Contribute contribute){
      this.name = name;
      this.contribute = contribute;
      
      this.avatar = UrlDownloader.downloadImg(Sgl.githubUserAvatars + name, Core.atlas.find("nomap"));
    }
  }
}

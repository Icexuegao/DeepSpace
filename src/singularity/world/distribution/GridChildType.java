package singularity.world.distribution;

import arc.Core;

public enum GridChildType{
  output,
  input,
  acceptor,
  container;
  
  public String locale(){
    return Core.bundle.get("misc." + name());
  }
}

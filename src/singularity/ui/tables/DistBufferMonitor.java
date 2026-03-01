package singularity.ui.tables;

import arc.struct.Seq;
import singularity.world.distribution.DistributeNetwork;
import singularity.world.distribution.buffers.BaseBuffer;

public class DistBufferMonitor extends Monitor{
  private final Seq<BaseBuffer<?, ?, ?>> targetBuffer = new Seq<>();

  public DistBufferMonitor(int maxCount){

  }

  @Override
  public void startMonit(DistributeNetwork distNetwork){
    if(distNetwork.netStructValid()){
      for(BaseBuffer<?, ?, ?> buffer: targetBuffer){
        buffer.startCalculate(false);
      }
    }
  }

  @Override
  public void endMonit(DistributeNetwork distNetwork){
    if(distNetwork.netStructValid()){
      for(BaseBuffer<?, ?, ?> buffer: targetBuffer){
        buffer.startCalculate(true);
      }
    }
  }
}

package singularity.graphic.renders;

import arc.Core;
import arc.Events;
import arc.graphics.Gl;
import arc.graphics.g2d.Draw;
import arc.math.geom.Vec3;
import arc.util.Tmp;
import mindustry.game.EventType;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.graphics.g3d.PlanetRenderer;
import mindustry.type.Planet;

public class SglPlanetRender extends PlanetRenderer{
  @Override
  public void render(PlanetParams params){
    Draw.flush();
    Gl.clear(Gl.depthBufferBit);
    Gl.enable(Gl.depthTest);
    Gl.depthMask(true);
    
    Gl.enable(Gl.cullFace);
    Gl.cullFace(Gl.back);
    
    int w = params.viewW <= 0 ? Core.graphics.getWidth() : params.viewW;
    int h = params.viewH <= 0 ? Core.graphics.getHeight() : params.viewH;
    
    bloom.blending = !params.drawSkybox;
    
    cam.resize(w, h);
    cam.position.set(params.camPos);
    cam.direction.set(params.camDir);
    cam.up.set(params.camUp);
    cam.update();
    
    projector.proj(cam.combined);
    batch.proj(cam.combined);
    
    Events.fire(EventType.Trigger.universeDrawBegin);
    
    //begin bloom
    bloom.resize(w, h);
    bloom.capture();
    
    if(params.drawSkybox){
      //render skybox at 0,0,0
      Vec3 lastPos = Tmp.v31.set(cam.position);
      cam.position.setZero();
      cam.update();
      
      Gl.depthMask(false);
      
      skybox.render(cam.combined);
      
      Gl.depthMask(true);
      
      cam.position.set(lastPos);
      cam.update();
    }
    
    Events.fire(EventType.Trigger.universeDraw);

    Planet solarSystem = params.planet.solarSystem;
    renderPlanet(solarSystem, params);
    renderTransparent(solarSystem, params);
    
    bloom.render();
    
    Events.fire(EventType.Trigger.universeDrawEnd);
    
    Gl.enable(Gl.blend);
    
    if(params.renderer != null){
      params.renderer.renderProjections(params.planet);
    }
    
    Gl.disable(Gl.cullFace);
    Gl.disable(Gl.depthTest);
    
    cam.update();
  }
}

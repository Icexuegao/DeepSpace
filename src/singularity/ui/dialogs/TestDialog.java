package singularity.ui.dialogs;

import arc.scene.ui.Dialog;

public class TestDialog extends Dialog {
 /* public TestDialog() {
    super("", SglStyles.transGrayBack);
    titleTable.clear();
    addCloseButton();

    Mesh grid = FieldHandler.getValueDefault(Planets.serpulo.mesh, "mesh");
    Mesh solGrid = FieldHandler.getValueDefault(Planets.sun.mesh, "mesh");
    Mesh cube = new Mesh(true, 24, 36,
        VertexAttribute.position3, VertexAttribute.texCoords, VertexAttribute.normal, VertexAttribute.color
    );

    float c = Color.white.toFloatBits();
    TextureRegion region = Blocks.copperWall.region;
    float u1 = region.u, v1 = region.v;
    float u2 = region.u2, v2 = region.v2;
    cube.setVertices(new float[]{
        // 后面
        -0.5f, -0.5f, -0.5f, u1, v1, 0.0f,  0.0f, -1.0f,  c,
        0.5f, -0.5f, -0.5f,  u2, v1, 0.0f,  0.0f, -1.0f,  c,
        0.5f,  0.5f, -0.5f,  u2, v2, 0.0f,  0.0f, -1.0f,  c,
        -0.5f,  0.5f, -0.5f, u1, v2, 0.0f,  0.0f, -1.0f, c,
        // 前面                                     c,
        -0.5f, -0.5f,  0.5f, u1, v1,  0.0f,  0.0f,  1.0f, c,
        0.5f, -0.5f,  0.5f,  u2, v1, 0.0f,  0.0f,  1.0f,  c,
        0.5f,  0.5f,  0.5f,  u2, v2, 0.0f,  0.0f,  1.0f,  c,
        -0.5f,  0.5f,  0.5f, u1, v2,  0.0f,  0.0f,  1.0f, c,
        // 左侧                                     c,
        -0.5f, -0.5f, -0.5f, u1, v1, -1.0f,  0.0f,  0.0f, c,
        -0.5f,  0.5f, -0.5f, u2, v1, -1.0f,  0.0f,  0.0f, c,
        -0.5f,  0.5f,  0.5f, u2, v2, -1.0f,  0.0f,  0.0f, c,
        -0.5f, -0.5f,  0.5f, u1, v2, -1.0f,  0.0f,  0.0f, c,
        // 右侧                                     c,
        0.5f, -0.5f, -0.5f, u1, v1, 1.0f,  0.0f,  0.0f,  c,
        0.5f,  0.5f, -0.5f, u2, v1, 1.0f,  0.0f,  0.0f,  c,
        0.5f,  0.5f,  0.5f, u2, v2, 1.0f,  0.0f,  0.0f,  c,
        0.5f, -0.5f,  0.5f, u1, v2, 1.0f,  0.0f,  0.0f,  c,
        // 顶部                                     c,
        -0.5f,  0.5f, -0.5f, u1, v1,  0.0f,  1.0f,  0.0f, c,
        0.5f,  0.5f, -0.5f,  u2, v1, 0.0f,  1.0f,  0.0f,  c,
        0.5f,  0.5f,  0.5f,  u2, v2, 0.0f,  1.0f,  0.0f,  c,
        -0.5f,  0.5f,  0.5f, u1, v2,  0.0f,  1.0f,  0.0f, c,
        // 底部                                     c,
        -0.5f, -0.5f, -0.5f, u1, v1,  0.0f, -1.0f,  0.0f, c,
        0.5f, -0.5f, -0.5f,  u2, v1, 0.0f, -1.0f,  0.0f,  c,
        0.5f, -0.5f,  0.5f,  u2, v2, 0.0f, -1.0f,  0.0f,  c,
        -0.5f, -0.5f,  0.5f, u1, v2,  0.0f, -1.0f,  0.0f, c,
    });

    cube.setIndices(new short[]{
        // 后面
        0, 2, 1,
        0, 3, 2,
        // 前面
        4, 5, 6,
        4, 6, 7,
        // 左侧
        8, 10, 9,
        8, 11, 10,
        // 右侧
        12, 13, 14,
        12, 14, 15,
        // 顶部
        16, 18, 17,
        16, 19, 18,
        // 底部
        20, 21, 22,
        20, 22, 23
    });

    Stage3D stage = new Stage3D();
    stage.postAutoUpdate(1);
    stage.skybox = new Skybox(new Cubemap("cubemaps/stars/"));
    stage.renderSkybox = true;
    
    PlanetShaderProgram s = new PlanetShaderProgram();
    BaseGeneralShaderProgram m = new BaseGeneralShaderProgram();
    SolarShaderProgram sol = new SolarShaderProgram();

    stage.add(new RendererObj(){{
      material = new Material(s);
      setMesh(grid);
      bounds().set(Tmp.v31.set(-10, -10, -10), Tmp.v32.set(10, 10, 10));
      setPosition(10, 0, 15);
    }
      @Override
      public void update() {
        rotate(0, -0.5f*Time.delta, 0);
      }
    });
    stage.add(new RendererObj(){{
      material = new Material(s);
      setMesh(grid);
      bounds().set(Tmp.v31.set(-10, -10, -10), Tmp.v32.set(10, 10, 10));

      setPosition(20, 0, 12);
    }
      @Override
      public void update() {
        rotate(0, Time.delta, 0);
      }
    });
    stage.add(new RendererObj(){{
      material = new Material(s);
      setMesh(grid);
      bounds().set(Tmp.v31.set(-10, -10, -10), Tmp.v32.set(10, 10, 10));

      RendererObj s = this;
      stage.add(new RendererObj(){{
        material = new Material(m);

        material.setData(m.diffTexture, Blocks.copperWall.region.texture);
        parentObj = s;

        setMesh(cube);
        bounds().set(Tmp.v31.set(-1, -1, -1), Tmp.v32.set(1, 1, 1));
        setPosition(0.5f, 0, 0);
        setScale(0.5f, 0.5f, 0.5f);
      }});
    }
      @Override
      public void update() {
        setPosition(
            Mathf.sinDeg(Time.globalTime*0.2f)*10, -2, Mathf.cosDeg(Time.globalTime*0.2f)*15
        );
        rotate(0, Time.delta, 0);
      }
    });

    stage.add(new Light(){{
        setMaterial(new Material(sol));
      setMesh(solGrid);
      bounds().set(Tmp.v31.set(-10, -10, -10), Tmp.v32.set(10, 10, 10));
      setPosition(0, 0, 0);
    }});

    Camera3D camera = stage.camera3D;
    camera.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
    camera.up.set(0, 1, 0);
    Vec3 right = new Vec3(1, 0, 0);

    Vec3 camPos = new Vec3(3, 0, 0);
    camera.position.set(camPos);
    camera.lookAt(0, 0, 0);
    camera.update();
    update(() -> {
      Tmp.v31.set(
          Core.input.axis(Binding.moveX),
          0,
          Core.input.axis(Binding.moveY)
      );
      Mat3D.rot(Tmp.v31, camera.combined);
      Tmp.v31.y = 0;
      Tmp.v31.nor().scl(0.1f);
      if (Core.input.keyDown(KeyCode.space)) Tmp.v31.add(0, 0.1f, 0);
      if (Core.input.keyDown(KeyCode.shiftLeft)) Tmp.v31.add(0, -0.1f, 0);
      camPos.add(Tmp.v31);

      camera.position.set(camPos);
      camera.up.set(0, 1, 0);
      camera.update();
    });

    addCaptureListener(new InputListener(){
      float lx, ly;

      @Override
      public boolean mouseMoved(InputEvent event, float x, float y) {
        if (lx != x){
          camera.direction.rotate(camera.up, (x - lx) * 0.1f);
          right.rotate(camera.up, (x - lx) * 0.1f);
          lx = x;
        }
        if (ly != y){
          camera.direction.rotate(right, - (y - ly) * 0.1f);
          ly = y;
        }

        return false;
      }
    });

    cont.fill((x, y, w, h) -> {
      stage.renderer();
    }).touchable = Touchable.disabled;

    cont.fill(t -> {
      WindowedMean mean = new WindowedMean(10);
      Interval interval = new Interval();
      t.add("").update(l -> {
        mean.add(Time.delta);
        if (interval.get(10f)) {
          l.setText(Strings.fixed(60/mean.mean(), 2) + "fps");
        }
      });
    });
  }*/
}

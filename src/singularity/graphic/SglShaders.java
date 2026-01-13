package singularity.graphic;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.gl.Shader;
import arc.math.geom.Vec2;
import arc.util.ArcRuntimeException;
import arc.util.Time;
import singularity.Singularity;

import static mindustry.Vars.renderer;

public class SglShaders {
  // 3D stage
  public static Shader planet, solar, standardBase, standard;

  // scene space shaders
  public static Shader baseShader, simpleScreen;
  public static SglSurfaceShader boundWater;
  public static MaskShader alphaMask;
  public static MirrorFieldShader mirrorField;
  public static AlphaAdjust linearAlpha, lerpAlpha;

  // local space shaders
  public static WaveShader wave;

  public static final Fi internalShaderDir = Singularity.getInternalFile("shaders").child("sgl");

  public static class SglShader extends Shader{
    public SglShader(String vertexShader, String fragmentShader) {
      super(vertexShader, fragmentShader);
    }

    public SglShader(Fi vertexShader, Fi fragmentShader) {
      super(vertexShader, fragmentShader);
    }

    @Override
    protected String preprocess(String source, boolean fragment) {
      //disallow gles qualifiers
      if(source.contains("#ifdef GL_ES")){
        throw new ArcRuntimeException("Shader contains GL_ES specific code; this should be handled by the preprocessor. Code: \n```\n" + source + "\n```");
      }

      //disallow explicit versions
      if(source.contains("#version")){
        throw new ArcRuntimeException("Shader contains explicit version requirement; this should be handled by the preprocessor. Code: \n```\n" + source + "\n```");
      }

      //add GL_ES precision qualifiers
      if(fragment){
        source =
            "#ifdef GL_ES\n" +
            "precision " + (source.contains("#define HIGHP") && !source.contains("//#define HIGHP") ? "highp" : "mediump") + " float;\n" +
            "precision mediump int;\n" +
            "#else\n" +
            "#define lowp  \n" +
            "#define mediump \n" +
            "#define highp \n" +
            "#endif\n" + source;
      }else{
        //strip away precision qualifiers
        source =
            "#ifndef GL_ES\n" +
            "#define lowp  \n" +
            "#define mediump \n" +
            "#define highp \n" +
            "#endif\n" + source;
      }

      //preprocess source to function correctly with OpenGL 3.x core
      //note that this is required on Mac
      if(Core.gl30 != null){

        //if there already is a version, do nothing
        //if on a desktop platform, pick 150 or 130 depending on supported version
        //if on anything else, it's GLES, so pick 300 ES
        String version =
            source.contains("#version ") ? "" :
                Core.app.isDesktop() ? (Core.graphics.getGLVersion().atLeast(3, 2) ? "150" : "130") :
                    "300 es";

        return
            "#version " + version + "\n"
            + "#extension GL_ARB_explicit_attrib_location : require\n"
            + (fragment ? "out" + (Core.app.isMobile() ? " lowp" : "") + " vec4 fragColor;\n" : "")
            + source
                .replace("varying", fragment ? "in" : "out")
                .replace("attribute", fragment ? "???" : "in")
                .replace("texture2D(", "texture(")
                .replace("textureCube(", "texture(")
                .replace("gl_FragColor", "fragColor");
      }
      return source;
    }
  }

  public static void load() {
    planet = new SglShader(
        internalShaderDir.child("3d").child("planet.vert"),
        internalShaderDir.child("3d").child("planet.frag")
    );
    solar = new SglShader(
        internalShaderDir.child("3d").child("solar.vert"),
        internalShaderDir.child("3d").child("solar.frag")
    );
    standardBase = new SglShader(
        internalShaderDir.child("3d").child("standard_base.vert"),
        internalShaderDir.child("3d").child("standard_base.frag")
    );

    simpleScreen = new SglShader(Core.files.internal("shaders/screenspace.vert"), internalShaderDir.child("simple.frag"));
    baseShader = new SglShader(Core.files.internal("shaders/screenspace.vert"), internalShaderDir.child("dist_base.frag"));
    boundWater = new SglSurfaceShader("boundwater");
    alphaMask = new MaskShader("alpha_mask");
    mirrorField = new MirrorFieldShader();
    wave = new WaveShader("wave");
    linearAlpha = new AlphaAdjust(internalShaderDir.child("linear_alpha_adjust.frag"));
    lerpAlpha = new AlphaAdjust(internalShaderDir.child("lerp_alpha_adjust.frag"));
  }

  public static class SglSurfaceShader extends SglShader {
    Texture noiseTex;

    public SglSurfaceShader(String frag){
      super(Core.files.internal("shaders/screenspace.vert"), internalShaderDir.child(frag + ".frag"));
      loadNoise();
    }

    public SglSurfaceShader(String vertRaw, String fragRaw){
      super(vertRaw, fragRaw);
      loadNoise();
    }

    public void loadNoise(){
      Core.assets.load("sprites/noise.png", Texture.class).loaded = t -> {
        t.setFilter(Texture.TextureFilter.linear);
        t.setWrap(Texture.TextureWrap.repeat);
      };
    }

    @Override
    public void apply(){
      setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
      setUniformf("u_resolution", Core.camera.width, Core.camera.height);
      setUniformf("u_time", Time.time);

      if(hasUniform("u_noise")){
        if(noiseTex == null){
          noiseTex = Core.assets.get("sprites/noise.png", Texture.class);
        }

        noiseTex.bind(1);
        renderer.effectBuffer.getTexture().bind(0);

        setUniformi("u_noise", 1);
      }
    }
  }

  public static class WaveShader extends SglShader{
    public Color waveMix = Color.white;
    public float mixAlpha = 0.4f;
    public float mixOmiga = 0.75f;
    public float maxThreshold = 0.9f;
    public float minThreshold = 0.6f;
    public float waveScl = 0.2f;

    public WaveShader(String frag){
      super(Core.files.internal("shaders/screenspace.vert"), internalShaderDir.child(frag + ".frag"));
    }

    @Override
    public void apply(){
      setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
      setUniformf("u_resolution", Core.camera.width, Core.camera.height);
      setUniformf("u_time", Time.time);

      setUniformf("mix_color", waveMix);
      setUniformf("mix_alpha", mixAlpha);
      setUniformf("mix_omiga", mixOmiga);
      setUniformf("wave_scl", waveScl);
      setUniformf("max_threshold", maxThreshold);
      setUniformf("min_threshold", minThreshold);
    }
  }

  public static class MirrorFieldShader extends SglShader {
    public Color waveMix = Color.white;
    public Vec2 offset = new Vec2(0, 0);
    public float stroke = 2;
    public float gridStroke = 0.8f;
    public float mixAlpha = 0.4f;
    public float alpha = 0.2f;
    public float maxThreshold = 1;
    public float minThreshold = 0.7f;
    public float waveScl = 0.03f;
    public float sideLen = 10;

    public MirrorFieldShader() {
      super(Core.files.internal("shaders/screenspace.vert"), internalShaderDir.child( "mirrorfield.frag"));
    }

    @Override
    public void apply(){
      setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
      setUniformf("u_resolution", Core.camera.width, Core.camera.height);
      setUniformf("u_time", Time.time);

      setUniformf("offset", offset);
      setUniformf("u_step", stroke);
      setUniformf("mix_color", waveMix);
      setUniformf("mix_alpha", mixAlpha);
      setUniformf("u_stroke", gridStroke);
      setUniformf("u_alpha", alpha);
      setUniformf("wave_scl", waveScl);
      setUniformf("max_threshold", maxThreshold);
      setUniformf("min_threshold", minThreshold);
      setUniformf("side_len", sideLen);
    }
  }

  public static class MaskShader extends SglShader {
    public Texture texture;

    public MaskShader(String fragment) {
      super(Core.files.internal("shaders/screenspace.vert"), internalShaderDir.child(fragment + ".frag"));
    }

    @Override
    public void apply() {
      setUniformi("u_texture", 1);
      setUniformi("u_mask", 0);

      texture.bind(1);
    }
  }

  public static class AlphaAdjust extends SglShader{
    public float coef;

    public AlphaAdjust(Fi fragmentShader) {
      super(Core.files.internal("shaders/screenspace.vert"), fragmentShader);
    }

    @Override
    public void apply() {
      setUniformf("u_coef", coef);
    }
  }
}

package singularity.graphic.graphic3d;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Mesh;
import arc.graphics.Texture;
import arc.graphics.VertexAttribute;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.g3d.Camera3D;
import arc.math.geom.Mat3D;
import arc.math.geom.Quat;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;

import java.nio.FloatBuffer;

public class Draw3D {
  private static final float diff = 0.0001f;
  private static final Vec3 temp31 = new Vec3(), temp32 = new Vec3(), temp33 = new Vec3();
  private static final Vec2 temp21 = new Vec2(), temp22 = new Vec2(), temp23 = new Vec2();
  private static final Color tempC1 = new Color(), tempC2 = new Color(), tempC3 = new Color();

  private static StandardBatch3D batch;

  public static void preTransform(Mat3D transform){
    batch.preTransform(transform);
  }

  public static void resetPreTransform(){
    batch.resetPreTransform();
  }
  public static void preRotate(Vec3 axis, float degrees){
    batch.enablePreTransform();
    batch.getPreTransform().rotate(axis, degrees);
  }
  public static void preRotate(Vec3 v1, Vec3 v2){
    batch.enablePreTransform();
    batch.getPreTransform().rotate(v1, v2);
  }
  public static void preRotate(Quat quat){
    batch.enablePreTransform();
    batch.getPreTransform().rotate(quat);
  }
  public static void preMove(float x, float y, float z){
    batch.enablePreTransform();
    batch.getPreTransform().translate(x, y, z);
  }
  public static void preMove(Vec3 mov){
    batch.enablePreTransform();
    batch.getPreTransform().translate(mov);
  }
  public static void preScale(float sx, float sy, float sz){
    batch.enablePreTransform();
    batch.getPreTransform().scale(sx, sy, sz);
  }
  public static void preScale(Vec3 scl){
    batch.enablePreTransform();
    batch.getPreTransform().translate(scl);
  }

  public static void cube(float x, float y, float z, float size, Color color){
    cube(Core.atlas.white(), x, y, z, size, color);
  }

  public static void cube(TextureRegion region, float x, float y, float z, float size, Color color){
    float off = size/2;
    batch.rect(region,
        x - off, y - off, z + off + diff,
        x + off, y - off, z + off + diff,
        x + off, y + off, z + off + diff,
        x - off, y + off, z + off + diff,
        color
    );
    batch.rect(region,
        x + off + diff, y - off, z + off,
        x + off + diff, y - off, z - off,
        x + off + diff, y + off, z - off,
        x + off + diff, y + off, z + off,
        color
    );
    batch.rect(region,
        x + off, y - off, z - off - diff,
        x - off, y - off, z - off - diff,
        x - off, y + off, z - off - diff,
        x + off, y + off, z - off - diff,
        color
    );
    batch.rect(region,
        x - off - diff, y - off, z - off,
        x - off - diff, y - off, z + off,
        x - off - diff, y + off, z + off,
        x - off - diff, y + off, z - off,
        color
    );
    batch.rect(region,
        x - off, y + off + diff, z + off,
        x + off, y + off + diff, z + off,
        x + off, y + off + diff, z - off,
        x - off, y + off + diff, z - off,
        color
    );
    batch.rect(region,
        x - off, y - off - diff, z - off,
        x + off, y - off - diff, z - off,
        x + off, y - off - diff, z + off,
        x - off, y - off - diff, z + off,
        color
    );
  }

  public static void cube(TextureRegion region, TextureRegion norm, float x, float y, float z, float size, Color color){
    float off = size/2;
    batch.rect(region, norm,
        x - off, y - off, z + off + diff,
        x + off, y - off, z + off + diff,
        x + off, y + off, z + off + diff,
        x - off, y + off, z + off + diff,
        color
    );
    batch.rect(region, norm,
        x + off + diff, y - off, z + off,
        x + off + diff, y - off, z - off,
        x + off + diff, y + off, z - off,
        x + off + diff, y + off, z + off,
        color
    );
    batch.rect(region, norm,
        x + off, y - off, z - off - diff,
        x - off, y - off, z - off - diff,
        x - off, y + off, z - off - diff,
        x + off, y + off, z - off - diff,
        color
    );
    batch.rect(region, norm,
        x - off - diff, y - off, z - off,
        x - off - diff, y - off, z + off,
        x - off - diff, y + off, z + off,
        x - off - diff, y + off, z - off,
        color
    );
    batch.rect(region, norm,
        x - off, y + off + diff, z + off,
        x + off, y + off + diff, z + off,
        x + off, y + off + diff, z - off,
        x - off, y + off + diff, z - off,
        color
    );
    batch.rect(region, norm,
        x - off, y - off - diff, z - off,
        x + off, y - off - diff, z - off,
        x + off, y - off - diff, z + off,
        x - off, y - off - diff, z + off,
        color
    );
  }

  public static void rect(
      Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, Color color
  ){
    batch.rect(Core.atlas.white(), v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z, v4.x, v4.y, v4.z, color);
  }

  public static void rect(
      TextureRegion region,
      Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, Color color
  ){
    batch.rect(region, v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z, v4.x, v4.y, v4.z, color);
  }

  public static void rect(
      TextureRegion region, TextureRegion normRegion,
      Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, Color color
  ){
    batch.rect(region, normRegion, v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z, v4.x, v4.y, v4.z, color);
  }

  public static void rect(
      float x1, float y1, float z1,
      float x2, float y2, float z2,
      float x3, float y3, float z3,
      float x4, float y4, float z4,
      Color color
  ){
    batch.rect(Core.atlas.white(), x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, color);
  }

  public static void rect(
      TextureRegion region,
      float x1, float y1, float z1,
      float x2, float y2, float z2,
      float x3, float y3, float z3,
      float x4, float y4, float z4,
      Color color
  ){
    batch.rect(region, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, color);
  }

  public static void rect(
      TextureRegion region, TextureRegion normRegion,
      float x1, float y1, float z1,
      float x2, float y2, float z2,
      float x3, float y3, float z3,
      float x4, float y4, float z4,
      Color color
  ){
    batch.rect(region, normRegion, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, color);
  }

  public static void drawMesh(
      Mesh mesh
  ){

  }

  public static void drawMesh(
      TextureRegion region, TextureRegion normRegion, TextureRegion diffRegion, TextureRegion specRegion, Mesh mesh
  ){

  }

  public static void meshToStandardVertices(
      Mesh mesh, Mesh map, int[] attrMapIndex
  ){
    TextureRegion w = Core.atlas.white();
    float u = (w.u + w.u2)/2f;
    float v = (w.v + w.v2)/2f;

    VertexAttribute[] attrList = mesh.attributes;
    VertexAttribute[] toAttrList = map.attributes;
    FloatBuffer buffer = mesh.getVerticesBuffer();
    FloatBuffer toBuffer = map.getVerticesBuffer();
    toBuffer.position(0);
    toBuffer.limit(mesh.getMaxVertices()*map.vertexSize/4);

    int[] offList = new int[attrMapIndex.length];
    for (int i = 0, len = attrMapIndex.length; i < len; i++) {
      int index = attrMapIndex[i];
      int n = 0;
      for (int o = 0; o < index; o++) {
        n += toAttrList[o].size/4;
      }
      offList[i] = n;
    }

    int vertices = 0;
    int vertexSize = map.vertexSize/4;

    buffer.position(0);
    while (buffer.hasRemaining()){
      for (int i = 0, len = attrList.length; i < len; i++) {
        VertexAttribute attr = attrList[i];
        int s = attr.size/4;
        int off = offList[i];
        toBuffer.position(vertices * vertexSize + off);
        for (int j = 0; j < s; j++) {
          toBuffer.put(buffer.get());
        }
      }

      toBuffer.position(vertices * vertexSize + StandardBatch3D.coordOff); toBuffer.put(u); toBuffer.put(v);
      toBuffer.position(vertices * vertexSize + StandardBatch3D.coordNormOff); toBuffer.put(u); toBuffer.put(v);
      toBuffer.position(vertices * vertexSize + StandardBatch3D.coordSpecOff); toBuffer.put(u); toBuffer.put(v);

      toBuffer.position(vertices * vertexSize + StandardBatch3D.tangOff);
      toBuffer.put(0); toBuffer.put(0); toBuffer.put(0);

      vertices++;
    }
  }

  public static void draw(
      Texture texture, Texture normTexture, Texture diffTexture, Texture specTexture,
      float[] vertices, int off, int counts
  ){
    batch.vertices(texture, normTexture, diffTexture, specTexture, vertices, off, counts);
  }

  public static void setBatch(StandardBatch3D batch3D) {
    Draw3D.batch = batch3D;
  }

  public static void alpha(boolean b) {
    batch.setAlpha(b);
  }

  public static void begin(StandardBatch3D batch3D) {
    batch = batch3D;
    batch.begin();
  }

  public static void begin(StandardBatch3D batch3D, boolean cullFace) {
    batch = batch3D;
    batch.begin(cullFace);
  }

  public static void end() {
    batch.end();
    batch = null;
  }

  public static void lightDir(int x, int y, int z) {batch.setDirLight(x, y, z);}
  public static void dirLightColor(Color color) {batch.setDirLightColor(color);}
  public static void dirLightColor(Color color, float intensity) {batch.setDirLightColor(color, intensity);}
  public static LightSource getLightSource(int index){return batch.getLight(index);}
  public static void resetLights(){batch.resetLights();}
  public static void activeLights(int num){batch.activeLights(num);}
  public void setAmbientColor(Color color, float strength){batch.setAmbientColor(color, strength);}

  public static void setAmbientColor(float r, float g, float b, float strength) {
    batch.setAmbientColor(tempC1.set(r, g, b), strength);
  }

  public Color getAmbientColor() {return batch.getAmbientColor();}

  public static void camera(Camera3D camera) {
    batch.setCamera(camera);
  }
}

package singularity.graphic.graphic3d;

import arc.files.Fi;
import arc.graphics.*;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.g3d.Camera3D;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import singularity.graphic.SglShaders;
import singularity.graphic.graphic3d.DrawRequests.DrawRequest;
import singularity.graphic.graphic3d.DrawRequests.SortedDrawRequest;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class StandardBatch3D {
  protected static final Vec3 temp1 = new Vec3();
  public static final int
      coordOff = 3,
      coordNormOff = 5,
      coordSpecOff = 7,
      colorOff = 9,
      normOff = 10,
      tangOff = 13;

  protected FrameBuffer targetBuffer;

  // attributes
  protected final Mesh mesh;
  protected final int maxVertices;
  protected final int vertexSize;
  protected final int maxVerticesSize;
  protected final int primitiveType;

  // shaders
  protected Shader baseShader;
  protected Shader normalShader;
  protected Shader standardShader;

  // state - modified will cause this batch flush
  protected Texture lastTexture;
  protected Texture lastNormTexture;
  protected Texture lastSpecTexture;
  protected final Vec3 directionalLightDir = new Vec3(0, -1, 0);
  protected final Color directionalLightColor = new Color();
  protected final Mat3D transform = new Mat3D().idt();
  protected final Camera3D camera = new Camera3D();
  protected final Color ambientColor = new Color();
  protected final LightSource[] lights;
  protected int activeLights = 0;

  // variables - dynamicInformation
  protected boolean isAlpha = false;
  protected boolean enablePreTransform = false;
  protected final Mat3D preTransform = new Mat3D().idt();

  // batch state
  protected boolean flushing = false, sort = true, isDrawing = false;
  protected final FloatBuffer buffer;
  protected int vertexIdx = 0;
  protected DrawRequest[] noSortRequests = new DrawRequest[6400];
  protected int noSortReqCount = 0, noSortReqVertIdx = 0;
  protected SortedDrawRequest[] sortedRequests = new SortedDrawRequest[2048];
  protected float[] sortedRequestDst2 = new float[2048];
  protected int sortedReqCount = 0, sortedReqVertIdx = 0;
  protected float[] noSortReqVertices;
  protected float[] sortedReqVeritces;

  // temps
  protected float[] tempVertices;

  {
    for (int i = 0; i < noSortRequests.length; i++) {
      noSortRequests[i] = new DrawRequest();
    }

    for (int i = 0; i < sortedRequests.length; i++) {
      sortedRequests[i] = new SortedDrawRequest();
    }
  }

  public StandardBatch3D(int max) {
    this(max, 4);
  }

  public StandardBatch3D(int max, int maxLights) {
    this(max, maxLights, Gl.triangles);
  }

  public StandardBatch3D(int max, int maxLights, int primitiveType) {
    this.lights = new LightSource[maxLights];
    this.maxVertices = max*3;
    this.primitiveType = primitiveType;

    setupShaders(maxLights);

    VertexAttribute[] attributes = buildVertexAttributes();
    mesh = new Mesh(false, maxVertices, 0, attributes);
    vertexSize = mesh.vertexSize / 4;
    maxVerticesSize = maxVertices*vertexSize;
    buffer = mesh.getVerticesBuffer();
    buffer.position(0);
    buffer.limit(maxVerticesSize);

    noSortReqVertices = new float[10000 * vertexSize];
    sortedReqVeritces = new float[2048 * vertexSize];
    tempVertices = new float[64 * vertexSize];
  }

  protected void setupShaders(int maxLights){
    class MultiLightShader extends Shader{
      public MultiLightShader(Fi vertexShader, Fi fragmentShader) {
        super(vertexShader, fragmentShader);
      }

      @Override
      protected String preprocess(String source, boolean fragment) {
        source = "#define LIGHTS " + maxLights + "\n" + source;

        return super.preprocess(source, fragment);
      }
    }

    baseShader = new MultiLightShader(
        SglShaders.internalShaderDir.child("3d").child("standard_base.vert"),
        SglShaders.internalShaderDir.child("3d").child("standard_base.frag")
    );
    normalShader = new MultiLightShader(
        SglShaders.internalShaderDir.child("3d").child("standard_norm.vert"),
        SglShaders.internalShaderDir.child("3d").child("standard_norm.frag")
    );
    standardShader = new MultiLightShader(
        SglShaders.internalShaderDir.child("3d").child("standard.vert"),
        SglShaders.internalShaderDir.child("3d").child("standard.frag")
    );
  }

  public static VertexAttribute[] buildVertexAttributes(){
    Seq<VertexAttribute> attribs = Seq.with(
        VertexAttribute.position3,
        new VertexAttribute(2, Shader.texcoordAttribute + "0"),
        new VertexAttribute(2, Shader.texcoordAttribute + "1"),
        new VertexAttribute(2, Shader.texcoordAttribute + "2"),
        VertexAttribute.color,
        VertexAttribute.normal,
        new VertexAttribute(3, "a_tangent")
    );

    return attribs.toArray(VertexAttribute.class);
  }

  protected void applyShader(Shader shader){
    shader.bind();
    shader.apply();

    shader.setUniformMatrix4("u_proj", camera.projection.val);
    shader.setUniformMatrix4("u_view", camera.view.val);
    shader.setUniformMatrix4("u_transform", transform.val);

    shader.setUniformf("u_cameraPos", camera.position);
    shader.setUniformf("u_lightDir", directionalLightDir);
    shader.setUniformf("u_lightColor", directionalLightColor);
    shader.setUniformf("u_ambientColor", ambientColor);

    shader.setUniformi("u_activeLights", activeLights);
    for (int i = 0; i < lights.length; i++) {
      lights[i].apply(shader, i);
    }
  }

  protected void setSort(boolean sort) {
    this.sort = sort;
  }

  public void setTarget(FrameBuffer buffer) {
    if (isDrawing) flush();
    this.targetBuffer = buffer;
  }
  public FrameBuffer getTarget() {
    return targetBuffer;
  }

  public void preTransform(Mat3D transform){
    preTransform.set(transform);
  }
  public Mat3D getPreTransform(){
    return preTransform;
  }
  public void resetPreTransform(){
    preTransform.idt();
    enablePreTransform = false;
  }
  public void enablePreTransform(){
    enablePreTransform = true;
  }

  public Camera3D getCamera() {
    return camera;
  }
  public void setCamera(Camera3D camera) {
    flush();
    this.camera.width = camera.width;
    this.camera.height = camera.height;
    this.camera.perspective = camera.perspective;
    this.camera.position.set(camera.position);
    this.camera.direction.set(camera.direction);
    this.camera.up.set(camera.up);
    this.camera.update();
  }

  public void setShaders(Shader base, Shader normText, Shader standard){
    flush();
    this.baseShader = base;
    this.normalShader = normText;
    this.standardShader = standard;
  }
  public void setBaseShader(Shader baseShader) {
    flush();
    this.baseShader = baseShader;
  }
  public void setNormalShader(Shader normalShader) {
    flush();
    this.normalShader = normalShader;
  }
  public void setStandardShader(Shader standardShader) {
    flush();
    this.standardShader = standardShader;
  }
  public Shader getBaseShader(){ return baseShader; }
  public Shader getNormalShader(){ return normalShader; }
  public Shader getStandardShader(){ return standardShader; }

  public void setTransform(Mat3D trans){
    flush();
    transform.set(trans);
  }
  public Mat3D getTransform(){
    return transform;
  }

  public Vec3 getDirLight(){
    return directionalLightDir;
  }
  public void setDirLight(Vec3 pos){
    flush();
    directionalLightDir.set(pos);
  }
  public void setDirLight(float x, float y, float z){
    flush();
    directionalLightDir.set(x, y, z);
  }
  public void setDirLightColor(Color color){
    flush();
    directionalLightColor.set(color);
  }
  public void setDirLightColor(Color color, float intensity){
    flush();
    directionalLightColor.set(color);
    directionalLightColor.a(intensity);
  }
  public Color getDirLightColor(){
    return directionalLightColor;
  }

  public void resetLights(){
    flush();
    activeLights = 0;
  }
  public LightSource getLight(int index){
    return lights[index];
  }
  public void activeLights(int num){
    flush();
    activeLights = num;
  }

  public void setAmbientColor(Color color, float strength){
    flush();
    ambientColor.set(color);
    ambientColor.a(strength);
  }
  public Color getAmbientColor() {
    return ambientColor;
  }

  public void setAlpha(boolean isAlpha){
    this.isAlpha = isAlpha;
  }

  public void switchTexture(Texture texture){
    if (lastTexture != texture) {
      flush();
      lastTexture = texture;
    }
  }

  public void switchTexture(Texture texture, Texture normTexture){
    if (lastTexture != texture || lastNormTexture != normTexture) {
      flush();
      lastTexture = texture;
      lastNormTexture = normTexture;
    }
  }

  public void switchTexture(Texture texture, Texture normTexture, Texture specTexture){
    if (lastTexture != texture || lastNormTexture != normTexture || lastSpecTexture != specTexture) {
      flush();
      lastTexture = texture;
      lastNormTexture = normTexture;
      lastSpecTexture = specTexture;
    }
  }

  public void tri(
      Texture texture,
      float x1, float y1, float z1, float u1, float v1,
      float x2, float y2, float z2, float u2, float v2,
      float x3, float y3, float z3, float u3, float v3,
      Color color
  ){
    tri(
        texture, null, null,
        x1, y1, z1, u1, v1, 0, 0, 0, 0,
        x2, y2, z2, u2, v2, 0, 0, 0, 0,
        x3, y3, z3, u3, v3, 0, 0, 0, 0,
        color
    );
  }

  public void tri(
      Texture texture, Texture textureNorm,
      float x1, float y1, float z1, float u1, float v1, float un1, float vn1,
      float x2, float y2, float z2, float u2, float v2, float un2, float vn2,
      float x3, float y3, float z3, float u3, float v3, float un3, float vn3,
      Color color
  ){
    tri(
        texture, textureNorm, null,
        x1, y1, z1, u1, v1, un1, vn1, 0, 0,
        x2, y2, z2, u2, v2, un2, vn2, 0, 0,
        x3, y3, z3, u3, v3, un3, vn3, 0, 0,
        color
    );
  }

  @SuppressWarnings("DuplicatedCode")
  public void tri(
      Texture texture, Texture textureNorm, Texture textureSpec,
      float x1, float y1, float z1, float u1, float v1, float un1, float vn1, float us1, float vs1,
      float x2, float y2, float z2, float u2, float v2, float un2, float vn2, float us2, float vs2,
      float x3, float y3, float z3, float u3, float v3, float un3, float vn3, float us3, float vs3,
      Color color
  ){
    if (enablePreTransform) {
      temp1.set(x1, y1, z1);
      Mat3D.prj(temp1, preTransform);
      x1 = temp1.x; y1 = temp1.y; z1 = temp1.z;
      temp1.set(x2, y2, z2);
      Mat3D.prj(temp1, preTransform);
      x2 = temp1.x; y2 = temp1.y; z2 = temp1.z;
      temp1.set(x3, y3, z3);
      Mat3D.prj(temp1, preTransform);
      x3 = temp1.x; y3 = temp1.y; z3 = temp1.z;
    }

    float t1 = u2 - u1;
    float b1 = v2 - v1;
    float t2 = u3 - u1;
    float b2 = v3 - v1;

    float ex1 = x2 - x1, ey1 = y2 - y1, ez1 = z2 - z1;
    float ex2 = x3 - x1, ey2 = y3 - y1, ez2 = z3 - z1;

    float k = t1*b2 - b1*t2;

    float normalX = ey1*ez2 - ez1*ey2;
    float normalY = ez1*ex2 - ex1*ez2;
    float normalZ = ex1*ey2 - ey1*ex2;

    float tangX = k*(b2*ex1 - b1*ex2);
    float tangY = k*(b2*ey1 - b1*ey2);
    float tangZ = k*(b2*ez1 - b1*ez2);

    float ln = 1.0f/(float) Math.sqrt(normalX*normalX + normalY*normalY + normalZ*normalZ);
    normalX *= ln; normalY *= ln; normalZ *= ln;
    float lt = 1.0f/(float) Math.sqrt(tangX*tangX + tangY*tangY + tangZ*tangZ);
    tangX *= lt; tangY *= lt; tangZ *= lt;

    float[] vertices;
    int ind;

    int count = vertexSize*3;
    boolean batched = sort && !flushing;
    boolean sorted = isAlpha;
    int vertIdx = sorted? sortedReqVertIdx: noSortReqVertIdx;
    if (batched) {
      checkReqExpands(noSortReqCount + 1, sortedReqCount + 1);
      checkVertExpands(noSortReqVertIdx + count, sortedReqVertIdx + count);

      vertices = sorted? sortedReqVeritces : noSortReqVertices;
      ind = vertIdx;
    }
    else {
      if (vertexIdx + vertexSize*3 >= maxVerticesSize) flush();
      switchTexture(texture, textureNorm, textureSpec);
      vertices = checkTmpArr(count);
      ind = 0;
    }

    float colorBit = color.toFloatBits();
    vertices[ind] = x1; vertices[ind + 1] = y1; vertices[ind + 2] = z1;
    vertices[ind + coordOff] = u1; vertices[ind + coordOff + 1] = v1;
    vertices[ind + coordNormOff] = un1; vertices[ind + coordNormOff + 1] = vn1;
    vertices[ind + coordSpecOff] = us1; vertices[ind + coordSpecOff + 1] = vs1;
    vertices[ind + colorOff] = colorBit;
    vertices[ind + normOff] = normalX; vertices[ind + normOff + 1] = normalY; vertices[ind + normOff + 2] = normalZ;
    vertices[ind + tangOff] = tangX; vertices[ind + tangOff + 1] = tangY; vertices[ind + tangOff + 2] = tangZ;
    ind += vertexSize;

    vertices[ind] = x2; vertices[ind + 1] = y2; vertices[ind + 2] = z2;
    vertices[ind + coordOff] = u2; vertices[ind + coordOff + 1] = v2;
    vertices[ind + coordNormOff] = un2; vertices[ind + coordNormOff + 1] = vn2;
    vertices[ind + coordSpecOff] = us2; vertices[ind + coordSpecOff + 1] = vs2;
    vertices[ind + colorOff] = colorBit;
    vertices[ind + normOff] = normalX; vertices[ind + normOff + 1] = normalY; vertices[ind + normOff + 2] = normalZ;
    vertices[ind + tangOff] = tangX; vertices[ind + tangOff + 1] = tangY; vertices[ind + tangOff + 2] = tangZ;
    ind += vertexSize;

    vertices[ind] = x3; vertices[ind + 1] = y3; vertices[ind + 2] = z3;
    vertices[ind + coordOff] = u3; vertices[ind + coordOff + 1] = v3;
    vertices[ind + coordNormOff] = un3; vertices[ind + coordNormOff + 1] = vn3;
    vertices[ind + coordSpecOff] = us3; vertices[ind + coordSpecOff + 1] = vs3;
    vertices[ind + colorOff] = colorBit;
    vertices[ind + normOff] = normalX; vertices[ind + normOff + 1] = normalY; vertices[ind + normOff + 2] = normalZ;
    vertices[ind + tangOff] = tangX; vertices[ind + tangOff + 1] = tangY; vertices[ind + tangOff + 2] = tangZ;

    if (batched) {
      int requestIdx = sorted? sortedReqCount: noSortReqCount;
      DrawRequest request = sorted? sortedRequests[requestIdx]: noSortRequests[requestIdx];
      if (!sorted){
        DrawRequest lastRequest = requestIdx > 0? noSortRequests[requestIdx - 1]: null;

        if (lastRequest != null && lastRequest.mesh == null
            && lastRequest.texture == texture
            && lastRequest.normalTexture == textureNorm
            && lastRequest.specTexture == textureSpec
        ) {
          lastRequest.verticesSize += count;
          noSortReqVertIdx += count;
        }
        else {
          noSortReqCount++;
          noSortReqVertIdx += count;

          request.verticesOffset = vertIdx;
          request.verticesSize = count;
          request.texture = texture;
          request.normalTexture = textureNorm;
          request.specTexture = textureSpec;
        }
      }
      else {
        SortedDrawRequest sortedReq = (SortedDrawRequest) request;
        sortedReqCount++;
        sortedReqVertIdx += count;

        sortedReq.verticesOffset = vertIdx;
        sortedReq.verticesSize = count;
        sortedReq.texture = texture;
        sortedReq.normalTexture = textureNorm;
        sortedReq.specTexture = textureSpec;

        sortedReq.isAlpha = true;
        sortedReq.runTask = null;

        sortedReq.dst2 = calcVerticesDst(vertices, sortedReqVertIdx, count);
        sortedRequestDst2[requestIdx] = sortedReq.dst2;
      }
    }
    else {
      buffer.position(vertexIdx);
      buffer.put(vertices, 0, count);
      vertexIdx += count;
    }
  }
  public void vertices(Texture texture, float[] vertices, int offset, int counts){
    vertices(texture, null, null, null, vertices, offset, counts);
  }

  public void vertices(Texture texture, Texture normTexture, float[] vertices, int offset, int counts){
    vertices(texture, normTexture, null, null, vertices, offset, counts);
  }

  public void vertices(
      Texture texture, Texture normTexture, Texture diffTexture, Texture specTexture,
      float[] vertices, int offset, int counts
  ){
    if (sort && !flushing){
      checkReqExpands(noSortReqCount + 1, sortedReqCount + 1);
      checkVertExpands(noSortReqVertIdx + counts, sortedReqVertIdx + counts);
      boolean sorted = isAlpha;

      int requestIdx = sorted? sortedReqCount: noSortReqCount;
      DrawRequest request = sorted? sortedRequests[requestIdx]: noSortRequests[requestIdx];

      if (!sorted){
        putReqVertices(noSortReqVertices, noSortReqVertIdx, vertices, offset, counts);

        DrawRequest lastRequest = requestIdx > 0? noSortRequests[requestIdx - 1]: null;
        if (lastRequest != null && lastRequest.mesh == null
            && lastRequest.texture == texture && lastRequest.normalTexture == normTexture
            && lastRequest.specTexture == specTexture
        ){
          lastRequest.verticesSize += counts;
          noSortReqVertIdx += counts;
        }
        else {
          noSortReqCount++;

          request.texture = texture;
          request.normalTexture = normTexture;
          request.specTexture = specTexture;

          request.verticesOffset = noSortReqVertIdx;
          request.verticesSize = counts;
        }

        noSortReqVertIdx += counts;
      }
      else {
        putReqVertices(sortedReqVeritces, sortedReqVertIdx, vertices, offset, counts);
        SortedDrawRequest sortedReq = (SortedDrawRequest) request;

        sortedReqCount++;

        sortedReq.texture = texture;
        sortedReq.normalTexture = normTexture;
        sortedReq.specTexture = specTexture;

        sortedReq.verticesOffset = sortedReqVertIdx;
        sortedReq.verticesSize = counts;
        sortedReq.isAlpha = true;
        sortedReq.runTask = null;

        sortedReq.dst2 = calcVerticesDst(vertices, offset, counts);
        sortedRequestDst2[requestIdx] = sortedReq.dst2;

        sortedReqVertIdx += counts;
      }
    }
    else putVertices(texture, normTexture, specTexture, vertices, offset, counts);
  }

  public void draw(Texture texture, Texture normTexture, Texture specTexture, Mesh mesh){
    if (sort && !flushing){
      checkReqExpands(noSortReqCount + 1, sortedReqCount + 1);
      boolean sorted = isAlpha;

      DrawRequest request = sorted? sortedRequests[sortedReqCount]: noSortRequests[noSortReqCount];
      request.texture = texture;
      request.normalTexture = normTexture;
      request.specTexture = specTexture;
      request.mesh = mesh;

      if (sorted){
        sortedReqCount++;
        SortedDrawRequest sortedReq = (SortedDrawRequest) request;

        transform.getTranslation(temp1);
        sortedReq.dst2 = camera.position.dst2(temp1);
        sortedReq.isAlpha = true;
        sortedReq.runTask = null;
      }
      else {
        noSortReqCount++;
      }
    }
    else {
      putMesh(texture, normTexture, specTexture, isAlpha, mesh);
    }
  }

  protected void checkReqExpands(int noSortCount, int sortedCount){
    if (noSortRequests.length < noSortCount) {
      int oldSize = noSortRequests.length;
      noSortRequests = Arrays.copyOf(noSortRequests, noSortCount*7/4);
      for (int i = oldSize; i < sortedCount; i++) {
        noSortRequests[i] = new DrawRequest();
      }
    }
    if (sortedRequests.length < sortedCount) {
      int oldSize = sortedRequests.length;
      sortedRequests = Arrays.copyOf(sortedRequests, sortedCount*7/4);
      for (int i = oldSize; i < sortedCount; i++) {
        sortedRequests[i] = new SortedDrawRequest();
      }
      sortedRequestDst2 = Arrays.copyOf(sortedRequestDst2, sortedCount);
    }
  }

  protected void checkVertExpands(int noSortVertSize, int sortedVertSize){
    if (noSortReqVertices.length < noSortVertSize) {
      noSortReqVertices = Arrays.copyOf(noSortReqVertices, noSortVertSize);
    }
    if (sortedReqVeritces.length < sortedVertSize) {
      sortedReqVeritces = Arrays.copyOf(sortedReqVeritces, sortedVertSize);
    }
  }

  protected float calcVerticesDst(float[] vertices, int offset, int counts) {
    float dst = 0;
    float cx = camera.position.x, cy = camera.position.y, cz = camera.position.z;

    for (int i = offset; i < offset + counts; i += vertexSize) {
      float dx = vertices[i] - cx; float dy = vertices[i + 1] - cy; float dz = vertices[i + 2] - cz;
      dst = Math.max(dst, dx * dx + dy * dy + dz * dz);
    }

    return dst;
  }

  protected void putVertices(
      Texture texture, Texture normTexture, Texture specTexture,
      float[] vertices, int offset, int counts
  ){
    int verticesLen = maxVerticesSize;
    int remainingVertices = verticesLen;

    if (lastTexture != texture || lastNormTexture != normTexture || lastSpecTexture != specTexture) {
      switchTexture(texture, normTexture, specTexture);
    }
    else {
      remainingVertices -= vertexIdx;
      if(remainingVertices == 0){
        flush();
        remainingVertices = verticesLen;
      }
    }
    int copyCount = Math.min(remainingVertices, counts);
    FloatBuffer buf = buffer;

    if (enablePreTransform) {
      float[] tmpVert = checkTmpArr(counts);
      for (int i = 0; i < counts; i += vertexSize) {
        preTrn(tmpVert, i, vertices, offset + i, preTransform);
      }
      buf.put(tmpVert, 0, copyCount);
    }
    else {
      buf.put(vertices, offset, copyCount);
    }

    vertexIdx += copyCount;
    counts -= copyCount;

    while(counts > 0){
      offset += copyCount;
      flush();
      copyCount = Math.min(verticesLen, counts);
      if (enablePreTransform) {
        float[] tmpVert = checkTmpArr(counts);
        for (int i = 0; i < counts; i += vertexSize) {
          preTrn(tmpVert, i, vertices, offset + i, preTransform);
        }
        buf.put(tmpVert, 0, copyCount);
      }
      else {
        buf.put(vertices, offset, copyCount);
      }
      vertexIdx += copyCount;
      counts -= copyCount;
    }
  }

  protected void putMesh(Texture texture, Texture normalTexture, Texture specTexture, boolean isAlpha, Mesh mesh) {
    Shader shader = getCurrShader(normalTexture != null, specTexture != null);
    applyShader(shader);

    texture.bind(0);
    shader.setUniformi("u_texture", 0);

    if (normalTexture != null) {
      normalTexture.bind(1);
      shader.setUniformi("u_normalTex", 1);

      if (specTexture != null) {
        specTexture.bind(2);
        shader.setUniformi("u_specularTex", 2);
      }
    }

    if (isAlpha) Gl.depthMask(false);
    mesh.render(shader, primitiveType);
    if (isAlpha) Gl.depthMask(true);

    Gl.activeTexture(Gl.texture0);
  }

  protected void putReqVertices(float[] reqVertices, int vertexIdx, float[] vertices, int offset, int counts){
    if (enablePreTransform) {
      for (int i = 0; i < counts; i += vertexSize) {
        preTrn(reqVertices, vertexIdx + i, vertices, offset + i, preTransform);
      }
    }
    else {
      System.arraycopy(vertices, offset, reqVertices, vertexIdx, counts);
    }
  }

  protected static void preTrn(float[] vertices, int idx, float[] source, int off, Mat3D preTan) {
    //position
    temp1.set(source[off], source[off + 1], source[off + 2]);
    Mat3D.prj(temp1, preTan);
    vertices[idx] = temp1.x; vertices[idx + 1] = temp1.y; vertices[idx + 2] = temp1.z;

    //coords
    //base
    vertices[idx + 3] = source[off + 3];
    vertices[idx + 4] = source[off + 4];
    //norm
    vertices[idx + 5] = source[off + 5];
    vertices[idx + 6] = source[off + 6];
    //diff
    vertices[idx + 7] = source[off + 7];
    vertices[idx + 8] = source[off + 8];
    //spec
    vertices[idx + 9] = source[off + 9];
    vertices[idx + 10] = source[off + 10];

    //color
    vertices[idx + 11] = source[off + 11];

    //normal
    temp1.set(source[off + 12], source[off + 13], source[off + 14]);
    Mat3D.rot(temp1, preTan);
    vertices[idx + 12] = temp1.x; vertices[idx + 13] = temp1.y; vertices[idx + 14] = temp1.z;
    //tangent
    temp1.set(source[off + 15], source[off + 16], source[off + 17]);
    Mat3D.rot(temp1, preTan);
    vertices[idx + 15] = temp1.x; vertices[idx + 16] = temp1.y; vertices[idx + 17] = temp1.z;
    //subTangent
    temp1.set(source[off + 18], source[off + 19], source[off + 20]);
    Mat3D.rot(temp1, preTan);
    vertices[idx + 18] = temp1.x; vertices[idx + 19] = temp1.y; vertices[idx + 20] = temp1.z;
  }

  protected float[] checkTmpArr(int size) {
    if (tempVertices.length < size) {
      return this.tempVertices = new float[size];
    }
    return tempVertices;
  }

  public void rect(
      TextureRegion region,
      float x1, float y1, float z1,
      float x2, float y2, float z2,
      float x3, float y3, float z3,
      float x4, float y4, float z4,
      Color color
  ){
    tri(
        region.texture, null, null,
        x1, y1, z1, region.u, region.v2, 0, 0, 0, 0,
        x2, y2, z2, region.u2, region.v2, 0, 0, 0, 0,
        x3, y3, z3, region.u2, region.v, 0, 0, 0, 0,
        color
    );
    tri(
        region.texture, null, null,
        x3, y3, z3, region.u2, region.v, 0, 0, 0, 0,
        x4, y4, z4, region.u, region.v, 0, 0, 0, 0,
        x1, y1, z1, region.u, region.v2, 0, 0, 0, 0,
        color
    );
  }

  public void rect(
      TextureRegion region, TextureRegion normRegion,
      float x1, float y1, float z1,
      float x2, float y2, float z2,
      float x3, float y3, float z3,
      float x4, float y4, float z4,
      Color color
  ){
    tri(
        region.texture, normRegion.texture, null,
        x1, y1, z1, region.u, region.v2, normRegion.u, normRegion.v2, 0, 0,
        x2, y2, z2, region.u2, region.v2, normRegion.u2, normRegion.v2, 0, 0,
        x3, y3, z3, region.u2, region.v, normRegion.u2, normRegion.v, 0, 0,
        color
    );
    tri(
        region.texture, normRegion.texture, null,
        x3, y3, z3, region.u2, region.v, normRegion.u2, normRegion.v, 0, 0,
        x4, y4, z4, region.u, region.v, normRegion.u, normRegion.v, 0, 0,
        x1, y1, z1, region.u, region.v2, normRegion.u, normRegion.v2, 0, 0,
        color
    );
  }

  public void rect(
      TextureRegion region, TextureRegion normRegion, TextureRegion specRegion,
      float x1, float y1, float z1,
      float x2, float y2, float z2,
      float x3, float y3, float z3,
      float x4, float y4, float z4,
      Color color
  ){
    tri(
        region.texture, normRegion.texture, specRegion.texture,
        x1, y1, z1, region.u, region.v2, normRegion.u, normRegion.v2, specRegion.u, specRegion.v2,
        x2, y2, z2, region.u2, region.v2, normRegion.u2, normRegion.v2, specRegion.u2, specRegion.v2,
        x3, y3, z3, region.u2, region.v, normRegion.u2, normRegion.v, specRegion.u2, specRegion.v,
        color
    );
    tri(
        region.texture, normRegion.texture, specRegion.texture,
        x3, y3, z3, region.u2, region.v, normRegion.u2, normRegion.v, specRegion.u2, specRegion.v,
        x4, y4, z4, region.u, region.v, normRegion.u, normRegion.v, specRegion.u, specRegion.v,
        x1, y1, z1, region.u, region.v2, normRegion.u, normRegion.v2, specRegion.u, specRegion.v2,
        color
    );
  }

  public void begin(){
    begin(true);
  }

  public void begin(boolean cullFace){
    if (isDrawing) throw new RuntimeException("Batch already started");


    Blending.normal.apply();

    if (targetBuffer != null) {
      targetBuffer.begin(Color.clear);
    }

    Gl.enable(Gl.depthTest);
    Gl.depthMask(true);
    Gl.clear(Gl.depthBufferBit);

    Gl.depthFunc(Gl.less);

    if (cullFace) {
      Gl.enable(Gl.cullFace);
      Gl.cullFace(Gl.back);
    }

    isDrawing = true;
  }

  public void end(){
    if (!isDrawing) throw new RuntimeException("Batch not started");

    flush();

    if (targetBuffer != null) {
      targetBuffer.end();
      targetBuffer.blit(SglShaders.simpleScreen);
    }

    Gl.disable(Gl.cullFace);
    Gl.disable(Gl.depthTest);

    isDrawing = false;
  }

  public void flush(){
    flushRequests();
    flushVertices();
  }

  protected void flushVertices() {
    if (!isDrawing) throw new RuntimeException("Batch not started");

    if(vertexIdx < vertexSize*3) return;

    Shader shader = getCurrShader(lastNormTexture != null, lastSpecTexture != null);
    applyShader(shader);

    lastTexture.bind(0);
    shader.setUniformi("u_texture", 0);

    if (lastNormTexture != null) {
      lastNormTexture.bind(1);
      shader.setUniformi("u_normalTex", 1);

      if (lastSpecTexture != null) {
        lastSpecTexture.bind(2);
        shader.setUniformi("u_specularTex", 2);
      }
    }

    Mesh mesh = this.mesh;
    //calling buffer() marks it as dirty, so it gets reuploaded upon render
    mesh.getVerticesBuffer();

    buffer.position(0);
    buffer.limit(vertexIdx);

    if (isAlpha) Gl.depthMask(false);
    mesh.render(shader, primitiveType, 0, vertexIdx/vertexSize);
    if (isAlpha) Gl.depthMask(true);

    buffer.limit(buffer.capacity());
    buffer.position(0);

    vertexIdx = 0;

    Gl.activeTexture(Gl.texture0);
  }

  protected Shader getCurrShader(boolean hasNormal, boolean hasSpecular) {
    if (hasNormal && hasSpecular) return standardShader;
    else if (hasNormal) return normalShader;
    else return baseShader;
  }

  protected void flushRequests(){
    if(!flushing && (noSortReqCount > 0 || sortedReqCount > 0)){
      flushing = true;

      sortRequests();

      boolean lastPreTrnEnabled = enablePreTransform;
      boolean lastAlpha = isAlpha;
      enablePreTransform = false;

      isAlpha = false;
      DrawRequest[] requests = noSortRequests;
      int noSortCount = noSortReqCount;
      for (int i = 0; i < noSortCount; i++) {
        DrawRequest req = requests[i];

        if (req.mesh != null) {
          putMesh(
              req.texture, req.normalTexture, req.specTexture, false, req.mesh
          );
        }
        else {
          putVertices(
              req.texture, req.normalTexture, req.specTexture,
              noSortReqVertices, req.verticesOffset, req.verticesSize
          );
        }
      }

      isAlpha = true;
      SortedDrawRequest[] sortedReq = sortedRequests;
      int sortedCount = sortedReqCount;
      for (int i = 0; i < sortedCount; i++) {
        SortedDrawRequest req = sortedReq[i];

        if (req.mesh != null) {
          putMesh(
              req.texture, req.normalTexture, req.specTexture, req.isAlpha, req.mesh
          );
        }
        else {
          putVertices(
              req.texture, req.normalTexture, req.specTexture,
              noSortReqVertices, req.verticesOffset, req.verticesSize
          );
        }
      }

      enablePreTransform = lastPreTrnEnabled;
      isAlpha = lastAlpha;

      flushing = false;
      noSortReqCount = 0;
      sortedReqCount = 0;
      noSortReqVertIdx = 0;
      sortedReqVertIdx = 0;
    }
  }

  protected void sortRequests() {
    MergeSorter.init(sortedRequests.length);
    //if (multiThread) MergeSorter.sortMultiThreads(processorThreads, requests, dst2, 0, requestCount - 1);
    MergeSorter.sort(sortedRequests, sortedRequestDst2, 0, sortedReqCount - 1);
  }

  static class SortTask extends ForkJoinTask<Void> implements Pool.Poolable {
    int threads;
    SortedDrawRequest[] requests;
    float[] dst2;
    int left;
    int right;

    @Override
    public void reset() {
      requests = null;
      dst2 = null;
      left = right = threads = 0;
    }

    @Override public Void getRawResult() {return null;}
    @Override protected void setRawResult(Void value) {}
    @Override
    protected boolean exec() {
      MergeSorter.sortMultiThreads(threads, requests, dst2, left, right);
      return true;
    }
  }

  static class MergeSorter {
    private static final ForkJoinPool commonPool = new ForkJoinPool();

    static float[] tmpDst2l;
    static float[] tmpDst2r;
    static SortedDrawRequest[] tmpRequestsL;
    static SortedDrawRequest[] tmpRequestsR;

    public static void init(int size){
      if(tmpDst2l == null || tmpDst2l.length < size) {
        tmpDst2l = new float[size];
        tmpDst2r = new float[size];
        tmpRequestsL = new SortedDrawRequest[size];
        tmpRequestsR = new SortedDrawRequest[size];
      }
    }

    public static void sortMultiThreads(int threads, SortedDrawRequest[] requests, float[] dst2, int left, int right) {
      if (threads <= 1) sort(requests, dst2, left, right);
      else if (left < right) {
        int mid = (left + right) / 2;

        SortTask l = Pools.obtain(SortTask.class, SortTask::new);
        SortTask r = Pools.obtain(SortTask.class, SortTask::new);

        l.threads = r.threads = threads/2;
        l.dst2 = r.dst2 = dst2;
        l.requests = r.requests = requests;
        l.left = left; l.right = mid;
        r.left = mid + 1; r.right = right;

        ForkJoinTask<?> t1 = commonPool.submit(l);
        ForkJoinTask<?> t2 = commonPool.submit(r);

        t1.join();
        t2.join();

        Pools.free(l);
        Pools.free(r);

        merge(requests, dst2, left, mid, right);
      }
    }

    public static void sort(SortedDrawRequest[] requests, float[] dst2, int left, int right) {
      if (left < right) {
        if (right - left <= 16) {
          insert(requests, dst2, left, right);
        }
        else {
          int mid = (left + right)/2;
          sort(requests, dst2, left, mid);
          sort(requests, dst2, mid + 1, right);
          merge(requests, dst2, left, mid, right);
        }
      }
    }

    private static void insert(SortedDrawRequest[] requests, float[] dst2, int left, int right){
      for (int i = left + 1; i <= right; i++) {
        float key = dst2[i];
        SortedDrawRequest req = requests[i];
        int j = i - 1;
        while (j >= left && dst2[j] < key) {
          dst2[j + 1] = dst2[j];
          requests[j + 1] = requests[j];
          j--;
        }
        dst2[j + 1] = key;
        requests[j + 1] = req;
      }
    }

    private static void merge(SortedDrawRequest[] requests, float[] dst2, int left, int mid, int right) {
      int n1 = mid - left + 1;
      int n2 = right - mid;

      float[] tmpDl = tmpDst2l;
      SortedDrawRequest[] tmpRl = tmpRequestsL;
      float[] tmpDr = tmpDst2r;
      SortedDrawRequest[] tmpRr = tmpRequestsR;

      System.arraycopy(dst2, left, tmpDl, left, n1);
      System.arraycopy(requests, left, tmpRl, left, n1);
      System.arraycopy(dst2, mid + 1, tmpDr, mid + 1, n2);
      System.arraycopy(requests, mid + 1, tmpRr, mid + 1, n2);

      int i = 0, j = 0;
      int k = left;
      while (i < n1 && j < n2) {
        int ni = left + i;
        int nj = mid + 1 + j;
        if (tmpDl[ni] >= tmpDr[nj]) {
          dst2[k] = tmpDl[ni];
          requests[k] = tmpRl[ni];
          i++;
        } else {
          dst2[k] = tmpDr[nj];
          requests[k] = tmpRr[nj];
          j++;
        }
        k++;
      }

      while (i < n1) {
        int ni = left + i;
        dst2[k] = tmpDl[ni];
        requests[k] = tmpRl[ni];
        i++;
        k++;
      }

      while (j < n2) {
        int nj = mid + 1 + j;
        dst2[k] = tmpDr[nj];
        requests[k] = tmpRr[nj];
        j++;
        k++;
      }
    }
  }
}

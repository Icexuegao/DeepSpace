package ice.vars.layout;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.IntMap;
import arc.struct.Seq;
import mindustry.graphics.Pal;

import java.util.Arrays;

public class TechTreeLayout {
  private int[][] edgeMat;
  private int[][] distanceMat;

  private final Seq<Node> context = new Seq<>(Node.class);
  private final Seq<Node> rawContexts = new Seq<>(Node.class);
  private final Seq<Seq<Node>> layers = new Seq<>();

  public float alignWidth = 120;
  public float alignHeight = 120;

  public void reset(){
    context.clear();
    rawContexts.clear();
    layers.clear();
    distanceMat = null;
  }

  public void inputNodes(Seq<Node> nodes){
    context.addAll(nodes);
    rawContexts.addAll(nodes);
  }

  public void inputNodes(Iterable<Node> nodes){
    for (Node node : nodes) {
      context.add(node);
      rawContexts.add(node);
    }
  }

  @SuppressWarnings("unchecked")
  public void init(){
    layers.clear();

    for (int i = 0; i < context.size; i++) {
      context.get(i).contextIndex = i;
    }

    int[] maxDepth = new int[]{0};
    for (Node root : context.select(e -> e.parents.isEmpty())) {
      root.visitTree((depth, node) -> {
        node.contextDepth = depth;
        maxDepth[0] = Math.max(maxDepth[0], depth);
      });
    }

    Seq<Node>[] layer = new Seq[maxDepth[0] + 1];
    for (int i = 0; i <= maxDepth[0]; i++) {
      int depth = i;
      layer[i] = context.select(e -> e.contextDepth == depth);
    }
    layers.addAll(layer);

   /* edgeMat = new int[context.size][context.size];
    distanceMat = new int[context.size][context.size];*/

     edgeMat = new int[50][50];
    distanceMat = new int[50][50];
    for (int[] n : edgeMat) {
      Arrays.fill(n, -1);
    }
    for (int[] n : distanceMat) {
      Arrays.fill(n, -1);
    }

    for (Node node : context) {
      for (Node child : node.children) {
        edgeMat[node.contextIndex][child.contextIndex] = 1;
      }
    }

    standardLayers();
    insertLineMark();

    for (int i = 0; i < context.size; i++) {
      int cursor = i;

      Node curr = context.get(i);

      curr.visitTree((depth, node) -> {
        distanceMat[cursor][node.contextIndex] = depth;
        distanceMat[node.contextIndex][cursor] = depth;
      });
    }

    sortLayers();
  }

  private void standardLayers() {
    Seq<Node> aligned = new Seq<>();
    for (int lay = 0; lay < layers.size; lay++) {
      aligned.clear();
      for (Node node : layers.get(lay)) {
        for (Node child : node.children) {
          if (child.contextDepth == lay) {
            aligned.add(child);
            if (lay + 1 >= layers.size) {
              layers.add(new Seq<Node>());
            }
            layers.get(lay + 1).addUnique(child);
            child.contextDepth = lay + 1;
          }
        }
      }
      layers.get(lay).removeAll(aligned);
    }
  }

  private void insertLineMark() {
    for (Node node : context) {
      if (node.isLineMark) continue;
      for (Node child : node.children.copy()) {
        if (child.contextDepth - node.contextDepth > 1){
          node.delink(child);

          Node curr = node;
          for (int dep = 1; dep < child.contextDepth - node.contextDepth; dep++) {
            Node fc = curr;

            Seq<Node> lay = layers.get(node.contextDepth + dep);
            Node ins = lay.find(n -> n.isLineMark && n.parents.first() == fc);
            if (ins == null) {
              ins = new Node(true);
              rawContexts.add(ins);
              curr.addChildren(ins);
              ins.contextDepth = node.contextDepth + dep;
              lay.add(ins);
            }
            curr = ins;
          }
          curr.addChildren(child);
        }
      }
    }
  }

  private void sortLayers(){
    for (int i = layers.size - 2; i >= 0; i--) {
      Seq<Node> layer0 = layers.get(i + 1);
      Seq<Node> layer1 = layers.get(i);
      float[] order = new float[layer1.size];

      for (int l = 0; l < layer1.size; l++) {
        Node node = layer1.get(l);
        float o = 0;
        for (Node child : node.children) {
          o += layer0.indexOf(child);
        }
        order[l] = o/node.children.size;
      }

      layer1.sort((a, b) -> Float.compare(order[layer1.indexOf(a)], order[layer1.indexOf(b)]));
    }

    for (int i = 1; i < layers.size; i++) {
      Seq<Node> layer0 = layers.get(i - 1);
      Seq<Node> layer1 = layers.get(i);
      float[] order = new float[layer1.size];

      for (int l = 0; l < layer1.size; l++) {
        Node node = layer1.get(l);
        float o = 0;
        for (Node parent : node.parents) {
          o += layer0.indexOf(parent);
        }
        order[l] = o/node.parents.size;
      }

      layer1.sort((a, b) -> Float.compare(order[layer1.indexOf(a)], order[layer1.indexOf(b)]));
    }
  }

  @SuppressWarnings("unchecked")
  public void layout(){
    int maxHeight = 0;
    int refLayer = 0;

    for (int i = 0; i < layers.size; i++) {
      if (layers.get(i).size > maxHeight) {
        maxHeight = layers.get(i).size;
        refLayer = i;
      }
    }

    Seq<Node>[] grid = new Seq[layers.size];
    for (int i = 0; i < grid.length; i++) {
      grid[i] = new Seq<>();
      grid[i].setSize(maxHeight*2);
    }

    Seq<Node> seq = layers.get(refLayer);
    for (int i = 0; i < seq.size; i++) {
      grid[refLayer].set(i*2, seq.get(i));
    }

    for (int i = refLayer + 1; i < layers.size; i++) {
      Seq<Node> ref = grid[i - 1];
      Seq<Node> curr = layers.get(i);
      IntMap<Seq<Node>> inserts = new IntMap<>();

      for (Node node : curr) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Node parent : node.parents) {
          int index = ref.indexOf(parent);
          min = Math.min(min, index);
          max = Math.max(max, index);
        }
        
        int center = Mathf.ceil((min + max)/2f);
        inserts.get(center, Seq::new).add(node);
      }

      insertNodes(inserts, grid, i);
    }

    for (int i = refLayer - 1; i >= 0; i--){
      Seq<Node> ref = grid[i + 1];
      Seq<Node> curr = layers.get(i);
      IntMap<Seq<Node>> inserts = new IntMap<>();

      for (Node node : curr) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Node parent : node.children) {
          int index = ref.indexOf(parent);
          min = Math.min(min, index);
          max = Math.max(max, index);
        }

        int center = Mathf.ceil((min + max)/2f);
        inserts.get(center, Seq::new).add(node);
      }

      insertNodes(inserts, grid, i);
    }

    maxHeight = 0;
    for (Seq<Node> nodes : grid) {
      maxHeight = Math.max(maxHeight, nodes.size);
    }

    float width = layers.size * alignWidth;
    float height = maxHeight * alignHeight/2f;
    for (int l = 0; l < grid.length; l++) {
      Seq<Node> nodes = grid[l];
      for (int o = 0; o < nodes.size; o++) {
        Node node = nodes.get(o);
        if (node != null) {
          node.setX(-width/2 + l*alignWidth);
          node.setY(height/2 - o*alignHeight/2f);
        }
      }
    }

    for (int i = 0; i < layers.size; i++) {
      Seq<Node> layer = layers.get(i);
      Seq<Node> layer0 = i > 0? grid[i - 1]: null;
      Seq<Node> layer1 = i < grid.length - 1? grid[i + 1]: null;

      for (Node node : layer) {
        if (layer0 != null) {
          node.parents.sort((a, b) -> Float.compare(layer0.indexOf(a), layer0.indexOf(b)));
        }

        if (layer1 != null) {
          node.children.sort((a, b) -> Float.compare(layer1.indexOf(a), layer1.indexOf(b)));
        }
      }
    }
  }

  public Seq<Line> buildLines(float padWidth, float pad) {
    Seq<Line> result = new Seq<>();
    for (Node node : rawContexts) {
      float centerOrig = checkCenter(node, true);

      Node curr = node;
      while (curr.project == null) {
        curr = curr.parents.first();
      }
      boolean isCompleted = curr.project.isCompleted();

      for (Node child : node.children) {
        curr = child;
        while (curr.project == null) {
          curr = curr.children.first();
        }
        boolean childCompleted = curr.project.isCompleted();
        boolean childValid = curr.project.dependenciesCompleted();

        float centerTo = checkCenter(child, false);

        Draw.color(isCompleted && childValid? childCompleted? Pal.accent: Pal.accent.cpy().lerp(Color.lightGray, Mathf.absin(10, 1)): Color.lightGray);

        drawLine(result, node, centerOrig, child, centerTo, padWidth, pad);
      }
    }

    return result;
  }

  private void drawLine(Seq<Line> result, Node from, float centerFrom, Node to, float centerTo, float padWidth, float pad){
    int outs = from.children.size;

    int ordFrom = from.children.indexOf(to);
    int ordTo = to.parents.indexOf(from);

    float offOrd = ordFrom - centerFrom;
    float offToOrd = ordTo - centerTo;

    float offFrom = Mathf.floor(outs/2f)*pad - Mathf.floor(Math.abs(offOrd))*pad;
    float offTo = Mathf.floor(Math.abs(offToOrd))*pad;
    float off = padWidth/2f + offFrom + offTo;

    float originX = from.getX() + from.width/2f;
    float originY = from.getY() - offOrd*pad;

    float toX = to.getX() - to.width/2f;
    float toY = to.getY() - offToOrd*pad;

    Line line = new Line();
    line.from = from;
    line.to = to;
    line.beginX = originX;
    line.beginY = originY;
    line.endX = toX;
    line.endY = toY;
    line.offset = off;

    result.add(line);
  }

  static public float checkCenter(Node node, boolean isOut){
    Seq<Node> nodes = isOut? node.children: node.parents;
    float originY = node.getY();

    if (nodes.size == 1) return 0;

    for (int i = 0; i < nodes.size - 1; i++) {
      Node a = nodes.get(i);
      Node b = nodes.get(i + 1);

      if (Mathf.equal(a.getY(), node.getY(), 0.01f)) return i;
      if (Mathf.equal(b.getY(), node.getY(), 0.01f)) return i + 1;
      if (a.getY() > originY && b.getY() < originY) return i + 0.5f;
    }

    return nodes.size%2*0.5f;
  }

  private static void insertNodes(IntMap<Seq<Node>> inserts, Seq<Node>[] grid, int currLayer) {
    class Pair implements Comparable<Pair>{
      int center;
      final Seq<Node> nodes;

      Pair(int center, Seq<Node> node) {
        this.center = center;
        this.nodes = node;
      }

      @Override
      public int compareTo(Pair o) {
        return Integer.compare(center, o.center);
      }
    }

    Seq<Pair> pairs = new Seq<>();
    for (IntMap.Entry<Seq<Node>> entry : inserts.entries()) {
      pairs.add(new Pair(entry.key, entry.value));
    }
    pairs.sort();

    int cursor = pairs.first().center - pairs.first().nodes.size;
    if (cursor < 0){
      int off = -cursor;

      for (Seq<Node> lay : grid) {
        for (int i = 0; i < off; i++) {
          lay.insert(0, null);
        }
      }

      for (Pair pair : pairs) {
        pair.center += off;
      }

      cursor = 0;
    }

    for (Pair pair: pairs) {
      int off = pair.nodes.size/2*2;
      cursor = Math.max(cursor, pair.center - off);

      for (Node node : pair.nodes) {
        if (grid[currLayer].size <= cursor) grid[currLayer].setSize(cursor + 1);

        grid[currLayer].set(cursor, node);
        cursor += 2;
      }
    }
  }

  public int[][] getDistanceMat(){
    return distanceMat;
  }

  public int[][] getEdgeMat(){
    return edgeMat;
  }

  public Seq<Node> getContext(){
    return context;
  }

  public Seq<Node> getRawContexts(){
    return rawContexts;
  }

  public Seq<Seq<Node>> getLayers(){
    return layers;
  }
}

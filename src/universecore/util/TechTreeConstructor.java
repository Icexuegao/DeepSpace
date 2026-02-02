package universecore.util;

import arc.func.Cons;
import arc.struct.ObjectMap;
import arc.struct.Queue;
import arc.struct.Seq;
import mindustry.content.Planets;
import mindustry.content.TechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;

/**科技树的构建辅助工具，提供了一些构造科技树的方法
 *
 * @since 1.4
 * @author EBwilson*/
public class TechTreeConstructor{
  public final TechTree.TechNode node;

  private static TechTree.TechNode currRoot = Planets.serpulo.techTree;

  private final static ObjectMap<TechTree.TechNode, ObjectMap<UnlockableContent, TechTree.TechNode>> allMaps = new ObjectMap<>();
  private final static ObjectMap<TechTree.TechNode, TechTree.TechNode> all = new ObjectMap<>();
  private final static Queue<TechTree.TechNode> queue = new Queue<>();

  public static void rebuildAll(){
    allMaps.clear();
    all.clear();
    for(TechTree.TechNode root: TechTree.roots){
      allMaps.put(root, ObjectMap.of(root.content, root));
    }

    queue.clear();
    for(TechTree.TechNode node: TechTree.all){
      TechTree.TechNode curr = node;
      while(curr.parent != null && !allMaps.containsKey(curr.parent) && !all.containsKey(curr.parent)){
        queue.add(curr);
        curr = node.parent;
      }
      if(curr.parent != null){
        queue.add(curr);
        TechTree.TechNode root = allMaps.containsKey(curr.parent)? curr.parent: all.get(curr.parent);
        ObjectMap<UnlockableContent, TechTree.TechNode> map = allMaps.get(root, ObjectMap::new);

        for(TechTree.TechNode techNode: queue){
          map.put(techNode.content, techNode);
          all.put(techNode, root);
        }
      }
      queue.clear();
    }
  }

  /**设置当前处理科技树的根节点，任何时候使用这个类中的方法更改科技树时，都需要使用这个方法设置根节点
   *
   * @param root 这个科技树的根节点*/
  public static void currentRoot(TechTree.TechNode root){
    currRoot = root;
  }

  /**获取一个科技树节点的根节点
   *
   * @param node 目标科技树节点*/
  public static TechTree.TechNode getRoot(TechTree.TechNode node){
    return all.get(node);
  }

  /**获取指定的内容在当前指定的科技树中对应的科技树节点
   *
   * @param content 获取节点的内容*/
  public static TechTree.TechNode get(UnlockableContent content){
    TechTree.TechNode node = allMaps.get(currRoot, ObjectMap::new).get(content);
    if(node == null){
      rebuildAll();
      node = allMaps.get(currRoot, ObjectMap::new).get(content);
    }
    if(node == null) throw new RuntimeException("no such tech node \"" + content + "\" assign in tree that root by \"" + currRoot.content + "\"");
    return node;
  }

  public static Seq<TechTree.TechNode> getByRoot(TechTree.TechNode root){
    return allMaps.get(root, Empties.nilMapO()).values().toSeq();
  }

  public static void nodeProduce(UnlockableContent parent, UnlockableContent content, Seq<Objectives.Objective> objectives, Cons<TechTreeConstructor> child){
    new TechTreeConstructor(parent, content, content.researchRequirements(), objectives.add(new Objectives.Produce(content)), child);
  }

  public static void nodeProduce(UnlockableContent parent, UnlockableContent content, Cons<TechTreeConstructor> children){
    nodeProduce(parent, content, new Seq<>(), children);
  }

  public static void nodeProduce(TechTree.TechNode parent, UnlockableContent content, Seq<Objectives.Objective> objectives, Cons<TechTreeConstructor> child){
    new TechTreeConstructor(parent, content, content.researchRequirements(), objectives.add(new Objectives.Produce(content)), child);
  }

  public static void nodeProduce(TechTree.TechNode parent, UnlockableContent content, Cons<TechTreeConstructor> children){
    node(parent, content, new Seq<>(), children);
  }

  public static void node(UnlockableContent parent, UnlockableContent content, Seq<Objectives.Objective> objectives, Cons<TechTreeConstructor> child){
    new TechTreeConstructor(parent, content, content.researchRequirements(), objectives, child);
  }

  public static void node(UnlockableContent parent, UnlockableContent content, Cons<TechTreeConstructor> children){
    node(parent, content, new Seq<>(), children);
  }

  public static void node(TechTree.TechNode parent, UnlockableContent content, Seq<Objectives.Objective> objectives, Cons<TechTreeConstructor> child){
    new TechTreeConstructor(parent, content, content.researchRequirements(), objectives, child);
  }

  public static void node(TechTree.TechNode parent, UnlockableContent content, Cons<TechTreeConstructor> children){
    node(parent, content, new Seq<>(), children);
  }

  public TechTreeConstructor(TechTree.TechNode parent, UnlockableContent research, ItemStack[] requirements, Seq<Objectives.Objective> objectives, Cons<TechTreeConstructor> child){
    node = new TechTree.TechNode(parent, research, requirements);
    if(objectives != null) node.objectives.addAll(objectives);
    child.get(this);
  }

  public TechTreeConstructor(UnlockableContent parent, UnlockableContent research, Cons<TechTreeConstructor> child){
    this(parent, research, research.researchRequirements(), null, child);
  }

  public TechTreeConstructor(UnlockableContent parent, UnlockableContent research, Seq<Objectives.Objective> objectives, Cons<TechTreeConstructor> child){
    this(parent, research, research.researchRequirements(), objectives, child);
  }

  public TechTreeConstructor(UnlockableContent parent, UnlockableContent research, ItemStack[] requirements, Cons<TechTreeConstructor> child){
    this(parent, research, requirements, null, child);
  }

  public TechTreeConstructor(UnlockableContent parent, UnlockableContent research, ItemStack[] requirements, Seq<Objectives.Objective> objectives, Cons<TechTreeConstructor> child){
    this(get(parent), research, requirements, objectives, child);
  }

  public void node(UnlockableContent research, Cons<TechTreeConstructor> child){
    node(research, research.researchRequirements(), null, child);
  }

  public void node(UnlockableContent research, Seq<Objectives.Objective> objectives, Cons<TechTreeConstructor> child){
    node(research, research.researchRequirements(), objectives, child);
  }

  public void node(UnlockableContent research, ItemStack[] requirements, Cons<TechTreeConstructor> child){
    node(research, requirements, null, child);
  }

  public void node(UnlockableContent research, ItemStack[] requirements, Seq<Objectives.Objective> objectives, Cons<TechTreeConstructor> child){
    new TechTreeConstructor(node, research, requirements, objectives, child);
  }

  public void nodeProduce(UnlockableContent content, Cons<TechTreeConstructor> children){
    nodeProduce(content, new Seq<>(), children);
  }

  public void nodeProduce(UnlockableContent content, Seq<Objectives.Objective> objectives, Cons<TechTreeConstructor> child){
    new TechTreeConstructor(node, content, content.researchRequirements(), objectives.add(new Objectives.Produce(content)), child);
  }
}

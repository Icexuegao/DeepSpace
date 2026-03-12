package ice

import arc.math.Mathf
import arc.struct.Seq
import kotlin.math.min

/**
 * 力导布局算法实现
 * 用于图的可视化布局，通过模拟物理力使节点均匀分布
 */
class ForceDirectedLayout {
  companion object {
    // 力导布局参数
    const val FORCE_DIRECTED_REPULSION = 100f      // 节点间斥力强度
    const val FORCE_DIRECTED_ATTRACTION = 0.01f    // 边引力强度
    const val FORCE_DIRECTED_DAMPING = 0.85f       // 阻尼系数 (速度衰减)
    const val FORCE_DIRECTED_MAX_VELOCITY = 10f    // 最大速度限制
    const val FORCE_DIRECTED_ITERATIONS = 50
  }

  /**
   * 图节点类
   */
  data class Node(
    val id: Int, var x: Float = 0f, var y: Float = 0f, var vx: Float = 0f,  // X 方向速度
    var vy: Float = 0f,  // Y 方向速度
    var fixed: Boolean = false  // 是否固定位置
  )

  /**
   * 图边类
   */
  data class Edge(
    val from: Node, val to: Node, val length: Float = 100f  // 期望的边长度
  )

  private val nodes = Seq<Node>()
  private val edges = Seq<Edge>()

  /**
   * 添加节点
   * @param id 节点 ID
   * @param x X 坐标
   * @param y Y 坐标
   * @param fixed 是否固定位置（固定的节点不会受力移动）
   */
  fun addNode(id: Int, x: Float = 0f, y: Float = 0f, fixed: Boolean = false): Node {
    val node = Node(id, x, y, fixed = fixed)
    nodes.add(node)
    return node
  }

  /**
   * 添加边
   */
  fun addEdge(from: Node, to: Node, length: Float = 100f): Edge {
    val edge = Edge(from, to, length)
    edges.add(edge)
    return edge
  }

  /**
   * 清除所有节点和边
   */
  fun clear() {
    nodes.clear()
    edges.clear()
  }

  /**
   * 执行力导布局算法
   * @param iterations 迭代次数，默认 50 次
   * @param repulsion 斥力强度，默认 100
   * @param attraction 引力强度，默认 0.01
   * @param damping 阻尼系数，默认 0.85
   */
  fun layout(
    iterations: Int = FORCE_DIRECTED_ITERATIONS, repulsion: Float = FORCE_DIRECTED_REPULSION, attraction: Float = FORCE_DIRECTED_ATTRACTION, damping: Float = FORCE_DIRECTED_DAMPING
  ) {
    // 初始化节点位置 (随机分布在画布中心区域)
    initializePositions()

    // 迭代计算
    for (i in 0 until iterations) {
      // 1. 计算节点间的斥力 (库仑定律)
      calculateRepulsion(repulsion)

      // 2. 计算边的引力 (胡克定律)
      calculateAttraction(attraction)

      // 3. 更新节点位置
      updatePositions(damping)
    }
  }

  private var initialized = false  // 添加初始化标记

  /**
   * 渐进式布局 (适用于动画效果)
   * @param delta 时间增量（秒）
   * @param repulsion 斥力强度
   * @param attraction 引力强度
   * @param damping 阻尼系数
   */
  fun layoutStep(
    delta: Float = 0.016f,  // 默认约 60FPS
    repulsion: Float = FORCE_DIRECTED_REPULSION, attraction: Float = FORCE_DIRECTED_ATTRACTION, damping: Float = FORCE_DIRECTED_DAMPING
  ) {
    // 首次调用时初始化位置
    if (!initialized) {
      initializePositions()
      initialized = true
    }

    // 根据时间增量缩放力的效果
    val timeScale = min(delta * 60f, 3f)  // 归一化到 60FPS，最大 3 倍

    calculateRepulsion(repulsion * timeScale)
    calculateAttraction(attraction * timeScale)
    updatePositions(damping, delta)
  }

  /**
   * 初始化节点位置
   */
  private fun initializePositions() {
    if (nodes.size == 0) return

    for ((_, node) in nodes.withIndex()) {
      if (!node.fixed) {
        // 给每个节点一个微小的随机偏移，避免完全重叠
        node.x = Mathf.random(-1f, 1f)
        node.y = Mathf.random(-1f, 1f)
        node.vx = 0f
        node.vy = 0f
      }
    }

    initialized = true
  }

  /**
   * 计算节点间的斥力
   * 使用库仑定律：F = k / d²
   */
  private fun calculateRepulsion(repulsion: Float) {
    for (i in 0 until nodes.size) {
      val node1 = nodes[i]
      for (j in i + 1 until nodes.size) {
        val node2 = nodes[j]

        val dx = node2.x - node1.x
        val dy = node2.y - node1.y
        val distance = Mathf.sqrt(dx * dx + dy * dy)

        if (distance > 0.1f) {
          // 斥力与距离平方成反比
          val force = repulsion / (distance * distance)

          // 计算力的分量
          val fx = (dx / distance) * force
          val fy = (dy / distance) * force

          // 施加力到两个节点 (方向相反)
          if (!node1.fixed) {
            node1.vx -= fx
            node1.vy -= fy
          }
          if (!node2.fixed) {
            node2.vx += fx
            node2.vy += fy
          }
        }
      }
    }
  }

  /**
   * 计算边的引力
   * 使用胡克定律：F = k * d
   */
  private fun calculateAttraction(attraction: Float) {
    for (edge in edges) {
      val dx = edge.to.x - edge.from.x
      val dy = edge.to.y - edge.from.y
      val distance = Mathf.sqrt(dx * dx + dy * dy)

      if (distance > 0.1f) {
        // 引力与距离成正比 (相对于期望长度)
        val displacement = distance - edge.length
        val force = attraction * displacement

        // 计算力的分量
        val fx = (dx / distance) * force
        val fy = (dy / distance) * force

        // 施加力到两个节点 (相互吸引)
        if (!edge.from.fixed) {
          edge.from.vx += fx
          edge.from.vy += fy
        }
        if (!edge.to.fixed) {
          edge.to.vx -= fx
          edge.to.vy -= fy
        }
      }
    }
  }

  /**
   * 更新节点位置
   * @param damping 阻尼系数
   * @param delta 时间增量
   */
  private fun updatePositions(damping: Float, delta: Float = 0.016f) {
    val timeScale = min(delta * 60f, 3f)  // 归一化到 60FPS

    for (node in nodes) {
      if (!node.fixed) {
        // 限制最大速度
        val velocity = Mathf.sqrt(node.vx * node.vx + node.vy * node.vy)
        if (velocity > FORCE_DIRECTED_MAX_VELOCITY) {
          val scale = FORCE_DIRECTED_MAX_VELOCITY / velocity
          node.vx *= scale
          node.vy *= scale
        }

        // 应用阻尼
        node.vx *= damping
        node.vy *= damping

        // 更新位置（考虑时间增量）
        node.x += node.vx * timeScale
        node.y += node.vy * timeScale
      }
    }
  }



  /**
   * 获取所有节点
   */
  fun getNodes(): Seq<Node> = nodes

  /**
   * 获取所有边
   */
  fun getEdges(): Seq<Edge> = edges

  /**
   * 检查布局是否已稳定 (所有节点速度接近 0)
   */
  fun isStable(threshold: Float = 0.01f): Boolean {
    for (node in nodes) {
      if (!node.fixed) {
        val velocity = Mathf.sqrt(node.vx * node.vx + node.vy * node.vy)
        if (velocity > threshold) return false
      }
    }
    return true
  }
}
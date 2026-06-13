package ice.ui.galgame

object DialogueEngineObject {
  val engine = DialogueEngine("start")

  init {

    engine.addNode(
      DialogueNode("start") {
        text = "🏰 【小镇广场】\n你站在小镇广场上，一位白发老人向你走来..."
        nextId = "main_ask"
      })

    engine.addNode(
      DialogueNode("main_ask") {
        text = "👴 老人：勇敢的冒险者，我的传家项链被森林里的怪物偷走了！\n你能帮我找回来吗？"
        options = listOf(
          DialogueOption("✅ 接受任务（进入长分支）", "branch_long_1"), DialogueOption("❌ 拒绝任务", "branch_refuse_1")
        )
      })

// ========== 长分支（5段对话，最后回主线） ==========
    engine.addNode(
      DialogueNode("branch_long_1") {
        text = "🗡️ 你：当然可以！我正好需要锻炼一下。\n老人：太好了！项链应该在森林深处的洞穴里。"
        nextId = "branch_long_2"
      })

    engine.addNode(
      DialogueNode("branch_long_2") {
        text = "🌲 【你进入了黑暗森林】\n一路上你遇到了几只哥布林，轻松击败了它们。"
        nextId = "branch_long_3"
      })

    engine.addNode(
      DialogueNode("branch_long_3") {
        text = "🏔️ 你来到了洞穴入口，里面传来诡异的声响..."
        options = listOf(
          DialogueOption("🔥 举着火把进去", "branch_long_4"), DialogueOption("⚡ 直接冲进去", "branch_long_4")
        )
      })

    engine.addNode(
      DialogueNode("branch_long_4") {
        text = "🕷️ 洞穴深处，你发现了一只巨大的蜘蛛守护着项链！\n经过激烈的战斗，你击败了蜘蛛，拿到了项链！"
        nextId = "main_return_1"
      })

// ========== 拒绝分支（临时插入2句话，然后回主线） ==========
    engine.addNode(
      DialogueNode("branch_refuse_1") {
        text = "🗡️ 你：抱歉，我没时间。\n老人：求你了！我会给你丰厚报酬的！"
        options = listOf(
          DialogueOption("💔 还是拒绝", "ending_bad"), DialogueOption("😅 好吧好吧，我帮你", "temp_insert_1")
        )
      })

// 临时插入对话（2句话后回到长分支）
    engine.addNode(
      DialogueNode("temp_insert_1") {
        text = "👴 老人：太感谢了！你真是好人！\n森林里的怪物很危险，你要小心啊。"
        nextId = "temp_insert_2"
      })

    engine.addNode(
      DialogueNode("temp_insert_2") {
        text = "🗡️ 你：放心吧，我会小心的。\n【你决定接受任务，向森林出发】"
        nextId = "branch_long_1"
      })

// ========== 主线回归点 ==========
    engine.addNode(
      DialogueNode("main_return_1") {
        text = "🏆 【回到小镇】\n你把项链还给老人，他激动得热泪盈眶。\n👴 老人：谢谢你！这些金币给你，还有这把宝剑啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊！"
        onEnter = {

        }
        options = listOf(
          DialogueOption("🎉 继续冒险（好结局）", "ending_good"), DialogueOption("🏠 回家休息（普通结局）", "ending_normal")
        )
      })

// ========== 结局 ==========
    engine.addNode(
      DialogueNode("ending_good") {
        text = "✨ 【好结局】✨\n你成为了小镇的英雄，带着宝剑继续冒险，\n最终成为了传说中的勇者！\n\n游戏结束"
      })

    engine.addNode(
      DialogueNode("ending_normal") {
        text = "🌅 【普通结局】\n你回到家中，过上了平静的生活。\n偶尔会想起这段冒险...\n\n游戏结束"
      })

    engine.addNode(
      DialogueNode("ending_bad") {
        text = "💀 【坏结局】💀\n老人失望地离开了，再也没有人来找你帮忙。\n你后悔地摇了摇头...\n\n游戏结束"
      })
  }
}
package ice.content

import arc.util.Scaling
import ice.content.block.CrafterBlocks
import ice.graphics.IceColor
import ice.library.scene.element.typinglabel.TLabel
import ice.library.scene.ui.itooltip
import ice.type.Remains
import ice.ui.menusDialog.RemainsDialog
import ice.ui.menusDialog.RemainsDialog.slotPos
import ice.world.content.blocks.environment.IceOreBlock
import ice.world.content.unit.ability.InterceptAbilty
import mindustry.Vars
import mindustry.type.ItemStack
import mindustry.type.UnitType
import mindustry.world.meta.Stats
import universecore.world.consumers.ConsumeType

@Suppress("unused")
object Remainss {
  val 娜雅的手串 = Remains("娜雅的手串").apply {
    setDescription("一串温润的玉石手串,在帝国任职期间由娜雅赠予")
    effect = "核心机增加拦截护盾"
    val units = IUnitTypes.getCoreUnits()
    var map = HashMap<UnitType, InterceptAbilty>()
    units.forEach {
      map[it] = InterceptAbilty(40f, it.hitSize + 5)
    }
    install = {
      units.forEach {
        it.abilities.addUnique(map[it])
        it.stats = Stats()
        it.checkStats()
      }
    }
    uninstall = {
      units.forEach {
        it.abilities.remove(map[it])
        it.stats = Stats()
        it.checkStats()
      }
    }
  }
  val 坚固的装甲板 = Remains("坚固的装甲板").apply {
    val hea = 500
    setDescription("多层淬火钢板铆接而成,表面布满划痕与凹坑")
    effect = "单位[${IUnitTypes.断业.localizedName}]的生命值提升[$hea]"
    install = {
      IUnitTypes.断业.health += hea
      IUnitTypes.断业.stats = Stats()
      IUnitTypes.断业.checkStats()
    }
    uninstall = {
      IUnitTypes.断业.health -= hea
      IUnitTypes.断业.stats = Stats()
      IUnitTypes.断业.checkStats()
    }
  }
  val 不焚者的余烬 = Remains("不焚者的余烬").apply {
    val f = 5
    setDescription("温热的结晶体,烈焰中被焚尽却未曾死去之人的最后残留")
    effect = "单位[${IUnitTypes.仆从.localizedName}]的武器伤害提升[$f]"
    install = {
      IUnitTypes.仆从.weapons.forEach {
        it.bullet.damage += if (it.mirror) f / 2 else f
      }
      IUnitTypes.仆从.stats = Stats()
      IUnitTypes.仆从.checkStats()
    }
    uninstall = {
      IUnitTypes.仆从.weapons.forEach {
        it.bullet.damage -= if (it.mirror) f / 2 else f
      }
      IUnitTypes.仆从.stats = Stats()
      IUnitTypes.仆从.checkStats()
    }
  }
  val 纯净水晶坠饰 = Remains("纯净水晶坠饰").apply {
    setDescription("一块天然形成,毫无杂质的透明白水晶")
    effect = "玩家核心机[免疫所有状态]"
    val units = IUnitTypes.getCoreUnits()
    install = {
      units.forEach {
        it.immunities.addAll(Vars.content.statusEffects())
        it.stats = Stats()
        it.checkStats()
      }

    }
    uninstall = {
      units.forEach {
        it.immunities.clear()
        it.stats = Stats()
        it.checkStats()
      }
    }
  }
  val 不朽者胚胎 = Remains("不朽者胚胎").apply {
    val pos = 2
    level = 1
    color = IceColor.r2
    install = {
      slotPos += pos
    }
    uninstall = {
      slotPos -= pos
    }
    val text = "一个被囚禁的血肉胚胎\n拥抱我,我将赐你永恒\n不必畏惧刀剑与瘟疫,不必屈服于时光与死亡\n用你的过去,换取未来\n用你的灵魂,换取存在\n直至你我合而为一"//
    setDescriptionTable {
      for (string in text.split("\n")) {
        it.add(TLabel(string)).grow().wrap().pad(5f).color(color).row()
      }
    }
    effect = "遗物槽位+[$pos]"
    disabled = {
      Vars.state.isGame || (RemainsDialog.enableSeq.contains(this) && RemainsDialog.enableSeq.size > slotPos - pos)
    }
  }
  val 脊骨寄生虫 = Remains("脊骨寄生虫").apply {
    color = IceColor.r2
    setDescriptionTable {
      it.add("一种具有高度神经亲和性的节状生物,渴望与血肉生物的中枢神经系统结合").grow().wrap().pad(5f).color(color).row()
      it.table { table ->
        table.add("影响单位: ").pad(5f).color(color)
        table.image(IUnitTypes.蚀虻.uiIcon).size(45f).scaling(Scaling.fit).itooltip("${IUnitTypes.蚀虻.localizedName}")
      }
    }
    val fg = 1.2f
    effect = "[爬行类]血肉畸变体速度提升[${((fg - 1) * 100).toInt()}%]"
    install = {
      IUnitTypes.蚀虻.speed *= fg
      IUnitTypes.蚀虻.stats = Stats()
      IUnitTypes.蚀虻.checkStats()
      IUnitTypes.蚀虻Middle.speed *= fg
      IUnitTypes.蚀虻Middle.stats = Stats()
      IUnitTypes.蚀虻Middle.checkStats()
      IUnitTypes.蚀虻End.speed *= fg
      IUnitTypes.蚀虻End.stats = Stats()
      IUnitTypes.蚀虻End.checkStats()
    }
    uninstall = {
      IUnitTypes.蚀虻.speed /= fg
      IUnitTypes.蚀虻.stats = Stats()
      IUnitTypes.蚀虻.checkStats()
      IUnitTypes.蚀虻Middle.speed /= fg
      IUnitTypes.蚀虻Middle.stats = Stats()
      IUnitTypes.蚀虻Middle.checkStats()
      IUnitTypes.蚀虻End.speed /= fg
      IUnitTypes.蚀虻End.stats = Stats()
      IUnitTypes.蚀虻End.checkStats()
    }
  }
  val 心跳鼓 = Remains("心跳鼓").apply {
    color = IceColor.r2
    setDescription("带有奇异弹性的心肌隔膜,沉稳的节拍能让你的心跳同步")

    effect = "使状态[${IStatus.回响.localizedName}]的影响提升[20%]"
    install = {
      IStatus.回响.speedMultiplier += 0.2f
      IStatus.回响.stats = Stats()
      IStatus.回响.checkStats()
    }
    uninstall = {
      IStatus.回响.speedMultiplier -= 0.2f
      IStatus.回响.stats = Stats()
      IStatus.回响.checkStats()
    }
  }
  val 玄岩板 = Remains("玄岩板").apply {
    setDescription("由奇异,沉重的玄武岩打磨而成")
    effect = "[${CrafterBlocks.碳控熔炉.localizedName}]所需燃料减少[1]"
    var itemStack = ItemStack()
    CrafterBlocks.碳控熔炉.consumers.find {
      it.get(ConsumeType.item)!!.consItems!!.find { stack ->
        val bool: Boolean = stack.item == IItems.生煤
        if (bool) itemStack = stack
        bool
      } != null
    }

    install = {
      itemStack.amount -= 1
      CrafterBlocks.碳控熔炉.stats = Stats()
      CrafterBlocks.碳控熔炉.checkStats()
    }
    uninstall = {
      itemStack.amount += 1
      CrafterBlocks.碳控熔炉.stats = Stats()
      CrafterBlocks.碳控熔炉.checkStats()
    }
  }
  val 谐振探针 = Remains("谐振探针").apply {
    setDescription("一种用于探测矿物谐振频率的装置")
    effect = "矿物地板不再[隐藏]"
    install = {
      Vars.content.blocks().forEach {
        if (it is IceOreBlock) {
          it.display = true
        }
      }
      Vars.renderer.blocks.floor.reload()
    }
    uninstall = {
      Vars.content.blocks().forEach {
        if (it is IceOreBlock) {
          it.display = false
        }
      }
      Vars.renderer.blocks.floor.reload()
    }
  }
  val 流光罗盘 = Remains("流光罗盘").apply {
    setDescription("表面刻有古老的符文,会发出淡淡的光芒")
    effect = "核心机增加[1]速度"
    val lucifer = IUnitTypes.路西法
    install = {
      lucifer.speed += 1f
      lucifer.stats = Stats()
      lucifer.checkStats()
    }
    uninstall = {
      lucifer.speed -= 1f
      lucifer.stats = Stats()
      lucifer.checkStats()
    }
  }
}
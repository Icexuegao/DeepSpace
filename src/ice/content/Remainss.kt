package ice.content

import arc.util.Scaling
import ice.content.block.CrafterBlocks
import ice.content.block.DefenseBlocks
import ice.content.remains.迷思海
import ice.graphics.IceColor
import ice.library.IFiles.appendModName
import ice.library.scene.element.typinglabel.TLabel
import ice.library.scene.style.DynamicTextureDrawable
import ice.library.scene.ui.itooltip
import ice.type.Remains
import ice.ui.menusDialog.RemainsDialog.slotPos
import ice.world.content.blocks.environment.IceOreBlock
import ice.world.content.unit.ability.InterceptAbilty
import mindustry.Vars
import mindustry.content.StatusEffects
import mindustry.type.ItemStack
import mindustry.type.UnitType
import mindustry.world.meta.Stats
import universecore.world.consumers.ConsumeType

@Suppress("unused")
object Remainss {
  val 娜雅的手串 = Remains("remains_naya_bracelet").apply {
    localization {
      zh_CN {
        this.localizedName = "娜雅的手串"
        description = "一串温润的玉石手串,在帝国任职期间由娜雅赠予"
      }
    }
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
  val 坚固的装甲板 = Remains("remains_armor_plates").apply {
    localization {
      zh_CN {
        this.localizedName = "坚固的装甲板"
        description = "多层淬火钢板铆接而成,表面布满划痕与凹坑"
      }
    }
    val hea = 500
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
  val 不焚者的余烬 = Remains("remains_unburnt_ashes").apply {
    localization {
      zh_CN {
        this.localizedName = "不焚者的余烬"
        description = "温热的结晶体,烈焰中被焚尽却未曾死去之人的最后残留"
      }
    }
    val f = 5
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
  val 纯净水晶坠饰 = Remains("remains_pure_crystal_pendant").apply {
    localization {
      zh_CN {
        this.localizedName = "纯净水晶坠饰"
        description = "一块天然形成,毫无杂质的透明白水晶"
      }
    }
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
  val 玄岩板 = Remains("remains_basalt_plate").apply {
    localization {
      zh_CN {
        this.localizedName = "玄岩板"
        description = "由奇异,沉重的玄武岩打磨而成"
      }
    }
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
  val 谐振探针 = Remains("remains_resonance_probe").apply {
    localization {
      zh_CN {
        this.localizedName = "谐振探针"
        description = "一种用于探测矿物谐振频率的装置"
      }
    }
    effect = "矿物地板不再[隐藏]"
    install = {
      Vars.content.blocks().forEach {
        if (it is IceOreBlock) {
          it.display = true
          it.useColor = true
        }
      }
      Vars.renderer.blocks.floor.reload()
      Vars.renderer.minimap.reset()
    }
    uninstall = {
      Vars.content.blocks().forEach {
        if (it is IceOreBlock) {
          it.display = false
          it.useColor = false
        }
      }
      Vars.renderer.blocks.floor.reload()
      Vars.renderer.minimap.reset()
    }
  }
  val 流光罗盘 = Remains("remains_flowing_compass").apply {
    localization {
      zh_CN {
        this.localizedName = "流光罗盘"
        description = "表面刻有古老的符文,会发出淡淡的光芒"
      }
    }
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 19
      it.frameDuration = 60f / 7f
    }
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

  val 不朽者胚胎 = Remains("remains_immortal_embryo").apply {
    localization {
      zh_CN {
        this.localizedName = "不朽者胚胎"
      }
    }

    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 16
      it.frameDuration = 60f / 4f
    }
    val pos = 2
    level = 1
    remainsColor = IceColor.r2
    install = {
      slotPos += pos
    }
    uninstall = {
      slotPos -= pos
    }
    val text = """
      一个被囚禁的血肉胚胎
      拥抱我,我将赐你永恒
      不必畏惧刀剑与瘟疫,不必屈服于时光与死亡
      用你的过去,换取未来
      用你的灵魂,换取存在
      直至你我合而为一
    """.trimIndent()
    setDescriptionTable {
      for(string in text.split("\n")) {
        it.add(TLabel(string)).pad(5f).color(remainsColor).row()
      }
    }
    effect = "遗物槽位+[$pos]"
    disabled = {
      Vars.state.isGame || (Remains.getEnableds().contains(this) && Remains.getEnableds().size > slotPos - pos)
    }
  }
  val 脊骨寄生虫 = Remains("remains_spine_parasite").apply {
    localization {
      zh_CN {
        this.localizedName = "脊骨寄生虫"
        description = "一种具有高度神经亲和性的节状生物,渴望与血肉生物的中枢神经系统结合"
      }
    }
    remainsColor = IceColor.r2
    setDescriptionTable {
      it.table { table ->
        table.add("影响单位: ").pad(5f).color(remainsColor)
        table.image(IUnitTypes.蚀虻.uiIcon).size(45f).scaling(Scaling.fit).itooltip("${IUnitTypes.蚀虻.localizedName}")
      }
    }
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 10
      it.frameDuration = 60f / 4f
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
  val 心跳鼓 = Remains("remains_heartbeat_drum").apply {
    remainsColor = IceColor.r2
    localization {
      zh_CN {
        this.localizedName = "心跳鼓"
        description = "弹性心肌隔膜,回响着怀念之音"
      }
    }

    effect = "使状态[${IStatus.回响.localizedName}]的影响提升[20%]"
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 9
      it.frameDuration = 60f / 6f
    }
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
  val 多余的视线 = Remains("remains_extra_gaze").apply {
    remainsColor = IceColor.r2
    localization {
      zh_CN {
        localizedName = "多余的视线"
        description = "同一片神经系统的两个节点,我们相认的媒介"
      }
    }
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 24
      it.frameDuration = 15f
    }
    effect = "相控雷达锁定上限+[10]"
    install = {
      DefenseBlocks.相控雷达.maxTargetSize += 10
      DefenseBlocks.相控雷达.stats = Stats()
      DefenseBlocks.相控雷达.checkStats()
    }
    uninstall = {
      DefenseBlocks.相控雷达.maxTargetSize -= 10
      DefenseBlocks.相控雷达.stats = Stats()
      DefenseBlocks.相控雷达.checkStats()
    }
  }
  val 血腥玛丽 = Remains("remains_bloody_mary").apply {
    remainsColor = IceColor.r2
    localization {
      zh_CN {
        localizedName = "血腥玛丽"
        description = "血与酒液在杯中摇匀,辛辣之后,只余缓慢扩散的猩红"
      }
    }
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 13
      it.frameDuration = 15f
    }

    effect = "为核心机攻击附加流血效果"
    install = {
      for(type in IUnitTypes.getCoreUnits()) {
        for(weapon in type.weapons) {
          if (weapon.bullet.status == StatusEffects.none) {
            weapon.bullet.status = IStatus.流血
          }
        }
        type.stats = Stats()
        type.checkStats()
      }
    }
    uninstall = {
      for(type in IUnitTypes.getCoreUnits()) {
        for(weapon in type.weapons) {
          if (weapon.bullet.status == IStatus.流血) {
            weapon.bullet.status = StatusEffects.none
          }
        }
        type.stats = Stats()
        type.checkStats()
      }
    }
  }
  val 迷思海 = 迷思海()
  val 现彼岸 = Remains("remains_higanbana").apply {
    localization {
      zh_CN {
        localizedName = "现彼岸"
      }
    }
    remainsColor = IceColor.r2
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 8
      it.frameDuration = 60f / 3f
    }
  }
  val 胎生百合 = Remains("remains_tumor_bush").apply {
    localization {
      zh_CN {
        localizedName = "胎生百合"
      }
    }
    remainsColor = IceColor.r2
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 8
      it.frameDuration = 60f / 3f
    }
  }
}
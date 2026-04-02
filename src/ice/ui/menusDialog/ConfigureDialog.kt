package ice.ui.menusDialog

import arc.Core
import arc.flabel.FLabel
import arc.func.*
import arc.graphics.Color
import arc.math.Mathf
import arc.scene.actions.Actions
import arc.scene.ui.Button
import arc.scene.ui.ImageButton
import arc.scene.ui.TextButton
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Table
import arc.util.Strings
import arc.util.Tmp
import ice.audio.IMusics
import ice.audio.ISounds
import ice.content.AtomSchematics
import ice.core.SettingValue
import ice.entities.ModeDifficulty
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.element.ProgressBar
import ice.library.scene.element.typinglabel.TLabel
import ice.library.util.toStringi
import ice.ui.Documents
import ice.ui.bundle.description
import ice.ui.bundle.localizedName
import ice.ui.dialog.BaseMenusDialog
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.gen.Icon
import mindustry.ui.Styles
import mindustry.world.meta.StatUnit
import singularity.Sgl
import singularity.graphic.SglDrawConst
import singularity.ui.SglUI
import singularity.ui.dialogs.ModConfigDialog
import singularity.ui.dialogs.ModConfigDialog.*
import singularity.ui.fragments.entityinfo.EntityInfoFrag
import singularity.ui.fragments.entityinfo.HealthBarStyle

object ConfigureDialog : BaseMenusDialog(IceStats.设置.localized(), IStyles.menusButton_configure) {
  var config: ModConfigDialog = ModConfigDialog()

  init {
    config.addConfig(
      "general",
      Icon.settings,
      ConfigSepLine("musicMenu", "音乐"),
      object : ConfigTable("musicBar", {
        val padTop = it.add(ProgressBar(IStyles.pa1) { IMusics.title.position / 168f }).padTop(10f).padBottom(10f)
        padTop.row()
      }) {
        override fun getHieght(): Float {
          return Float.NEGATIVE_INFINITY
        }
      },

      ConfigCheck("启用主菜单音乐", { SettingValue.启用主菜单音乐 = it }, SettingValue::启用主菜单音乐),

      ConfigSlider(
        "主菜单音乐音量",
        { f: Float -> f.toString() },
        { f: Float -> SettingValue.menuMusicVolume = f },
        { SettingValue.menuMusicVolume },
        0f,
        10f,
        0.1f
      ),

      ConfigSepLine("mainMenu", IceStats.主菜单.localizedName),
      ConfigCheck("禁用mod主界面背景", { SettingValue.禁用mod主界面背景 = it }, SettingValue::禁用mod主界面背景),
      ConfigCheck("进入游戏自动弹出mod主菜单", { SettingValue.进入游戏自动弹出mod主菜单 = it }, SettingValue::进入游戏自动弹出mod主菜单),

      ConfigSepLine("mode", "游戏模式"),
      object : ConfigTable("musicBar", {
        val fLabel = TLabel(SettingValue.difficulty.description).also { it1 ->
          it1.setColor(SettingValue.difficulty.color)
        }
        it.table { it1 ->
          it1.add(fLabel)
        }.expand().left().pad(5f).row()
        it.table { it2 ->
          ModeDifficulty.entries.forEach { mod ->
            it2.addBox(
              mod.localizedName,
              { SettingValue.difficulty == mod },
              if (SettingValue.difficulty == mod) mod.color else IceColor.b4,
              { x, f ->
                  f.update {
                    f.setColor(if (SettingValue.difficulty == mod) mod.color else IceColor.b4)
                  }
              }) {
              SettingValue.difficulty = mod
              fLabel.restart(mod.description)
              fLabel.setColor(mod.color)

            }
          }
        }.expand().left().row()
      }) {
        override fun getHieght(): Float {
          return Float.NEGATIVE_INFINITY
        }
      },

      ConfigSepLine("infoDisplay", "信息显示器"),
      ConfigCheck("显示实体信息", { SettingValue.显示实体信息 = it }, SettingValue::显示实体信息),
      ConfigSlider(
        "状态指示器不透明度",
        { (it * 100).toStringi(1) + "%" },
        { SettingValue.状态指示器不透明度 = it },
        SettingValue::状态指示器不透明度,
        0.3f,
        1f,
        0.001f
      ),
      object : ConfigSlider(
        "信息显示刷新间隔", { SettingValue.信息显示刷新间隔 = it }, SettingValue::信息显示刷新间隔, 0f, 60f, 1f
      ) {
        init {
          str = Prov { Strings.autoFixed(SettingValue.信息显示刷新间隔 / 60, 2) + StatUnit.seconds.localized() }
        }
      },
      ConfigSlider(
        "最多信息显示数目",
        { if (it <= 64) it.toInt().toString() else Core.bundle.get("misc.unlimited") },
        { SettingValue.最多信息显示数目 = it },
        { SettingValue.最多信息显示数目 },
        1f,
        (EntityInfoFrag.MAX_LIMITED + 1).toFloat(),
        1f
      ),
      ConfigSlider("信息面板缩放", { SettingValue.信息面板缩放 = it }, SettingValue::信息面板缩放, 0.5f, 4f, 0.1f),
      ConfigSlider(
        "范围显示模式选中半径", { SettingValue.范围显示模式选中半径 = it }, { SettingValue.范围显示模式选中半径 }, 64f, 512f, 1f
      ),
      ConfigButton("生命指示器风格") {

        object : TextButton("", IStyles.frameButtonStyle) {
          init {
            label.setColor(IceColor.b4)
            clicked {
              config.setHover { t ->
                t.setSize(220f, 0f)
                t.update {
                  val d = localToStageCoordinates(Tmp.v1.set(x - width, y))
                  t.setPosition(d.x, d.y)
                  t.isTransform = true
                }
                t.visible = true

                t.top().pane(Styles.noBarPane) { p ->
                  p.defaults().top().growX().height(45f)
                  for (style in HealthBarStyle.entries) {
                    p.button({ b -> b.add(style.name) }, Styles.underlineb, {
                      Sgl.config.healthBarStyle = style
                      t.clearActions()
                      t.actions(
                        Actions.parallel(Actions.alpha(0f, 0.5f), Actions.sizeTo(t.getWidth(), 0f, 0.5f)),
                        Actions.run { config.clearHover() })
                    }).update { b -> b.setChecked(Sgl.config.healthBarStyle == style) }
                    p.row()
                  }
                }.growX().fillY().maxHeight(380f).pad(-5f).top().get().isScrollingDisabledX = true
                t.clearActions()
                t.actions(
                  Actions.alpha(0f), Actions.parallel(Actions.alpha(1f, 0.5f), Actions.sizeTo(t.getWidth(), 380f, 0.5f))
                )
              }
            }

            update { setText(Sgl.config.healthBarStyle.name) }
          }
        }
      },
      ConfigSlider("状态指示器尺寸", { SettingValue.状态指示器尺寸 = it }, { SettingValue.状态指示器尺寸 }, 4f, 16f, 1f),
      ConfigCheck("显示状态效果的剩余时间", { SettingValue.显示状态效果的剩余时间 = it }, SettingValue::显示状态效果的剩余时间),
      ConfigSepLine("data", IceStats.数据.localizedName),
      ConfigButton("重置已阅读的mod提示信息") {
        Button(IStyles.frameButtonUp2, IStyles.frameButtonDown2).apply {
          add(Core.bundle.get("settings.reset")).color(IceColor.b4)
          clicked {
            Vars.ui.showConfirm(Core.bundle.get("settings.resetHintsConfirm")) {
              //  SglHint.resetCompletedHints();
              //  config.requireRelaunch()

              Documents.DocumentNotificationData.reset()
            }
          }
        }
      },
      ConfigButton("重置所有已阅读的提示信息") {
        object : TextButton(Core.bundle.get("settings.reset"), IStyles.frameButtonStyle) {
          init {
            label.setColor(IceColor.b4)
            clicked {
              Vars.ui.showConfirm(Core.bundle.get("settings.resetAllHintsConfirm")) {
                //  SglHint.resetAllCompletedHints();
                //  config.requireRelaunch()
              }
            }
          }
        }
      })
  }

  init {
    config.addConfig(
      "graphic", Icon.image,
      ConfigSepLine("uiView", "UI视效"),
      ConfigCheck("启用UI模糊", { b: Boolean -> Sgl.config.enableBlur = b }, { Sgl.config.enableBlur }),
      ConfigSlider(
        "模糊级别", { f: Float -> Sgl.config.blurLevel = f.toInt() }, { Sgl.config.blurLevel.toFloat() }, 1f, 8f, 1f
      ),
      ConfigSlider(
        "模糊采样强度", { f: Float -> Sgl.config.backBlurLen = f }, { Sgl.config.backBlurLen }, 0.5f, 8f, 0.25f
      ),

      object : ConfigSlider(
        "画质预设", Floatc { f: Float ->
          if (f >= 0 && f < SglUI.grapPreset.size) {
            val a = SglUI.grapPreset[f.toInt()]
            Sgl.config.animateLevel = (a[0] as Number).toInt()
            Sgl.config.enableShaders = (a[1] as Boolean?)!!
            Sgl.config.mathShapePrecision = (a[2] as Number).toInt().toFloat()
            Sgl.config.enableDistortion = (a[3] as Boolean?)!!
            Sgl.config.enableParticle = (a[4] as Boolean?)!!
            Sgl.config.maxParticleCount = (a[5] as Number).toInt()
            Sgl.config.enableLightning = (a[6] as Boolean?)!!
          }
        }, Floatp { this.matchLevel() }, 0f, SglUI.grapPreset.size.toFloat(), 1f
      ) {
        init {
          str = Prov { Core.bundle.get("settings.graph_" + matchLevel()) }
        }
      },
      ConfigSlider(
        "动画级别", { f: Float -> Sgl.config.animateLevel = f.toInt() }, { Sgl.config.animateLevel.toFloat() }, 1f, 3f, 1f
      ),
      ConfigCheck("启用mod的着色器", { b: Boolean -> Sgl.config.enableShaders = b }, { Sgl.config.enableShaders }),
      ConfigSlider(
        "数学图形着色器精度",
        { f: Float? -> (Mathf.round(f!! * 1000f) / 10f).toString() + "%" },
        { f: Float -> Sgl.config.mathShapePrecision = f },
        { Sgl.config.mathShapePrecision },
        0.1f,
        1f,
        0.001f
      ),
      ConfigCheck("启用扭曲效果着色器", { b: Boolean -> Sgl.config.enableDistortion = b }, { Sgl.config.enableDistortion }),
      ConfigCheck("启用视觉粒子效果", { b: Boolean -> Sgl.config.enableParticle = b }, { Sgl.config.enableParticle }),
      ConfigSlider(
        "最大存在粒子数量",
        { f: Float -> Sgl.config.maxParticleCount = f.toInt() },
        { Sgl.config.maxParticleCount.toFloat() },
        0f,
        4096f,
        8f
      ),
      ConfigCheck("启用视觉性闪电绘制", { b: Boolean -> Sgl.config.enableLightning = b }, { Sgl.config.enableLightning }),

      ConfigCheck("启用QQ头像获取", { b: Boolean -> SettingValue.启用QQ头像获取 = b }, SettingValue::启用QQ头像获取),
      ConfigCheck(
        "启用多合成角标常显", { b: Boolean -> SettingValue.启用多合成角标常显 = b }, SettingValue::启用多合成角标常显
      ),

      ConfigSepLine("animateView", "动画视效"),

      ConfigSlider(
        "视野最大缩放限制", { f: Float ->
          SettingValue.视野最大缩放限制 = f
        }, { SettingValue.视野最大缩放限制 }, 0f, 40f, 0.1f
      ),
      ConfigSlider(
        "视野最小缩放限制", { f: Float ->
          SettingValue.视野最小缩放限制 = f
        }, { SettingValue.视野最小缩放限制 }, 0.1f, 1.5f, 0.1f
      ),
      ConfigCheck(
        "启用包裹物品绘制", { b: Boolean -> SettingValue.启用包裹物品绘制 = b }, SettingValue::启用包裹物品绘制
      ),
      ConfigCheck("启用星球区块ID", { b: Boolean -> SettingValue.启用星球区块ID = b }, SettingValue::启用星球区块ID),
    )
  }

  init {
    config.addConfig(
      "advance",
      SglDrawConst.configureIcon,
      ConfigSepLine("interops", Core.bundle.get("infos.modInterop")),
      ConfigCheck("enableModsInterops", { b: Boolean ->
        Sgl.config.enableModsInterops = b
        config.requireRelaunch()
      }, { Sgl.config.enableModsInterops }),
      object : ConfigCheck("interopAssignUnitCosts", Boolc { b: Boolean ->
        Sgl.config.interopAssignUnitCosts = b
        config.requireRelaunch()
      }, Boolp { Sgl.config.interopAssignUnitCosts }) {
        init {
          disabled = Boolp { !Sgl.config.enableModsInterops }
        }
      },
      object : ConfigCheck("interopAssignEmpModels", Boolc { b: Boolean ->
        Sgl.config.interopAssignEmpModels = b
        config.requireRelaunch()
      }, Boolp { Sgl.config.interopAssignEmpModels }) {
        init {
          disabled = Boolp { !Sgl.config.enableModsInterops }
        }
      },
      ConfigSepLine("reciprocal", Core.bundle.get("infos.override")),
      object : ConfigCheck("modReciprocal", Boolc { b: Boolean ->
        Sgl.config.modReciprocal = b
        config.requireRelaunch()
      }, Boolp { Sgl.config.modReciprocal }) {
        init {
          str = Prov { if (Sgl.config.modReciprocal) "" else Core.bundle.get("infos.reciprocalWarn") }
        }
      },
      object : ConfigCheck("modReciprocalContent", Boolc { b: Boolean ->
        Sgl.config.modReciprocalContent = b
        config.requireRelaunch()
      }, Boolp { Sgl.config.modReciprocalContent }) {
        init {
          str = Prov { if (Sgl.config.modReciprocalContent) "" else Core.bundle.get("infos.reciprocalWarn") }
        }
      },
      ConfigSepLine("debugs", Core.bundle.get("infos.debug")),

      ConfigCheck("启用调试模式", { b: Boolean -> SettingValue.启用调试模式 = b }, { SettingValue.启用调试模式 }),
      ConfigCheck("星球区块调试", { b: Boolean -> SettingValue.星球区块调试 = b }, { SettingValue.星球区块调试 }),
      ConfigButton("删除所有物品原理图") {
        object : TextButton("重置", Styles.flatt) {
          init {
            clicked {
              for (schematic in AtomSchematics.AtomSchematic.all) {
                schematic.cleanLock()
              }
            }
          }
        }
      },
      ConfigButton("删除科技树所有缓存物品") {
        object : TextButton("重置", Styles.flatt) {
          init {
            clicked {
              Sgl.researches.reset()
            }
          }
        }
      },
      ConfigButton("恢复默认配置") {
        object : TextButton("重置", Styles.flatt) {
          init {
            clicked {
              SettingValue.clear()
              Vars.ui.showInfoOnHidden("游戏将退出以重新加载配置") {
                Core.app.exit()
              }
            }
          }
        }
      },
      ConfigCheck("loadInfo", { b: Boolean -> Sgl.config.loadInfo = b }, { Sgl.config.loadInfo }),
      object : ConfigCheck("debugMode", Boolc { b: Boolean -> Sgl.config.debugMode = b }, Boolp { Sgl.config.debugMode }) {
        init {
          str = Prov { Core.bundle.get("infos.unusableDebugButton") }
          disabled = Boolp { true }
        }
      })
  }

  fun matchLevel(): Float {
    for (i in SglUI.grapPreset.indices) {
      val a = SglUI.grapPreset[i]

      if (a[0] == Sgl.config.animateLevel && a[1] == Sgl.config.enableShaders && a[2] == Sgl.config.mathShapePrecision && a[3] == Sgl.config.enableDistortion && a[4] == Sgl.config.enableParticle && a[5] == Sgl.config.maxParticleCount && a[6] == Sgl.config.enableLightning) return i.toFloat()
    }
    return SglUI.grapPreset.size.toFloat()
  }

  override fun build(cont: Table) {
    config.rebuild()
    cont.add(config).grow()
  }

  fun Table.addBox(
    name: String, checked: Boolp, color: Color = IceColor.b4, configure: Cons2<ImageButton, FLabel>, clicked: Runnable = {}
  ): Cell<Table> {
    val label = FLabel(name).also { it.setColor(color) }
    val button = ImageButton(IStyles.checkBoxStyle).apply {
      isChecked = checked.get()
      imageCell.size(32f, 44f).expand().left()
      clicked {
        ISounds.remainInstall.play()
        clicked.run()
      }
      update {
        isChecked = checked.get()
      }
    }
    configure.get(button, label)
    return add(Table().apply {
      add(button).padRight(8f)
      add(label)
    }).margin(10f).top().left().pad(5f)
  }
}
package ice.ui.menusDialog

import arc.Core
import arc.func.ConsT
import arc.func.Floatc
import arc.graphics.Color
import arc.graphics.Texture
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.scene.Element
import arc.scene.style.Drawable
import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.TextButton
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Http
import arc.util.Interval
import arc.util.Log
import arc.util.Time
import arc.util.io.Streams
import arc.util.serialization.Jval
import ice.DeepSpace
import ice.core.SettingValue
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.graphics.RandSetTextrue
import ice.library.IFiles
import ice.library.scene.ui.*
import ice.library.struct.log
import ice.ui.dialog.BaseMenusDialog
import ice.world.meta.IceEffects
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.ui.Styles
import singularity.Singularity
import singularity.graphic.SglDrawConst
import universecore.util.UrlDownloader
import java.util.regex.Pattern

object ModInfoDialog : BaseMenusDialog(IceStats.模组.localized(), IStyles.menusButton_infos) {


  private val UNC_RELEASE_FILE: Pattern = Pattern.compile("^${DeepSpace.displayName}.+\\.(jar|zip)$")

  private var newVersion: String? = null
  private var updateUrl: String? = null
  private var checking: Boolean = false
  private var downloadProgress: Float = 0f

  init {
    ContributorTable("EBwilson", "2534946881", Work.program_icon_work).itooltip("以我现在的视角看还要继续的话unc得整个重构一遍")
    ContributorTable("硫缺铅", "1164806786", Work.translate_icon_work).itooltip("理解宇宙有助于身体健康")
    ContributorTable("帕奇维克", "154864663", Work.artist_icon_work).itooltip("广告招租位")
    ContributorTable("前之骈", "2519583310", Work.copywriting_icon_work).itooltip("请务必关注neurosama喵,谢谢喵!")
    ContributorTable("ZL洋葱", "813466636", Work.artist_icon_work).itooltip("你知道吗,模组作者在QQ短视频上推过意义不明的奥特曼视频")
    ContributorTable("NeilGreenFly", "1471761931", Work.program_icon_work).itooltip("我想想")
    ContributorTable("Reflcaly_反射", "2354671478", Work.artist_icon_work).itooltip("期待与你的再次见面!再见!")
    ContributorTable("Carrot", "1456616666", Work.artist_icon_work).itooltip("哇还有奇点")
    ContributorTable("Ventivu", "3123632012", Work.program_icon_work).itooltip("咨询一下,有获取sgl模组的授权吗")
    ContributorTable("zero", "3129162464", Work.sounds_icon_work).itooltip("请务必关注neurosama喵,谢谢喵!").itooltip("我喜欢你")


    AssistedTable("Novarc", "2124363741").itooltip("等终末地出了我再继续写mod")
    AssistedTable("Eipusino", "1428509711").itooltip("颉姐你带我走吧")
    AssistedTable("sowiearch", "68237730").itooltip("我喜欢你 莲莲我喜欢你")
    AssistedTable("松鼠", "501410836").itooltip("全部草飞")
    AssistedTable("HOOHHOOH", "1876862665").itooltip("愚昧的活着不如清醒的死去")
    AssistedTable("MrT", "949707328").itooltip("某个憨批笑脸头套")
    AssistedTable("坠机的牢阔", "2863192836").itooltip("有机会也试试[#F6A34FFF]战锤[]这个模组吖")
    AssistedTable("喵喵怪", "3390401496").itooltip("界限?狗都不玩!")
    AssistedTable("zzc", "13918301").itooltip("可以来看看牢z的独游喵")
    AssistedTable("试听", "3067484362").itooltip("[#00F7FFFF]你说的对[][#FF0000FF]后面忘了...")
    AssistedTable("喵子", "1416437149").itooltip("胡萝卜素星球")
    AssistedTable("年年有鱼", "201427535").itooltip("人生总有起落轻轻一笑,调整自我明天还是美好的")
    AssistedTable("种余明的玉米", "2923607476").itooltip("你要这样我可要宣传我模了")
    AssistedTable("维生素X", "2484910089").itooltip("[#A4A5F5FF]我爱[][#F5BAE9FF]玲纱![]")
    AssistedTable("GRACHA", "1292683953").itooltip("*大屠戮的最后一刀刺向了自己的心脏 污浊随之翻滚喷涌")
    AssistedTable("zxs", "488254306").itooltip("JS异端")
  }

  override fun build(cont: Table) {
    cont.iPaneG { ta ->
      ta.top()
      ta.image(IStyles.tanksui).height(200f).pad(10f).row()

      ta.addLine("模组信息").padBottom(20f)

      ta.table(SglDrawConst.grayUIAlpha) { t3 ->
        t3.table { t2 ->
          t2.table { t ->
            t.defaults().left().pad(5f).growX().height(40f)
            t.add(DeepSpace.displayName).color(Pal.accent)
            t.row()
            t.add(Core.bundle.get("misc.author")).color(Pal.accent)
            t.add(DeepSpace.author)
            t.button(Core.bundle.get("mod.contributor"), SglDrawConst.contributeIcon, Styles.nonet, 28f) { }.update { b: TextButton? -> b!!.setChecked(false) }.width(230f)
            t.row()
            t.add(Core.bundle.get("misc.version")).color(Pal.accent)
            t.add(DeepSpace.version)
            t.table { update ->
              update.add(object : Element() {
                override fun draw() {
                  Draw.alpha(parentAlpha * color.a)

                  if (checking) {
                    Draw.color(Pal.accent)
                    Fill.square(x + width / 2, y + height / 2, 8f, Time.time)
                    Fill.square(x + width / 2, y + height / 2, 8f, 45 + 2 * Time.time)
                  } else {
                    if (newVersion == null) Draw.color(IceColor.b4)
                    else Draw.color(IceColor.b4, IceColor.r2, Mathf.absin(8f, 1f))

                    Fill.square(x + width / 2, y + height / 2, 8f)
                    Fill.square(x + width / 2, y + height / 2, 8f, 45f)
                  }
                }
              }).size(40f)
              update.add("").update { l: Label? ->
                l!!.setText(
                  if (checking) Core.bundle.get("infos.checkingUpgrade")
                  else if (newVersion != null) Core.bundle.format("infos.hasUpdate", newVersion)
                  else Core.bundle.get("infos.newestVersion")
                )
              }
            }.width(230f)
            t.row()
            t.add(Core.bundle.get("infos.releaseDate")).color(Pal.accent)
            t.add(DeepSpace.updateDate)
            t.button("", Icon.upload, Styles.nonet, 28f) {
              checkOrDoUpdate()
            }.update { b: TextButton? ->
              b!!.setText(
                if (newVersion != null) Core.bundle.get("misc.update")
                else Core.bundle.get("infos.checkUpdate")
              )
            }.width(230f)
          }.fillY().padTop(40f).margin(4f).padBottom(20f).minWidth(700f)
        }.row()

        t3.table { t3 ->
          t3.defaults().pad(3f)

          t3.table { ta ->
            ta.add("模组的部分内容融合自以下已停更的模组").color(Pal.accent)
          }.row()
          t3.table { ta ->
            ta.table { t1 ->

              t1.table(IFiles.createNinePatch("contributors")) {
                it.image(TextureRegion(Texture(DeepSpace.mod.root.child("icon_singularity.png")))).grow()
              }.margin(5f).size(100f).pad(5f).row()
              t1.table {
                it.add("Singularity").color(Pal.accent)
              }.growX().row()

              t1.table {
                it.add("作者:").expand().left().color(Pal.accent)
                it.add("EBwilson").expand().right().color(Pal.accent)
              }.growX().row()

            }.padRight(30f)

            ta.table { t1 ->

              t1.table(IFiles.createNinePatch("contributors")) {
                it.image(TextureRegion(Texture(DeepSpace.mod.root.child("icon_curseOfFlesh.png")))).grow()
              }.margin(5f).size(100f).pad(5f).row()

              t1.table {
                it.add("CurseOfFlesh").color(Pal.accent)
              }.growX().row()

              t1.table {
                it.add("作者:").expand().left().color(Pal.accent)
                it.add("帕奇维克").expand().right().color(Pal.accent)
              }.growX().row()
            }

          }.growX().row()
          t3.table { ta ->
            ta.add("所有内容均已获得授权,感谢各mod作者的帮助").color(Pal.accent)
          }.row()

        }.margin(10f).growX()
      }.padBottom(20f).row()


      ta.addLine("亲爱贡献者").padBottom(20f)
      ta.table {
        it.table(SglDrawConst.grayUIAlpha) { table ->
          table.defaults().pad(8f)
          table.table(Tex.underline) { t ->
            t.left().defaults().left().fill()
            t.add("在此,特别谢鸣为mod开发过程提供了不可或缺的帮助的开发者和贡献者").color(Pal.accent)
          }.growX().row()

          val radst = Array(ContributorTable.contributors.size) {
            RandSetTextrue("contributors", 11)
          }
          val interval = Interval()
          table.iTable { cons ->
            cons.defaults().pad(4f)
            cons.setRowsize(5)



            ContributorTable.contributors.forEach { contributor ->
              cons.table(radst[ContributorTable.contributors.indexOf(contributor)].def) { t ->
                t.top().defaults().center().top().pad(16f).padTop(12f)
                t.image(contributor.work.icon()).color(IceColor.b4).size(64f)
                t.row()
                t.add(contributor.work.des).color(IceColor.b4).padTop(6f)
                t.row()
                t.add(contributor).fillY()
                t.row()
              }.fillY()
            }
          }.update {
            if (interval.get(120f)) {
              radst[IceEffects.rand.random(0, radst.size - 1)].blink = true
            }
          }
        }.grow().margin(8f).row()
      }.padBottom(20f).row()

      ta.addLine("特别感谢").padBottom(20f)
      ta.iTable { itable ->

        itable.table(SglDrawConst.grayUIAlpha) { table ->
          table.defaults().pad(8f)
          table.table(Tex.underline) { t ->
            t.left().defaults().left().fill()
            t.addCR("感谢所有为DeepSpace mod项目开发提供了帮助的贡献者")
          }.growX().row()

          table.iTable { cons ->
            cons.defaults().pad(4f)
            cons.left()
            cons.setRowsize(5)
            AssistedTable.assisteds.forEach { assisted ->
              cons.table(IFiles.createNinePatch("contributors")) { t ->
                t.top().defaults().center().top().pad(16f).padTop(12f)
                t.add(assisted).fillY()
                t.row()
              }.fillY()
            }
          }
          table.row()
          table.left().addCR("最后,感谢所有游玩本mod的玩家,我们的工作最后得到的承认就是最大的鼓励,再一次,感谢您的游玩!").fill().left()
        }.grow().margin(8f).row()

      }.padBottom(20f).row()

      ta.addLine("相关链接")
      ta.table {
        SponsoredDialog.buildButton(it, Icon.githubSquare, IceColor.b4, "Github项目", "mod的开源地址") {
          openUrl(DeepSpace.githubProjectUrl)
        }.growX().row()
        SponsoredDialog.buildButton(it, SglDrawConst.qqIcon, Pal.lightishGray, Core.bundle.get("misc.qq"), Core.bundle.get("infos.qq")) {
          openUrl(DeepSpace.qqGropsUrl)
        }.growX().row()
        SponsoredDialog.buildButton(it, Icon.discord, Pal.lightOrange, "Discord论坛", "DeepSpace的discord聊天室") {

        }.growX().row()
        SponsoredDialog.buildButton(it, SglDrawConst.telegramIcon, Color.valueOf("7289da"), "Telegram页面", "Telegram上的本mod群组") {}.growX().row()
      }.growX()

    }
  }

  override fun hide() {
    super.hide()
    checking = false
    newVersion = null
    updateUrl = null
  }

  private fun checkOrDoUpdate() {
    if (newVersion != null) {
      if (updateUrl == null) Vars.ui.showException("what? updateUrl was null!", NullPointerException())
      else {
        downloadMod()
      }
    } else {
      checking = true

      Http.get(DeepSpace.githubProjReleaseApi, ConsT { res: Http.HttpResponse ->

        val response = Jval.read(res.resultAsString)
        if (!checking) return@ConsT

        if (isNewVersion(response.getString("tag_name"))) {
          newVersion = response.getString("tag_name")
          for (asset in response.get("assets").asArray()) {
            log { asset.getString("name") }
            if (asset.has("name") && UNC_RELEASE_FILE.matcher(asset.getString("name")).matches()) {
              updateUrl = asset.getString("browser_download_url")
            }
          }
          Core.app.post { Vars.ui.showInfoFade("[#${IceColor.r2}]模组有最新版本: ${response.getString("tag_name")}[]") }
        } else {
          Core.app.post { Vars.ui.showInfoFade("[#${IceColor.b4}]模组当前已是最新版本[]") }
        }

        Core.app.post { checking = false }
      }) { _ ->
        Core.app.post {
          checking = false
          Vars.ui.showInfoFade("[crimson]检查更新失败,请检查网络连接")
        }
      }
    }
  }

  private fun downloadMod() {
    downloadProgress = 0f
    Vars.ui.loadfrag.show("@downloading")
    Vars.ui.loadfrag.setProgress { downloadProgress }
    Http.get(updateUrl, { result: Http.HttpResponse? ->
      try {
        val file = Vars.tmpDirectory.child("${DeepSpace.displayName}-$newVersion.jar")
        val len = result!!.contentLength

        file.write(false).use { stream ->
          Streams.copyProgress(result.resultAsStream, stream, len, 4096, if (len <= 0) Floatc { _: Float -> } else Floatc { p: Float -> downloadProgress = p })
        }
        val mod = Vars.mods.importMod(file)
        mod.repo = DeepSpace.repo
        file.delete()

        Core.app.post {
          Vars.ui.loadfrag.hide()
          Vars.ui.showConfirm("@mods.reloadexit") {
            Log.info("Exiting to reload mods.")
            Core.app.exit()
          }
        }
      } catch (e: Throwable) {
        Vars.ui.showException(e)
        Log.err(e)
      }
    }, { e: Throwable? ->
      Vars.ui.showException(e)
      Log.err(e)
    })
  }

  private fun isNewVersion(version: String): Boolean {
    var newestVersion = false
    try {
      val newVersion = version.filter { it.isDigit() }
      val currVersion = DeepSpace.version.filter { it.isDigit() }
      newestVersion = newVersion.toInt() > currVersion.toInt()
    } catch (_: Throwable) {
    }
    return newestVersion
  }

  private fun openUrl(url: String?) {
    if (!Core.app.openURI(url)) {
      Vars.ui.showErrorMessage("@linkfail")
      Core.app.clipboardText = url
    }
  }

  fun getQQImage(number: String): TextureRegion {
    if (!SettingValue.启用QQ头像获取)return Core.atlas.find("nomap")
    return UrlDownloader.downloadImg("https://q.qlogo.cn/headimg_dl?dst_uin=$number&spec=640&img_type=jpg", Core.atlas.find("nomap"))
  }

  private class ContributorTable(name: String, number: String, val work: Work) : Table() {
    companion object {
      val contributors = Seq<ContributorTable>()
    }

    init {
      add(Stack(Image(getQQImage(number)), Image(IFiles.findModPng("wdwd")))).size(180f).get()
      row()
      add(name).color(Pal.accent).fill()
      contributors.add(this)
    }
  }

  private class AssistedTable(name: String, number: String) : Table() {
    companion object {
      val assisteds = Seq<AssistedTable>()
    }

    init {
      add(Stack(Image(getQQImage(number)), Image(IFiles.findModPng("wdwd")))).size(180f).get()
      row()
      addCR(name).fill()
      assisteds.add(this)
    }
  }

  private enum class Work(val des: String) {
    artist_icon_work("贴图/美术"),
    translate_icon_work("翻译"),
    sounds_icon_work("音乐/音效"),
    copywriting_icon_work("文案"),
    program_icon_work("程序/调试");

    fun icon(): Drawable = Singularity.getModDrawable(name)
  }
}
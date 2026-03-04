package ice.audio

import arc.Core
import arc.audio.Filters
import arc.audio.Music
import arc.math.Mathf
import arc.struct.Seq
import arc.util.Time
import ice.core.SettingValue
import ice.library.struct.log
import ice.ui.MenusDialog
import mindustry.Vars
import mindustry.audio.SoundControl
import mindustry.gen.Musics
import kotlin.math.log10

class SoundControl : SoundControl() {
  val iceAmbientMusic = Seq<Music>()
  val iceDarkMusic = Seq<Music>()
  val iceBossMusic = Seq<Music>()

  init {
    iceAmbientMusic.addAll(Musics.game1, Musics.game3, Musics.game6, Musics.game8, Musics.game9, Musics.fine)
    iceDarkMusic.addAll(IMusics.核心过载仪式)// Seq.with(Musics.game2, Musics.game5, Musics.game7, Musics.game4)
    iceBossMusic.addAll(IMusics.异端核心)
  }

  override fun isDark(): Boolean {
    if (Vars.player.team().data().hasCore() && Vars.player.team().data().core().healthf() < 0.85f) {
      //core damaged -> dark
      return true
    }

    //it may be dark based on wave
    if (Mathf.chance(((log10(((Vars.state.wave - 17f) / 19f).toDouble()) + 1).toFloat() / 4f).toDouble())) {
      return true
    }

    //dark based on enemies
    return Mathf.chance((Vars.state.enemies / 70f + 0.1f).toDouble())
  }

  override fun playRandom() {
    if (Vars.state.boss() != null) {
      playOnce(iceBossMusic.random())
    } else if (isDark()) {
      playOnce(iceDarkMusic.random())
    } else {
      playOnce(iceAmbientMusic.random())
    }
  }

  override fun update() {
    val paused = Vars.state.isGame && Core.scene.hasDialog()
    val playing = Vars.state.isGame

    //check if current track is finished
    if (current != null && !current.isPlaying()) {
      current = null
      fade = 0f
    }

    //fade the lowpass filter in/out, poll every 30 ticks just in case performance is an issue
    if (timer.get(1, 30f)) {
      Core.audio.soundBus.fadeFilterParam(0, Filters.paramWet, if (paused) 1f else 0f, 0.4f)
    }

    //play/stop ordinary effects
    if (playing != wasPlaying) {
      wasPlaying = playing

      if (playing) {
        Core.audio.soundBus.play()
        setupFilters()
      } else {
        //stopping a single audio bus stops everything else, yay!
        Core.audio.soundBus.stop()
        //play music bus again, as it was stopped above
        Core.audio.musicBus.play()

        Core.audio.soundBus.play()
      }
    }

    Core.audio.setPaused(Core.audio.soundBus.id, Vars.state.isPaused)

    if (MenusDialog.isShown) {
      if (SettingValue.启用主菜单音乐) play(IMusics.title)
    } else if (Vars.state.isMenu) {
      silenced = false
      if (Vars.ui.planet.isShown) {
        play(Vars.ui.planet.state.planet.launchMusic)
      } else if (Vars.ui.editor.isShown) {
        play(Musics.editor)
      } else {
        play(Musics.menu)
      }
    } else if (Vars.state.rules.editor) {
      silenced = false
      play(Musics.editor)
    } else {
      //这只是将最后一首曲目淡出，为游戏内音乐腾出空间
      silence()

      if (Core.settings.getBool("alwaysmusic")) {
        if (current == null) {
          playRandom()
        }
      } else if (Time.timeSinceMillis(lastPlayed) > 1000 * musicInterval / 60f) {
        //chance to play it per interval
        if (Mathf.chance(musicChance.toDouble())) {
          lastPlayed = Time.millis()
          playRandom()
        }
      }
    }

    updateLoops()
  }

  override fun play(music: Music?) {
    if (!shouldPlay()) {
      if (current != null) {
        current.setVolume(0f)
      }
      fade = 0f
      return
    }
    //更新当前曲目的音量
    if (current != null) {
      if (current == IMusics.title) {
        current.setVolume(SettingValue.menuMusicVolume)
      } else {
        current.setVolume(fade * Core.settings.getInt("musicvol") / 100f)
      }
    }
    //一旦曲目完全淡出，就不要更新，只需停止即可
    if (silenced) {
      return
    }

    if (current == null && music != null) {
      //开始在新曲目中演奏
      current = music
      current.isLooping = true
      current.setVolume(0f.also {fade = it})
      current.play()
      silenced = false
    } else if (current === music) {
      //淡入淡出播放轨道
      val clamp = Mathf.clamp(fade + Time.delta / foutTime)
      fade = clamp * if (current == IMusics.title) SettingValue.menuMusicVolume else 1f
    } else {
      //淡出当前曲目
      val clamp = Mathf.clamp(fade - Time.delta / foutTime)
      fade = clamp// clamp*
      if (current == IMusics.title) {
        current.volume = clamp * SettingValue.menuMusicVolume
      }
      if (fade <= 0.01f) {
        //stop current track when it hits 0 volume
        current.stop()
        current = null
        silenced = true
        //play newly scheduled track
        current = music ?: return
        current.setVolume(fade)
        current.isLooping = true
        current.play()
        silenced = false
      }
    }
  }
}
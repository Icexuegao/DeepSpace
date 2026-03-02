package ice.audio

import arc.Core
import arc.audio.Filters
import arc.audio.Music
import arc.math.Mathf
import arc.struct.Seq
import arc.util.Time
import ice.core.SettingValue
import ice.ui.MenusDialog
import mindustry.Vars
import mindustry.audio.SoundControl
import mindustry.gen.Musics

class SoundControl : SoundControl() {
  val iceAmbientMusic = Seq<Music>()
  val iceDarkMusic = Seq<Music>()
  val iceBossMusic = Seq<Music>()

  init {
    iceAmbientMusic.addAll(Musics.game1, Musics.game3, Musics.game6, Musics.game8, Musics.game9, Musics.fine)
    iceDarkMusic.addAll(IMusics.Core_Overload_Rite)// Seq.with(Musics.game2, Musics.game5, Musics.game7, Musics.game4)
    iceBossMusic.addAll(Musics.boss1, Musics.boss2, Musics.game2, Musics.game5)
  }

  override fun playRandom() {
    if (Vars.state.boss() != null) {
      playOnce(iceBossMusic.random(lastRandomPlayed))
    } else if (isDark()) {
      playOnce(iceDarkMusic.random(lastRandomPlayed))
    } else {
      playOnce(iceAmbientMusic.random(lastRandomPlayed))
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
      play(IMusics.title)
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
      //this just fades out the last track to make way for ingame music
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
      current.setVolume(0f.also { fade = it })
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
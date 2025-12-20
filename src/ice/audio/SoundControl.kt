package ice.audio

import arc.Core
import arc.audio.Filters
import arc.audio.Music
import arc.math.Mathf
import arc.util.Time
import ice.core.SettingValue
import ice.ui.dialog.MenusDialog
import mindustry.Vars
import mindustry.audio.SoundControl
import mindustry.gen.Musics

class SoundControl : SoundControl() {
    override fun update() {
        val paused = Vars.state.isGame && Core.scene.hasDialog()
        val playing = Vars.state.isGame
        //check if current track is finished
        if (current != null && !current.isPlaying) {
            current = null
            fade = 0f
        }
        //淡入/淡出低通滤波器，每 30 刻轮询一次，以防性能出现问题
        if (timer.get(1, 30f)) {
            Core.audio.soundBus.fadeFilterParam(0, Filters.paramWet, if (paused) 1f else 0f, 0.4f)
        }
        //播放/停止普通效果
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

        if (Vars.state.isMenu) {
            silenced = false
            if (Vars.ui.planet.isShown) {
                play(Vars.ui.planet.state.planet.launchMusic)
            } else if (Vars.ui.editor.isShown) {
                play(Musics.editor)
            } else if (MenusDialog.isShown()) {
                if (SettingValue.启用主菜单音乐) {
                    play(IMusics.title)
                }
            } else {
                play(Musics.menu)
            }
        } else if (Vars.state.rules.editor) {
            silenced = false
            play(Musics.editor)
        } else {
            //这只是淡出最后一首曲目，为游戏内音乐让路
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
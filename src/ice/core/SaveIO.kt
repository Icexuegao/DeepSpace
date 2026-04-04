package ice.core

import arc.Events
import arc.struct.Seq
import arc.util.Log
import arc.util.io.Reads
import ice.library.world.Load
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.ctype.ContentType
import mindustry.ctype.MappableContent
import mindustry.game.EventType.ContentPatchLoadEvent
import mindustry.io.SaveIO
import mindustry.io.versions.Save11
import mindustry.world.WorldContext
import java.io.ByteArrayInputStream
import java.io.DataInput
import java.io.DataInputStream
import java.io.DataOutput

object SaveIO : Load {
  override fun init() {
    SaveIO.versionArray.set(10, object : Save11() {
      override fun readMap(stream: DataInput?, context: WorldContext?) {
        val width = stream!!.readUnsignedShort()
        val height = stream.readUnsignedShort()

        val generating = context!!.isGenerating

        if (!generating) context.begin()
        try {
          context.resize(width, height)

          //read floor and create tiles first
          run {
            var i = 0
            while (i < width * height) {
              val x = i % width
              val y = i / width
              var floorid = stream.readShort()
              val oreid = stream.readShort()
              val consecutives = stream.readUnsignedByte()
              if (Vars.content.block(floorid.toInt()) === Blocks.air) floorid = Blocks.stone.id

              context.create(x, y, floorid.toInt(), oreid.toInt(), 0.toShort().toInt())

              for (j in i + 1..<i + 1 + consecutives) {
                val newx = j % width
                val newy = j / width
                context.create(newx, newy, floorid.toInt(), oreid.toInt(), 0.toShort().toInt())
              }

              i += consecutives
              i++
            }
          }

          //read blocks
          var i = 0
          while (i < width * height) {
            var block = Vars.content.block(stream.readShort().toInt())
            val tile = context.tile(i)
            if (block == null) block = Blocks.air
            var isCenter = true
            val packedCheck = stream.readByte()
            val hadEntity = (packedCheck.toInt() and 1) != 0
            //data check (bit 3): 7 bytes (3x block-specific bytes + 1x 4-byte extra data int)
            val hadData = (packedCheck.toInt() and 4) != 0

            var data: Byte = 0
            var floorData: Byte = 0
            var overlayData: Byte = 0
            var extraData = 0

            if (hadData) {
              data = stream.readByte()
              floorData = stream.readByte()
              overlayData = stream.readByte()
              extraData = stream.readInt()
            }

            if (hadEntity) {
              isCenter = stream.readBoolean()
            }

            //set block only if this is the center; otherwise, it's handled elsewhere
            if (isCenter) {
              tile.setBlock(block)
              if (tile.build != null) tile.build.enabled = true
            }

            //must be assigned after setBlock, because that can reset data
            if (hadData) {
              tile.data = data
              tile.floorData = floorData
              tile.overlayData = overlayData
              tile.extraData = extraData
              context.onReadTileData()
            }

            if (hadEntity) {
              if (isCenter) { //only read entity for center blocks

                if (block.hasBuilding()) {

                  val chunkData = readChunkToBytes(stream)

                  try {
                    val byteStream = DataInputStream(ByteArrayInputStream(chunkData))
                    val reads = Reads(byteStream)

                    val revision = reads.b()
                    tile.build.readAll(reads, revision)
                  } catch (e: Throwable) {
                    Log.warn("建筑 $block 读取失败，已跳过")
                  }
                } else {
                  skipChunk(stream)
                }


                context.onReadBuilding()
              }
            } else if (!hadData) { //never read consecutive blocks if there's data
              val consecutives = stream.readUnsignedByte()

              for (j in i + 1..<i + 1 + consecutives) {
                context.tile(j).setBlock(block)
              }

              i += consecutives
            }
            i++
          }
        } finally {
          if (!generating) context.end()
        }
      }

      override fun writeContentHeader(stream: DataOutput?) {
        super.writeContentHeader(stream)
      }

      override fun readContentHeader(stream: DataInput?) {
        val mapped = stream!!.readUnsignedByte()

        val map = Array<Array<MappableContent?>?>(ContentType.all.size) { arrayOfNulls(0) }

        for(i in 0..<mapped) {
          val toInt = stream.readByte().toInt()
          val type = ContentType.all[toInt.coerceIn(0, ContentType.all.size-1)]
          val total = stream.readShort()
          map[type.ordinal] = arrayOfNulls(total.toInt())

          for(j in 0..<total) {
            val name = stream.readUTF()
            //fallback only for blocks
            map[type.ordinal.coerceIn(type.ordinal,map.size)]!![j] =
              Vars.content.getByName(type, if (type == ContentType.block) fallback.get(name, name) else name)
          }
        }

        Vars.content.setTemporaryMapper(map)

        //HACK: versions below 11 don't read the patch chunk, which means the event for reading patches is never triggered.
        //manually fire the event here for older versions.
        if (version < 11) {
          val patches = Seq<String?>()
          Events.fire<ContentPatchLoadEvent?>(ContentPatchLoadEvent(patches))

          if (patches.size > 0) {
            try {
              Vars.state.patcher.apply(patches)
            } catch(e: Throwable) {
              Log.err("Failed to apply patches: " + patches, e)
            }
          }
        }
      }
    })
    SaveIO.versions.clear()
    for (version in SaveIO.versionArray) {
      SaveIO.versions.put(version.version, version)
    }
  }

  // 辅助函数：读取 chunk 到字节数组
  private fun readChunkToBytes(stream: DataInput): ByteArray {
    val len = stream.readInt()
    val bytes = ByteArray(len)
    stream.readFully(bytes)
    return bytes
  }
}
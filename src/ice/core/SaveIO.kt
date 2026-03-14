package ice.core

import arc.util.Log
import arc.util.io.Reads
import ice.library.world.Load
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.io.SaveIO
import mindustry.io.versions.Save11
import mindustry.world.WorldContext
import java.io.ByteArrayInputStream
import java.io.DataInput
import java.io.DataInputStream

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
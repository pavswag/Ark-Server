package io.kyros.content.instance

import io.kyros.model.entity.player.Player
import io.kyros.model.entity.player.Position

class Pallet {

    /**
     * Set of 13 x 13 x 4 tiles that will be sent to the client
     */
    private val tiles = Array(13) { Array(13) { arrayOfNulls<PalletTile>(4) } }


    /**
     *  Set the x-y-z [PalletTile]
     */
    fun setTile(x: Int, y: Int, z: Int, tile: PalletTile) {
        tiles[x][y][z] = tile
    }

    /**
     *  Get a specific [PalletTile] from the pallet
     */
    fun getTile(x: Int, y: Int, z: Int) = tiles[x][y][z]

    /**
     * Send the region packet for this pallet to the [player]
     */
    fun sendFor(player: Player) {
        if (player.outStream != null) {
            player.outStream.createFrameVarSizeWord(241)
            player.outStream.writeWordA(player.mapRegionY + 6)
            System.out.println("MapY: " + player.mapRegionY + 6)
            player.outStream.initBitAccess()
            for (z in 0..3) {
                for (x in 0..12) {
                    for (y in 0..12) {
                        val tile = getTile(x, y, z)
                        var b = false
                        if (x < 2 || x > 10 || y < 2 || y > 10) b = true
                        player.outStream.writeBits(1, if (!b && tile != null) 1 else 0)
                        if (tile != null && !b) {
                            player.outStream.writeBits(26, tile.getRegionX() shl 14 or (tile.getRegionY() shl 3) or (tile.z shl 24) or (tile.rot shl 1))
                        }
                    }
                }
            }
            player.getOutStream().finishBitAccess()
            player.getOutStream().writeShort(player.mapRegionX + 6) //im a retard
            player.getOutStream().endFrameVarSizeWord()
            player.flushOutStream()
        }

    }


    companion object {

        /**
         * Create a [Pallet] of the given map position
         */
        fun copyMap(position: Position): Pallet {
            val pallet = Pallet()

            val cen = PalletTile(position.x, position.y, 0)

            pallet.setTile(6, 6, 0, cen)

            for (r in -5..5) {
                for (c in -5..5) {
                    val palTile = PalletTile(position.x + 8 * r, position.y + 8 * c, 0)
                    pallet.setTile(6 + r, 6 + c, 0, palTile)
                }
            }

            return pallet
        }

        /**
         * Create a [Pallet] of empty grass tiles
         */
        fun emptyMap(): Pallet {
            val pallet = Pallet()

            val cen = PalletTile(1912, 5751, 0)

            pallet.setTile(6, 6, 0, cen)

            for (r in -5..5) {
                for (c in -5..5) {
                    val palTile = cen
                    pallet.setTile(6 + r, 6 + c, 0, palTile)
                }
            }

            return pallet
        }

    }
}
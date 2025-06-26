package io.kyros.content.instance

/**
 * Represents an 8 by 8 region
 */
class PalletTile(val x: Int, val y: Int, val z: Int, val rot: Int = 0) {

    /**
     * Gets the x coordinate in terms of the region-x
     */
    fun getRegionX() = x / 8

    /**
     * Gets the y coordinate in terms of the region-y
     */
    fun getRegionY() = y / 8

    /**
     * Get the modular height (height % 4 displays the same on the client)
     */
    fun getModZ() = z % 4

    fun getModRot() = rot % 4

}
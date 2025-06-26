package io.kyros.model.collisionmap;

import io.kyros.Server;
import io.kyros.model.world.objects.GlobalObject;

/**
 * 
 * @author Killamess Used to represent a object or npc tile.
 */
public class Tile {
	
	private final int[] pointer = new int[3];
	
	public Tile(int x, int y, int z) {
		this.pointer[0] = x;
		this.pointer[1] = y;
		this.pointer[2] = z;
	}
	
	public int[] getTile() {
		return pointer;
	}
	
	public int getTileX() {
		return pointer[0];
	}
	
	public int getTileY() {
		return pointer[1];
	}
	
	public int getTileHeight() {
		return pointer[2];
	}

	public static GlobalObject getObject(int id, int x, int y, int z) {
		return Server.getGlobalObjects().get(id, x, y, z);
	}

}
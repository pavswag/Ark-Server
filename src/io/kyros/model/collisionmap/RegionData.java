package io.kyros.model.collisionmap;

import com.displee.cache.CacheLibrary;

public class RegionData {
	private final int regionHash;
    private final int landscape;
    private final int objects;
	public final int baseX;
	public final int baseY;

	public int getRegionHash() {
		return regionHash;
	}

	public int getLandscape() {
		return landscape;
	}

	public int getObjects() {
		return objects;
	}

	public RegionData(int regionHash, int landscape, int objects) {
		this.regionHash = regionHash;
		this.landscape = landscape;
		this.objects = objects;
		this.baseX = (regionHash >> 8) * 64;
		this.baseY = (regionHash & 0xff) * 64;
	}


	public byte[] getMapData(CacheLibrary library) {
		var index = library.index(5);
		int mapArchiveId = index.archiveId("m" + ((baseX >> 3) / 8) + "_" + ((baseY >> 3) / 8));
		return mapArchiveId == -1 ? null : library.data(5, mapArchiveId);
	}

	public byte[] getLandscapeData(CacheLibrary library) {
		var index = library.index(5);
		int landArchiveId = index.archiveId("l" + ((baseX >> 3) / 8) + "_" + ((baseY >> 3) / 8));
		return landArchiveId == -1 ? null : library.data(5, landArchiveId);
	}
	
}
package io.kyros.cache.definitions;

import io.kyros.model.world.objects.GlobalObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Created by Bart on 8/11/2015.
 * i created a java version of this for osrsps i think i put it in osa too? can u open osa? this is osa
 * 50% of this class is regtionmanager/region
 */
public class MapDefinition implements Definition {

    private int rx;
    private int rz;


    public int[][][] clips = new int[4][][];
    public byte[][][] heightMap;

    // ok there
    public ObjectArrayList<GlobalObject>[][][] objs = new ObjectArrayList[4][][];

    // everything else u can convert its from
    // regionmanager mostly

    public void load(DefinitionRepository defrepo, byte[] map, byte[] objects, Boolean all) {

    }
}

package io.kyros.cache.definitions;

import io.kyros.Server;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import io.kyros.cache.DataStore;
import org.slf4j.LoggerFactory;

/**
 * Created by Bart on 7/11/2015.
 */
@SuppressWarnings("unchecked")
public class DefinitionRepository {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DefinitionRepository.class);

    private Object2ObjectArrayMap<Class<? extends Definition>, Definition[]> definitionMaps = new Object2ObjectArrayMap<>();
    private DataStore store;

    public DefinitionRepository() {
        logger.info("Loading definition repository...");
        store = Server.store;

        load(store, true);
    }

    public void load(DataStore store, boolean lazy) {
        // Load items
        int numItems = store.getIndex(2).getDescriptor().getLastFileId(10);
        ItemDefinition[] items = new ItemDefinition[numItems + 5];
        definitionMaps.put(ItemDefinition.class, items);

        int numAnims = store.getIndex(2).getDescriptor().getLastFileId(12);
        AnimationDefinition[] animationDefinitions = new AnimationDefinition[numAnims + 150];
        definitionMaps.put(AnimationDefinition.class, animationDefinitions);

        for (int id = 0; id < numItems; id++) {
            items[id] = loadDefinition(ItemDefinition.class, id);
        }

        // Load npcs
        int numNpcs = store.getIndex(2).getDescriptor().getLastFileId(9);
        NpcDefinition[] npcs = new NpcDefinition[numNpcs + 20_000];
        definitionMaps.put(NpcDefinition.class, npcs);

        if (!lazy) {
        /**
         * Required force read full cache..
         */
            for (int id = 0; id < numNpcs; id++) { // load osrs only
                npcs[id] = loadDefinition(NpcDefinition.class, id);
            }
        }

        // Load objects - disabled, 177 maps are dumped to raw data and loaded by 317 objdef codec. cache/osrs/maps/.dat + .idx
        int numObjects = store.getIndex(2).getDescriptor().getLastFileId(6);
        ObjectDefinition[] objects = new ObjectDefinition[numObjects];
        definitionMaps.put(ObjectDefinition.class, objects);

        /*if (!lazy) {
            for (int id = 0; id < numObjects; id++) {
                objects[id] = loadDefinition(ObjectDefinition.class, id);
            }
        }*/

        // Load maps - disabled, 177 maps are dumped to raw data and loaded by 317 objdef codec. cache/osrs/maps/.dat + .idx
        int maxMaps = 256 * 256;
        MapDefinition[] maps = new MapDefinition[maxMaps];
        definitionMaps.put(MapDefinition.class, maps);

        // This definition is a bit... hacky. It's to avoid recursive dependencies.
        /*if (!lazy) {
            for (int x = 0; x < 256; x++) {
                for (int z = 0; z < 256; z++) {
                    int region = (x << 8) | z;

                    try {
                        int mapId = store.getIndex(5).getDescriptor().getArchiveID("m" + x + "_" + z);
                        int landscapeId = store.getIndex(5).getDescriptor().getArchiveID("l" + x + "_" + z);
                        if (mapId >= 0 && landscapeId >= 0) {
                            maps[region] = new MapDefinition(x, z);
                        }
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }

            for (int x = 0; x < 256; x++) {
                for (int z = 0; z < 256; z++) {
                    int region = (x << 8) | z;

                    try {
                        loadDefinition(MapDefinition.class, region);
                    } catch (Exception ignored) {
                        if (ignored.getMessage() == null || !ignored.getMessage().contains("Error while parsing archive header"))
                            ignored.printStackTrace();
                    }
                }
            }
        }*/

        // Load varbits
        int numVarbits = store.getIndex(2).getDescriptor().getLastFileId(14);
        VarbitDefinition[] varbits = new VarbitDefinition[numVarbits];
        definitionMaps.put(VarbitDefinition.class, varbits);

        if (!lazy) {
            for (int id = 0; id < numVarbits; id++) {
                varbits[id] = loadDefinition(VarbitDefinition.class, id);
            }
        }

        // Load enums
        int numEnums = store.getIndex(2).getDescriptor().getLastFileId(8);
        EnumDefinition[] enums = new EnumDefinition[numEnums];
        definitionMaps.put(EnumDefinition.class, enums);

        if (!lazy) {
            for (int id = 0; id < numEnums; id++) {
                enums[id] = loadDefinition(EnumDefinition.class, id);
            }
        }

        int spotAnimCount = store.getIndex(2).getDescriptor().getLastFileId(13);

        // Load Area
        int numArea = store.getIndex(2).getDescriptor().getLastFileId(35);
        AreaDefinition[] areas = new AreaDefinition[numItems];
        definitionMaps.put(AreaDefinition.class, areas);

        if (!lazy) {
            for (int id = 0; id < numItems; id++) {
                areas[id] = loadDefinition(AreaDefinition.class, id);
            }
        }


        int numHealthBar = store.getIndex(2).getDescriptor().getLastFileId(33);
        HealthBarDefinition[] healthBarDefinitions = new HealthBarDefinition[numItems];
        definitionMaps.put(HealthBarDefinition.class, healthBarDefinitions);

        if (!lazy) {
            for (int id = 0; id < numItems; id++) {
                healthBarDefinitions[id] = loadDefinition(HealthBarDefinition.class, id);
            }
        }

       /* logger.info("Loaded {} item definitions.", numItems);*/
       /* logger.info("Loaded {} npc definitions.", numNpcs);*/
       /* logger.info("Loaded {} object definitions.", numObjects);*/
       /* logger.info("Loaded {} varbit definitions.", numVarbits);*/
       /* logger.info("Loaded {} enum definitions.", numEnums);*/
       /* logger.info("Loaded {} spotanim definitions.", spotAnimCount);*/
       /* logger.info("Loaded {} area definitions.", numArea);*/
/*
        logger.info("Loaded {} struct definitions.", store.getIndex(2).getDescriptor().getLastFileId(34));
        logger.info("Loaded {} varclient definitions.", store.getIndex(2).getDescriptor().getLastFileId(19));
        logger.info("Loaded {} varclientstring definitions.", store.getIndex(2).getDescriptor().getLastFileId(15));
        logger.info("Loaded {} varplayer definitions.", store.getIndex(2).getDescriptor().getLastFileId(16));
        logger.info("Loaded {} clientscript definitions.", store.getIndex(12).getDescriptor().getCount());
        logger.info("Loaded {} sprites.", store.getIndex(8).getDescriptor().getCount());
        logger.info("Loaded {} textures.", store.getIndex(9).getDescriptor().getCount());
        logger.info("Loaded {} skeletons.", store.getIndex(0).getDescriptor().getCount());
        logger.info("Loaded {} skins.", store.getIndex(1).getDescriptor().getCount());
        logger.info("Loaded {} interfaces.", store.getIndex(3).getDescriptor().getCount());
        logger.info("Loaded {} health bar definitions.", numHealthBar);*/
    }

    private <T extends Definition> T loadDefinition(Class<T> type, int id, boolean create) {
        if (type == ItemDefinition.class) {
            return (T) new ItemDefinition(id, store.getIndex(2).getContainer(10).getFileData(id, true, true));
        } else if (type == VarbitDefinition.class) {
            return (T) new VarbitDefinition(id, store.getIndex(2).getContainer(14).getFileData(id, true, true));
        } else if (type == EnumDefinition.class) {
            return (T) new EnumDefinition(id, store.getIndex(2).getContainer(8).getFileData(id, true, true));
        } else if (type == AnimationDefinition.class) {
            return (T) new AnimationDefinition(id, store.getIndex(2).getContainer(12).getFileData(id, true, true));
        } else if (type == NpcDefinition.class) {
            return (T) new NpcDefinition(id, store.getIndex(2).getContainer(9).getFileData(id, true, true));
        }
        else if (type == ObjectDefinition.class) {
            return (T) new ObjectDefinition(id, store.getIndex(2).getContainer(6).getFileData(id, true, true));
        } else if (type == MapDefinition.class) {
            //We don't load this the equivalent is loading the .dat maps
        } else if (type == VarpDefinition.class) {
            return (T) new VarpDefinition(id, store.getIndex(2).getContainer(16).getFileData(id, true, true));
        } else if (type == SpotanimDefinition.class) {
            return (T) new SpotanimDefinition(id, store.getIndex(2).getContainer(13).getFileData(id, true, true));
        } else if (type == AreaDefinition.class) {
            return (T) new AreaDefinition(id, store.getIndex(2).getContainer(35).getFileData(id, true, true));
        } else if(type == HealthBarDefinition.class) {
            return (T) new HealthBarDefinition(id, store.getIndex(2).getContainer(33).getFileData(id, true, true));
        }

        return null;
    }

    public <T extends Definition> T getItemDefinition(int id) {
        return (T) get(ItemDefinition.class, id, false);
    }

    private <T extends Definition> T loadDefinition(Class<T> type, int id) {
        return loadDefinition(type, id, false);
    }

    public <T extends Definition> T get(Class<T> type, int id) {
        return get(type, id, false);
    }

    public <T extends Definition> T get(Class<T> type, int id, boolean create) {
        T[] arr = (T[]) definitionMaps.get(type);

        if (id < 0 || id >= arr.length)
            return null;

        if (arr[id] == null) {
            return arr[id] = (T) loadDefinition(type, id, create);
        }

        return ((T[]) definitionMaps.get(type))[id];
    }

    public void unset(Class<? extends Definition> type, int id) {
        Object[] arr = (Object[]) definitionMaps.get(type);
        arr[id] = null;
    }

    public int total(Class<? extends Definition> type) {
        return definitionMaps.get(type).length;
    }

}

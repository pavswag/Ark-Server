package io.kyros.sql.MainSql;

import io.kyros.Server;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 09/03/2024
 */
public class SqlManager {

    public static SQLNetwork getGameSqlNetwork() {
        return Server.gameSqlNetwork;
    }
    public static SQLNetwork getRealmSqlNetwork() {
        return Server.realmSqlNetwork;
    }

}

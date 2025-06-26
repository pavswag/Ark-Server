package io.kyros.sql.MainSql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 09/03/2024
 */
public interface SQLNetworkTransaction {

    void call(Connection connection) throws SQLException;

}

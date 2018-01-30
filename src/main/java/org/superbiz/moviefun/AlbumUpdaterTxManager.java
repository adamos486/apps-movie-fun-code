package org.superbiz.moviefun;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

public class AlbumUpdaterTxManager extends DataSourceTransactionManager {

    public AlbumUpdaterTxManager(DataSource dataSource) {
        super(dataSource);
    }
}

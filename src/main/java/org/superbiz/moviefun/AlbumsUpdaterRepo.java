package org.superbiz.moviefun;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class AlbumsUpdaterRepo {
    private JdbcTemplate jdbc;
    private AlbumUpdaterTxManager manager;

    public AlbumsUpdaterRepo(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
        this.manager = new AlbumUpdaterTxManager(dataSource);
    }

    public boolean shouldStartAlbumUpdate() {
        jdbc.execute("SELECT (started_at) FROM album_scheduler_task FOR UPDATE");
        //TODO: If time limit has expired
        jdbc.execute("UPDATE");
        return true;
    }
}

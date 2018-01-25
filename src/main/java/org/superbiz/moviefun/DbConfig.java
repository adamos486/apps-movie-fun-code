package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration public class DbConfig {
  @Bean public DataSource albumsDataSource(@Value("${moviefun.datasources.albums.url}") String url,
      @Value("${moviefun.datasources.albums.username}") String username,
      @Value("${moviefun.datasources.albums.password}") String password) {
    return constructDataSource(url, username, password);
  }

  @Bean public DataSource moviesDataSource(@Value("${moviefun.datasources.movies.url}") String url,
      @Value("${moviefun.datasources.movies.username}") String username,
      @Value("${moviefun.datasources.movies.password}") String password) {
    return constructDataSource(url, username, password);
  }

  @Bean public HibernateJpaVendorAdapter getJpaAdapter() {
    HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
    adapter.setDatabase(Database.MYSQL);
    adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
    adapter.setGenerateDdl(true);
    return adapter;
  }

  @Bean public LocalContainerEntityManagerFactoryBean createAlbumsEntityBean(DataSource albumsDataSource,
      HibernateJpaVendorAdapter hibernateAdapter) {
    LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
    factoryBean.setDataSource(albumsDataSource);
    factoryBean.setJpaVendorAdapter(hibernateAdapter);
    factoryBean.setPackagesToScan("org.superbiz.moviefun");
    factoryBean.setPersistenceUnitName("persist-albums");
    return factoryBean;
  }

  @Bean public LocalContainerEntityManagerFactoryBean createMoviesEntityBean(DataSource moviesDataSource,
      HibernateJpaVendorAdapter hibernateAdapter) {
    LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
    factoryBean.setDataSource(moviesDataSource);
    factoryBean.setJpaVendorAdapter(hibernateAdapter);
    factoryBean.setPackagesToScan("org.superbiz.moviefun");
    factoryBean.setPersistenceUnitName("persist-movies");
    return factoryBean;
  }

  @Bean public PlatformTransactionManager moviesTxManager(
      @Qualifier("createMoviesEntityBean") LocalContainerEntityManagerFactoryBean createMoviesEntityBean) {
    return new JpaTransactionManager(createMoviesEntityBean.getObject());
  }

  @Bean public PlatformTransactionManager albumsTxManager(
      @Qualifier("createAlbumsEntityBean") LocalContainerEntityManagerFactoryBean createAlbumsEntityBean) {
    return new JpaTransactionManager(createAlbumsEntityBean.getObject());
  }

  private HikariDataSource constructDataSource(String url, String username, String password) {
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(url);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    return dataSource;
  }
}

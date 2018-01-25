package org.superbiz.moviefun;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

@Controller public class HomeController {

  private final MoviesBean moviesBean;
  private final AlbumsBean albumsBean;
  private final MovieFixtures movieFixtures;
  private final AlbumFixtures albumFixtures;
  private final PlatformTransactionManager moviesTxManager;
  private final PlatformTransactionManager albumsTxManager;

  @Autowired
  public HomeController(
      MoviesBean moviesBean,
      AlbumsBean albumsBean,
      MovieFixtures movieFixtures,
      AlbumFixtures albumFixtures,
      @Qualifier("moviesTxManager") PlatformTransactionManager moviesTxManager,
      @Qualifier("albumsTxManager") PlatformTransactionManager albumsTxManager) {
    this.moviesBean = moviesBean;
    this.albumsBean = albumsBean;
    this.movieFixtures = movieFixtures;
    this.albumFixtures = albumFixtures;
    this.moviesTxManager = moviesTxManager;
    this.albumsTxManager = albumsTxManager;
  }

  @GetMapping("/") public String index() {
    return "index";
  }

  @GetMapping("/setup") public String setup(Map<String, Object> model) {
    createAlbums();
    createMovies();

    model.put("movies", moviesBean.getMovies());
    model.put("albums", albumsBean.getAlbums());

    return "setup";
  }

  private void createMovies() {
    TransactionStatus tx = moviesTxManager.getTransaction(null);
    for (Movie movie : movieFixtures.load()) {
      moviesBean.addMovie(movie);
    }
    moviesTxManager.commit(tx);
  }

  private void createAlbums() {
    TransactionStatus tx = albumsTxManager.getTransaction(null);
    for (Album album : albumFixtures.load()) {
      albumsBean.addAlbum(album);
    }
    albumsTxManager.commit(tx);
  }
}

package org.superbiz.moviefun.albums;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller public class AlbumsController {

  private final AlbumsBean albumsBean;

  public AlbumsController(AlbumsBean albumsBean) {
    this.albumsBean = albumsBean;
  }

  @GetMapping("/albums") public String index(Map<String, Object> model) {
    model.put("albums", albumsBean.getAlbums());
    return "albums";
  }
}

package org.superbiz.moviefun.albums;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import org.apache.tika.Tika;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.Blob;
import org.superbiz.moviefun.BlobStore;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;

@Controller @RequestMapping("/albums") public class AlbumsController {

  private final AlbumsBean albumsBean;
  private BlobStore blobStore;

  public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
    this.albumsBean = albumsBean;
    if (blobStore != null) {
      this.blobStore = blobStore;
    }
  }

  @GetMapping public String index(Map<String, Object> model) {
    model.put("albums", albumsBean.getAlbums());
    return "albums";
  }

  @GetMapping("/{albumId}")
  public String details(@PathVariable long albumId, Map<String, Object> model) {
    model.put("album", albumsBean.find(albumId));
    return "albumDetails";
  }

  @PostMapping("/{albumId}/cover") public String uploadCover(@PathVariable long albumId,
      @RequestParam("file") MultipartFile uploadedFile) throws IOException {
    String fileName = "covers/" + albumId;

    Blob blob = new Blob(fileName, uploadedFile.getInputStream(), uploadedFile.getContentType());
    blobStore.put(blob);
    return format("redirect:/albums/%d", albumId);
  }

  @DeleteMapping
  public HttpEntity<?> deleteCovers() {
    blobStore.deleteAll();
    return new HttpEntity<>(HttpStatus.OK);
  }

  @GetMapping("/{albumId}/cover") public HttpEntity<byte[]> getCover(@PathVariable long albumId)
      throws IOException, URISyntaxException {
    Path coverFilePath = getExistingCoverPath(albumId);
    String coverFileName = format("covers/%d", albumId);
    Optional<Blob> optionalBlob = blobStore.get(coverFileName);


    byte[] targetArray = new byte[optionalBlob.get().getIs().available()];
    optionalBlob.get().getIs().read(targetArray);

    HttpHeaders headers = createImageHttpHeaders(coverFilePath, targetArray);
    return new HttpEntity<>(targetArray, headers);
  }

  private HttpHeaders createImageHttpHeaders(Path coverFilePath, byte[] imageBytes)
      throws IOException {
    String contentType = new Tika().detect(coverFilePath);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType(contentType));
    headers.setContentLength(imageBytes.length);
    return headers;
  }

  private File getCoverFile(@PathVariable long albumId) {
    try {
      String coverFileName = format("covers/%d", albumId);
      Optional<Blob> optionalBlob = blobStore.get(coverFileName);
      File targetFile = new File(optionalBlob.get().getName());

      return targetFile;

    } catch (IOException e) {
      System.out.println(e.getLocalizedMessage());
    }
    return null;
  }

  private Path getExistingCoverPath(@PathVariable long albumId) throws URISyntaxException {
    File coverFile = getCoverFile(albumId);
    Path coverFilePath;

    if (coverFile.exists()) {
      coverFilePath = coverFile.toPath();
    } else {
      coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
    }

    return coverFilePath;
  }
}

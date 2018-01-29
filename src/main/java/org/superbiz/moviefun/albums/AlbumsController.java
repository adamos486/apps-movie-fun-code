package org.superbiz.moviefun.albums;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import org.apache.tika.Tika;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.Blob;
import org.superbiz.moviefun.FileStore;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

@Controller @RequestMapping("/albums") public class AlbumsController {

  private final AlbumsBean albumsBean;
  private FileStore fileStore;

  public AlbumsController(AlbumsBean albumsBean) {
    this.albumsBean = albumsBean;
    this.fileStore = new FileStore();
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
//    saveUploadToFile(uploadedFile, getCoverFile(albumId));
    Blob blob = new Blob(uploadedFile.getName(), uploadedFile.getInputStream(), uploadedFile.getContentType());
    fileStore.put(blob);
    return format("redirect:/albums/%d", albumId);
  }

  @GetMapping("/{albumId}/cover") public HttpEntity<byte[]> getCover(@PathVariable long albumId)
      throws IOException, URISyntaxException {
    //TODO: Needs replacement
    Path coverFilePath = getExistingCoverPath(albumId);

    String coverFileName = format("covers/%d", albumId);
    Optional<Blob> optionalBlob = fileStore.get(coverFileName);
    byte[] targetArray = new byte[optionalBlob.get().getIs().available()];
    optionalBlob.get().getIs().read(targetArray);

//    byte[] imageBytes = readAllBytes(coverFilePath);
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
    //TODO: Needs Replacement
    try {
      String coverFileName = format("covers/%d", albumId);
      Optional<Blob> optionalBlob = fileStore.get(coverFileName);
      File targetFile = new File(optionalBlob.get().getName());

      return targetFile;

    } catch (IOException e) {
      System.out.println(e.getLocalizedMessage());
    }
    return null;
    //return new File(coverFileName);
  }

  private Path getExistingCoverPath(@PathVariable long albumId) throws URISyntaxException {
    //TODO: Needs replacement
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

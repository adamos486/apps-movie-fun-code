package org.superbiz.moviefun;

import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.String.format;

public class FileStore implements BlobStore {

  @Override public void put(org.superbiz.moviefun.Blob blob) throws IOException {
    if (blob == null) {
      return;
    }

    File targetFile = new File(blob.getName());

    byte[] targetArray = new byte[blob.getIs().available()];
    blob.getIs().read(targetArray);

    if (targetFile.exists()) {
      targetFile.delete();
      targetFile.getParentFile().mkdirs();
      targetFile.createNewFile();
    }

    try {
      Path file = Paths.get(targetFile.getAbsolutePath());
      Files.write(file, targetArray);
    } catch (IOException e) {
      System.out.println("Error writing blob file... " + e.getLocalizedMessage());
    }
  }

  @Override public Optional<org.superbiz.moviefun.Blob> get(String name) throws IOException {
    File f = new File(name);
    InputStream is = new FileInputStream(f);

    Blob blob = new Blob(name, is, "jpeg");
    return Optional.of(blob);
  }

  @Override public void deleteAll() {
    try {
      FileUtils.cleanDirectory(new File("covers"));
    } catch (IOException e) {
      System.out.println("Got an error clearing covers:: " + e.getLocalizedMessage());
    }
  }
}

package org.superbiz.moviefun;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

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
      Path file = Paths.get(targetFile.getName());
      Files.write(file, targetArray);
    } catch (IOException e) {
      System.out.println("Error writing blob file... " + e.getLocalizedMessage());
    }
  }

  @Override public Optional<org.superbiz.moviefun.Blob> get(String name) throws IOException {
    //TODO: Check for the existence of a file with that name.
    //TODO: Open a file that exists and return that.
    //TODO: Return something else if it doesn't.


    ClassLoader loader = FileStore.class.getClassLoader();
    Blob blob = new Blob(name, loader.getResourceAsStream(name), ".jpg");
    return Optional.of(blob);
  }

  @Override public void deleteAll() {

  }
}

package org.superbiz.moviefun;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CsvUtils {

  public static String readFile(String path) {
    ClassLoader loader = CsvUtils.class.getClassLoader();
    InputStream resourceAsStream = loader.getResourceAsStream(path);
    Scanner scan = new Scanner(resourceAsStream).useDelimiter("\\A");

    //Scanner scanner = new Scanner(new File(path)).useDelimiter("\\A");
    //
    if (scan.hasNext()) {
      return scan.next();
    } else {
      return "";
    }
  }

  public static <T> List<T> readFromCsv(ObjectReader objectReader, String path) {
    try {
      List<T> results = new ArrayList<>();

      MappingIterator<T> iterator = objectReader.readValues(readFile(path));

      while (iterator.hasNext()) {
        results.add(iterator.nextValue());
      }

      return results;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

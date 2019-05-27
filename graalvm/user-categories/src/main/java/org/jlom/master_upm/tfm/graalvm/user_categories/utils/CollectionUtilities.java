package org.jlom.master_upm.tfm.graalvm.user_categories.utils;

import java.util.Set;
import java.util.TreeSet;

public class CollectionUtilities {

  public static <T> Set<T> union(Set<T> setA, Set<T> setB) {
    Set<T> tmp = new TreeSet<>(setA);
    tmp.addAll(setB);
    return tmp;
  }

  public static <T> Set<T> difference(Set<T> setA, Set<T> setB) {
    Set<T> tmp = new TreeSet<>(setA);
    tmp.removeAll(setB);
    return tmp;
  }
}

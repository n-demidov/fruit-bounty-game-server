package com.demidovn.fruitbounty.game.model;

import java.util.Objects;
import lombok.ToString;

@ToString
public class Pair<K,V> {

  private K key;
  private V value;

  public K getKey() { return key; }

  public V getValue() { return value; }

  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Pair<?, ?> pair = (Pair<?, ?>) o;
    return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

}



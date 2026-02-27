package com.example.catalog.domain;

public record Catalog(String catalogId, String name) {
  public Catalog withName(String newName) {
    return new Catalog(catalogId, newName);
  }
}

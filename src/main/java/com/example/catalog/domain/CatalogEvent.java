package com.example.catalog.domain;

import akka.javasdk.annotations.TypeName;

public sealed interface CatalogEvent {
  @TypeName("catalog-created")
  record CatalogCreated(String name) implements CatalogEvent {}
}

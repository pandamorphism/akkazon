package com.example.catalog.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import com.example.catalog.domain.CatalogEvent;

import java.util.List;

@Component(id = "catalogs-view")
public class CatalogsView extends View {

  public record CatalogRow(String catalogId, String name) {}
  public record CatalogList(List<CatalogRow> catalogs) {}

  @Consume.FromEventSourcedEntity(CatalogEntity.class)
  public static class CatalogsUpdater extends TableUpdater<CatalogRow> {

    public Effect<CatalogRow> onEvent(CatalogEvent event) {
      return switch (event) {
        case CatalogEvent.CatalogCreated created -> {
          String catalogId = updateContext().eventSubject().get();
          yield effects().updateRow(new CatalogRow(catalogId, created.name()));
        }
      };
    }
  }

  @Query("SELECT * AS catalogs FROM catalogs")
  public QueryEffect<CatalogList> getAllCatalogs() {
    return queryResult();
  }
}

package com.example.catalog.application;

import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import com.example.catalog.domain.CatalogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component(id = "catalogs-view")
public class CatalogsView extends View {

  private static final Logger log = LoggerFactory.getLogger(CatalogsView.class);

  public record CatalogRow(String catalogId, String name) {}
  public record CatalogList(List<CatalogRow> catalogs) {}

  @Consume.FromEventSourcedEntity(CatalogEntity.class)
  public static class CatalogsUpdater extends TableUpdater<CatalogRow> {

    private static final Logger log = LoggerFactory.getLogger(CatalogsUpdater.class);

    public Effect<CatalogRow> onEvent(CatalogEvent event) {
      log.info("[VIEW-UPDATER] Received event: {}", event);
      return switch (event) {
        case CatalogEvent.CatalogCreated created -> {
          String catalogId = updateContext().eventSubject().get();
          log.info("[VIEW-UPDATER] Processing CatalogCreated: catalogId={}, name={}", catalogId, created.name());
          CatalogRow row = new CatalogRow(catalogId, created.name());
          log.info("[VIEW-UPDATER] Updating view row: {}", row);
          yield effects().updateRow(row);
        }
      };
    }
  }

  @Query("SELECT * AS catalogs FROM catalogs")
  public QueryEffect<CatalogList> getAllCatalogs() {
    log.info("[VIEW] getAllCatalogs() query invoked");
    QueryEffect<CatalogList> result = queryResult();
    log.info("[VIEW] Query executed, returning results");
    return result;
  }
}

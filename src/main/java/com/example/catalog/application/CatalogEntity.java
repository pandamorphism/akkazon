package com.example.catalog.application;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import com.example.catalog.domain.Catalog;
import com.example.catalog.domain.CatalogEvent;

@Component(id = "catalog")
public class CatalogEntity extends EventSourcedEntity<Catalog, CatalogEvent> {

  private final String entityId;

  public CatalogEntity(EventSourcedEntityContext context) {
    this.entityId = context.entityId();
  }

  @Override
  public Catalog emptyState() {
    return new Catalog(entityId, "");
  }

  public Effect<Done> create(String name) {
    if (!currentState().name().isEmpty()) {
      return effects().error("Catalog already exists");
    }
    return effects()
      .persist(new CatalogEvent.CatalogCreated(name))
      .thenReply(newState -> Done.getInstance());
  }

  public ReadOnlyEffect<Catalog> get() {
    return effects().reply(currentState());
  }

  @Override
  public Catalog applyEvent(CatalogEvent event) {
    return switch (event) {
      case CatalogEvent.CatalogCreated created -> new Catalog(entityId, created.name());
    };
  }
}

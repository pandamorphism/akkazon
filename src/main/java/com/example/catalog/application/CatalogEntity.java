package com.example.catalog.application;

import akka.Done;
import akka.javasdk.annotations.Component;
import akka.javasdk.eventsourcedentity.EventSourcedEntity;
import akka.javasdk.eventsourcedentity.EventSourcedEntityContext;
import com.example.catalog.domain.Catalog;
import com.example.catalog.domain.CatalogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(id = "catalog")
public class CatalogEntity extends EventSourcedEntity<Catalog, CatalogEvent> {

  private static final Logger log = LoggerFactory.getLogger(CatalogEntity.class);
  private final String entityId;

  public CatalogEntity(EventSourcedEntityContext context) {
    this.entityId = context.entityId();
    log.info("[ENTITY] CatalogEntity initialized for entityId={}", entityId);
  }

  @Override
  public Catalog emptyState() {
    log.debug("[ENTITY] emptyState() called for entityId={}", entityId);
    return new Catalog(entityId, "");
  }

  public Effect<Done> create(String name) {
    log.info("[ENTITY] create() called for entityId={}, name={}, currentState.name={}", entityId, name, currentState().name());
    if (!currentState().name().isEmpty()) {
      log.warn("[ENTITY] Catalog already exists: entityId={}, existingName={}", entityId, currentState().name());
      return effects().error("Catalog already exists");
    }
    log.info("[ENTITY] Persisting CatalogCreated event: entityId={}, name={}", entityId, name);
    return effects()
      .persist(new CatalogEvent.CatalogCreated(name))
      .thenReply(newState -> {
        log.info("[ENTITY] CatalogCreated event persisted successfully: entityId={}, newState={}", entityId, newState);
        return Done.getInstance();
      });
  }

  public ReadOnlyEffect<Catalog> get() {
    return effects().reply(currentState());
  }

  @Override
  public Catalog applyEvent(CatalogEvent event) {
    log.info("[ENTITY] applyEvent() called for entityId={}, event={}", entityId, event);
    Catalog newState = switch (event) {
      case CatalogEvent.CatalogCreated created -> {
        log.info("[ENTITY] Applying CatalogCreated: entityId={}, name={}", entityId, created.name());
        yield new Catalog(entityId, created.name());
      }
    };
    log.info("[ENTITY] Event applied, new state: entityId={}, state={}", entityId, newState);
    return newState;
  }
}

package com.example.catalog.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;

@HttpEndpoint("/catalogs")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class CatalogEndpoint {

  private static final Logger log = LoggerFactory.getLogger(CatalogEndpoint.class);
  private final ComponentClient componentClient;

  public CatalogEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public record CreateCatalogRequest(String name) {}

  @Post("/{catalogId}")
  public HttpResponse createCatalog(String catalogId, CreateCatalogRequest request) {
    log.info("[ENDPOINT] Received POST request to create catalog: catalogId={}, name={}", catalogId, request.name());
    componentClient
      .forEventSourcedEntity(catalogId)
      .method(CatalogEntity::create)
      .invoke(request.name());
    log.info("[ENDPOINT] Successfully invoked CatalogEntity.create for catalogId={}", catalogId);
    return HttpResponse.create().withStatus(201);
  }

  @Get
  public CatalogsView.CatalogList getAllCatalogs() {
    log.info("[ENDPOINT] Received GET request to retrieve all catalogs");
    CatalogsView.CatalogList result = componentClient
      .forView()
      .method(CatalogsView::getAllCatalogs)
      .invoke();
    log.info("[ENDPOINT] Retrieved {} catalogs from view", result.catalogs().size());
    return result;
  }
}

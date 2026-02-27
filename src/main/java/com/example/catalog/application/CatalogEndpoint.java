package com.example.catalog.application;

import akka.Done;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.client.ComponentClient;

@HttpEndpoint("/catalogs")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class CatalogEndpoint {

  private final ComponentClient componentClient;

  public CatalogEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public record CreateCatalogRequest(String name) {}

  @Post("/{catalogId}")
  public HttpResponse createCatalog(String catalogId, CreateCatalogRequest request) {
    componentClient
      .forEventSourcedEntity(catalogId)
      .method(CatalogEntity::create)
      .invoke(request.name());
    return HttpResponse.create().withStatus(201);
  }

  @Get
  public CatalogsView.CatalogList getAllCatalogs() {
    return componentClient
      .forView()
      .method(CatalogsView::getAllCatalogs)
      .invoke();
  }
}

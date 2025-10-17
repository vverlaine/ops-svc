package com.proyecto.ops.customers.model;

import java.util.UUID;

public class CustomerBasic {
  private UUID id;
  private String name;

  public CustomerBasic(UUID id, String name) {
    this.id = id;
    this.name = name;
  }
  public UUID getId() { return id; }
  public String getName() { return name; }
}
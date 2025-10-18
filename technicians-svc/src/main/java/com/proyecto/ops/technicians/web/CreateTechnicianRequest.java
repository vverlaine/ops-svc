package com.proyecto.ops.technicians.web;

import java.util.List;

public class CreateTechnicianRequest {
  private Boolean active;
  private List<String> skills;

  public Boolean getActive() { return active; }
  public void setActive(Boolean active) { this.active = active; }

  public List<String> getSkills() { return skills; }
  public void setSkills(List<String> skills) { this.skills = skills; }
}
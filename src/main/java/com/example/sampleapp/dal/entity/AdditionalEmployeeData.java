package com.example.sampleapp.dal.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "AdditionalEmployeeData")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true, chain = true)
public class AdditionalEmployeeData {

  @Id
  @Column(name = "EmployeeId")
  private Integer employeeId;

  @Setter(AccessLevel.NONE)
  @JoinColumn(name = "EmployeeId")
  @OneToOne(optional = false) @MapsId private Employee employee;

  @Column(name = "JoiningYear")
  private Integer joiningYear;

  @Column(name = "AliasName")
  private String aliasName;

  public AdditionalEmployeeData(final Employee employee) {
    this.employee = employee;
  }
}

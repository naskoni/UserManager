package com.naskoni.usermanager.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@MappedSuperclass
public class AbstractUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name", nullable = false, length = 50)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 50)
  private String lastName;

  @Temporal(TemporalType.DATE)
  @Column(name = "date_of_birth", columnDefinition = "datetime(3)")
  private Date dateOfBirth;
}

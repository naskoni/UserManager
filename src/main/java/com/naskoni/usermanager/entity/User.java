package com.naskoni.usermanager.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "app_user")
public class User extends AbstractUser {

  @Column(name = "email_address", nullable = false, unique = true)
  private String emailAddress;

  @Setter(AccessLevel.PRIVATE)
  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(columnDefinition = "timestamp(3)", nullable = false, updatable = false)
  private Date created;

  @Setter(AccessLevel.PRIVATE)
  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(columnDefinition = "timestamp(3)", nullable = false)
  private Date updated;
}

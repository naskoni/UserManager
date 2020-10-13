package com.naskoni.usermanager.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "app_user_archive")
public class UserArchive extends AbstractUser {

  @Column(name = "email_address", nullable = false)
  private String emailAddress;

  @Setter(AccessLevel.PRIVATE)
  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "deleted", columnDefinition = "timestamp(3)", nullable = false, updatable = false)
  private Date deleted;
}

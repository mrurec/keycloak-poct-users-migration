package com.mrurec.keycloakusersmigration.poct.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity(name = "OrganizationView")
@Table(name = "organizationview")
@Getter
@Setter
@Accessors(chain = true)
public class Organization implements Serializable {
    @Id
    private String identifier;
}

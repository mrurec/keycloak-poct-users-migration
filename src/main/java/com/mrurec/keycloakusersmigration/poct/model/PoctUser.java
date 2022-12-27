package com.mrurec.keycloakusersmigration.poct.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "UserView")
@Table(name = "userview")
@Getter
@Setter
@Accessors(chain = true)
public class PoctUser implements Serializable {

    private static final long serialVersionUID = 8621902934019283046L;

    @Id
    private String identifier;
    private String agbcode;
    @NotNull
    private boolean archived;
    private String email;
    private String mobilenumber;
    private String nameFamily;
    private String nameGiven;
    private String namePrefix;
    private String nameText;
    @NotNull
    private String organizationid;
    @JsonIgnore
    private String password;
    private String title;
    private String username;
    private String subject;
    private String issuer;
    private String operatorid;
    private String zorgportaalid;
}

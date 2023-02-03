package com.mrurec.keycloakusersmigration.poct.repository;

import com.mrurec.keycloakusersmigration.poct.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String> {

    @Query(value = "select t.mfarequired from organizationview o " +
            " inner join tenantview t on o.tenantid = t.identifier where o.identifier = :orgId",
    nativeQuery = true)
    boolean isMfaRequiredForOrganisation(@Param("orgId") String orgId);
}

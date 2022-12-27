package com.mrurec.keycloakusersmigration.poct.repository;

import com.mrurec.keycloakusersmigration.poct.model.PoctUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoctUserRepository extends JpaRepository<PoctUser, String> {
    List<PoctUser> findByEmail(String email);

    @Query(value = "select distinct u.email from userview u where u.email is not null and u.issuer <> :issuer and u.zorgportaalid is null order by u.email --#pageable\\n",
            nativeQuery = true)
    Page<String> findEmailsForBatchProcessing(@Param("issuer") String issuer, Pageable pageable);
}

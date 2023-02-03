package com.mrurec.keycloakusersmigration.batch;

import com.mrurec.keycloakusersmigration.keycloak.KeycloakProperties;
import com.mrurec.keycloakusersmigration.poct.model.PoctUser;
import com.mrurec.keycloakusersmigration.poct.repository.OrganizationRepository;
import com.mrurec.keycloakusersmigration.poct.repository.PoctUserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BatchProcessor implements ItemProcessor<String, List<PoctUser>> {
    private final PoctUserRepository poctUserRepository;
    private final OrganizationRepository organizationRepository;
    private final KeycloakProperties keycloakProperties;
    private final Keycloak kc;

    @Value("${poct.user.issuer}")
    private String issuer;

    @Override
    public List<PoctUser> process(String email) throws Exception {
        List<PoctUser> poctUsers = poctUserRepository.findByEmail(email);
        String firstName = poctUsers.get(0).getNameGiven();
        String lastName = poctUsers.get(0).getNameFamily();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(keycloakProperties.getTemporaryPassword());
        credential.setTemporary(true);

        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setCredentials(List.of(credential));

        List<String> organizationIds = poctUsers.stream()
                .map(PoctUser::getOrganizationid)
                .toList();

        for (String organizationId : organizationIds) {
            if (organizationRepository.isMfaRequiredForOrganisation(organizationId)) {
                user.setRequiredActions(List.of("CONFIGURE_TOTP_WITH_AMR"));
                break;
            }
        }

        kc.realm(keycloakProperties.getRealm()).users().create(user);
        List<UserRepresentation> userRepresentations = kc.realm(keycloakProperties.getRealm()).users().search(email);
        String newSubject = userRepresentations.get(0).getId();

        for (PoctUser poctUser : poctUsers) {
            poctUser.setIssuer(issuer);
            poctUser.setSubject(newSubject);
        }
        return poctUsers;
    }
}

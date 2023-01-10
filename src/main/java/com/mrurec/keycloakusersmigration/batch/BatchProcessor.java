package com.mrurec.keycloakusersmigration.batch;

import com.mrurec.keycloakusersmigration.keycloak.KeycloakProperties;
import com.mrurec.keycloakusersmigration.poct.model.PoctUser;
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
    private final PoctUserRepository repository;
    private final KeycloakProperties keycloakProperties;
    private final Keycloak kc;

    @Value("${poct.user.issuer}")
    private String issuer;

    @Override
    public List<PoctUser> process(String email) throws Exception {
        List<PoctUser> poctUsers = repository.findByEmail(email);
        String firstName = poctUsers.get(0).getNameGiven();
        String lastName = poctUsers.get(0).getNameFamily();
        String newSubject;

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue("Test123456");
        credential.setTemporary(true);

        // TODO: 22.12.2022 [yury] after setting SMTP remove credentials things

        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setCredentials(List.of(credential));
        kc.realm(keycloakProperties.getRealm()).users().create(user);
        List<UserRepresentation> userRepresentations = kc.realm(keycloakProperties.getRealm()).users().search(email);
        newSubject = userRepresentations.get(0).getId();

        for (PoctUser poctUser : poctUsers) {
            poctUser.setIssuer(issuer);
            poctUser.setSubject(newSubject);
        }
        return poctUsers;
    }
}

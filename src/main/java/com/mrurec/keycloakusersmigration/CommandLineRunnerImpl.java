package com.mrurec.keycloakusersmigration;

//@AllArgsConstructor
public class CommandLineRunnerImpl  {
/*    private PoctUserService poctUserService;
    private PoctUserRepository repository;
    private final KeycloakProperties keycloakProperties;

//    @Override
    public void run(String... args) throws Exception {
        String email = "sharona_abels@hotmail.com";
        List<PoctUser> poctUsers = poctUserService.findByEmail(email);
        String firstName = poctUsers.get(0).getNameGiven();
        String lastName = poctUsers.get(0).getNameFamily();
        String issuer = "https://dev-is.poctconnect.nl/realms/poct";
        String subject;
        try (Keycloak kc = KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getServerUrl())
                .realm("master")
                .clientId("admin-cli")
                .grantType(OAuth2Constants.PASSWORD)
                .username(keycloakProperties.getAdminUsername())
                .password(keycloakProperties.getAdminPassword())
                .build()) {
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
            subject = userRepresentations.get(0).getId();
        }
        for (PoctUser poctUser : poctUsers) {
            poctUser.setIssuer(issuer);
            poctUser.setSubject(subject);
            poctUserService.save(poctUser);
        }
    }*/
}

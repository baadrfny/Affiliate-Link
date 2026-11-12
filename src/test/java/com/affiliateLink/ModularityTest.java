package com.affiliateLink;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest {

    @Test
    void shouldVerifyModuleArchitecture() {

        ApplicationModules modules =
                ApplicationModules.of(AffiliateLinkApplication.class);

        modules.verify();
    }
}
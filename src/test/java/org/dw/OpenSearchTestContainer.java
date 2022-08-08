package org.dw;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.DockerImageName;

public class OpenSearchTestContainer extends GenericContainer<OpenSearchTestContainer> {

    public OpenSearchTestContainer() {
        super(DockerImageName.parse("opensearchproject/opensearch").withTag("2.1.0"));
        withExposedPorts(9200);
        withEnv("discovery.type", "single-node");
        withEnv("DISABLE_INSTALL_DEMO_CONFIG", "true");
        withEnv("DISABLE_SECURITY_PLUGIN", "true");
        setWaitStrategy((new HttpWaitStrategy())
                .forPort(9200)
                .forStatusCodeMatching(response -> response == 200 || response == 401));
    }

}

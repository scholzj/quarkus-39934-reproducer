package org.acme;

import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "greeting", mixinStandardHelpOptions = true)
public class GreetingCommand implements Runnable {

    @Parameters(paramLabel = "<name>", defaultValue = "picocli",
        description = "Your name.")
    String name;

    @Override
    public void run() {
        KubernetesClient client = new KubernetesClientBuilder().build();

        Pod pod = new PodBuilder()
                .withNewMetadata()
                    .withName("nginx")
                .endMetadata()
                .withNewSpec()
                    .withContainers(new ContainerBuilder()
                            .withName("nginx")
                            .withImage("nginx:1.14.2")
                            .withPorts(new ContainerPortBuilder().withContainerPort(80).build())
                            .build())
                    // Uncomment to fix the error
                    //.withOverhead(null)
                .endSpec()
                .build();

        try {
            client.pods().resource(pod).create();
        } catch (Exception e) {
            System.out.println("Failed to create the Pod: " + e);
        }
    }

}

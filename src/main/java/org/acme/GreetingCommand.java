package org.acme;

import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "quarkus-39934-reproducer", mixinStandardHelpOptions = true)
public class GreetingCommand implements Runnable {

    @CommandLine.Option(names = {"-n", "--namespace"}, defaultValue = "my-namespace", description = "Namespace where the od should be created.")
    String namespace;

    @Override
    public void run() {
        KubernetesClient client = new KubernetesClientBuilder().build();

        Pod pod = new PodBuilder()
                .withNewMetadata()
                    .withNamespace(namespace)
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

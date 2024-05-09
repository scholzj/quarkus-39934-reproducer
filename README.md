# Quarkus #39934 reproducer

This repository contains a reproducer for Quarkus https://github.com/quarkusio/quarkus/issues/39934 issue.
To reproduce it, do a native build:
```
mvn clean package -Pnative
```

And run it:
```
target/code-with-quarkus-1.0.0-SNAPSHOT-runner
```

You should see output like this:
```
$ target/code-with-quarkus-1.0.0-SNAPSHOT-runner
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/
2024-05-09 02:07:22,061 INFO  [io.quarkus] (main) code-with-quarkus 1.0.0-SNAPSHOT native (powered by Quarkus 3.10.0) started in 0.024s.
2024-05-09 02:07:22,061 INFO  [io.quarkus] (main) Profile prod activated.
2024-05-09 02:07:22,061 INFO  [io.quarkus] (main) Installed features: [cdi, kubernetes-client, picocli, smallrye-context-propagation, vertx]
Failed to create the Pod: io.fabric8.kubernetes.client.KubernetesClientException: Failure executing: POST at: https://api.ci-ln-vv7tpwb-76ef8.origin-ci-int-aws.dev.rhcloud.com:6443/api/v1/namespaces/myproject/pods. Message: pods "nginx" is forbidden: pod rejected: Pod Overhead set without corresponding RuntimeClass defined Overhead. Received status: Status(apiVersion=v1, code=403, details=StatusDetails(causes=[], group=null, kind=pods, name=nginx, retryAfterSeconds=null, uid=null, additionalProperties={}), kind=Status, message=pods "nginx" is forbidden: pod rejected: Pod Overhead set without corresponding RuntimeClass defined Overhead, metadata=ListMeta(_continue=null, remainingItemCount=null, resourceVersion=null, selfLink=null, additionalProperties={}), reason=Forbidden, status=Failure, additionalProperties={}).
2024-05-09 02:07:22,664 INFO  [io.quarkus] (main) code-with-quarkus stopped in 0.002s
```

Where the Kubernetes cluster complains about `pod rejected: Pod Overhead set without corresponding RuntimeClass defined Overhead`.
The problem seems to happen only with native build.
Running it with `mvn quarkus:dev` seems to work fine and creates the Pod.

Uncommenting the `.withOverhead(null)` part of the code in `GreetingCommand.java` on line 35 and rebuilding it fixes the error.


## My environment:

* MacOS (M1/Aarch64 based)
* Java version:
  ```
  openjdk 21.0.2 2024-01-16
  OpenJDK Runtime Environment GraalVM CE 21.0.2+13.1 (build 21.0.2+13-jvmci-23.1-b30)
  OpenJDK 64-Bit Server VM GraalVM CE 21.0.2+13.1 (build 21.0.2+13-jvmci-23.1-b30, mixed mode, sharing)
  ```
* Maven version:
  ```
  Maven home: /Users/scholzj/.m2/wrapper/dists/apache-maven-3.9.6-bin/3311e1d4/apache-maven-3.9.6
  Java version: 21.0.2, vendor: GraalVM Community, runtime: /Library/Java/JavaVirtualMachines/graalvm-community-openjdk-21.0.2+13.1/Contents/Home
  Default locale: en_CZ, platform encoding: UTF-8
  OS name: "mac os x", version: "14.4.1", arch: "aarch64", family: "mac"
  ```
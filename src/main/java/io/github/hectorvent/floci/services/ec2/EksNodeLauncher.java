package io.github.hectorvent.floci.services.ec2;

import io.github.hectorvent.floci.services.ec2.model.Instance;

/**
 * Launches EC2 instances whose user data joins them to an EKS cluster, such as the nodes that
 * Karpenter starts, as worker nodes of that cluster.
 */
public interface EksNodeLauncher {

    /**
     * Starts the instance as a worker node when its user data names a running cluster. The
     * instance stays pending until the node container runs.
     *
     * @return false when the instance is not an EKS node, so EC2 launches it as usual
     */
    boolean launchWorkerNode(Instance instance, String region);
}

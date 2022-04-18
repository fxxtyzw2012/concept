package com.github.linyuzai.connection.loadbalance.discovery;

import com.github.linyuzai.connection.loadbalance.core.server.ConnectionServer;
import com.github.linyuzai.connection.loadbalance.core.server.ConnectionServerProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class DiscoveryConnectionServerProvider implements ConnectionServerProvider {

    private final DiscoveryClient discoveryClient;

    private final Registration registration;

    private final ConnectionServer client;

    public DiscoveryConnectionServerProvider(DiscoveryClient discoveryClient, Registration registration) {
        this.discoveryClient = discoveryClient;
        this.registration = registration;
        this.client = new ServiceInstanceConnectionServer(registration);
    }

    @Override
    public List<ConnectionServer> getConnectionServers() {
        List<ConnectionServer> servers = new ArrayList<>();
        List<ServiceInstance> instances = discoveryClient.getInstances(registration.getServiceId());
        for (ServiceInstance instance : instances) {
            ConnectionServer server = newConnectionServer(instance);
            if (client.getInstanceId().equals(server.getInstanceId())) {
                continue;
            }
            servers.add(server);
        }
        return servers;
    }

    public ConnectionServer newConnectionServer(ServiceInstance instance) {
        return new ServiceInstanceConnectionServer(instance);
    }

    @AllArgsConstructor
    public static class ServiceInstanceConnectionServer implements ConnectionServer {

        private ServiceInstance instance;

        @Override
        public String getInstanceId() {
            return instance.getInstanceId();
        }

        @Override
        public String getServiceId() {
            return instance.getServiceId();
        }

        @Override
        public String getHost() {
            return instance.getHost();
        }

        @Override
        public int getPort() {
            return instance.getPort();
        }

        @Override
        public Map<String, String> getMetadata() {
            return instance.getMetadata();
        }

        @Override
        public URI getUri() {
            return instance.getUri();
        }

        @Override
        public String getScheme() {
            return instance.getScheme();
        }

        @Override
        public boolean isSecure() {
            return instance.isSecure();
        }
    }
}

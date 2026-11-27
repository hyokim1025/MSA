package se.magnus.util.http;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceUtil {
    private final String serviceAddress;

    public ServiceUtil(@Value("${server.port}") String port) {
        this.serviceAddress = resolveAddress(port);
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    private String resolveAddress(String port) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostName() + "/" + localHost.getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            return "unknown host name/unknown IP address:" + port;
        }
    }
}

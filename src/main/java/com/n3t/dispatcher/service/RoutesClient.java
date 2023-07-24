package com.n3t.dispatcher.service;

import com.google.maps.routing.v2.*;
import com.google.type.LatLng;
import com.n3t.dispatcher.domain.GeoLocation;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.NettyChannelBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class RoutesClient {
    // private static final Logger logger =
    // Logger.getLogger(RoutesClient.class.getName());
    private final RoutesGrpc.RoutesBlockingStub blockingStub;

    public RoutesClient(String apiKey) {
        Channel channel = NettyChannelBuilder.forAddress("routes.googleapis.com", 443).build();
        channel = ClientInterceptors.intercept(channel,
                new RoutesInterceptor(apiKey));
        blockingStub = RoutesGrpc.newBlockingStub(channel);
    }

    public Iterator<RouteMatrixElement> computeRouteMatrix(Stream<GeoLocation> ambulancesLocation,
                                                           GeoLocation patientLocation) {
        ComputeRouteMatrixRequest.Builder requestBuilder = ComputeRouteMatrixRequest.newBuilder();
        Stream<RouteMatrixOrigin> listOrigins = ambulancesLocation
                .map((geoLocation) -> RouteMatrixOrigin.newBuilder().setWaypoint(geoLocation.toWaypoint()).build());
        ComputeRouteMatrixRequest request = requestBuilder.addAllOrigins(listOrigins.toList())
                .addDestinations(RouteMatrixDestination.newBuilder().setWaypoint(patientLocation.toWaypoint()))
                .setTravelMode(RouteTravelMode.DRIVE).setRoutingPreference(RoutingPreference.TRAFFIC_AWARE).build();
        return blockingStub.withDeadlineAfter(5, TimeUnit.SECONDS).computeRouteMatrix(request);
    }

    // For more detail on inserting API keys, see:
    // https://cloud.google.com/endpoints/docs/grpc/restricting-api-access-with-api-keys#java
    // For more detail on system parameters (such as FieldMask), see:
    // https://cloud.google.com/apis/docs/system-parameters
    private static final class RoutesInterceptor implements ClientInterceptor {
        private static final Logger logger = Logger.getLogger(RoutesInterceptor.class.getName());
        private static Metadata.Key API_KEY_HEADER = Metadata.Key.of("x-goog-api-key",
                Metadata.ASCII_STRING_MARSHALLER);
        private static Metadata.Key FIELD_MASK_HEADER = Metadata.Key.of("x-goog-fieldmask",
                Metadata.ASCII_STRING_MARSHALLER);
        private final String apiKey;

        public RoutesInterceptor(String apiKey) {
            this.apiKey = apiKey;
        }

        @Override
        public ClientCall interceptCall(MethodDescriptor method, CallOptions callOptions, Channel next) {
            logger.info("Intercepted " + method.getFullMethodName());
            ClientCall call = next.newCall(method, callOptions);
            call = new ForwardingClientCall.SimpleForwardingClientCall(call) {
                @Override
                public void start(Listener responseListener, Metadata headers) {
                    headers.put(API_KEY_HEADER, apiKey);
                    // Note that setting the field mask to * is OK for testing, but discouraged in
                    // production.
                    // For example, for ComputeRoutes, set the field mask to
                    // "routes.distanceMeters,routes.duration,routes.polyline.encodedPolyline"
                    // in order to get the route distances, durations, and encoded polylines.
                    // headers.put(FIELD_MASK_HEADER, "routes.distanceMeters,routes.duration");
                    headers.put(FIELD_MASK_HEADER, "*");
                    super.start(responseListener, headers);
                }
            };
            return call;
        }
    }

}
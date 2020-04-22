package com.hhit.grpcclient;


import com.hhit.grpclib.GreeterGrpc;
import com.hhit.grpclib.HelloReply;
import com.hhit.grpclib.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class GrpcClient {

    private ManagedChannel channel;
    private GreeterGrpc.GreeterBlockingStub blockingStub;
    private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());

    public GrpcClient(){
        channel = ManagedChannelBuilder.forAddress("127.0.0.1",8888)
                .usePlaintext(true)
                .build();

        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public  void greet(String name){
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try{
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e)
        {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Message from gRPC-Server: "+response.getMessage());
    }
}

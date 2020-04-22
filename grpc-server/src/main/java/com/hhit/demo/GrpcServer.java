package com.hhit.demo;

import com.hhit.grpclib.GreeterGrpc;
import com.hhit.grpclib.HelloReply;
import com.hhit.grpclib.HelloRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class GrpcServer {
    private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());


//    @Value("${grpc.server.port}")
//    Integer port;

    private Integer port=8888;
    private Server server;

    protected void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new GreeterImpl())
                .build()
                .start();
        logger.info("Server started, listening on "+ port);

        Runtime.getRuntime().addShutdownHook(new Thread(){

            @Override
            public void run(){

                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                GrpcServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop(){
        if (server != null){
            server.shutdown();
        }
    }

    // block 一直到退出程序
    protected void blockUntilShutdown() throws InterruptedException {
        if (server != null){
            server.awaitTermination();
        }
    }

    // 实现 定义一个实现服务接口的类
    private class GreeterImpl extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver){
            HelloReply reply = HelloReply.newBuilder().setMessage(("Server reply:  "+req.getName())).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
            System.out.println("Message from gRPC-Client:" + req.getName());
        }
    }
}

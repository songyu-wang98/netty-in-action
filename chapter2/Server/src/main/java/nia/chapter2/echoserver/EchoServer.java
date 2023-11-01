package nia.chapter2.echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Listing 2.2 EchoServer class
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args)
        throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: " + EchoServer.class.getSimpleName() +
                " <port>"
            );
            return;
        }
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup(); // 创建 EventLoopGroup 实例以进行事件的处理，如接受新连接以及读/写数据
        try {
            ServerBootstrap b = new ServerBootstrap(); // 创建 ServerBootstrap 实例以引导和绑定服务器
            b.group(group)
                .channel(NioServerSocketChannel.class) // 指定使用 NIO 的传输 Channel
                .localAddress(new InetSocketAddress(port)) // 使用指定的端口设置套接字地址
                .childHandler(new ChannelInitializer<SocketChannel>() { // 添加一个 EchoServerHandler 到子 Channel 的 ChannelPipeline
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(serverHandler); // EchoServerHandler 被标注为
                        // @Shareable，所以我们可以总是使用同样的实例初始化每一个新的 Channel
                    }
                });

            ChannelFuture f = b.bind().sync(); // 异步地绑定服务器；调用 sync() 方法阻塞当前线程直到绑定完成
            System.out.println(EchoServer.class.getName() +
                " started and listening for connections on " + f.channel().localAddress());
            f.channel().closeFuture().sync(); // 获取 Channel 的 CloseFuture，并且阻塞当前线程直到它完成
        } finally {
            group.shutdownGracefully().sync(); // 关闭 EventLoopGroup，释放所有的资源
        }
    }
}

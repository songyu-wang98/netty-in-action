package nia.chapter2.echoclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Listing 2.4 Main class for the client
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start()
        throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); // 创建 Bootstrap
            b.group(group) // 指定 EventLoopGroup 以处理客户端事件；需要适用于 NIO 的实现
                .channel(NioSocketChannel.class) // 适用于 NIO 传输的 Channel 类型
                .remoteAddress(new InetSocketAddress(host, port)) // 设置服务器的 InetSocketAddress
                .handler(new ChannelInitializer<SocketChannel>() { // 在创建 Channel 时，向 ChannelPipeline 中添加一个 EchoClientHandler 实例
                    @Override
                    public void initChannel(SocketChannel ch)
                        throws Exception {
                        ch.pipeline().addLast(
                             new EchoClientHandler());
                    }
                });
            ChannelFuture f = b.connect().sync(); // 连接到远程节点，阻塞等待直到连接完成
            f.channel().closeFuture().sync(); // 阻塞，直到 Channel 关闭
        } finally {
            group.shutdownGracefully().sync(); // 调用 shutdownGracefully() 来关闭线程池和释放所有资源
        }
    }

    public static void main(String[] args)
            throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: " + EchoClient.class.getSimpleName() +
                    " <host> <port>"
            );
            return;
        }

        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        new EchoClient(host, port).start();
    }
}


import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.channel.socket.SocketChannel

class Server {
    companion object {
        private const val PORT = 8981
    }
    fun start() {
        val parentGroup = NioEventLoopGroup(1)
        val childGroup = NioEventLoopGroup();

        try {
            val serverBootstrap = ServerBootstrap()
            serverBootstrap.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel::class.java)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(LoggingHandler(LogLevel.DEBUG))
                .childHandler(
                    object : ChannelInitializer<SocketChannel>() {
                        public override fun initChannel(ch: SocketChannel) {
                            ch.pipeline().addLast(EchoHandler())
                        }
                    }
                )

            val cf = serverBootstrap.bind(PORT).sync()
            cf.channel().closeFuture().sync()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            parentGroup.shutdownGracefully()
            childGroup.shutdownGracefully()
        }
    }
}
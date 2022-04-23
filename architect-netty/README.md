# quick
1. easy是简单的客户端/服务端代码
2. marshalling是对java序列化的编解码器，marshal（数据编码），unmashal（数据分解）
3. pkg1是定长半包处理器，FixedLengthFrameDecoder
4. pkg2是DelimiterBasedFrameDecoder分隔符做码流结束标识的消息进行编解码，处理拆包/粘包
5. protobuf是基于Protobuf协议对POJO对象进行编解码，包括半包处理


# ch12
根据Netty权威指南（第2版）中第12章-私有协议栈的实现代码

# custom
自定义的协议

# http
1. 基于http协议的netty客户端/服务端


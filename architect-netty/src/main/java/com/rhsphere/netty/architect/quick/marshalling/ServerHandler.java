package com.rhsphere.netty.architect.quick.marshalling;


import com.rhsphere.netty.architect.utils.GzipUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author ludepeng
 * @since 2022/3/26 10:34 上午
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 接受request请求 并进行业务处理
		RequestData rd = (RequestData) msg;
		System.err.println("id: " + rd.getId() + ", name: " + rd.getName() + ", requestMessage: " + rd.getRequestMessage());

		byte[] attachment = GzipUtils.ungzip(rd.getAttachment());

		String path = System.getProperty("user.dir")
			+ File.separatorChar + "receive" + File.separatorChar + "001.jpg";

		FileOutputStream fos = new FileOutputStream(path);
		fos.write(attachment);
		fos.close();

		//	回送相应数据
		ResponseData responseData = new ResponseData();
		responseData.setId("response " + rd.getId());
		responseData.setId("response " + rd.getName());
		responseData.setResponseMessage("响应信息");

		ctx.writeAndFlush(responseData);
	}


}

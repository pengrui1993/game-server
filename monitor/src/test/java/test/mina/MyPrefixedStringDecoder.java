package test.mina;

import org.apache.mina.core.buffer.BufferDataException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.prefixedstring.PrefixedStringDecoder;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/** 重写mina源码MyPrefixedStringDecoder 重写解码方法，支持安全沙箱*/
public class MyPrefixedStringDecoder extends CumulativeProtocolDecoder {
	
		private final AttributeKey POLICY = new AttributeKey(this.getClass(),"policy");
		public static final String security = "<policy-file-request/>";
		private static String xmlStr = "";
		static {
			StringBuffer xmlBuffer = new StringBuffer();
			xmlBuffer.append("<cross-domain-policy>");
			xmlBuffer.append("<allow-access-from domain=\"");
			xmlBuffer.append("*");
			xmlBuffer.append("\" to-ports=\"");
			xmlBuffer.append("*");
			xmlBuffer.append("\"/>");
			xmlBuffer.append("</cross-domain-policy>");
			xmlBuffer.append("\0");
			xmlStr = xmlBuffer.toString();
		}
		public final static int DEFAULT_PREFIX_LENGTH = 4;
	    public final static int DEFAULT_MAX_DATA_LENGTH = 64*1024;//64k
	    private final Charset charset;
	    private int prefixLength = DEFAULT_PREFIX_LENGTH;
	    private int maxDataLength = DEFAULT_MAX_DATA_LENGTH;
	    /**
	     * @param charset       the charset to use for encoding
	     * @param prefixLength  the length of the prefix
	     * @param maxDataLength maximum number of bytes allowed for a single String
	     */
	    public MyPrefixedStringDecoder(Charset charset, int prefixLength, int maxDataLength) {
	        this.charset = charset;
	        this.prefixLength = prefixLength;
	        this.maxDataLength = maxDataLength;
	    }

	    public MyPrefixedStringDecoder(Charset charset, int prefixLength) {
	        this(charset, prefixLength, DEFAULT_MAX_DATA_LENGTH);
	    }

	    public MyPrefixedStringDecoder(Charset charset) {
	        this(charset, DEFAULT_PREFIX_LENGTH);
	    }

	    /**
	     * Sets the number of bytes used by the length prefix
	     *
	     * @param prefixLength the length of the length prefix (1, 2, or 4)
	     */
	    public void setPrefixLength(int prefixLength) {
	        this.prefixLength = prefixLength;
	    }

	    /**
	     * Gets the length of the length prefix (1, 2, or 4)
	     *
	     * @return length of the length prefix
	     */
	    public int getPrefixLength() {
	        return prefixLength;
	    }

	    /**
	     * Sets the maximum allowed value specified as data length in the incoming data
	     * <p>
	     * Useful for preventing an OutOfMemory attack by the peer.
	     * The decoder will throw a {@link BufferDataException} when data length
	     * specified in the incoming data is greater than maxDataLength
	     * The default value is {@link PrefixedStringDecoder#DEFAULT_MAX_DATA_LENGTH}.
	     * </p>
	     *
	     * @param maxDataLength maximum allowed value specified as data length in the incoming data
	     */
	    public void setMaxDataLength(int maxDataLength) {
	        this.maxDataLength = maxDataLength;
	    }

	    /**
	     * Gets the maximum number of bytes allowed for a single String
	     *
	     * @return maximum number of bytes allowed for a single String
	     */
	    public int getMaxDataLength() {
	        return maxDataLength;
	    }
		static final int MSG_HEADER_SIZE = 4;
		static final ByteOrder GAME_ENCODER_ENDIAN = ByteOrder.BIG_ENDIAN;
	    @Override
		protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
	    	in.order(GAME_ENCODER_ENDIAN);
	    	//安全沙箱请求检测
	    	int k = in.position();//记录位置，不要因为检测而导互位置改变
	    	if (isSecurityRequest(session, in)) { //flash 安全沙箱处理
				//out.write(security);
	    		session.write(security);
				in.free();
				return true;
	    	}else{
	    		//mina默认解码
	    		in.position(k);//重新定位
	    		int remaining_size=in.remaining();
	    		if(remaining_size>4)//看看这个消息是否合法，合法消息是前面一个4bytes的int描述后面消息的大小
	    		{
	    			
	    			int data_size=in.getInt();
	    			if(data_size<0 || data_size>remaining_size-MSG_HEADER_SIZE)
	    			{
	    				in.position(k);//
		        		return false;
	    			}else
	    			{
	    				in.position(k);//重新定位
	    			}
	    		}
	    	
	    		//
		        if (in.prefixedDataAvailable(prefixLength, maxDataLength)) 
		        {
		        	 
		        	remaining_size=in.remaining();
			        if(remaining_size>MSG_HEADER_SIZE)
			        {
			        	int data_size=in.getInt();
			        	//
			        	IoBuffer bbuf=IoBuffer.allocate(data_size+MSG_HEADER_SIZE);
			        	bbuf.order(GAME_ENCODER_ENDIAN);
			        	//
			        	in.position(k);//重新定位
			        	//
			        	in.get(bbuf.array(), 0, data_size+MSG_HEADER_SIZE);
			        	//设置读取之后的位置
			        	in.position(k+MSG_HEADER_SIZE+data_size);//
			        	//System.out.println("msg size="+data_size);
			        	//
			        	bbuf.position(0);
			        	//
			        	out.write(bbuf);
			        	
			        	return true;
		        	}
		        }
		        return false;
		        
	    	}
	    	
	    }

		/** 是否为策略文件请求*/
		private boolean isSecurityRequest(IoSession session, IoBuffer in) {
			Boolean policy = (Boolean) session.getAttribute(POLICY);
			if (policy != null) {
				return false;
			}
			String request = this.getRequest(in);
			boolean result;
			session.setAttribute(POLICY, result = request.startsWith(security));
			return result;
		}
		
		private String getRequest(IoBuffer in) {
			byte[] bytes = new byte[in.limit()];
			in.get(bytes);// 从IoBuffer中获取数据并放入bytes中
			return new String(bytes, StandardCharsets.UTF_8);
		}
}

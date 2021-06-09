package utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;


import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class Utils {
	public static int KeepNumBit = 8;
	public static byte[] toByteArray(String hexString) {
		if (hexString == null || hexString.length() < 1)
			throw new IllegalArgumentException("this hexString must not be empty");

		hexString = hexString.toLowerCase();
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++) {
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}
		return byteArray;
	}

	public static String toHexString(byte[] byteArray) {
		if (byteArray == null || byteArray.length < 1)
			throw new IllegalArgumentException("this byteArray must not be null or empty");

		final StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			if ((byteArray[i] & 0xff) < 0x10)
				hexString.append("0");
			hexString.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return hexString.toString().toLowerCase();
	}
	//+
	public static double doubleAdd(double d1,double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.add(bd2).doubleValue();
	}
	/**
	 * "-"
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double doubleSub(double d1,double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.subtract(bd2).doubleValue();
	}
	//*
	public static double doubleMul(double d1,double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.multiply(bd2).doubleValue();
	}
	//"/"
	public static double doubleDiv(double d1,double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.divide(bd2,KeepNumBit,BigDecimal.ROUND_DOWN).doubleValue();
	}
	public static double doubleDiv(double d1,double d2,int roundNum) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.divide(bd2,roundNum,BigDecimal.ROUND_DOWN).doubleValue();
	}
	public static double doubelKeepNum(double d1) {
		//ROUND_UP	
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		return bd1.divide(new BigDecimal("1"),KeepNumBit,BigDecimal.ROUND_DOWN).doubleValue();
	}
	/*public static double doubelKeepNumUp(double d1) {
		//ROUND_UP	
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		return bd1.divide(new BigDecimal("1"),KeepNumBit,BigDecimal.ROUND_UP).doubleValue();
	}*/
	/*public static double keepNumAmount(double num,double price) {
		return Utils.doubelKeepNum(Utils.doubleMul(num, price));
	}
	public static double keepNumAmountUp(double num,double price) {
		return Utils.doubelKeepNum(Utils.doubleMul(num, price));
	}*/
	public static String changeListToString(List list) {
		if(list == null || list.size()<1) {
			return "[]";
		}
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for(int i = 0;i<list.size();i++) {//Object object : list) {
			builder.append(JsonObject.mapFrom(list.get(i)).toString());
			if(i<list.size()-1) {
				builder.append(",");
			}
		}
		builder.append("]");
		return builder.toString();
	}
	/**
	 * 获取当前小数点后位数
	 * @param num
	 * @return
	 */
	public static int getNumPointLength(double num) {
		String s = num + "";
		return s.substring(s.indexOf('.') + 1).length();
	}
	public static Future<JsonObject> getConfig(Vertx vertx,String file) {
		Future<JsonObject> futrue = Future.future();
		ConfigStoreOptions fileStore = new ConfigStoreOptions()
		        .setType("file")
		        .setConfig(new JsonObject().put("path", file));

		ConfigRetrieverOptions options = new ConfigRetrieverOptions()
		        .addStore(fileStore);
		ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
		retriever.getConfig(ar -> {
			  if (ar.succeeded()) {
				  futrue.complete(ar.result());
			  } else {
				  futrue.fail("解析config失败");
			  }
		});
		return futrue;
	}
	public static int changUserid(String str) {
		if(str == null || str.length()<1) {
			return -1;
		}
		try {
			return Integer.parseInt(str);
		}catch (Exception e) {
		}
		return -1;
	}
	public static String getLocalHost(String startip) {
		try {
			return getLocalHostLANAddress(startip).getHostAddress();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static InetAddress getLocalHostLANAddress(String startip) throws Exception {
		try {
	        InetAddress candidateAddress = null;
	        // 遍历所有的网络接口
	        for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
	            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
	            // 在所有的接口下再遍历IP
	            for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
	                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
	                if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
	                    if (inetAddr.isSiteLocalAddress() && inetAddr.getHostAddress().startsWith(startip)) {
	                        // 如果是site-local地址，就是它了
	                        return inetAddr;
	                    } else if (candidateAddress == null) {
	                        // site-local类型的地址未被发现，先记录候选地址
	                        candidateAddress = inetAddr;
	                    }
	                }
	            }
	        }
	        if (candidateAddress != null) {
	            return candidateAddress;
	        }
	        // 如果没有发现 non-loopback地址.只能用最次选的方案
	        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
	        return jdkSuppliedAddress;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	/***
	 *  true:already in using  false:not using 
	 * @param port
	 */
	public static boolean isLoclePortUsing(String startip,int port){
		boolean flag = true;
		try {
			flag = isPortUsing(Utils.getLocalHost(startip), port);
		} catch (Exception e) {
		}
		return flag;
	}
	/***
	 *  true:already in using  false:not using 
	 * @param host
	 * @param port
	 * @throws UnknownHostException 
	 */
	public static boolean isPortUsing(String host,int port) throws UnknownHostException{
		boolean flag = false;
		//InetAddress theAddress = InetAddress.getByName(host);
		try {
			Socket s = new Socket(); 
			s.bind(new InetSocketAddress(host, port)); 
			s.close();
			flag=true;
			//socket.close();
			//flag = true;
		} catch (IOException e) {
			//e.printStackTrace();
		}
		return flag;
	}


	public static void main(String[] args) {  
	}
}

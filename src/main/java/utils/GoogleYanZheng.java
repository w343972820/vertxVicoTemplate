package com.binance.api.client.mainVico;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class GoogleYanZheng {

	//public ArrayList<Long> ha= new ArrayList<>();
	int window_size = 3; // default 3 - max 17
	public void setWindowSize(int s) {
		if (s >= 1 && s <= 17)
			window_size = s;
	}
	public ArrayList<Long> check_code(String secret, long timeMsec) {
		Base32 codec = new Base32();
		byte[] decodedKey = codec.decode(secret);
		long t = (timeMsec / 1000L) / 30L;
		long hash=0;
		ArrayList<Long> hashs=new ArrayList<Long>();
		for (int i = -window_size; i <= window_size; ++i) {		
			try {
				hash = verify_code(decodedKey, t + i);
				hashs.add(hash);
				//System.out.println(hash);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}
		return hashs;
		/*String list1="";
		String list2="";
		String list3="";
		String results="";
		if (hashs.size()>3) {
			list1=Long.toString(hashs.get(hashs.size()-1));
			list2=Long.toString(hashs.get(hashs.size()-2));
			list3=Long.toString(hashs.get(hashs.size()-3));
			if (list1.length()<6 && list1.length()>4) {
				results="0"+list1;
				return results;
			}
			if (list1.length()==6) {
				return list1;
			}
			if (list2.length()<6 && list2.length()>4) {
				results="0"+list2;
				return results;
			}
			if (list2.length()==6) {
				return list2;
			}
			if (list3.length()<6 && list3.length()>4) {
				results="0"+list3;
				return results;
			}
			if (list3.length()==6) {
				return list3;
			}		
		}
		
		return Long.toString(hash);*/
	}

	private static int verify_code(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
		byte[] data = new byte[8];
		long value = t;
		for (int i = 8; i-- > 0; value >>>= 8) {
			data[i] = (byte) value;
		}
		SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signKey);
		byte[] hash = mac.doFinal(data);
		int offset = hash[20 - 1] & 0xF;
		// We're using a long because Java hasn't got unsigned int.
		long truncatedHash = 0;
		for (int i = 0; i < 4; ++i) {
			truncatedHash <<= 8;
			// We are dealing with signed bytes:
			// we just keep the first byte.
			truncatedHash |= (hash[offset + i] & 0xFF);
		}
		truncatedHash &= 0x7FFFFFFF;
		truncatedHash %= 1000000;
		return (int) truncatedHash;
	}
	
	public ArrayList<Long>  getGoogleYanZheng(String secret) {
		GoogleYanZheng ga = new GoogleYanZheng();
		//String secret="N3W2ZQR2QWSEJIWO";
		long t = System.currentTimeMillis();
		ga.setWindowSize(5);
		ArrayList<Long>  r = ga.check_code(secret, t);
		return r;
	}
}

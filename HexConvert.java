package com.hotent.core.util;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wly on 2018/4/17.
 */
public class HexConvert {
	
	/**
	 *  ASCII 转 16进制
	 */
	public static String  convertStringToHex(String str){
	 
	    char[] chars = str.toCharArray();
	 
	    StringBuffer hex = new StringBuffer();
	    for(int i = 0; i < chars.length; i++){
	        hex.append(Integer.toHexString((int)chars[i]));
	    }
	 
	    return hex.toString();
	}
	
	/**
	 * 16进制转  ASCII
	 */
	public static String convertHexToString(String hex){
	 
	    StringBuilder sb = new StringBuilder();
	    StringBuilder sb2 = new StringBuilder();
	 
	    for( int i=0; i<hex.length()-1; i+=2 ){
	 
	        String s = hex.substring(i, (i + 2));           
	        int decimal = Integer.parseInt(s, 16);          
	        sb.append((char)decimal);
	        sb2.append(decimal);
	    }
	 
	    return sb.toString();
	}
	public static byte[] hexStringToBytes(String hexString) {
	    if (hexString == null || hexString.equals("")) {
	        return null;
	    }
	    // toUpperCase将字符串中的所有字符转换为大写
	    hexString = hexString.toUpperCase();
	    int length = hexString.length() / 2;
	    // toCharArray将此字符串转换为一个新的字符数组。
	    char[] hexChars = hexString.toCharArray();
	    byte[] d = new byte[length];
	    for (int i = 0; i < length; i++) {
	        int pos = i * 2;
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
	    }
	    return d;
	}
	//返回匹配字符
	private static byte charToByte(char c) {
	    return (byte) "0123456789ABCDEF".indexOf(c);
	}
	 
	//将字节数组转换为short类型，即统计字符串长度
	public static short bytes2Short2(byte[] b) {
	    short i = (short) (((b[1] & 0xff) << 8) | b[0] & 0xff);
	    return i;
	}
	//将字节数组转换为16进制字符串
	public static String BinaryToHexString(byte[] bytes) {
	    String hexStr = "0123456789ABCDEF";
	    String result = "";
	    String hex = "";
	    for (byte b : bytes) {
	        hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
	        hex += String.valueOf(hexStr.charAt(b & 0x0F));
	        result += hex + " ";
	    }
	    return result;
	}

	/**
	 * 16进制转10进制
	 */
	public static int covert16To10(String content){
        int number=0;
        String [] HighLetter = {"A","B","C","D","E","F"};
        Map<String,Integer> map = new HashMap<>();
        for(int i = 0;i <= 9;i++){
            map.put(i+"",i);
        }
        for(int j= 10;j<HighLetter.length+10;j++){
            map.put(HighLetter[j-10],j);
        }
        String[]str = new String[content.length()];
        for(int i = 0; i < str.length; i++){
            str[i] = content.substring(i,i+1);
        }
        for(int i = 0; i < str.length; i++){
            number += map.get(str[i])*Math.pow(16,str.length-1-i);
        }
        return number;
    }
	public static String covert10To16(int content){
		return Integer.toHexString(content);
	}
	/**
	 * 累加和校验实现方式16进制  CheckSum
	 */
	public static String makeChecksum(String data) {
		if (data == null || data.equals("")) {
			return "";
		}
		int total = 0;
		int len = data.length();
		int num = 0;
		while (num < len) {
			String s = data.substring(num, num + 2);
			//System.out.println(s);
			total += Integer.parseInt(s, 16);
			num = num + 2;
		}
		/**
		* 用256求余最大是255，即16进制的FF
		*/
		//System.out.println(total);
		int mod = total%256;
		//System.out.println(mod);
		//Math.floorMod(total,256);
		String hex = Integer.toHexString(mod);
		len = hex.length();
		// 如果不够校验位的长度，补0,这里用的是两位校验
		if (len < 2) {
			hex = "0" + hex;
		}
		return hex.toUpperCase();
	}

    //---------------------------------------------------------------------------
    /**
     * 计算CRC16/Modbus校验码   ,高位在前低位在后        实际应用   
     *
     * @param str 十六进制字符串
     * @return
     */
    public static String getCRC(String str) {
        byte[] bytes = toBytes(str);
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
 
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        String crc = Integer.toHexString(CRC);
        if (crc.length() == 2) {
            crc = "00" + crc;
        } else if (crc.length() == 3) {
            crc = "0" + crc;
        }
        //crc = crc.substring(2, 4) + crc.substring(0, 2);
        return crc.toUpperCase();
    }
    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        byte[] bytes = new BigInteger(str, 16).toByteArray();
        return bytes;
    }
    /**
     * 接收到的字节数组转换16进制字符串
     */
    public static String byteToStr(byte[] b, int size) {
        String ret = "";
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }
    /**
     * 充电子板协议-计算校验值
     */
    public static String getCRC_16CheckSum(String hexdata) {
        if (hexdata == null || hexdata.equals("")) {
            return "00";
        }
        hexdata = hexdata.replaceAll(" ", "");
        int total = 0;
        int len = hexdata.length();
        if (len % 2 != 0) {
            return "00";
        }
        int num = 0;
        while (num < len) {
            String s = hexdata.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        String data = hexInt(total);
        return data.substring(data.length() - 2, data.length());
    }
 
    public static String hexInt(int total) {
        int a = total / 256;
        int b = total % 256;
        if (a > 255) {
            return hexInt(a) + format(b);
        }
        return format(a) + format(b);
    }
 
    public static String format(int hex) {
        String hexa = Integer.toHexString(hex);
        int len = hexa.length();
        if (len < 2) {
            hexa = "0" + hexa;
        }
        return hexa;
    }
    
    
    //-----------------
    /**
    * @Title:bytes2HexString
    * @Description:字节数组转16进制字符串
    * @param b
    * 字节数组
    * @return 16进制字符串
    * @throws
    */
    public static String bytes2HexString(byte[] b) {
	    StringBuffer result = new StringBuffer();
	    for (int i = 0; i < b.length; i++) {
	    	result.append(String.format("%02X",b[i]));
	    }
	    return result.toString();
    }

    /**
    * @Title:hexString2Bytes
    * @Description:16进制字符串转字节数组
    * @param src
    * 16进制字符串
    * @return 字节数组
    * @throws
    */
    public static byte[] hexString2Bytes(String src) {
	    int l = src.length() / 2;
	    byte[] ret = new byte[l];
	    for (int i = 0; i < l; i++) {
	    ret[i] = Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
	    }
	    return ret;
    }

    /**
    * @Title:string2HexUTF8
    * @Description:字符UTF8串转16进制字符串
    * @param strPart
    * 字符串
    * @return 16进制字符串
    * @throws
    */
    public static String string2HexUTF8(String strPart) {
    	return string2HexString(strPart,"UTF-8");
    }

    /**
    * @Title:string2HexUnicode
    * @Description:字符Unicode串转16进制字符串
    * @param strPart
    * 字符串
    * @return 16进制字符串
    * @throws
    */
    public static String string2HexUnicode(String strPart) {
    	return string2HexString(strPart,"Unicode");
    }
    /**
    * @Title:string2HexGBK
    * @Description:字符GBK串转16进制字符串
    * @param strPart
    * 字符串
    * @return 16进制字符串
    * @throws
    */
    public static String string2HexGBK(String strPart) {
    	return string2HexString(strPart,"GBK");
    }
    /**
    * @Title:string2HexString
    * @Description:字符串转16进制字符串
    * @param strPart 字符串
    * @param tochartype hex目标编码
    * @return 16进制字符串
    * @throws
    */
    public static String string2HexString(String strPart,String tochartype) {
	    try{
	    	return bytes2HexString(strPart.getBytes(tochartype));
	    }catch (Exception e){
	    	return "";
	    }
    }

    /**
    * @Title:hexUTF82String
    * @Description:16进制UTF-8字符串转字符串
    * @param src
    * 16进制字符串
    * @return 字节数组
    * @throws
    */
    public static String hexUTF82String(String src) {
    	return hexString2String(src,"UTF-8","UTF-8");
    }

    /**
    * @Title:hexGBK2String
    * @Description:16进制GBK字符串转字符串
    * @param src
    * 16进制字符串
    * @return 字节数组
    * @throws
    */
    public static String hexGBK2String(String src) {
    	return hexString2String(src,"GBK","UTF-8");
    }

    /**
    * @Title:hexUnicode2String
    * @Description:16进制Unicode字符串转字符串
    * @param src
    * 16进制字符串
    * @return 字节数组
    * @throws
    */
    public static String hexUnicode2String(String src) {
    	return hexString2String(src,"Unicode","UTF-8");
    }

    /**
    * @Title:hexString2String
    * @Description:16进制字符串转字符串
    * @param src
    * 16进制字符串
    * @return 字节数组
    * @throws
    */
    public static String hexString2String(String src,String oldchartype, String chartype) {
	    byte[] bts=hexString2Bytes(src);
	    try{if(oldchartype.equals(chartype))
	    	return new String(bts,oldchartype);
	    else
	    	return new String(new String(bts,oldchartype).getBytes(),chartype);
	    }
	    catch (Exception e){
	    	return"";
	    }	
    }
  
	public static void main(String[] args) {

		/*
	    System.out.println("======ASCII码转换为16进制======");
	    String str = "313030303639";
	    System.out.println("字符串: " + str);
	    System.out.println(HexConvert.hexStringToBytes(str));
	    */
	    /*
	    String hex ="FFFFFF";;
	    System.out.println("====转换为16进制=====" + hex);
	 
	    System.out.println("======16进制转换为ASCII======");
	    System.out.println("Hex : " + hex);
	    System.out.println("ASCII : " + HexConvert.convertHexToString(hex));
	 
	    byte[] bytes = HexConvert.hexStringToBytes( hex );
	 
	    System.out.println(HexConvert.BinaryToHexString( bytes ));
	    */
	}
}
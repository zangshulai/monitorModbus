package com.hotent.core.sms.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Arrays;

import com.hotent.core.util.HexConvert;

import isunlandSrc.intelligentSecurity.standard.service.SecurityTasksPlan.RTasksPlanMonitoringService;
//这个类是用来解析数据的
public class SocketData implements Runnable{
    private Socket socket;
    private InputStream in;
    public SocketData(Socket clientSocket) {
        try {
            // 得到socket连接
            socket = clientSocket;
            // 得到客户端发来的消息
            in= socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*
         首先 GPRS模块 会向服务器发送注册心跳（A8 81） 服务器必须应答  （A8 81）  服务器不应答 超时 就会链接备用服务器   备用服务器是厂家的 
         其次 设备数据会通过 GPRS模块的数据帧（A8 81） 上传自己的数据（3A）
         设备自己的上传逻辑是 先发送心跳帧（25 27）  其中包含上传参数 周期等参数  服务器必须应答 设备根据服务器的应答 自行上报（25 26）参数帧     或者浓度帧（25 25）
	*/
    
    public void run() {
        try {
        	OutputStream out = null;
        	
        	//准备空包
            byte[] bufferData = new byte[180];
            int len = -1;
            while ((len = in.read(bufferData)) != -1) {
            	String hexString = printHexString(bufferData);
            	//RTasksPlanMonitoringService.initMonitorData(hexString,"000000");
            	
            	//针对字节处理
            	//需要处理应答的方法
            	String vosMod[] = hexString.split(" ", -1);
        		if (vosMod!=null && vosMod.length>0 && ("A8".equals(vosMod[0]) && "81".equals(vosMod[1]))){
        			String dataType=vosMod[20]+vosMod[21]+vosMod[22];
        			if ("3A0125".equals(dataType)==false){
        				//GPRS模块 主动向服务器发送注册心跳
            			String vos[]=Arrays.copyOfRange(vosMod, 0, 17);
            			String strmakeChecksum=Arrays.toString(vos).replace("[", "").replace("]", "").replace(",", "").replace(" ", "").trim()+"03"+vosMod[18];
            			String strBack=strmakeChecksum+HexConvert.makeChecksum(strmakeChecksum);
            			
            			strBack=HexConvert.string2HexUTF8(strBack);
            			strBack=HexConvert.hexUTF82String(strBack);
            			
            			//应答数据
            			//RTasksPlanMonitoringService.initMonitorData(strBack,"100000");
            			
            			//服务器应答GPRS模块注册心跳
            			out=socket.getOutputStream();
            			out.write(hexStringToByteArray(strBack));
            			
            			/*
            			OutputStream os=socket.getOutputStream();
            			PrintWriter pw=new PrintWriter(os);
            			String info="[3G*4700546714*0005*VERNO]";
            			pw.write(info);
            			pw.flush();
            			*/
        			}else if ("27".equals(vosMod[23])){
        				//3.1 仪器上传心跳帧报文
        				String vos[]=Arrays.copyOfRange(vosMod, 20, 45);
        				
        				String strBack="3A";
        				String strCrcCode=vos[1]+vos[2]+vos[3];
        				//长度    数据长度指数据内容所有字节数
        				strCrcCode+="15";
        				strCrcCode+=vos[5]+vos[6]+vos[7]+vos[8]+vos[9]+vos[10];
        				
        				//上报类型
        				strCrcCode+="80";
        				//心跳周期  5秒心跳   联络心跳
        				strCrcCode+="08";  
        				//数据上报周期    10秒上传一种气体
        				strCrcCode+="0A";  
        				//是否主动上报
        				strCrcCode+="01";
        				//数据上报类型
        				strCrcCode+="01";
        				//保留字节   固定值
        				String vos2[]=Arrays.copyOfRange(vosMod, 32, 42);
        				String saveData=Arrays.toString(vos2).replace("[", "").replace("]", "").replace(",", "").replace(" ", "").trim();
        				strCrcCode+=saveData;
        				strBack+=strCrcCode;
        				String strCrc=HexConvert.getCRC(strCrcCode);
        				strBack+=strCrc;
        				strBack+="3A";
        				
        				strBack=HexConvert.string2HexUTF8(strBack);
            			strBack=HexConvert.hexUTF82String(strBack);
            			
            			//应答数据
            			//RTasksPlanMonitoringService.initMonitorData(strBack,"100000");
            			
            			//输出获取到所有字节  16进制
            			out=socket.getOutputStream();
            			out.write(hexStringToByteArray(strBack));
        			}else if ("26".equals(vosMod[23])){
        				//参数数据时（25 26），是一个通道一个条数据，浓度数据（25 25）是多个通道一条数据
        				String vos[]=Arrays.copyOfRange(vosMod, 20, 65);
        				String strBack=Arrays.toString(vos).replace("[", "").replace("]", "").replace(",", "").trim();
        				RTasksPlanMonitoringService.initMonitorData(strBack);
        			}
        		} // end if begin  A8 81 所有的数据都带有这个
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    // 字节数组转字符串
    private String printHexString(byte[] b) {
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sbf.append(hex.toUpperCase() + " ");
        }
        return sbf.toString().trim();
    }
}
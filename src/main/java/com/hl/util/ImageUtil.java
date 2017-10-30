package com.hl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.org.apache.bcel.internal.generic.I2F;

import sun.misc.BASE64Encoder;

import org.apache.mahout.common.IntPairWritable.FirstGroupingComparator;
import org.springframework.util.Base64Utils;

public class ImageUtil {
	
	//分目录，输入上一级的文件夹，文件名，创建文件夹，返回url_suffix
	public static String getUrlSuffix(String imagePath, String dir,String uuidName){
		String hash = Integer.toHexString(uuidName.hashCode());
		for(char c : hash.toCharArray()){
			dir += ("/" + c);
		}
		//创建本地文件夹
		new File(imagePath + dir).mkdirs();
		//返回url_suffix
		return dir + "/" + uuidName;
	}
	
	public static boolean generateImage(String imgStr, String folder_path, String file_name) {
		File folder = new File(folder_path);
		if(folder.exists() == false){
			folder.mkdirs();
		}
		if (imgStr == null) {
			System.out.println("返回的图片数据为空");
			return false;
		}
		try {
			String dataPrix;
			String data;
			String[]d = imgStr.split("base64,");
	        if(d != null && d.length == 2){
	            dataPrix = d[0];
	            data = d[1];
	        }else{
	            throw new Exception("上传失败，数据不合法");
	        }
			byte[] bs = Base64Utils.decodeFromString(data);
			FileOutputStream fos = new FileOutputStream(new File(folder_path, file_name));
			fos.write(bs);
			fos.flush();
			fos.close();
			System.out.println("返回的图片成功写入本地");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("存储base64图片失败");
			return false;
		}
		return true;
	}

	public static boolean generateImage(String imgStr, String folder_path) {
		if (imgStr == null) {
			System.out.println("返回的图片数据为空");
			return false;
		}
		try {
			String dataPrix;
			String data;
			String[]d = imgStr.split("base64,");
	        if(d != null && d.length == 2){
	            dataPrix = d[0];
	            data = d[1];
	        }else{
	            throw new Exception("上传失败，数据不合法");
	        }
			byte[] bs = Base64Utils.decodeFromString(data);
			FileOutputStream fos = new FileOutputStream(new File(folder_path));
			fos.write(bs);
			fos.flush();
			fos.close();
			System.out.println("返回的图片成功写入本地");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("存储base64图片失败");
			return false;
		}
		return true;
	}
	
	public static String GetImageStr(String imgFile) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		//imgFile = "E:/study_and_work/invoice/repo/SIRS4.5/SIRS4.0/x64/Debug/1.bmp";// 待处理的图片
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 对字节数组Base64编码
		return Base64Utils.encodeToString(data);// 返回Base64编码过的字节数组字符串
	}

    public static String bmpTojpg(String origin_file) {
    	//将bmp文件转换为jpg文件，放在同一路径下
        try {
        	//参数是jpg的路径，要得到对应的bmp路径
        	int last_index = origin_file.lastIndexOf("/");
        	String file_name_origin = origin_file.substring(last_index + 1, origin_file.length());
        	String dir = origin_file.substring(0, last_index + 1);//包括斜杠
        	
        	int last_dot = file_name_origin.lastIndexOf(".");
        	String temp = file_name_origin.substring(0,last_dot);
        	String file_name = temp + ".jpg";
        	
        	String dstFile = dir + file_name;
        	
            FileInputStream in = new FileInputStream(origin_file);
            Image TheImage = read(in);
            int wideth = TheImage.getWidth(null);
            int height = TheImage.getHeight(null);
            BufferedImage tag = new BufferedImage(wideth, height,BufferedImage.TYPE_INT_RGB);
            tag.getGraphics().drawImage(TheImage, 0, 0, wideth, height, null);
            FileOutputStream out = new FileOutputStream(dstFile);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(tag);
            out.close();
            return file_name;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("bmp转jpg失败");
        }
        return null;
    }
  
    public static String suffixToJpg(String url){
    	//将一个url的后缀变为jpg
		int last_index_dot = url.lastIndexOf(".");
		String pre = url.substring(0,last_index_dot);
		String jpg_url = pre + ".jpg";
		System.out.println(jpg_url);
		return jpg_url;	
    }
    
    public static String suffixToBmp(String url){
    	//将一个url的后缀变为jpg
		int last_index_dot = url.lastIndexOf(".");
		String pre = url.substring(0,last_index_dot);
		return pre + ".bmp";	
    }
    
    public static String getUrlSuffix(String url){
    	int start = 0;
    	int count = 0;
    	for(int i = 0; i < url.length(); i++){
    		if(url.charAt(i) == '/') count ++;
    		if(count == 4){
    			start = i;
    			break;
    		}
    	}
    	return url.substring(start, url.length());
    }
    
    public static String getFileName(String url){
    	int last_index = url.lastIndexOf("/");
    	String name = url.substring(last_index + 1, url.length());
    	return name;
    }
    
    public static int getImageSize(String local_path){
    	//返回图片大小
    	Integer image_size = 0;
		File file = new File(local_path);
		if(file.exists()){
			Double len = new Long(file.length()).doubleValue();
			image_size = new Double(len/1024).intValue();
			System.out.println("图片大小为"+ image_size+"KB");
		}
		return image_size;
    }
    
    public static void deleteAllModelImage(String root, String url_suffix){
    	//删除所有本地文件
		
    	if(url_suffix.contains("handle")){
    		File file1 = new File(root + url_suffix);
    		if (file1.exists()) {
    			file1.delete();
    		}
    		
    		String original_url = url_suffix.replace("handle", "original");
    		File file2 = new File(root + original_url);
    		if (file2.exists()) {
				file2.delete();
			}
    		//删除bmp
    		File file3 = new File(root + ImageUtil.suffixToBmp(original_url));
    		if (file3.exists()) {
				file3.delete();
			}
    	}
    	else if (url_suffix.contains("original")){
    		File file1 = new File(root + url_suffix);
    		if (file1.exists()) {
    			file1.delete();
    		}
    		//删除bmp
    		File file2 = new File(root + ImageUtil.suffixToBmp(url_suffix));
    		if (file2.exists()) {
				file2.delete();
			}
    		
    		String original_url = url_suffix.replace("original", "handle");
    		File file3 = new File(root + original_url);
    		if (file3.exists()) {
				file3.delete();
			}
		}
    	
    }
    
    private static int constructInt(byte[] in, int offset) {
        int ret = ((int) in[offset + 3] & 0xff);
        ret = (ret << 8) | ((int) in[offset + 2] & 0xff);
        ret = (ret << 8) | ((int) in[offset + 1] & 0xff);
        ret = (ret << 8) | ((int) in[offset + 0] & 0xff);
        return (ret);
    }
 
    
    private static int constructInt3(byte[] in, int offset) {
        int ret = 0xff;
        ret = (ret << 8) | ((int) in[offset + 2] & 0xff);
        ret = (ret << 8) | ((int) in[offset + 1] & 0xff);
        ret = (ret << 8) | ((int) in[offset + 0] & 0xff);
        return (ret);
    }
 
    private static long constructLong(byte[] in, int offset) {
        long ret = ((long) in[offset + 7] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 6] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 5] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 4] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 3] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 2] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 1] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 0] & 0xff);
        return (ret);
    }
 
    private static double constructDouble(byte[] in, int offset) {
        long ret = constructLong(in, offset);
        return (Double.longBitsToDouble(ret));
    }
 
    private static short constructShort(byte[] in, int offset) {
        short ret = (short) ((short) in[offset + 1] & 0xff);
        ret = (short) ((ret << 8) | (short) ((short) in[offset + 0] & 0xff));
        return (ret);
    }
 
    static class BitmapHeader {
        public int iSize, ibiSize, iWidth, iHeight, iPlanes, iBitcount,
                iCompression, iSizeimage, iXpm, iYpm, iClrused, iClrimp;
 
        // 读取bmp文件头信息
        public void read(FileInputStream fs) throws IOException {
            final int bflen = 14;
            byte bf[] = new byte[bflen];
            fs.read(bf, 0, bflen);
            final int bilen = 40;
            byte bi[] = new byte[bilen];
            fs.read(bi, 0, bilen);
            iSize = constructInt(bf, 2);
            ibiSize = constructInt(bi, 2);
            iWidth = constructInt(bi, 4);
            iHeight = constructInt(bi, 8);
            iPlanes = constructShort(bi, 12);
            iBitcount = constructShort(bi, 14);
            iCompression = constructInt(bi, 16);
            iSizeimage = constructInt(bi, 20);
            iXpm = constructInt(bi, 24);
            iYpm = constructInt(bi, 28);
            iClrused = constructInt(bi, 32);
            iClrimp = constructInt(bi, 36);
        }
    }
 
    public static Image read(FileInputStream fs) {
        try {
            BitmapHeader bh = new BitmapHeader();
            bh.read(fs);
            if (bh.iBitcount == 24) {
                return (readImage24(fs, bh));
            }
            if (bh.iBitcount == 32) {
                return (readImage32(fs, bh));
            }
            fs.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return (null);
    }
 
    // 24位
    protected static Image readImage24(FileInputStream fs, BitmapHeader bh)
            throws IOException {
        Image image;
        if (bh.iSizeimage == 0) {
            bh.iSizeimage = ((((bh.iWidth * bh.iBitcount) + 31) & ~31) >> 3);
            bh.iSizeimage *= bh.iHeight;
        }
        int npad = (bh.iSizeimage / bh.iHeight) - bh.iWidth * 3;
        int ndata[] = new int[bh.iHeight * bh.iWidth];
        byte brgb[] = new byte[(bh.iWidth + npad) * 3 * bh.iHeight];
        fs.read(brgb, 0, (bh.iWidth + npad) * 3 * bh.iHeight);
        int nindex = 0;
        for (int j = 0; j < bh.iHeight; j++) {
            for (int i = 0; i < bh.iWidth; i++) {
                ndata[bh.iWidth * (bh.iHeight - j - 1) + i] = constructInt3(
                        brgb, nindex);
                nindex += 3;
            }
            nindex += npad;
        }
        image = Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(bh.iWidth, bh.iHeight, ndata, 0,
                        bh.iWidth));
        fs.close();
        return (image);
    }
 
    // 32位
    protected static Image readImage32(FileInputStream fs, BitmapHeader bh)
            throws IOException {
        Image image;
        int ndata[] = new int[bh.iHeight * bh.iWidth];
        byte brgb[] = new byte[bh.iWidth * 4 * bh.iHeight];
        fs.read(brgb, 0, bh.iWidth * 4 * bh.iHeight);
        int nindex = 0;
        for (int j = 0; j < bh.iHeight; j++) {
            for (int i = 0; i < bh.iWidth; i++) {
                ndata[bh.iWidth * (bh.iHeight - j - 1) + i] = constructInt3(
                        brgb, nindex);
                nindex += 4;
            }
        }
        image = Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(bh.iWidth, bh.iHeight, ndata, 0,
                        bh.iWidth));
        fs.close();
        return (image);
    }

	//将一个字符串写入一个本地文件，测试用
	public static void writeToLocal(String str,String path){
		try {
			if(str == null){
				System.out.println("字符串为空");
			}
			File file = new java.io.File(path);
			if(!file.exists()){
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(str);
			System.out.println("写入本地文件测试成功");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("写入失败");
		}
	}

}

package com.huiting.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class NarrowImage {

	
	/**
	 * @param im
	 *            原始图像
	 * @param resizeTimes
	 *            倍数,比如0.5就是缩小一半,0.98等等double类型
	 * @return 返回处理后的图像
	 */
	public static BufferedImage zoomImage(String src,Float size) {
		BufferedImage result = null;
		try {
			File srcfile = new File(src);
			if (!srcfile.exists()) {
				System.out.println("文件不存在");
				
			}
			BufferedImage im = ImageIO.read(srcfile);
			/* 原始图像的宽度和高度 */
			int width = im.getWidth();
			int height = im.getHeight();
			//压缩计算
			float resizeTimes = size;//0.1f;  /*这个参数是要转化成的倍数,如果是1就是转化成1倍*/
			
			/* 调整后的图片的宽度和高度 */
			int toWidth = (int) (width * resizeTimes);
			int toHeight = (int) (height * resizeTimes);

			/* 新生成结果图片 */
			result = new BufferedImage(toWidth, toHeight,
					BufferedImage.TYPE_INT_RGB);

			result.getGraphics().drawImage(
					im.getScaledInstance(toWidth, toHeight,
							java.awt.Image.SCALE_SMOOTH), 0, 0, null);
			

		} catch (Exception e) {
			System.out.println("创建缩略图发生异常" + e.getMessage());
		}
		
		return result;
	}
	public static BufferedImage zoomImage(File srcfile,Float size) {
		BufferedImage result = null;
		try {
			if (srcfile==null||!srcfile.exists()) {
				System.out.println("文件不存在");
				
			}
			BufferedImage im = ImageIO.read(srcfile);
			/* 原始图像的宽度和高度 */
			int width = im.getWidth();
			int height = im.getHeight();
			//压缩计算
			float resizeTimes = size;//0.1f;  /*这个参数是要转化成的倍数,如果是1就是转化成1倍*/
			
			/* 调整后的图片的宽度和高度 */
			int toWidth = (int) (width * resizeTimes);
			int toHeight = (int) (height * resizeTimes);

			/* 新生成结果图片 */
			result = new BufferedImage(toWidth, toHeight,
					BufferedImage.TYPE_INT_RGB);

			result.getGraphics().drawImage(
					im.getScaledInstance(toWidth, toHeight,
							java.awt.Image.SCALE_SMOOTH), 0, 0, null);
			

		} catch (Exception e) {
			System.out.println("创建缩略图发生异常" + e.getMessage());
		}
		
		return result;
	}
	
	 public static boolean writeHighQuality(BufferedImage im, String fileFullPath) {
	        try {
	            /*输出到文件流*/
	            FileOutputStream newimage = new FileOutputStream(fileFullPath);
	            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimage);
	            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(im);
	            /* 压缩质量 */
	            jep.setQuality(1f, true);
	            encoder.encode(im, jep);
	           /*近JPEG编码*/
	            newimage.close();
	            return true;
	        } catch (Exception e) {
	            return false;
	        }
	    }
	 
	 /**
		 * 直接指定压缩后的宽高：
		 * (先保存原文件，再压缩、上传)
		 * 壹拍项目中用于二维码压缩
		 * @param oldFile 要进行压缩的文件全路径
		 * @param width 压缩后的宽度
		 * @param height 压缩后的高度
		 * @param quality 压缩质量
		 * @param smallIcon 文件名的小小后缀(注意，非文件后缀名称),入压缩文件名是yasuo.jpg,则压缩后文件名是yasuo(+smallIcon).jpg
		 * @return 返回压缩后的文件的全路径
		 */
		public static String zipImageFile(String oldFile, int width, int height,
				float quality, String smallIcon) {
			if (oldFile == null) {
				return null;
			}
			String newImage = null;
			try {
				/**对服务器上的临时文件进行处理 */
				Image srcFile = ImageIO.read(new File(oldFile));
				/** 宽,高设定 */
				BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);
				String filePrex = oldFile.substring(0, oldFile.indexOf('.'));
				/** 压缩后的文件名 */
				newImage = filePrex + smallIcon	+ oldFile.substring(filePrex.length());
				/** 压缩之后临时存放位置 */
				FileOutputStream out = new FileOutputStream(newImage);
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
				/** 压缩质量 */
				jep.setQuality(quality, true);
				encoder.encode(tag, jep);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return newImage;
		}
		
		/**
		 * 等比例压缩算法： 
		 * 算法思想：根据压缩基数和压缩比来压缩原图，生产一张图片效果最接近原图的缩略图
		 * @param srcURL 原图地址
		 * @param deskURL 缩略图地址
		 * @param comBase 压缩基数
		 * @param scale 压缩限制(宽/高)比例  一般用1：
		 * 当scale>=1,缩略图height=comBase,width按原图宽高比例;若scale<1,缩略图width=comBase,height按原图宽高比例
		 * @throws Exception
		 * @author shenbin
		 * @createTime 2014-12-16
		 * @lastModifyTime 2014-12-16
		 */
		public static void saveMinPhoto(String srcURL, String deskURL, double comBase,
				double scale) throws Exception {
			File srcFile = new java.io.File(srcURL);
			Image src = ImageIO.read(srcFile);
			int srcHeight = src.getHeight(null);
			int srcWidth = src.getWidth(null);
			int deskHeight = 0;// 缩略图高
			int deskWidth = 0;// 缩略图宽
			double srcScale = (double) srcHeight / srcWidth;
			/**缩略图宽高算法*/
			if ((double) srcHeight > comBase || (double) srcWidth > comBase) {
				if (srcScale >= scale || 1 / srcScale > scale) {
					if (srcScale >= scale) {
						deskHeight = (int) comBase;
						deskWidth = srcWidth * deskHeight / srcHeight;
					} else {
						deskWidth = (int) comBase;
						deskHeight = srcHeight * deskWidth / srcWidth;
					}
				} else {
					if ((double) srcHeight > comBase) {
						deskHeight = (int) comBase;
						deskWidth = srcWidth * deskHeight / srcHeight;
					} else {
						deskWidth = (int) comBase;
						deskHeight = srcHeight * deskWidth / srcWidth;
					}
				}
			} else {
				deskHeight = srcHeight;
				deskWidth = srcWidth;
			}
			BufferedImage tag = new BufferedImage(deskWidth, deskHeight, BufferedImage.TYPE_3BYTE_BGR);
			tag.getGraphics().drawImage(src, 0, 0, deskWidth, deskHeight, null); //绘制缩小后的图
			FileOutputStream deskImage = new FileOutputStream(deskURL); //输出到文件流
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(deskImage);
			encoder.encode(tag); //近JPEG编码
			deskImage.close();
		}
	 
	 /**
		 * 保存文件到服务器临时路径(用于文件上传)
		 * @param fileName
		 * @param is
		 * @return 文件全路径
		 */
		public static String writeFile(String fileName, InputStream is) {
			if (fileName == null || fileName.trim().length() == 0) {
				return null;
			}
			try {
				/** 首先保存到临时文件 */
				FileOutputStream fos = new FileOutputStream(fileName);
				byte[] readBytes = new byte[512];// 缓冲大小
				int readed = 0;
				while ((readed = is.read(readBytes)) > 0) {
					fos.write(readBytes, 0, readed);
				}
				fos.close();
				is.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return fileName;
		}
	 
	 public static void main(String[] args) throws Exception {
		 
		 String inputFoler = "d:\\15227380.jpg" ; 
         /*这儿填写你存放要缩小图片的文件夹全地址*/
        String outputFolder = "d:\\aa.jpg";  
        /*这儿填写你转化后的图片存放的文件夹*/
       NarrowImage narrowImage = new NarrowImage();
		narrowImage.writeHighQuality(narrowImage.zoomImage(inputFoler,0.22f), outputFolder);
       
        
        BufferedImage im = ImageIO.read(new File(inputFoler));
        System.out.println(im.getWidth()+"--"+im.getHeight());
         zipImageFile("d:\\15227380.jpg", 680, 480, 1f, "x2");
		 saveMinPhoto("d:\\15227380.jpg", "d:/11.jpg", 300, 0.9d);
		 
		 
		
	}

}

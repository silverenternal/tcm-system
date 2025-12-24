package com.tcm.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 图片处理工具类
 * 实现图片格式转换（统一为PNG）和尺寸调整（裁剪到512*512）
 */
public class ImageProcessingUtil {

    /**
     * 处理上传的图片：转换格式为PNG并调整尺寸
     * 如果图片尺寸超过512*512，则从中心向边缘裁剪到512*512
     */
    public static File processImage(MultipartFile originalFile) throws IOException {
        // 将MultipartFile转换为BufferedImage
        BufferedImage originalImage = ImageIO.read(originalFile.getInputStream());

        // 获取原始图片尺寸
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 目标尺寸
        int targetSize = 512;

        BufferedImage processedImage;

        if (originalWidth > targetSize || originalHeight > targetSize) {
            // 如果原始图片尺寸超过目标尺寸，进行中心裁剪
            processedImage = cropCenterToSize(originalImage, targetSize, targetSize);
        } else {
            // 如果原始图片尺寸不超过目标尺寸，直接使用原图
            processedImage = originalImage;
        }

        // 创建临时文件保存处理后的图片
        File tempFile = File.createTempFile("processed_image_", ".png");
        ImageIO.write(processedImage, "png", tempFile);

        return tempFile;
    }

    /**
     * 从图片中心裁剪到指定尺寸
     */
    private static BufferedImage cropCenterToSize(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 计算从中心裁剪的起始坐标
        int startX = Math.max(0, (originalWidth - targetWidth) / 2);
        int startY = Math.max(0, (originalHeight - targetHeight) / 2);

        // 确保裁剪区域不超出原图边界
        int cropWidth = Math.min(targetWidth, originalWidth - startX);
        int cropHeight = Math.min(targetHeight, originalHeight - startY);

        // 如果原图尺寸小于目标尺寸，则创建新图像并居中放置原图
        if (originalWidth < targetWidth || originalHeight < targetHeight) {
            BufferedImage newImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = newImage.createGraphics();
            // 设置背景为白色
            g2d.setPaint(Color.WHITE);
            g2d.fillRect(0, 0, targetWidth, targetHeight);
            
            // 计算居中位置
            int centeredX = (targetWidth - originalWidth) / 2;
            int centeredY = (targetHeight - originalHeight) / 2;
            
            // 绘制原图到新图像的中心位置
            g2d.drawImage(originalImage, centeredX, centeredY, null);
            g2d.dispose();
            return newImage;
        } else {
            // 如果原图尺寸大于等于目标尺寸，进行中心裁剪
            BufferedImage croppedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

            // 使用Graphics2D绘制裁剪后的图像
            Graphics2D g2d = croppedImage.createGraphics();

            // 绘制裁剪区域
            g2d.drawImage(originalImage.getSubimage(startX, startY, cropWidth, cropHeight), 0, 0, null);

            g2d.dispose();

            return croppedImage;
        }
    }

    /**
     * 调整图片尺寸（非裁剪方式，而是缩放）
     * 注：根据需求，我们使用裁剪方式，但保留此方法以备将来使用
     */
    @SuppressWarnings("unused")
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        return resizedImage;
    }

    /**
     * 从中心裁剪正方形图片
     */
    public static BufferedImage cropToSquareFromCenter(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int size = Math.min(width, height);

        int startX = (width - size) / 2;
        int startY = (height - size) / 2;

        return originalImage.getSubimage(startX, startY, size, size);
    }
}
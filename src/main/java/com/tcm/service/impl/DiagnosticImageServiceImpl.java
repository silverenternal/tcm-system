package com.tcm.service.impl;

import com.tcm.dto.DiagnosticImageResponse;
import com.tcm.model.DiagnosticImage;
import com.tcm.model.Visit;
import com.tcm.repository.DiagnosticImageRepository;
import com.tcm.repository.VisitRepository;
import com.tcm.service.DiagnosticImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DiagnosticImageServiceImpl implements DiagnosticImageService {

    @Autowired
    private DiagnosticImageRepository diagnosticImageRepository;

    @Autowired
    private VisitRepository visitRepository;

    // 图片存储路径
    private static final String UPLOAD_DIR = "uploads/diagnostic_images/";

    @Override
    public DiagnosticImageResponse uploadDiagnosticImage(Long visitId, MultipartFile file, String imageType, String description) throws Exception {
        // 验证上传的文件
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只允许上传图片文件");
        }

        // 获取对应的就诊记录
        Optional<Visit> visitOptional = visitRepository.findById(visitId);
        if (!visitOptional.isPresent()) {
            throw new IllegalArgumentException("找不到对应的就诊记录");
        }
        Visit visit = visitOptional.get();

        // 处理图片 - 转换格式和调整尺寸
        File processedFile = processImage(file);

        // 生成唯一的文件名
        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String uniqueFileName = UUID.randomUUID().toString() + ".png"; // 统一转换为PNG格式
        String filePath = UPLOAD_DIR + uniqueFileName;

        // 创建上传目录（如果不存在）
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 保存处理后的图片到文件系统
        File destinationFile = new File(filePath);
        Files.copy(processedFile.toPath(), destinationFile.toPath());

        // 创建诊断图片实体
        DiagnosticImage diagnosticImage = new DiagnosticImage();
        diagnosticImage.setVisit(visit);
        diagnosticImage.setImageType(imageType);
        diagnosticImage.setImagePath(filePath);
        diagnosticImage.setImageName(originalFileName);
        diagnosticImage.setImageSize(destinationFile.length());
        diagnosticImage.setOriginalFormat(fileExtension);
        diagnosticImage.setProcessedFormat("png");
        diagnosticImage.setDescription(description);

        // 保存到数据库
        DiagnosticImage savedImage = diagnosticImageRepository.save(diagnosticImage);
        
        // 构建响应对象，不包含关联实体以避免序列化问题
        return new DiagnosticImageResponse(
                savedImage.getId(),
                visitId,  // 使用传入的ID而不是实体中的关联对象
                savedImage.getImageType(),
                savedImage.getImagePath(),
                savedImage.getImageName(),
                savedImage.getImageSize(),
                savedImage.getWidth(),
                savedImage.getHeight(),
                savedImage.getOriginalFormat(),
                savedImage.getProcessedFormat(),
                savedImage.getDescription(),
                savedImage.getCreatedAt(),
                savedImage.getUpdatedAt()
        );
    }

    @Override
    public DiagnosticImageResponse getDiagnosticImageById(Long id) {
        Optional<DiagnosticImage> imageOpt = diagnosticImageRepository.findById(id);
        if (imageOpt.isPresent()) {
            DiagnosticImage image = imageOpt.get();
            return new DiagnosticImageResponse(
                    image.getId(),
                    image.getVisit() != null ? image.getVisit().getId() : null,
                    image.getImageType(),
                    image.getImagePath(),
                    image.getImageName(),
                    image.getImageSize(),
                    image.getWidth(),
                    image.getHeight(),
                    image.getOriginalFormat(),
                    image.getProcessedFormat(),
                    image.getDescription(),
                    image.getCreatedAt(),
                    image.getUpdatedAt()
            );
        }
        return null;
    }

    @Override
    public List<DiagnosticImageResponse> getDiagnosticImagesByVisitId(Long visitId) {
        List<DiagnosticImage> images = diagnosticImageRepository.findByVisitId(visitId);
        return images.stream()
                .map(image -> new DiagnosticImageResponse(
                        image.getId(),
                        visitId,  // 使用参数ID而不是实体关联
                        image.getImageType(),
                        image.getImagePath(),
                        image.getImageName(),
                        image.getImageSize(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getOriginalFormat(),
                        image.getProcessedFormat(),
                        image.getDescription(),
                        image.getCreatedAt(),
                        image.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DiagnosticImageResponse> getDiagnosticImagesByImageType(String imageType) {
        List<DiagnosticImage> images = diagnosticImageRepository.findByImageType(imageType);
        return images.stream()
                .map(image -> new DiagnosticImageResponse(
                        image.getId(),
                        image.getVisit() != null ? image.getVisit().getId() : null,
                        image.getImageType(),
                        image.getImagePath(),
                        image.getImageName(),
                        image.getImageSize(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getOriginalFormat(),
                        image.getProcessedFormat(),
                        image.getDescription(),
                        image.getCreatedAt(),
                        image.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DiagnosticImageResponse> getAllDiagnosticImages() {
        List<DiagnosticImage> images = diagnosticImageRepository.findAll();
        return images.stream()
                .map(image -> new DiagnosticImageResponse(
                        image.getId(),
                        image.getVisit() != null ? image.getVisit().getId() : null,
                        image.getImageType(),
                        image.getImagePath(),
                        image.getImageName(),
                        image.getImageSize(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getOriginalFormat(),
                        image.getProcessedFormat(),
                        image.getDescription(),
                        image.getCreatedAt(),
                        image.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDiagnosticImage(Long id) {
        diagnosticImageRepository.deleteById(id);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 处理图片 - 转换格式为PNG并调整尺寸
     */
    private File processImage(MultipartFile originalFile) throws IOException {
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
    private BufferedImage cropCenterToSize(BufferedImage originalImage, int targetWidth, int targetHeight) {
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
}
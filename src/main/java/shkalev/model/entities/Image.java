package shkalev.model.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import shkalev.model.image.ImageType;

import javax.persistence.*;
import java.util.Arrays;

@Entity
@ApiModel(description = "Сущьность - Изображение")
public class Image {
    @Id
    @GeneratedValue
    @ApiModelProperty(notes = "Автоматически генерируемый id")
    private long id;

    @ApiModelProperty(notes = "Ширина изображения")
    private int width;

    @ApiModelProperty(notes = "Высота изображения")
    private int height;

    @Enumerated(EnumType.STRING)
    @ApiModelProperty(notes = "Тип изображения (png/jpeg)")
    private ImageType imageType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @ApiModelProperty(notes = "Массив байт файла изображения")
    private byte[] imageBytes;

    public Image() {
    }

    public byte[] getImageBytes() {
        return Arrays.copyOf(imageBytes, imageBytes.length);
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = Arrays.copyOf(imageBytes, imageBytes.length);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public Image copy() {
        final Image image = new Image();
        image.setHeight(height);
        image.setWidth(width);
        image.setImageBytes(imageBytes);
        image.setImageType(imageType);
        return image;
    }
}

package shkalev.model.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Objects;

@Entity
@IdClass(ScreenshotId.class)
@ApiModel("Сущьность для хранения скриншотов, которые добавляются тестам")
public class Screenshot {

    @Id
    @ApiModelProperty(notes = "id теста")
    private String testId;
    @Id
    @ApiModelProperty(notes = "id скришота")
    private String screenshotId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @ApiModelProperty(notes = "Изображение")
    private Image image;

    public Screenshot() {
    }

    public Screenshot(final String testId, final String screenshotId, final Image image) {
        this.testId = testId;
        this.screenshotId = screenshotId;
        this.image = image;
    }

    public Screenshot(final String testId, final String screenshotId) {
        this(testId, screenshotId, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Screenshot that = (Screenshot) o;

        if (!Objects.equals(testId, that.testId)) return false;
        return Objects.equals(screenshotId, that.screenshotId);
    }

    @Override
    public int hashCode() {
        int result = testId != null ? testId.hashCode() : 0;
        result = 31 * result + (screenshotId != null ? screenshotId.hashCode() : 0);
        return result;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getScreenshotId() {
        return screenshotId;
    }

    public void setScreenshotId(String screenshotId) {
        this.screenshotId = screenshotId;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}

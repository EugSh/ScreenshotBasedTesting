package shkalev.model.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
@IdClass(ScreenshotDifferenceId.class)
@ApiModel("Сущьность для хранения последних результатов сравнения")
public class ScreenshotDifference {
    @Id
    @ApiModelProperty(notes = "id теста")
    private String testId;
    @Id
    @ApiModelProperty(notes = "id ожидаемого скриншота")
    private String expectedScreenId;
    @Id
    @ApiModelProperty(notes = "id текущего скриншота")
    private String actualScreenId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @ApiModelProperty(notes = "Изображение, результат сравнения")
    private Image image;

    @Enumerated(EnumType.STRING)
    @ApiModelProperty(notes = "Статус сравнения")
    private DifferenceStatus differenceStatus = DifferenceStatus.NONE;

    public ScreenshotDifference() {
    }

    public ScreenshotDifference(final String testId, final String expectedScreenId, final String actualScreenId) {
        this.testId = testId;
        this.expectedScreenId = expectedScreenId;
        this.actualScreenId = actualScreenId;
    }

    public ScreenshotDifference(final ScreenshotDifferenceId screenshotDifferenceId) {
        this(screenshotDifferenceId.getTestId(),
                screenshotDifferenceId.getExpectedScreenId(),
                screenshotDifferenceId.getActualScreenId());
    }

    public ScreenshotDifferenceId getDifferenceId() {
        return new ScreenshotDifferenceId(testId, expectedScreenId, actualScreenId);
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getExpectedScreenId() {
        return expectedScreenId;
    }

    public void setExpectedScreenId(String expectedScreenId) {
        this.expectedScreenId = expectedScreenId;
    }

    public String getActualScreenId() {
        return actualScreenId;
    }

    public void setActualScreenId(String actualScreenId) {
        this.actualScreenId = actualScreenId;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public DifferenceStatus getDifferenceStatus() {
        return differenceStatus;
    }

    public void setDifferenceStatus(DifferenceStatus differenceStatus) {
        this.differenceStatus = differenceStatus;
    }
}

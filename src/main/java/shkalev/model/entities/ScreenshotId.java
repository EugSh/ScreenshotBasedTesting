package shkalev.model.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;

@ApiModel("Сущьность для уникальной идентификации сущьности Screenshot")
public class ScreenshotId implements Serializable {
    private static final long serialVersionUID = -7821021498028946542L;
    @ApiModelProperty(notes = "id теста")
    private String testId;
    @ApiModelProperty(notes = "id скриншота")
    private String screenshotId;

    public ScreenshotId(final String testId, final String screenshotId) {
        this.testId = testId;
        this.screenshotId = screenshotId;
    }

    public ScreenshotId() {
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

    @Override
    public String toString() {
        return "ScreenshotId{" +
                "testId='" + testId + '\'' +
                ", screenshotId='" + screenshotId + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScreenshotId that = (ScreenshotId) o;

        if (!Objects.equals(testId, that.testId)) return false;
        return Objects.equals(screenshotId, that.screenshotId);
    }

    @Override
    public int hashCode() {
        int result = testId != null ? testId.hashCode() : 0;
        result = 31 * result + (screenshotId != null ? screenshotId.hashCode() : 0);
        return result;
    }
}

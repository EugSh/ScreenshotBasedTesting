package shkalev.model.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;


@ApiModel("Сущьность для уникальной идентификации сущьности ScreenshotDifference")
public class ScreenshotDifferenceId implements Serializable {
    private static final long serialVersionUID = 417935615141712488L;
    @ApiModelProperty(notes = "id теста")
    private String testId;
    @ApiModelProperty(notes = "id ожидаемого скриншота")
    private String expectedScreenId;
    @ApiModelProperty(notes = "id текущего скриншота")
    private String actualScreenId;

    public ScreenshotDifferenceId() {
    }

    public ScreenshotDifferenceId(String testId, String expectedScreenId, String actualScreenId) {
        this.testId = testId;
        this.expectedScreenId = expectedScreenId;
        this.actualScreenId = actualScreenId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScreenshotDifferenceId that = (ScreenshotDifferenceId) o;

        if (!Objects.equals(testId, that.testId)) return false;
        if (!Objects.equals(expectedScreenId, that.expectedScreenId))
            return false;
        return Objects.equals(actualScreenId, that.actualScreenId);
    }

    @Override
    public int hashCode() {
        int result = testId != null ? testId.hashCode() : 0;
        result = 31 * result + (expectedScreenId != null ? expectedScreenId.hashCode() : 0);
        result = 31 * result + (actualScreenId != null ? actualScreenId.hashCode() : 0);
        return result;
    }
}

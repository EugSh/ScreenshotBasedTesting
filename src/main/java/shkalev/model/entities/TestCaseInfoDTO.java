package shkalev.model.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

@ApiModel("Сущьность для получения результатов запроса к базе таблицы TestCase")
public class TestCaseInfoDTO {
    @ApiModelProperty(notes = "id теста")
    private String testId;
    @ApiModelProperty(notes = "id результат сравнения")
    private DifferenceStatus result;

    public TestCaseInfoDTO(String testId, DifferenceStatus result) {
        this.testId = testId;
        this.result = result;
    }

    public TestCaseInfoDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestCaseInfoDTO that = (TestCaseInfoDTO) o;

        return Objects.equals(testId, that.testId);
    }

    @Override
    public int hashCode() {
        return testId != null ? testId.hashCode() : 0;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public DifferenceStatus getResult() {
        return result;
    }

    public void setResult(DifferenceStatus result) {
        this.result = result;
    }
}

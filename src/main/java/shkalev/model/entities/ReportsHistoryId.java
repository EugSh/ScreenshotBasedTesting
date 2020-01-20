package shkalev.model.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;

@ApiModel("Сущьностьдля уникальной идентификации сущьности ReportsHistory")
public class ReportsHistoryId implements Serializable {
    private static final long serialVersionUID = 3095559843694416898L;
    @ApiModelProperty(notes = "id раунда")
    private String roundId;
    @ApiModelProperty(notes = "id теста")
    private String testId;
    @ApiModelProperty(notes = "id текущего скриншота")
    private String actualScreenId;

    public ReportsHistoryId() {
    }

    public ReportsHistoryId(String roundId, String testId, String actualScreenId) {
        this.roundId = roundId;
        this.testId = testId;
        this.actualScreenId = actualScreenId;
    }

    @Override
    public String toString() {
        return "ReportHistoryId{" +
                "roundId='" + roundId + '\'' +
                ", testId='" + testId + '\'' +
                ", actualScreenId='" + actualScreenId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportsHistoryId that = (ReportsHistoryId) o;

        if (!Objects.equals(roundId, that.roundId)) return false;
        if (!Objects.equals(testId, that.testId)) return false;
        return Objects.equals(actualScreenId, that.actualScreenId);
    }

    @Override
    public int hashCode() {
        int result = roundId != null ? roundId.hashCode() : 0;
        result = 31 * result + (testId != null ? testId.hashCode() : 0);
        result = 31 * result + (actualScreenId != null ? actualScreenId.hashCode() : 0);
        return result;
    }
}

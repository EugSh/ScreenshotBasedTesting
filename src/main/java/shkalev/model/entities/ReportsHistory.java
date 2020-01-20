package shkalev.model.entities;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
@IdClass(ReportsHistoryId.class)
@ApiModel("Сущьность для хранеиня истории запущенных раундов")
public class  ReportsHistory {
    @Id
    @ApiModelProperty(notes = "id раунда")
    private String roundId;
    @Id
    @ApiModelProperty(notes = "id теста")
    private String testId;
    @Id
    @ApiModelProperty(notes = "id скриншота")
    private String actualScreenId;

    @Enumerated(EnumType.STRING)
    @ApiModelProperty(notes = "Статус сравнения")
    private DifferenceStatus status;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @ApiModelProperty(notes = "Текущее изображение, с результатом сравнения")
    private Image actual;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @ApiModelProperty(notes = "Ожидаемое изображение")
    private Image expected;

    public ReportsHistory() {
    }

    public ReportsHistory(final String roundId, final String testId, final String actualScreenId) {
        this.roundId = roundId;
        this.testId = testId;
        this.actualScreenId = actualScreenId;
    }

    public Image getExpected() {
        return expected;
    }

    public void setExpected(Image expected) {
        this.expected = expected;
    }

    public String getRoundId() {
        return roundId;
    }

    public void setRoundId(String roundId) {
        this.roundId = roundId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getActualScreenId() {
        return actualScreenId;
    }

    public void setActualScreenId(String actualScreenId) {
        this.actualScreenId = actualScreenId;
    }

    public DifferenceStatus getStatus() {
        return status;
    }

    public void setStatus(DifferenceStatus status) {
        this.status = status;
    }

    public Image getActual() {
        return actual;
    }

    public void setActual(Image image) {
        this.actual = image;
    }
}

package shkalev.model.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Сущьность для передачи клиенту отчета о запущенном сравнении скриншотов, о тесте, о тестовом раунде, о запущенных ранее сравнениях.")
public class ReportsDTO {

    @ApiModelProperty(notes = "id")
    private String id;

    @ApiModelProperty(notes = "Статус сравнения")
    private DifferenceStatus status;

    @ApiModelProperty(notes = "Количество успешных сравнений")
    private int okCount;

    @ApiModelProperty(notes = "Количество провальных сравнений")
    private int failedCount;

    public ReportsDTO(final String id, final DifferenceStatus status, final long okCount, final long failedCount) {
        this.id = id;
        this.status = status;
        this.okCount = (int) okCount;
        this.failedCount = (int) failedCount;
    }

    public ReportsDTO() {
    }

    public int getOkCount() {
        return okCount;
    }

    public void setOkCount(int okCount) {
        this.okCount = okCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public DifferenceStatus getStatus() {
        return status;
    }

    public void setStatus(final DifferenceStatus status) {
        this.status = status;
    }
}

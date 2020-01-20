package shkalev.model.entities;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.List;

@Entity
@ApiModel("Сущьность для хранения созданных тестов")
public class TestCase {
    @Id
    @ApiModelProperty(notes = "id теста")
    private String testId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @ApiModelProperty(notes = "Эталонный скриншот")
    private Screenshot reference;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval= true)
    @ApiModelProperty(notes = "Список добавленных скриншотов")
    private List<Screenshot> screenshots;

    @Enumerated(EnumType.STRING)
    @ApiModelProperty(notes = "Статус выполнения теста")
    private DifferenceStatus differenceStatus = DifferenceStatus.NONE;

    public TestCase() {
    }

    public TestCase(final String testId) {
        this.testId = testId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public Screenshot getReference() {
        return reference;
    }

    public void setReference(Screenshot preference) {
        this.reference = preference;
    }

    public List<Screenshot> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<Screenshot> screenshots) {
        this.screenshots = screenshots;
    }

    public DifferenceStatus getDifferenceStatus() {
        return differenceStatus;
    }

    public void setDifferenceStatus(DifferenceStatus differenceStatus) {
        this.differenceStatus = differenceStatus;
    }
}

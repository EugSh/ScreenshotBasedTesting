package shkalev.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shkalev.model.entities.ReportsDTO;
import shkalev.model.entities.ScreenshotDifference;
import shkalev.model.entities.ScreenshotDifferenceId;

import java.util.List;

public interface ScreenshotDifferenceRepository extends JpaRepository<ScreenshotDifference, ScreenshotDifferenceId> {

    List<ScreenshotDifference> findAllByTestId(final String testId);

    @Query("select new shkalev.model.entities.ReportsDTO(s.testId, s.differenceStatus , count(s), count(s)) from ScreenshotDifference s group by s.testId, s.differenceStatus")
    List<ReportsDTO> getAllLiteReport();
}

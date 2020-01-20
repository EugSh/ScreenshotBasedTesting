package shkalev.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shkalev.model.entities.ReportsDTO;
import shkalev.model.entities.ReportsHistoryId;
import shkalev.model.entities.ReportsHistory;

import java.util.List;

public interface ReportsHistoryRepository extends JpaRepository<ReportsHistory, ReportsHistoryId> {
    @Query("select new shkalev.model.entities.ReportsDTO(r.roundId, r.status , count(r), count(r)) from ReportsHistory r group by r.roundId, r.status")
    List<ReportsDTO> getAllReportsLite();

    @Query("select new shkalev.model.entities.ReportsDTO(r.actualScreenId, r.status, count(r), count(r)) from ReportsHistory r where r.roundId = ?1 and r.testId = ?2 group by r.actualScreenId, r.status")
    List<ReportsDTO> getAllTestReportLite(final String roundId, final String testId);

    @Query("select new shkalev.model.entities.ReportsDTO(r.testId, r.status, count(r), count(r)) from ReportsHistory r where r.roundId = ?1 group by r.testId, r.status")
    List<ReportsDTO> getRoundReportLite(final String roundId);

    @Query("select r from ReportsHistory r where r.roundId = ?1")
    List<ReportsHistory> findAllByRoundId(final String roundId);

    @Query("select r from ReportsHistory r where r.roundId = ?1 and r.testId = ?2")
    List<ReportsHistory> findAllByRoundIdAndTestId(final String roundId, final String testId);
}

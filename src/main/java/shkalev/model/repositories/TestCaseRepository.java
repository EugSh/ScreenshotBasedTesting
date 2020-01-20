package shkalev.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shkalev.model.entities.TestCase;
import shkalev.model.entities.TestCaseInfoDTO;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, String> {

    @Query("select new shkalev.model.entities.TestCaseInfoDTO(testId, differenceStatus) from TestCase")
    List<TestCaseInfoDTO> getAllTestInfo();
}


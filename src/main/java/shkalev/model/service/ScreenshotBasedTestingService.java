package shkalev.model.service;

import shkalev.exception.ResourceNotFoundException;
import shkalev.model.entities.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface ScreenshotBasedTestingService {
    TestCase addTest(final TestCase testCase);

    String removeTest(final String testId);

    List<ReportsDTO> getAllTests();

    List<ReportsDTO> getScreenshotsInTest(final String testId) throws ResourceNotFoundException;

    Screenshot getScreenshotById(final ScreenshotId screenshotId) throws ResourceNotFoundException;

    ScreenshotId addScreenshot(final Screenshot screenshot) throws ResourceNotFoundException;

    ScreenshotId removeScreenshotById(final ScreenshotId screenshotId) throws ResourceNotFoundException;

    Screenshot getReference(final String testId) throws ResourceNotFoundException;

    ScreenshotId setReference(final ScreenshotId screenshotId) throws ResourceNotFoundException;

    ReportsDTO doDifference(final ScreenshotId screenshotId) throws ResourceNotFoundException, IOException;

    ScreenshotDifference getDifference(final ScreenshotId screenshotId) throws ResourceNotFoundException;

    ReportsDTO runTest(final String testId) throws ResourceNotFoundException, IOException;

    ReportsDTO runRound(final String roundId);

    Collection<ReportsDTO> getAllRoundsReport();

    Collection<ReportsDTO> getTestsReportInRound(final String roundId);

    Collection<ReportsDTO> getScreenshotsReportInTest(final String roundId, final String testId);

    ReportsHistory getScreenReport(final ReportsHistoryId id) throws ResourceNotFoundException;

    String removeRoundReport(final String roundId);

    String removeTestReport(final String roundId, final String testId);

    String removeScreenshotReport(final String roundId, final String testId, final String screenshotId);

}

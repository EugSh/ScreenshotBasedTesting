package shkalev.model.service.impl;

import org.springframework.stereotype.Service;
import shkalev.exception.ResourceNotFoundException;
import shkalev.model.entities.*;
import shkalev.model.entities.Image;
import shkalev.model.image.*;
import shkalev.model.image.Color;
import shkalev.model.repositories.ReportsHistoryRepository;
import shkalev.model.repositories.ScreenshotDifferenceRepository;
import shkalev.model.repositories.TestCaseRepository;
import shkalev.model.service.ScreenshotBasedTestingService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ScreenshotBasedTestingServiceImpl implements ScreenshotBasedTestingService {
    private final TestCaseRepository testRepository;
    private final ScreenshotDifferenceRepository differenceRepository;
    private final ReportsHistoryRepository historyRepository;

    public ScreenshotBasedTestingServiceImpl(final TestCaseRepository testRepository,
                                             final ScreenshotDifferenceRepository differenceRepository,
                                             final ReportsHistoryRepository historyRepository) {
        this.testRepository = testRepository;
        this.differenceRepository = differenceRepository;
        this.historyRepository = historyRepository;
    }

    @Override
    public TestCase addTest(TestCase testCase) {
        return testRepository.saveAndFlush(testCase);
    }

    @Override
    public String removeTest(String testId) {
        testRepository.deleteById(testId);
        return testId;
    }

    @Override
    public List<ReportsDTO> getAllTests() {
        final List<TestCaseInfoDTO> testCases = testRepository.getAllTestInfo();
        final Collection<ReportsDTO> testsReport = collapseWithStatus(differenceRepository.getAllLiteReport());
        final List<ReportsDTO> result = new LinkedList<>();
        for (final ReportsDTO report : testsReport) {
            testCases.remove(new TestCaseInfoDTO(report.getId(), null));
            result.add(report);
        }
        for (final TestCaseInfoDTO infoDTO : testCases) {
            result.add(new ReportsDTO(infoDTO.getTestId(), DifferenceStatus.NONE, 0, 0));
        }
        return result;
    }

    @Override
    public List<ReportsDTO> getScreenshotsInTest(final String testId) throws ResourceNotFoundException {
        final List<ScreenshotDifference> differences = differenceRepository.findAllByTestId(testId);
        final List<Screenshot> screenshots = getTestCaseById(testId).getScreenshots();
        final List<ReportsDTO> result = new LinkedList<>();
        for (final ScreenshotDifference difference : differences) {
            final ReportsDTO reportsDTO = new ReportsDTO(difference.getActualScreenId(),
                    difference.getDifferenceStatus(),
                    difference.getDifferenceStatus() == DifferenceStatus.OK ? 1 : 0,
                    difference.getDifferenceStatus() == DifferenceStatus.FAILED ? 1 : 0);
            result.add(reportsDTO);
            screenshots.remove(new Screenshot(testId, reportsDTO.getId()));
        }
        for (final Screenshot screenshot : screenshots) {
            final ReportsDTO reportsDTO = new ReportsDTO(screenshot.getScreenshotId(),
                    DifferenceStatus.NONE,
                    0,
                    0);
            result.add(reportsDTO);
        }
        return result;
    }

    @Override
    public Screenshot getScreenshotById(ScreenshotId screenshotId) throws ResourceNotFoundException {
        final TestCase testCase = getTestCaseById(screenshotId.getTestId());
        return getScreenshotById(screenshotId, testCase);
    }

    @Override
    public ScreenshotId addScreenshot(Screenshot screenshot) throws ResourceNotFoundException {
        final TestCase testCase = getTestCaseById(screenshot.getTestId());
        testCase.getScreenshots().add(screenshot);
        testRepository.saveAndFlush(testCase);
        return new ScreenshotId(screenshot.getTestId(), screenshot.getScreenshotId());
    }

    @Override
    public ScreenshotId removeScreenshotById(ScreenshotId screenshotId) throws ResourceNotFoundException {
        final TestCase testCase = getTestCaseById(screenshotId.getTestId());
        final Screenshot toBeRemoved = new Screenshot(screenshotId.getTestId(), screenshotId.getScreenshotId());
        testCase.getScreenshots().remove(toBeRemoved);
        testRepository.saveAndFlush(testCase);
        return screenshotId;
    }

    @Override
    public Screenshot getReference(String testId) throws ResourceNotFoundException {
        return getReference(getTestCaseById(testId));
    }

    @Override
    public ScreenshotId setReference(ScreenshotId screenshotId) throws ResourceNotFoundException {
        TestCase testCase = getTestCaseById(screenshotId.getTestId());
        final Screenshot newReference = getScreenshotById(screenshotId, testCase);
        final Screenshot prevReference = testCase.getReference();
        testCase.getScreenshots().remove(newReference);
        testCase.setReference(null);
        testCase = testRepository.saveAndFlush(testCase);
        testCase.setReference(newReference);
        if (prevReference != null) {
            testCase.getScreenshots().add(prevReference);
        }
        testRepository.saveAndFlush(testCase);
        return screenshotId;
    }

    @Override
    public ReportsDTO doDifference(ScreenshotId screenshotId)
            throws ResourceNotFoundException, IOException {
        final TestCase testCase = getTestCaseById(screenshotId.getTestId());
        final Screenshot screenshot = getScreenshotById(screenshotId, testCase);
        final Screenshot reference = getReference(testCase);
        final ScreenshotDifference screenshotDifference = runDifference(reference, screenshot);
        differenceRepository.saveAndFlush(screenshotDifference);
        final int ok = screenshotDifference.getDifferenceStatus() == DifferenceStatus.OK ? 1 : 0;
        return new ReportsDTO(screenshotDifference.getActualScreenId(),
                screenshotDifference.getDifferenceStatus(),
                ok,
                1 - ok);
    }

    @Override
    public ScreenshotDifference getDifference(ScreenshotId screenshotId) throws ResourceNotFoundException {
        final TestCase testCase = getTestCaseById(screenshotId.getTestId());
        final Screenshot screenshot = getScreenshotById(screenshotId, testCase);
        final Screenshot reference = getReference(testCase);
        final ScreenshotDifferenceId screenshotDifferenceId = new ScreenshotDifferenceId(screenshot.getTestId(),
                reference.getScreenshotId(),
                screenshot.getScreenshotId());
        return differenceRepository.findById(screenshotDifferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Result of screenshot difference not found"));
    }

    @Override
    public ReportsDTO runTest(String testId) throws ResourceNotFoundException, IOException {
        final TestCase testCase = getTestCaseById(testId);
        final List<ScreenshotDifference> differences = runTestDiff(getReference(testCase), testCase.getScreenshots());
        final int okCount = getCountOfStatus(DifferenceStatus.OK, differences);
        final DifferenceStatus status = okCount == differences.size() ? DifferenceStatus.OK : DifferenceStatus.FAILED;
        testCase.setDifferenceStatus(status);
        testRepository.saveAndFlush(testCase);
        differenceRepository.saveAll(differences);
        differenceRepository.flush();
        return new ReportsDTO(testId, status, okCount, differences.size() - okCount);
    }

    @Override
    public ReportsDTO runRound(String roundId) {
        final List<TestCase> tests = testRepository.findAll();
        final List<ScreenshotDifference> allDifferences = new LinkedList<>();
        final List<ReportsHistory> reports = new LinkedList<>();
        int failedStatus = 0;
        for (final TestCase test : tests) {
            try {
                final List<ScreenshotDifference> differences = runTestDiff(getReference(test), test.getScreenshots());
                allDifferences.addAll(differences);
                final int failed = getCountOfStatus(DifferenceStatus.FAILED, differences);
                failedStatus = failedStatus + failed;
                reports.addAll(getReports(differences, roundId, getReference(test)));
            } catch (ResourceNotFoundException | IOException e) {
                failedStatus++;
                test.setDifferenceStatus(DifferenceStatus.FAILED);
            }
        }
        testRepository.saveAll(tests);
        differenceRepository.saveAll(allDifferences);
        historyRepository.saveAll(reports);
        return new ReportsDTO(roundId,
                failedStatus == 0 ? DifferenceStatus.OK : DifferenceStatus.FAILED,
                reports.size() - failedStatus,
                failedStatus);
    }

    @Override
    public Collection<ReportsDTO> getAllRoundsReport() {
        return collapseWithStatus(historyRepository.getAllReportsLite());
    }

    @Override
    public Collection<ReportsDTO> getTestsReportInRound(String roundId) {
        return collapseWithStatus(historyRepository.getRoundReportLite(roundId));
    }

    @Override
    public Collection<ReportsDTO> getScreenshotsReportInTest(String roundId, String testId) {
        return collapseWithStatus(historyRepository.getAllTestReportLite(roundId, testId));
    }

    @Override
    public ReportsHistory getScreenReport(ReportsHistoryId id) throws ResourceNotFoundException {
        return historyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Report about screenshot not found"));
    }

    @Override
    public String removeRoundReport(String roundId) {
        historyRepository.deleteAll(historyRepository.findAllByRoundId(roundId));
        return roundId;
    }

    @Override
    public String removeTestReport(String roundId, String testId) {
        historyRepository.deleteAll(historyRepository.findAllByRoundIdAndTestId(roundId, testId));
        return testId;
    }

    @Override
    public String removeScreenshotReport(String roundId, String testId, String screenshotId) {
        historyRepository.deleteById(new ReportsHistoryId(roundId, testId, screenshotId));
        return screenshotId;
    }

    private TestCase getTestCaseById(final String testId) throws ResourceNotFoundException {
        return testRepository.findById(testId).orElseThrow(() -> new ResourceNotFoundException("Test with id " + testId + " not found"));
    }

    private Screenshot getScreenshotById(final ScreenshotId screenshotId, final TestCase testCase) throws ResourceNotFoundException {
        final Screenshot searched = new Screenshot(screenshotId.getTestId(), screenshotId.getScreenshotId());
        for (Screenshot screenshot : testCase.getScreenshots()) {
            if (screenshot.equals(searched)) {
                return screenshot;
            }
        }
        throw new ResourceNotFoundException("Screenshot with id " + screenshotId.getScreenshotId() + " not found");
    }

    private Screenshot getReference(final TestCase testCase) throws ResourceNotFoundException {
        final Screenshot reference = testCase.getReference();
        if (reference == null) {
            throw new ResourceNotFoundException("Reference screenshot for test " + testCase.getTestId() + " does not set");
        }
        return reference;
    }

    private ScreenshotDifference runDifference(final Screenshot reference, final Screenshot screenshot) throws IOException {
        final ImageMatrix expected = convertScreenshotToImageMatrix(reference);
        final ImageMatrix actual = convertScreenshotToImageMatrix(screenshot);
        final ScreenshotDifferenceId screenshotDifferenceId = new ScreenshotDifferenceId(screenshot.getTestId(),
                reference.getScreenshotId(),
                screenshot.getScreenshotId());
        try {
            final float sideCoeff = SlidingWindowImageComparator.getSideCoef(Math.max(expected.getRows(), expected.getCols()));
            final SlidingWindowImageComparator comparator = new SlidingWindowImageComparator(expected, actual, sideCoeff);
            final CompletableFuture<List<SlidingWindow>> futureList = comparator.compareImage(0, 0, expected.getRows(), expected.getCols(), 10);
            final List<SlidingWindow> windows = futureList.join();
            if (windows.size() == 0) {
                return getScreenDifferenceOK(screenshotDifferenceId, actual);
            } else {
                return getScreenDifferenceFailed(screenshotDifferenceId, actual, windows);
            }
        } catch (ImageSizeMismatchException e) {
            return getScreenDifferenceNotComparable(screenshotDifferenceId);
        }
    }

    private ImageMatrix convertScreenshotToImageMatrix(final Screenshot screenshot) throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(screenshot.getImage().getImageBytes());
        final BufferedImage bufferedImage = ImageIO.read(inputStream);
        return new ImageMatrix(screenshot.getImage().getHeight(),
                screenshot.getImage().getWidth(),
                screenshot.getImage().getImageType() == ImageType.PNG,
                ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData());
    }

    private ScreenshotDifference getScreenDifferenceOK(final ScreenshotDifferenceId screenshotDifferenceId,
                                                       final ImageMatrix matrix) throws IOException {

        final ScreenshotDifference screenshotDifference = new ScreenshotDifference(screenshotDifferenceId);
        screenshotDifference.setDifferenceStatus(DifferenceStatus.OK);
        screenshotDifference.setImage(getImageFromMatrix(matrix));
        return screenshotDifference;
    }

    private ScreenshotDifference getScreenDifferenceFailed(final ScreenshotDifferenceId screenshotDifferenceId,
                                                           final ImageMatrix matrix,
                                                           final List<SlidingWindow> foundDiff) throws IOException {
        final Color color = new Color(255, 0, 0);
        for (final SlidingWindow window : foundDiff) {
            matrix.fillLiteRectangle(window.getRow(), window.getCol(), window.getHeight(), window.getWidth(), color);
        }

        final ScreenshotDifference screenshotDifference = new ScreenshotDifference(screenshotDifferenceId);
        screenshotDifference.setDifferenceStatus(DifferenceStatus.FAILED);
        screenshotDifference.setImage(getImageFromMatrix(matrix));
        return screenshotDifference;
    }

    private Image getImageFromMatrix(final ImageMatrix matrix) throws IOException {
        final Image image = new Image();
        image.setHeight(matrix.getRows());
        image.setWidth(matrix.getCols());
        image.setImageType(matrix.hasAlpha() ? ImageType.PNG : ImageType.JPEG);
        image.setImageBytes(getFileBytes(matrix));
        return image;
    }

    private byte[] getFileBytes(final ImageMatrix matrix) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(matrix.getCols(), matrix.getRows(), matrix.hasAlpha() ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);
        final byte[] imageBytes = matrix.getPixelsAsByteArray();
        bufferedImage.setData(Raster.createRaster(bufferedImage.getSampleModel(), new DataBufferByte(imageBytes, imageBytes.length), new Point()));
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, matrix.hasAlpha() ? "png" : "jpeg", outputStream);
        return outputStream.toByteArray();
    }

    private ScreenshotDifference getScreenDifferenceNotComparable(final ScreenshotDifferenceId screenshotDifferenceId) {
        final ScreenshotDifference screenshotDifference = new ScreenshotDifference(screenshotDifferenceId);
        screenshotDifference.setDifferenceStatus(DifferenceStatus.FAILED);
        return screenshotDifference;
    }

    private List<ScreenshotDifference> runTestDiff(final Screenshot expected, final List<Screenshot> actual) throws IOException {
        final List<ScreenshotDifference> result = new LinkedList<>();
        for (final Screenshot screenshot : actual) {
            final ScreenshotDifference screenshotDifference = runDifference(expected, screenshot);
            result.add(screenshotDifference);
        }
        return result;
    }

    private int getCountOfStatus(final DifferenceStatus status,
                                 final List<ScreenshotDifference> differences) {
        int count = 0;
        for (final ScreenshotDifference difference : differences) {
            if (difference.getDifferenceStatus() == status) {
                count++;
            }
        }
        return count;
    }

    private List<ReportsHistory> getReports(final List<ScreenshotDifference> differences, final String roundId, final Screenshot reference) {
        final List<ReportsHistory> result = new LinkedList<>();
        for (final ScreenshotDifference difference : differences) {
            final ReportsHistory report = new ReportsHistory();
            report.setRoundId(roundId);
            report.setTestId(difference.getTestId());
            report.setActualScreenId(difference.getActualScreenId());
            report.setStatus(difference.getDifferenceStatus());
            report.setActual(difference.getImage());
            report.setExpected(reference.getImage().copy());
            result.add(report);
        }
        return result;
    }

    private Collection<ReportsDTO> collapseWithStatus(final List<ReportsDTO> list) {
        final Map<String, ReportsDTO> result = new TreeMap<>();
        for (final ReportsDTO report : list) {
            final ReportsDTO item = result.getOrDefault(report.getId(), new ReportsDTO());
            item.setId(report.getId());
            if (report.getStatus() == DifferenceStatus.OK) {
                item.setOkCount(report.getOkCount() + item.getOkCount());
            } else {
                item.setFailedCount(report.getFailedCount() + item.getFailedCount());
            }
            item.setStatus(item.getFailedCount() == 0 ? DifferenceStatus.OK : DifferenceStatus.FAILED);
            result.put(report.getId(), item);
        }
        return result.values();
    }
}

package shkalev.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shkalev.exception.InternalErrorException;
import shkalev.exception.ResourceNotFoundException;
import shkalev.model.entities.*;
import shkalev.model.image.ImageType;
import shkalev.model.service.ScreenshotBasedTestingService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api")
public class APIController {
    private final ScreenshotBasedTestingService service;

    @Autowired
    public APIController(final ScreenshotBasedTestingService service) {
        this.service = service;
    }

    @ApiOperation(value = "Получить скриншот, который был ранее добавлен", response = Screenshot.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Возвращает объект класса Screenshot"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию"),
            @ApiResponse(code = 404, message = "Скриншот с таким id не найден")})
    @RequestMapping(value = "/screenshot", method = RequestMethod.GET)
    public Screenshot getScreenshot(@ApiParam(value = "id теста, у которого получаем скриншот", required = true)
                                    @Valid
                                    @RequestParam("testId") final String testId,
                                    @ApiParam(value = "id скриншота", required = true)
                                    @Valid
                                    @RequestParam("screenshotId") final String screenshotId)
            throws ResourceNotFoundException {
        return service.getScreenshotById(new ScreenshotId(testId, screenshotId));
    }

    @ApiOperation(value = "Добавить скриншот в базу данных", response = ScreenshotId.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Скриншот успешно добавлен в базу. Возвращает объект класса ScreenshotId"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию"),
            @ApiResponse(code = 500, message = "Произошла IOException, во время получения массива байт из MultipartFile")})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/screenshot", method = RequestMethod.POST)
    public ScreenshotId addScreenshot(@ApiParam(value = "id теста, которому добавляем скриншот", required = true)
                                      @Valid
                                      @RequestParam("testId") final String testId,
                                      @ApiParam(value = "id скриншота, который добавляем", required = true)
                                      @Valid
                                      @RequestParam("screenshotId") final String screenshotId,
                                      @ApiParam(value = "сам скриншот", required = true)
                                      @Valid
                                      @RequestParam("screenshot") final MultipartFile screenshot,
                                      @ApiParam(value = "Высота скриншота", required = true)
                                      @Valid
                                      @RequestParam("height") final int height,
                                      @ApiParam(value = "Ширина скриншота", required = true)
                                      @Valid
                                      @RequestParam("width") final int width)
            throws ResourceNotFoundException, InternalErrorException {
        try {
            final Image image = new Image();
            image.setHeight(height);
            image.setWidth(width);
            image.setImageType(ImageType.parseType(screenshot.getContentType()));
            image.setImageBytes(screenshot.getBytes());
            final Screenshot screen = new Screenshot();
            screen.setTestId(testId);
            screen.setScreenshotId(screenshotId);
            screen.setImage(image);
            return service.addScreenshot(screen);
        } catch (IOException e) {
            throw new InternalErrorException("Problem with image.");
        }
    }

    @ApiOperation(value = "Удалить скриншот из базы", response = ScreenshotId.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Скриншот успешно удален из базы. Возвращает объект класса ScreenshotId"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию")})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/screenshot", method = RequestMethod.DELETE)
    public ScreenshotId deleteScreenshot(@ApiParam(value = "id теста, у которого удаляем скриншот", required = true)
                                         @Valid
                                         @RequestParam("testId") final String testId,
                                         @ApiParam(value = "id скриншота, который удаляем", required = true)
                                         @Valid
                                         @RequestParam("screenshotId") final String screenshotId) throws ResourceNotFoundException {
        return service.removeScreenshotById(new ScreenshotId(testId, screenshotId));
    }

    @ApiOperation(value = "Получить эталонный скриншот для теста", response = ScreenshotId.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Возвращает эталонный скиншот, объект класса Screenshot"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию"),
            @ApiResponse(code = 404, message = "Тест с таким id не найден")})
    @RequestMapping(value = "/reference", method = RequestMethod.GET)
    public Screenshot gerReference(@ApiParam(value = "id теста, для которого нужно получить эталонный скриншот", required = true)
                                   @Valid
                                   @RequestParam("testId") final String testId)
            throws ResourceNotFoundException {
        return service.getReference(testId);
    }

    @ApiOperation(value = "Установить эталонный скриншот для теста", response = ScreenshotId.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Эталонный скриншот успешно установлен, возвращает объект класса ScreenshotId"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию"),
            @ApiResponse(code = 404, message = "Тест, которому необходимо установить эталонный скриншот, " +
                    "или скриншот, который необходимо сделать эталонным, не найдены в базе")})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/reference", method = RequestMethod.POST)
    public ScreenshotId setReference(@ApiParam(value = "id теста, для которого нужно установить эталонный скриншот", required = true)
                                     @Valid
                                     @RequestParam("testId") final String testId,
                                     @ApiParam(value = "id скриншота, который необходимо сделать эталонным", required = true)
                                     @Valid
                                     @RequestParam("screenshotId") final String screenshotId)
            throws ResourceNotFoundException {
        return service.setReference(new ScreenshotId(testId, screenshotId));
    }

    @ApiOperation(value = "Создать тест", response = TestCase.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Тест успешно создан. Возвращает объект класса TestCase"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию")})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public TestCase createTest(@ApiParam(value = "id для создания теста", required = true)
                               @Valid
                               @RequestParam("testId") final String testId) {
        final TestCase testCase = new TestCase();
        testCase.setTestId(testId);
        return service.addTest(testCase);
    }

    @ApiOperation(value = "Удалить тест", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Тест успешно удален. Возвращает id удаленного теста"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию")})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/test", method = RequestMethod.DELETE)
    public String removeTest(@ApiParam(value = "id теста, который необходимо удалить", required = true)
                             @Valid
                             @RequestParam("testId") String testId) {
        return service.removeTest(testId);
    }

    @ApiOperation(value = "Получить список всех тестов", response = List.class)
    @ApiResponse(code = 200, message = "Тест успешно удален")
    @RequestMapping(value = "/tests", method = RequestMethod.GET)
    public List<ReportsDTO> getTests() {
        return service.getAllTests();
    }

    @ApiOperation(value = "Получить результаты сравнения", response = ScreenshotDifference.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Возвращает результат сравнения, объект класса ScreenshotDifference"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию"),
            @ApiResponse(code = 404, message = "Не найдено результатов сравнения скриншота и с эталонным скриншотом для теста")})
    @RequestMapping(value = "/difference", method = RequestMethod.GET)
    public ScreenshotDifference getDifference(@ApiParam(value = "id теста, в котором сравнивались скриншоты", required = true)
                                              @Valid
                                              @RequestParam("testId") final String testId,
                                              @ApiParam(value = "id скриншота, который сравнивался с эталонным", required = true)
                                              @Valid
                                              @RequestParam("screenshotId") final String screenshotId)
            throws ResourceNotFoundException {
        return service.getDifference(new ScreenshotId(testId, screenshotId));
    }

    @ApiOperation(value = "Выполнить сравнение скриншота с эталонным скриншотом для теста", response = ReportsDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Сравнение выполнено. Возвращает объект класса ReportDTO"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию"),
            @ApiResponse(code = 404, message = "Указанный тест не найден, или отсутствует в базе эталонный скриншот для теста или скриншот для сравнения ")})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/difference", method = RequestMethod.POST)
    public ReportsDTO compare(@ApiParam(value = "id теста, в котором будут сравниваться скриншоты", required = true)
                              @Valid
                              @RequestParam("testId") final String testId,
                              @ApiParam(value = "id скриншота, который будет сравниваться с эталонным", required = true)
                              @Valid
                              @RequestParam("screenshotId") final String screenshotId)
            throws ResourceNotFoundException, IOException {
        return service.doDifference(new ScreenshotId(testId, screenshotId));
    }

    @ApiOperation(value = "Запуск теста, те последовательное сравнение всех скриншотов с эталонным", response = ReportsDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Сравнение всех скриншотов с эталонным выполнено успешно. Возвращает объект класса ReportDTO"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию"),
            @ApiResponse(code = 404, message = "Указанный тест не найден, или отсутствует в базе эталонный скриншот для теста или скриншот для сравнения ")})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/test/run", method = RequestMethod.POST)
    public ReportsDTO runTest(@ApiParam(value = "id теста, в котором нужно сравнить все скриншоты с эталонным", required = true)
                              @Valid
                              @RequestParam("testId") String testId) throws ResourceNotFoundException, IOException {
        return service.runTest(testId);
    }

    @ApiOperation(value = "Запуск тестового раунда, те последовательный запуск всех тестов", response = ReportsDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Запуск всех тестов завершен успешно. Возвращает объект класса ReportDTO"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию"),
            @ApiResponse(code = 404, message = "Отсутствуют тесты")})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/round/run", method = RequestMethod.POST)
    public ReportsDTO runRound(@ApiParam(value = "id раунда, в котором будет запущен каждый тест", required = true)
                               @Valid
                               @RequestParam("roundId") String roundId) {
        return service.runRound(roundId);
    }

    @ApiOperation(value = "Возврат списка всех раундов с их результатами запусков", response = Collection.class)
    @ApiResponse(code = 400, message = "Возвращает коллекцию всех раундов, объект класса ReportDTO")
    @RequestMapping(value = "/reports", method = RequestMethod.GET)
    public Collection<ReportsDTO> getReports() {
        return service.getAllRoundsReport();
    }

    @ApiOperation(value = "Удаление тестового раундаиз истории запусков раундов", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Раунд удален и возращен id удаленного раунда"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию"),
            @ApiResponse(code = 404, message = "Раунда с указанным id не существует")})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/report", method = RequestMethod.DELETE)
    public String deleteReport(@ApiParam(value = "id раунда, историю которого необходимо удалить", required = true)
                               @Valid
                               @RequestParam("roundId") String roundId) {
        return service.removeRoundReport(roundId);
    }

    @ApiOperation(value = "Удаление теста из истории раунда", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Тест удален и возвращен id удаленного теста"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию"),
            @ApiResponse(code = 404, message = "Раунда с указанным id не существует или не существует" +
                    " теста с id, который запукался в рамках указанного раунда")})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/report/test", method = RequestMethod.DELETE)
    public String deleteTestReport(@ApiParam(value = "id раунда, в котором будет удален тест тест", required = true)
                                   @Valid
                                   @RequestParam("roundId") String roundId,
                                   @ApiParam(value = "id теста", required = true)
                                   @Valid
                                   @RequestParam("testId") String testId) {
        return service.removeTestReport(roundId, testId);
    }

    @ApiOperation(value = "Удаление скриншота из истории раунда", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Скриншот удален и возвращен id удаленного скриншота"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию"),
            @ApiResponse(code = 404, message = "Раунда с указанным id не существует или не существует " +
                    "теста с id, который запукался в рамках указанного раунда, или не существует такого скриншта")})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/report/test/screenshot", method = RequestMethod.DELETE)
    public String deleteScreenshotReport(@ApiParam(value = "id раунда, для теста которого необходимо удалить скриншот", required = true)
                                         @Valid
                                         @RequestParam("roundId") final String roundId,
                                         @ApiParam(value = "id теста", required = true)
                                         @Valid
                                         @RequestParam("testId") final String testId,
                                         @ApiParam(value = "id скриншота", required = true)
                                         @Valid
                                         @RequestParam("screenshotId") final String screenshotId) {
        return service.removeScreenshotReport(roundId, testId, screenshotId);
    }

    @ApiOperation(value = "Возвращает скриншот из истории раунда", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Возвращает скриншот из истории запуска раунда, объект класса ReportsHistory"),
            @ApiResponse(code = 400, message = "Параметры запроса не прошли валидацию"),
            @ApiResponse(code = 404, message = "Раунда с указанным id не существует или не существует " +
                    "теста с id, который запукался в рамках указанного раунда, или не существует такого скриншта")})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/report/test/screenshot", method = RequestMethod.GET)
    public ReportsHistory getScreenshotReport(@ApiParam(value = "id раунда, для теста которого необходимо удалить скриншот", required = true)
                                              @Valid
                                              @RequestParam("roundId") String roundId,
                                              @ApiParam(value = "id теста", required = true)
                                              @Valid
                                              @RequestParam("testId") String testId,
                                              @ApiParam(value = "id скриншота", required = true)
                                              @Valid
                                              @RequestParam("screenshotId") final String screenshotId)
            throws ResourceNotFoundException {
        return service.getScreenReport(new ReportsHistoryId(roundId, testId, screenshotId));
    }
}

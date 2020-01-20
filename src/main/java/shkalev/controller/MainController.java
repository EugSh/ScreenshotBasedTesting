package shkalev.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import shkalev.exception.ResourceNotFoundException;
import shkalev.model.entities.ReportsDTO;
import shkalev.model.service.ScreenshotBasedTestingService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    private final ScreenshotBasedTestingService service;

    public MainController(ScreenshotBasedTestingService service) {
        this.service = service;
    }

    @ApiOperation(value = "Корневая страница", response = String.class)
    @ApiResponse(code = 200, message = "Возвращает html корнейво страницы")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @ApiOperation(value = "Страница service", response = String.class)
    @ApiResponse(code = 200, message = "Возвращает страницу service")
    @RequestMapping(value = "/service", method = RequestMethod.GET)
    public String service(@ApiParam(value = "Переменная, для выбора рендера соответствующей страницы (rounds/tests)", required = true)
                          @RequestParam("section") final String section, final Model model) {
        model.addAttribute("section", section);
        return "service";
    }

    @ApiOperation(value = "Страница отчета о пройденных тестах раунда", response = String.class)
    @ApiResponse(code = 200, message = "Возвращает страницу с отчетом по тестам в раунде")
    @RequestMapping(value = "/service/report", method = RequestMethod.GET)
    public String roundReport(@ApiParam(value = "id раунда", required = true)
                              @RequestParam("roundId") final String roundId,
                              final Model model) {
        model.addAttribute("roundId", roundId);
        model.addAttribute("reports", service.getTestsReportInRound(roundId));
        return "report";
    }

    @ApiOperation(value = "Страница отчета со скриншотами теста", response = String.class)
    @ApiResponse(code = 200, message = "Возвращает страницу с результатми выполненого сравнения скриншотов в тесте")
    @RequestMapping(value = "/service/report/test", method = RequestMethod.GET)
    public String testReport(@ApiParam(value = "id раунда", required = true)
                             @RequestParam("roundId") final String roundId,
                             @ApiParam(value = "id теста", required = true)
                             @RequestParam("testId") final String testId,
                             final Model model) {
        model.addAttribute("roundId", roundId);
        model.addAttribute("testId", testId);
        model.addAttribute("reports", service.getScreenshotsReportInTest(roundId, testId));
        return "report";
    }

    @ApiOperation(value = "Страница отчета скриншота", response = String.class)
    @ApiResponse(code = 200, message = "Возвращает страницу с expected и actual скриншотом")
    @RequestMapping(value = "/service/report/test/screenshot", method = RequestMethod.GET)
    public String screenshotReport(@ApiParam(value = "id раунда", required = true)
                                   @RequestParam("roundId") final String roundId,
                                   @ApiParam(value = "id теста", required = true)
                                   @RequestParam("testId") final String testId,
                                   @ApiParam(value = "id скриншота", required = true)
                                   @RequestParam("screenshotId") final String screenshotId,
                                   final Model model) {
        model.addAttribute("roundId", roundId);
        model.addAttribute("testId", testId);
        model.addAttribute("screenshotId", screenshotId);
        return "screenshot";
    }

    @ApiOperation(value = "Страница теста", response = String.class)
    @ApiResponse(code = 200, message = "Возвращает страницу с информацией конкретного теста")
    @RequestMapping(value = "/service/test", method = RequestMethod.GET)
    public String test(@ApiParam(value = "id теста", required = true)
                       @RequestParam("testId") final String testId,
                       final Model model) {
        model.addAttribute("testId", testId);
        try {
            model.addAttribute("reports", service.getScreenshotsInTest(testId));
        } catch (ResourceNotFoundException e) {
            final List<ReportsDTO> empty = new ArrayList<>(1);
            model.addAttribute("reports", empty);
        }
        return "test";
    }

    @ApiOperation(value = "Страница скриншота теста", response = String.class)
    @ApiResponse(code = 200, message = "Возвращает страницу со скриншотом, эталонным скриншотом для теста и ,если было выполнено сравнение, с результатом сравнения")
    @RequestMapping(value = "/service/test/screenshot", method = RequestMethod.GET)
    public String test(@ApiParam(value = "id теста", required = true)
                       @RequestParam("testId") final String testId,
                       @ApiParam(value = "id скриншота", required = true)
                       @RequestParam("screenshotId") final String screenshotId,
                       final Model model) {
        model.addAttribute("testId", testId);
        model.addAttribute("screenshotId", screenshotId);
        return "screenshot";
    }

    @ApiOperation(value = "Страница загрузки скриншота", response = String.class)
    @ApiResponse(code = 200, message = "Возвращает страницу для загрузки скриншота")
    @RequestMapping(value = "/service/test/upload", method = RequestMethod.GET)
    public String upload(@ApiParam(value = "id теста, в который будет загружен скриншот", required = true)
                         @RequestParam("testId") final String testId,
                         final Model model) {
        model.addAttribute("testId", testId);
        return "upload";
    }
}

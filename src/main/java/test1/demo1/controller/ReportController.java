package test1.demo1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import test1.demo1.service.OracleDynamicProcedureService;
import test1.demo1.service.OracleProcedureService;
import test1.demo1.service.ProcedureMetadataService;
import test1.demo1.util.StoredProcedureExecutor;

import java.util.*;

@Controller
public class ReportController {

    @Autowired
    private ProcedureMetadataService metadataService;

    @Autowired
    private OracleDynamicProcedureService dynamicService;

    private static final String PROCEDURE_NAME = "PISETH.TPT_REPORT";

    @GetMapping("/report")
    public String form(Model model) {
        List<Map<String, String>> parameters = metadataService.getProcedureParameters("PISETH", "TPT_REPORT");
        model.addAttribute("parameters", parameters);
        return "report-form";
    }

    @PostMapping("/report")
    public String result(@RequestParam Map<String, String> allParams, Model model) {
        allParams.remove("_csrf"); // If using Spring Security
        List<Map<String, Object>> data = dynamicService.callProcedure(PROCEDURE_NAME, allParams);
        model.addAttribute("data", data);
        return "report-result";
    }

}


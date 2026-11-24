package com.bijlipay.Controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.bijlipay.Model.UploadedVpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bijlipay.Model.SettlementReport;
import com.bijlipay.Repository.SettlementReportRepository;
import com.bijlipay.Repository.UploadedVpaRepository;
import com.bijlipay.Service.VpaService;
import com.bijlipay.Utill.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api")
@Slf4j
public class VpaController {



    @Autowired
    private SettlementReportRepository settlementReportRepository;

    @Autowired
    private VpaService vpaService;

    @Autowired
    private UploadedVpaRepository uploadedVpaRepository;

    @GetMapping("/transaction-vpa/{vpa}")
    public ResponseEntity<?> getVpaReport(@PathVariable String vpa, HttpServletResponse response) throws Exception {
        log.info("To request vpa ----->{}", vpa != null ? vpa : "NA");
        try {
            List<SettlementReport> reports = settlementReportRepository.fetchByVpa(vpa);
            if (reports == null || reports.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND, "No records found for this VPA"), HttpStatus.NOT_FOUND);
            }
            String fileName = "VpaFile.xlsx";
            log.info("The report Name is ---->{}", fileName);
            vpaService.generateReportVpa(fileName, reports, response);
            return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "success"), HttpStatus.OK);
        } catch (Exception e) {
            log.info("Failed to generate VPA report", e);
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, "Failed"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/transaction-vpa-list")
    public ResponseEntity<?> getVapListOfReport(@RequestParam("file") MultipartFile multipartFile, HttpServletResponse response) throws Exception {
        log.info("To request list of vpa ----->");
        if (multipartFile.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, "Please upload excel file"), HttpStatus.BAD_REQUEST);
        }
        log.info("The request file is present --->{}", multipartFile.getOriginalFilename() != null ? multipartFile.getOriginalFilename() : "NA");
        try {
            List<String> vpaList = vpaService.extractVpasFromExcel(multipartFile);
            log.info("Total records vpa extracted from uploaded file --->{}", vpaList.size());
            if (vpaList.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, "No VPA values found in uploaded file"), HttpStatus.BAD_REQUEST);
            }
            List<SettlementReport> allReports = new ArrayList<>();
            for (String vpa : vpaList) {
                List<SettlementReport> reports = settlementReportRepository.fetchByVpa(vpa);
                if (reports != null && !reports.isEmpty()) {
                    log.info("To found vpa in table --->{}",reports.get(0).getVpa());
                    allReports.addAll(reports);
                } else {
                    log.info("No records found for vpa --->{}", vpa);
                }
            }
            log.info("Total records found from database VPAs --->{}", allReports.size());
            if (allReports.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_FOUND, "No records found for any VPA in the uploaded file"), HttpStatus.NOT_FOUND);
            }
            String fileName = "VpaListReport_" + LocalDate.now() + ".xlsx";
            vpaService. generateReportVpa(fileName, allReports, response);
            return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "success"), HttpStatus.OK);
        } catch (Exception e) {
            log.info("Failed to process VPA list report", e);
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, "Failed"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Upload VPA file from frontend.
     * This replaces the existing uploaded VPA list used to filter slot reports (LOT 1, 2, 3).
     * Only transactions related to these VPAs will be included in generated reports.
     */
    @PostMapping("/upload-vpa-file")
    public ResponseEntity<?> uploadVpaFile(@RequestParam("file") MultipartFile multipartFile) {
        log.info("Upload VPA file for report filtering ----->");
        if (multipartFile.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, "Please upload an excel file"), HttpStatus.BAD_REQUEST);
        }
        try {
            List<String> vpaList = vpaService.extractVpasFromExcel(multipartFile);
            log.info("Total VPAs extracted from uploaded file --->{}", vpaList.size());
            if (vpaList.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, "No VPA values found in uploaded file"), HttpStatus.BAD_REQUEST);
            }
            // Truncate existing VPAs and insert new ones
            uploadedVpaRepository.truncate();
            List<UploadedVpa> uploadedVpas = new ArrayList<>();
            for (String vpa : vpaList) {
                UploadedVpa uploadedVpa = new UploadedVpa();
                uploadedVpa.setVpa(vpa);
                uploadedVpas.add(uploadedVpa);
            }
            uploadedVpaRepository.saveAll(uploadedVpas);
            log.info("Successfully uploaded {} VPAs for report filtering", vpaList.size());
            return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "VPA file uploaded successfully. " + vpaList.size() + " VPAs stored for report filtering."), HttpStatus.OK);
        } catch (Exception e) {
            log.info("Failed to upload VPA file", e);
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, "Failed to process VPA file"), HttpStatus.BAD_REQUEST);
        }
    }


}

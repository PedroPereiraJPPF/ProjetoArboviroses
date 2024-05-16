package com.arboviroses.conectaDengue.Api.Controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountByAgeRange;
import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse;
import com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.CountAgravoBySexoDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.DataNotificationInfoDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.DataNotificationResponseDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.SaveCsvResponseDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.SuccessResponseDTO;
import com.arboviroses.conectaDengue.Api.Exceptions.InvalidAgravoException;
import com.arboviroses.conectaDengue.Domain.Services.Notifications.NotificationService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
@RestController
@RequestMapping("api")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController 
{
    @Autowired
    NotificationService notificationService;

    @PostMapping("/savecsvdata")
    public ResponseEntity<SuccessResponseDTO<SaveCsvResponseDTO>> readCsv(@RequestParam("csv_file") MultipartFile file) throws Exception {
        return ResponseEntity.ok().body(SuccessResponseDTO.setResponse(notificationService.saveCSVDataInDatabase(file), "dados do csv salvos com sucesso"));
    }

    @GetMapping("/notifications")
    public ResponseEntity<SuccessResponseDTO<Page<DataNotificationResponseDTO>>> getAll(Pageable pageable, HttpServletRequest request) throws InvalidAgravoException {
        return ResponseEntity.ok().body(SuccessResponseDTO.setResponse(notificationService.getNotificationsByIdAgravoPaginated(pageable, request), null));
    }

    @GetMapping("/notifications/count")
    public ResponseEntity<SuccessResponseDTO<Long>> countNotifications(Pageable pageable, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok().body(SuccessResponseDTO.setResponse(notificationService.countByIdAgravo(request), null));
    }

    @GetMapping("/notifications/count/sexo")
    public ResponseEntity<SuccessResponseDTO<CountAgravoBySexoDTO>> get(HttpServletRequest request) throws InvalidAgravoException {
        return ResponseEntity.ok().body(SuccessResponseDTO.setResponse(notificationService.getNotificationsInfoBySexo(request), null));
    }

    @GetMapping("/notifications/count/epidemiologicalWeek")
    public ResponseEntity<SuccessResponseDTO<AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse>> getSemana(HttpServletRequest request) throws InvalidAgravoException {
        return ResponseEntity.ok().body(SuccessResponseDTO.setResponse(notificationService.countNotificationsBySemanaEpidemiologica(request), null));
    }

    @GetMapping("/notifications/count/epidemiologicalWeek/accumulated")
    public ResponseEntity<SuccessResponseDTO<AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse>> getSemanaAccumulated(HttpServletRequest request) throws InvalidAgravoException {
        return ResponseEntity.ok().body(SuccessResponseDTO.setResponse(notificationService.countNotificationsBySemanaEpidemiologicaAccumulated(request), null));
    }

    @GetMapping("/notifications/count/ageRange")
    public ResponseEntity<SuccessResponseDTO<AgravoCountByAgeRange>> getByAgeRange(HttpServletRequest request) throws InvalidAgravoException {
        return ResponseEntity.ok().body(SuccessResponseDTO.setResponse(notificationService.getNotificationsCountByAgeRange(request), null));
    }

    @GetMapping("/notifications/count/neighborhood")
    public ResponseEntity<SuccessResponseDTO<List<BairroCountDTO>>> getBairro(HttpServletRequest request) throws InvalidAgravoException {
        return ResponseEntity.ok().body(SuccessResponseDTO.setResponse(notificationService.getBairroCount(request), null));
    }

    @GetMapping("/notifications/count/evolucao")
    public ResponseEntity<SuccessResponseDTO<Long>> getEvolucao(HttpServletRequest request) throws Exception {
        return ResponseEntity.ok().body(SuccessResponseDTO.setResponse(notificationService.countByEvolucao(request), null));
    }

    @GetMapping("/notifications/count/informacoes")
    public ResponseEntity<SuccessResponseDTO<Map<String, Integer>>> getInformacoes(HttpServletRequest request) throws Exception {
        return ResponseEntity.ok().body(SuccessResponseDTO.setResponse(notificationService.listEspecificNotificationData(request), null));
    }

    /**
     * @comment esse endpoint serve para testes
     * @param request
     * @return
     * @throws InvalidAgravoException
     */
    @GetMapping("/notifications/getinfo")
    public ResponseEntity<DataNotificationInfoDTO> getNotificationsBySexo(HttpServletRequest request) throws InvalidAgravoException {
        return ResponseEntity.ok().body(notificationService.getNotificationsInfoForGraphicsByIdAgravo(request));
    }
}
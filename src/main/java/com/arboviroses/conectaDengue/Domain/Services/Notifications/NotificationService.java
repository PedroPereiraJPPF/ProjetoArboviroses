package com.arboviroses.conectaDengue.Domain.Services.Notifications;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.arboviroses.conectaDengue.Utils.ConvertNameToIdAgravo;
import com.arboviroses.conectaDengue.Utils.MemoryOptimizationUtil;
import com.arboviroses.conectaDengue.Utils.StringToDateCSV;
import com.arboviroses.conectaDengue.Utils.MossoroData.NeighborhoodsMossoro;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.arboviroses.conectaDengue.Api.DTO.request.NotificationBatchDTO;
import com.arboviroses.conectaDengue.Api.DTO.request.NotificationDataDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountByAgeRange;
import com.arboviroses.conectaDengue.Api.DTO.response.AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse;
import com.arboviroses.conectaDengue.Api.DTO.response.BairroCountDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.CountAgravoBySexoDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.DataNotificationResponseDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.SaveCsvResponseDTO;
import com.arboviroses.conectaDengue.Api.Exceptions.InvalidAgravoException;
import com.arboviroses.conectaDengue.Domain.Entities.Notification.Notification;
import com.arboviroses.conectaDengue.Domain.Entities.Notification.NotificationWithError;
import com.arboviroses.conectaDengue.Domain.Filters.NotificationFilters;
import com.arboviroses.conectaDengue.Domain.Repositories.Notifications.NotificationRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import static com.arboviroses.conectaDengue.Utils.ConvertCSVLineToNotifications.convertCsvLineToNotificationObject;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationsErrorService notificationsErrorService;
    @PersistenceContext
    private EntityManager entityManager;

    public SaveCsvResponseDTO saveNotificationsFromBatch(NotificationBatchDTO notificationBatchDTO) {
        List<Notification> notifications = new ArrayList<>();
        List<NotificationWithError> notificationsWithError = new ArrayList<>();
        Long currentIteration = notificationsErrorService.getLastIteration() + 1;
        int successCount = 0;
        int errorCount = 0;

        for (NotificationDataDTO dto : notificationBatchDTO.getNotifications()) {
            try {
                Notification notification = convertDtoToNotification(dto);
                
                if (notificationsErrorService.notificationHasError(notification)) {
                    NotificationWithError notificationWithError = NotificationWithError.builder()
                        .idNotification(notification.getIdNotification())
                        .idAgravo(notification.getIdAgravo())
                        .idadePaciente(notification.getIdadePaciente())
                        .dataNotification(notification.getDataNotification())
                        .dataNascimento(notification.getDataNascimento())
                        .classificacao(notification.getClassificacao())
                        .sexo(notification.getSexo())
                        .idBairro(notification.getIdBairro())
                        .nomeBairro(notification.getNomeBairro())
                        .evolucao(notification.getEvolucao())
                        .semanaEpidemiologica(notification.getSemanaEpidemiologica())
                        .iteration(currentIteration)
                        .build();

                    notificationsWithError.add(notificationWithError);
                    errorCount++;
                } 
                
                notifications.add(notification);
                successCount++;
            } catch (Exception e) {
                errorCount++;

                e.printStackTrace();
            }
        }

        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
        }
        
        if (!notificationsWithError.isEmpty()) {
            notificationsErrorService.insertListOfNotifications(notificationsWithError);
        }

        return new SaveCsvResponseDTO(true);
    }

    private Notification convertDtoToNotification(NotificationDataDTO dto) throws ParseException {
        Notification notification = new Notification();
        notification.setIdNotification(dto.getNuNotific());
        notification.setIdAgravo(dto.getIdAgravo());
        notification.setIdadePaciente(dto.getIdade());
        notification.setClassificacao(dto.getClassiFin());
        notification.setSexo(dto.getCsSexo());
        notification.setIdBairro(dto.getIdBairro());
        notification.setNomeBairro(dto.getNmBairro());
        notification.setEvolucao(dto.getEvolucao());

        Date dataNascimento = StringToDateCSV.ConvertStringToDate(dto.getDtNasc());
        Date dataNotification = StringToDateCSV.ConvertStringToDate(dto.getDtNotific());
        
        notification.setDataNascimento(dataNascimento);
        notification.setDataNotification(dataNotification);
        
        if (notification.getDataNotification() != null) {
            notification.setSemanaEpidemiologica(calculateSemanaEpidemiologica(notification.getDataNotification()));
        }
        
        return notification;
    }

    private Integer calculateSemanaEpidemiologica(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    public SaveCsvResponseDTO saveNotificationsData(MultipartFile file) throws IOException, CsvException, NumberFormatException, ParseException
    {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IOException("Nome do arquivo não pode ser nulo.");
        }
    
        if (fileName.endsWith(".csv")) {
            return processCSVFileStreaming(file);
        } else if (fileName.endsWith(".xlsx")) {
            return processXLSXFileStreaming(file);
        } else {
            throw new IOException("Tipo de arquivo não suportado: " + fileName);
        }
    }

    private SaveCsvResponseDTO processCSVFileStreaming(MultipartFile file) throws IOException, CsvException {
        Long currentIteration = notificationsErrorService.getLastIteration() + 1;
        
        int batchSize = MemoryOptimizationUtil.getDynamicBatchSize();
        MemoryOptimizationUtil.logMemoryStats("Início processamento CSV");
        
        List<Notification> notificationBatch = new ArrayList<>(batchSize);
        List<NotificationWithError> errorBatch = new ArrayList<>(batchSize);
        
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] headerArray = csvReader.readNext();
            if (headerArray == null) {
                throw new IOException("Arquivo CSV vazio ou inválido.");
            }
            
            List<String> header = Arrays.asList(headerArray);
            String[] line;
            int processedCount = 0;
            
            while ((line = csvReader.readNext()) != null) {
                try {
                    processCSVLine(line, header, notificationBatch, errorBatch, currentIteration);
                    processedCount++;
                    
                    if (MemoryOptimizationUtil.isMemoryLow() && batchSize > 10) {
                        batchSize = Math.max(10, batchSize / 2);
                        System.out.println("Memória baixa detectada. Reduzindo batch size para: " + batchSize);
                    }
                    
                    if (notificationBatch.size() >= batchSize) {
                        saveBatches(notificationBatch, errorBatch);
                        MemoryOptimizationUtil.clearAndTrimLists(notificationBatch, errorBatch);
                        
                        if (processedCount % 100 == 0) {
                            MemoryOptimizationUtil.logMemoryStats("Processados " + processedCount + " registros");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao processar linha do CSV: " + e.getMessage());
                }
            }
            
            if (!notificationBatch.isEmpty() || !errorBatch.isEmpty()) {
                saveBatches(notificationBatch, errorBatch);
            }
            
            MemoryOptimizationUtil.logMemoryStats("Fim processamento CSV - Total: " + processedCount);
        }
        
        return new SaveCsvResponseDTO(true);
    }

    private SaveCsvResponseDTO processXLSXFileStreaming(MultipartFile file) throws IOException {
        Long currentIteration = notificationsErrorService.getLastIteration() + 1;
        
        int batchSize = Math.min(MemoryOptimizationUtil.getDynamicBatchSize() / 2, 25);
        MemoryOptimizationUtil.logMemoryStats("Início processamento XLSX");
        
        List<Notification> notificationBatch = new ArrayList<>(batchSize);
        List<NotificationWithError> errorBatch = new ArrayList<>(batchSize);
        
        try (InputStream inputStream = file.getInputStream(); 
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            NeighborhoodsMossoro neighborhoods = new NeighborhoodsMossoro();

            if (!rowIterator.hasNext()) {
                throw new IOException("O arquivo está vazio.");
            }

            Row headerRow = rowIterator.next();
            List<String> header = new ArrayList<>();
            for (Cell cell : headerRow) {
                header.add(cell.getStringCellValue());
            }

            int processedCount = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    processXLSXRowWithValidation(row, header, evaluator, neighborhoods, 
                                               notificationBatch, errorBatch, currentIteration);
                    processedCount++;

                    if (MemoryOptimizationUtil.isMemoryLow() && batchSize > 5) {
                        batchSize = Math.max(5, batchSize / 2);
                        System.out.println("Memória baixa detectada. Reduzindo batch size XLSX para: " + batchSize);
                    }

                    if (notificationBatch.size() >= batchSize) {
                        saveBatches(notificationBatch, errorBatch);
                        MemoryOptimizationUtil.clearAndTrimLists(notificationBatch, errorBatch);
                        
                        if (processedCount % 50 == 0) {
                            MemoryOptimizationUtil.logMemoryStats("Processados " + processedCount + " registros XLSX");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao processar linha do XLSX: " + e.getMessage());
                }
            }

            // Processar registros restantes
            if (!notificationBatch.isEmpty() || !errorBatch.isEmpty()) {
                saveBatches(notificationBatch, errorBatch);
            }

            MemoryOptimizationUtil.logMemoryStats("Fim processamento XLSX - Total: " + processedCount);
            return new SaveCsvResponseDTO(true);
        }
    }

    private void processXLSXRowWithValidation(Row row, List<String> header, FormulaEvaluator evaluator,
                                            NeighborhoodsMossoro neighborhoods,
                                            List<Notification> notificationBatch,
                                            List<NotificationWithError> errorBatch,
                                            Long currentIteration) {
        try {
            String[] csvLine = new String[header.size()];

            for (int i = 0; i < header.size(); i++) {
                Cell cell = row.getCell(i);
                csvLine[i] = getCellValueAsString(cell, evaluator);
            }

            String neighborhoodFromNotification = csvLine[header.indexOf("NM_BAIRRO")];

            if ((neighborhoodFromNotification = neighborhoods.search(neighborhoodFromNotification)) != null) {
                csvLine[header.indexOf("NM_BAIRRO")] = neighborhoodFromNotification;
                Notification notification = convertCsvLineToNotificationObject(csvLine, header);
                
                if (notificationsErrorService.notificationHasError(notification)) {
                    NotificationWithError notificationWithError = NotificationWithError.builder()
                        .idNotification(notification.getIdNotification())
                        .idAgravo(notification.getIdAgravo())
                        .idadePaciente(notification.getIdadePaciente())
                        .dataNotification(notification.getDataNotification())
                        .dataNascimento(notification.getDataNascimento())
                        .classificacao(notification.getClassificacao())
                        .sexo(notification.getSexo())
                        .idBairro(notification.getIdBairro())
                        .nomeBairro(notification.getNomeBairro())
                        .evolucao(notification.getEvolucao())
                        .semanaEpidemiologica(notification.getSemanaEpidemiologica())
                        .iteration(currentIteration)
                        .build();
                    
                    errorBatch.add(notificationWithError);
                } else {
                    notificationBatch.add(notification);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar linha XLSX: " + e.getMessage());
        }
    }

    private String getCellValueAsString(Cell cell, FormulaEvaluator evaluator) {
        try {
            if (cell == null) {
                return "";
            }
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        return dateFormat.format(cell.getDateCellValue());
                    } else {
                        return String.valueOf(cell.getNumericCellValue());
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    CellValue cellValue = evaluator.evaluate(cell);
                    switch (cellValue.getCellType()) {
                        case STRING:
                            return cellValue.getStringValue();
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                return dateFormat.format(cell.getDateCellValue());
                            } else {
                                return String.valueOf(cellValue.getNumberValue());
                            }
                        case BOOLEAN:
                            return String.valueOf(cellValue.getBooleanValue());
                        default:
                            return "";
                    }
                default:
                    return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    private void processCSVLine(String[] csvLine, List<String> header, 
                               List<Notification> notificationBatch, 
                               List<NotificationWithError> errorBatch, 
                               Long currentIteration) {
        try {
            NeighborhoodsMossoro neighborhoods = new NeighborhoodsMossoro();
            String neighborhoodFromNotification = csvLine[header.indexOf("NM_BAIRRO")];
            
            if ((neighborhoodFromNotification = neighborhoods.search(neighborhoodFromNotification)) != null) {
                csvLine[header.indexOf("NM_BAIRRO")] = neighborhoodFromNotification;
                Notification notification = convertCsvLineToNotificationObject(csvLine, header);
                
                if (notificationsErrorService.notificationHasError(notification)) {
                    NotificationWithError notificationWithError = NotificationWithError.builder()
                        .idNotification(notification.getIdNotification())
                        .idAgravo(notification.getIdAgravo())
                        .idadePaciente(notification.getIdadePaciente())
                        .dataNotification(notification.getDataNotification())
                        .dataNascimento(notification.getDataNascimento())
                        .classificacao(notification.getClassificacao())
                        .sexo(notification.getSexo())
                        .idBairro(notification.getIdBairro())
                        .nomeBairro(notification.getNomeBairro())
                        .evolucao(notification.getEvolucao())
                        .semanaEpidemiologica(notification.getSemanaEpidemiologica())
                        .iteration(currentIteration)
                        .build();
                    
                    errorBatch.add(notificationWithError);
                } else {
                    notificationBatch.add(notification);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar linha: " + e.getMessage());
        }
    }

    private void saveBatches(List<Notification> notificationBatch, List<NotificationWithError> errorBatch) {
        try {
            if (!notificationBatch.isEmpty()) {
                notificationRepository.saveAll(notificationBatch);
                notificationRepository.flush();
            }
            
            if (!errorBatch.isEmpty()) {
                notificationsErrorService.insertListOfNotifications(errorBatch);
            }
        } catch (Exception e) {
            System.err.println("Erro ao salvar batch: " + e.getMessage());
            throw e;
        }
    }

 

    public Page<DataNotificationResponseDTO> getAllNotificationsPaginated(Pageable pageable)
    {
        Page<Notification> notifications = notificationRepository.findAll(pageable);
        return notifications.map(DataNotificationResponseDTO::new);
    }

    public Page<DataNotificationResponseDTO> getNotificationsByIdAgravoPaginated(Pageable pageable, HttpServletRequest request) throws InvalidAgravoException
    {
        if (request.getParameter("agravo") == null) {
            return getAllNotificationsPaginated(pageable);
        }
        
        String agravo = ConvertNameToIdAgravo.convert(request.getParameter("agravo"));

        Page<Notification> notifications = notificationRepository.findByIdAgravo(pageable, agravo);
        return notifications.map(DataNotificationResponseDTO::new);
    }

    public CountAgravoBySexoDTO getNotificationsInfoBySexo(HttpServletRequest request) throws InvalidAgravoException 
    {
        return NotificationFilters.filtersForNotificationsInfoBySexo(request, notificationRepository);
    }

    public AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse countNotificationsBySemanaEpidemiologica(HttpServletRequest request) throws InvalidAgravoException
    {
        return new AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse(NotificationFilters.filtersForNotificationsInfoBySemanaEpidemiologica(request, notificationRepository)); 
    }

    public AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse countNotificationsBySemanaEpidemiologicaAccumulated(HttpServletRequest request) throws InvalidAgravoException
    {
        return new AgravoCountByEpidemiologicalSemanaEpidemiologicaResponse(NotificationFilters.filtersForNotificationsInfoBySemanaEpidemiologica(request, notificationRepository), true); 
    }

    public AgravoCountByAgeRange getNotificationsCountByAgeRange(HttpServletRequest request) throws InvalidAgravoException {
        return new AgravoCountByAgeRange(NotificationFilters.filtersForNotificationsByAgeRange(request, this.entityManager));
    }

    public List<BairroCountDTO> getBairroCount(HttpServletRequest request) throws InvalidAgravoException 
    {   
        return NotificationFilters.filtersForNotificationsCountNeighborhoods(request, notificationRepository);
    }

    public long countByEvolucao(HttpServletRequest request) throws Exception {
        return NotificationFilters.filterForCountByEvolucao(request, notificationRepository);
    }

    public long countByIdAgravo(HttpServletRequest request) throws Exception {
        return NotificationFilters.filterForCountByIdAgravo(request, notificationRepository);
    }

    public Map<Integer, Map<Integer, Long>> getNotificationCountsByYear(List<Integer> years) {
        Map<Integer, Map<Integer, Long>> result = new HashMap<>();

        for (Integer year : years) {
            List<Notification> notificationsForYear = notificationRepository.findByYearAndIdAgravo(year, "A90");
            Map<Integer, Long> notificationsByMonth = new HashMap<>();

            for (Notification notification : notificationsForYear) {
                int month = getMonth(notification.getDataNotification());
                notificationsByMonth.merge(month, 1L, Long::sum);
            }

            result.put(year, notificationsByMonth);
        }

        return result;
    }

    private int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }
}

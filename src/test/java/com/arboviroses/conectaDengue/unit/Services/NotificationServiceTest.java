package com.arboviroses.conectaDengue.unit.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import java.util.List;
import com.arboviroses.conectaDengue.Entities.DTO.response.SaveCsvResponseDTO;
import com.arboviroses.conectaDengue.Entities.Notification.Notification;
import com.arboviroses.conectaDengue.Exceptions.InvalidDateStringException;
import com.arboviroses.conectaDengue.Repositories.Notifications.NotificationRepository;
import com.arboviroses.conectaDengue.Services.Notifications.NotificationService;
import com.opencsv.exceptions.CsvException;


@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    public void test_if_save_csv_in_database_is_returning_sucess() throws ParseException, NumberFormatException, IOException, CsvException, InvalidDateStringException
    {   
        String csvContent = "NU_NOTIFIC,ID_AGRAVO,DT_NOTIFIC,DT_NASC,CLASSI_FIN,CS_SEXO,NM_BAIRRO,EVOLUCAO\n1,A90,10/06/2022,10/06/2003,1,F,ABOLICAO,1";
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        SaveCsvResponseDTO responseDTO = notificationService.saveCSVDataInDatabase(mockMultipartFile);

        Assertions.assertThat(responseDTO).isNotNull();
        assertEquals("sucesso", responseDTO.getData());
        assertEquals("dados do csv salvos com sucesso", responseDTO.getMessage());
    }

    @Test
    public void test_if_save_csv_in_database_is_using_the_method_saveAll() throws NumberFormatException, IOException, CsvException, ParseException, InvalidDateStringException
    {
        String csvContent = "NU_NOTIFIC,ID_AGRAVO,DT_NOTIFIC,DT_NASC,CLASSI_FIN,CS_SEXO,NM_BAIRRO,EVOLUCAO\n1,A90,10/06/2022,10/06/2003,1,F,ABOLICAO,1";
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());
        ArgumentCaptor<List<Notification>> notificationListCaptor = ArgumentCaptor.forClass(List.class);
        when(notificationRepository.saveAll(notificationListCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        SaveCsvResponseDTO responseDTO = notificationService.saveCSVDataInDatabase(mockMultipartFile);

        verify(notificationRepository, times(1)).saveAll(anyList());
    }
}
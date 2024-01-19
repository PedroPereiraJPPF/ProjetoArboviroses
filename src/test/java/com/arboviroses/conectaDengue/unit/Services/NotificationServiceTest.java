package com.arboviroses.conectaDengue.unit.Services;

import java.io.IOException;
import java.text.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.arboviroses.conectaDengue.Exceptions.InvalidDateStringException;
import com.arboviroses.conectaDengue.Repositories.Notifications.NotificationRepository;
import com.arboviroses.conectaDengue.Services.Notifications.NotificationService;
import com.opencsv.exceptions.CsvException;


@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @InjectMocks
    private NotificationService notificationService;
    @Mock
    private NotificationRepository notificationRepository;

    @Test
    public void test_if_save_csv_in_database_is_saving_the_correct_data() throws ParseException, NumberFormatException, IOException, CsvException, InvalidDateStringException
    {   

    }
}
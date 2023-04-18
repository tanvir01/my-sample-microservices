package com.tanservices.notification.kafka;


import com.tanservices.notification.FetchUserInfoService;
import com.tanservices.notification.openfeign.User;
import com.tanservices.notification.security.JwtContextHolder;
import com.tanservices.shipment.kafka.NotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

@Slf4j
@Component
public class KafkaConsumer {

    private final FetchUserInfoService fetchUserInfoService;

    public KafkaConsumer(FetchUserInfoService fetchUserInfoService) {
        this.fetchUserInfoService = fetchUserInfoService;
    }

    @KafkaListener(topics = "${spring.kafka.notification-topic}", groupId = "${spring.kafka.consumer-group}", containerFactory = "concurrentKafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, NotificationDto> consumerRecord)
    {
        log.info("key = " + consumerRecord.key() + " value = " + consumerRecord.value());

        DateTimeFormatter parseFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");
        LocalDateTime dateTime = LocalDateTime.parse(consumerRecord.key(), parseFormatter);

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        String formattedDateTime = dateTime.format(formatter);

        NotificationDto notificationDto = consumerRecord.value();

        //send notification
        String notificationMsg = "Notification for OrderId: " + notificationDto.orderId() +
                                " ShipmentId: " + notificationDto.shipmentId() +
                                ". Message: " + notificationDto.message() +
                                " at " + formattedDateTime;

        // fetch user info from auth provider ms
        Optional<User> user = fetchUserInfoService.getUserInfo(notificationDto.token());
        if(user.isEmpty()) {
            log.info("Failed to fetch User Info. Failed to send Notification: "+ notificationMsg);
        }

        log.info("Notification: "+ notificationMsg + " Sent To: " + user.get().email());
    }
}

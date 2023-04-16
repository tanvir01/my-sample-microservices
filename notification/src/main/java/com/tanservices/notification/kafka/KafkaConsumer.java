package com.tanservices.notification.kafka;


import com.tanservices.shipment.kafka.NotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Slf4j
@Component
public class KafkaConsumer {

    @KafkaListener(topics = "${spring.kafka.notification-topic}", groupId = "${spring.kafka.consumer-group}", containerFactory = "concurrentKafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, NotificationDto> consumerRecord)
    {
        log.info("key = " + consumerRecord.key() + " value = " + consumerRecord.value());

        DateTimeFormatter parseFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");
        LocalDateTime dateTime = LocalDateTime.parse(consumerRecord.key(), parseFormatter);

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        String formattedDateTime = dateTime.format(formatter);

        NotificationDto notificationDto = consumerRecord.value();

        // fetch user info from auth provider ms

        //send notification
        String notificationMsg = "Notification for OrderId: " + notificationDto.orderId() +
                                " ShipmentId: " + notificationDto.shipmentId() +
                                ". Message: " + notificationDto.message() +
                                " at " + formattedDateTime;
        log.info("Notification: "+ notificationMsg + " Sent To: " + notificationDto.userId());
        System.out.println("Notification: "+ notificationMsg + " Sent To: " + notificationDto.userId());
    }
}

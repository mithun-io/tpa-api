package com.tpa.mapper;

import com.tpa.dto.response.NotificationResponse;
import com.tpa.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

    NotificationResponse toNotificationResponse(Notification notification);

    List<NotificationResponse> toNotificationResponses(List<Notification> notifications);
}

package com.tpa.mapper;

import com.tpa.dto.response.auth.NotificationResponse;
import com.tpa.entity.Notification;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-17T09:13:20+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationResponse toNotificationResponse(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationResponse.NotificationResponseBuilder notificationResponse = NotificationResponse.builder();

        notificationResponse.createdAt( notification.getCreatedAt() );
        notificationResponse.id( notification.getId() );
        notificationResponse.message( notification.getMessage() );
        notificationResponse.read( notification.isRead() );
        notificationResponse.targetUrl( notification.getTargetUrl() );
        notificationResponse.title( notification.getTitle() );

        return notificationResponse.build();
    }

    @Override
    public List<NotificationResponse> toNotificationResponses(List<Notification> notifications) {
        if ( notifications == null ) {
            return null;
        }

        List<NotificationResponse> list = new ArrayList<NotificationResponse>( notifications.size() );
        for ( Notification notification : notifications ) {
            list.add( toNotificationResponse( notification ) );
        }

        return list;
    }
}

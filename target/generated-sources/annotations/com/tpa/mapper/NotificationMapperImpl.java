package com.tpa.mapper;

import com.tpa.dto.response.auth.NotificationResponse;
import com.tpa.entity.Notification;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-29T16:27:11+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationResponse toNotificationResponse(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationResponse.NotificationResponseBuilder notificationResponse = NotificationResponse.builder();

        notificationResponse.id( notification.getId() );
        notificationResponse.title( notification.getTitle() );
        notificationResponse.message( notification.getMessage() );
        notificationResponse.read( notification.isRead() );
        notificationResponse.createdAt( notification.getCreatedAt() );
        notificationResponse.targetUrl( notification.getTargetUrl() );

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

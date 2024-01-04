package com.arboviroses.conectaDengue.Repositories.Notifications;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arboviroses.conectaDengue.Entities.Notification.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> 
{

}

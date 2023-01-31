package com.nexusinfinity.electronicsportal.notification.model

data class PushNotification(
    val data: NotificationData,
    val to: String
)
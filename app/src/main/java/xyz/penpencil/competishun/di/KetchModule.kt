package xyz.penpencil.competishun.di

import android.app.NotificationManager
import android.content.Context
import com.ketch.Ketch
import com.ketch.NotificationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import xyz.penpencil.competishun.R
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KetchModule {

    @Provides
    @Singleton
    fun provideNotificationConfig(
        @ApplicationContext context: Context
    ): NotificationConfig {
        return NotificationConfig(
            enabled = true,
            channelName = "File Download",
            channelDescription = "Notify file download status",
            importance = NotificationManager.IMPORTANCE_HIGH,
            smallIcon = R.drawable.ic_launcher_foreground,
            showSpeed = true,
            showSize = true,
            showTime = true
        )
    }

    @Provides
    @Singleton
    fun provideKetch(
        @ApplicationContext context: Context,
        notificationConfig: NotificationConfig
    ): Ketch {
        return Ketch.builder()
            .setNotificationConfig(notificationConfig)
            .build(context)
    }
}

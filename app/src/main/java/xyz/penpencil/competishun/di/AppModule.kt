package xyz.penpencil.competishun.di

import android.content.Context
import com.ketch.Ketch
import com.ketch.NotificationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import xyz.penpencil.competishun.AppController
import xyz.penpencil.competishun.R
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppController(): AppController {
        return AppController()
    }

    @Provides
    @Singleton
    fun provideKetch(@ApplicationContext context: Context): Ketch {
        return Ketch.builder().setNotificationConfig(
            config = NotificationConfig(
                enabled = true,
                smallIcon = R.drawable.ic_launcher_foreground
            )
        ).build(context)
    }
}
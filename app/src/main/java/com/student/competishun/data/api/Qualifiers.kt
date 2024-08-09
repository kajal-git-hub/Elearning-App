package com.student.competishun.data.api


import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Gatekeeper

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class Curator


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Coinkeeper

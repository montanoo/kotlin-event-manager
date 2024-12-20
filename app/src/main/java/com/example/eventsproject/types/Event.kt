package com.example.eventsproject.types

data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val location: String,
    val price: Double,
    val stock: Int,
    val organizerId: Int,
    val organizer: Organizer?,
    val ratings: List<Rating>?,
    val isAttendee: Boolean,
    val comments: List<Comment>?,
)

data class Rating(
    val id: Int,
    val rating: Int,
    val comment: String?,
    val date: String,
    val userId: Int,
    val eventId: Int
)

data class Organizer(
    val id: Int,
    val username: String,
    val email: String,
    val createdAt: String,
    val updatedAt: String
)

data class Comment(
    val id: Int,
    val content: String,
    val date: String,
    val userId: Int,
    val eventId: Int,
    val user: User
)

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val createdAt: String,
    val updatedAt: String
)

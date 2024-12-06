package com.example.eventsproject.network

import com.example.eventsproject.types.Comment
import com.example.eventsproject.types.Event
import com.example.eventsproject.types.Rating
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class LoginRequest(val email: String, val password: String)

data class SignUpRequest(val username: String, val email: String, val password: String)

data class LoginResponse(
    val token: String,
    val user: User
)

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val createdAt: String,
    val updatedAt: String
)

data class SignUpResponse(
    val token: String,  // The JWT token
    val user: User      // Nested user object
)

data class AttendanceResponse(
    val id: Int,
    val userId: Int,
    val eventId: Int,
    val attendance: Boolean,
    val notifications: Boolean
)

data class ConfirmAttendanceRequest(val eventId: Int)

data class AddCommentRequest(
    val content: String,
    val eventId: Int
)

data class EventRequest(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val time: String,
    val price: Double,
    val stock: Int
)

data class RatingsRequest(
    val eventId: Int,
    val rating: Int,
)

data class RatingResponse(
    val id: Int,
    val rating: Int,
    val comment: String?,
    val date: String,
    val userId: Int,
    val eventId: Int
)

interface ApiService {
    @POST("user/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("user/register")
    suspend fun signUpUser(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @GET("event/all?time=future")
    suspend fun getComingSoonEvents(): Response<List<Event>>

    @GET("event/all?time=past")
    suspend fun getHistoryEvents(): Response<List<Event>>

    @GET("event/{id}")
    suspend fun getEventById(@Path("id") id: Int): Response<Event>

    @POST("attendance/create")
    suspend fun confirmAttendance(@Body confirmAttendanceRequest: ConfirmAttendanceRequest): Response<AttendanceResponse>

    @GET("event/{id}")
    suspend fun getEventDetails(@Path("id") eventId: Int): Response<Event>

    @POST("comments/create")
    suspend fun addComment(@Body request: AddCommentRequest): Response<Comment>

    @POST("event/update")
    suspend fun updateEvent(@Body event: EventRequest): Response<Unit>

    @POST("event/create")
    suspend fun createEvent(@Body eventRequest: EventRequest): Response<Unit>

    @POST("ratings/create")
    suspend fun submitRating(@Body ratingsRequest: RatingsRequest): Response<Rating>
}

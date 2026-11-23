package com.irmex.admin.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/** Contrato del backend FastAPI existente (no se modifica). */
interface IrmexApi {

    @POST("api/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("api/tarjetas")
    suspend fun tarjetas(): List<TarjetaDto>

    @GET("api/admin/tickets")
    suspend fun tickets(): List<TicketDto>

    @PUT("api/admin/tickets/{id}/responder")
    suspend fun responderTicket(@Path("id") id: Int, @Body body: RespuestaTicketRequest)

    @PUT("api/admin/tickets/{id}/acciones")
    suspend fun accionesTicket(
        @Path("id") id: Int,
        @Query("importante") importante: Boolean? = null,
        @Query("revisado") revisado: Boolean? = null,
    )

    @GET("api/renovaciones/pendientes")
    suspend fun renovacionesPendientes(): List<RenovacionDto>

    @PUT("api/renovaciones/{id}")
    suspend fun procesarRenovacion(@Path("id") id: Int, @Body body: EstatusRenovacionRequest)

    @GET("api/reportes/general")
    suspend fun resumenGeneral(): ResumenDto
}

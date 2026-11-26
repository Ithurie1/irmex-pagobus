package com.irmex.admin.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
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

    @POST("api/tarjetas")
    suspend fun crearTarjeta(@Body body: TarjetaCrearRequest)

    @PUT("api/tarjetas/{id}/vigencia")
    suspend fun ajustarVigencia(@Path("id") id: Int, @Body body: VigenciaRequest)

    @PUT("api/tarjetas/{id}/recarga-manual")
    suspend fun recargaManual(@Path("id") id: Int, @Body body: RecargaManualRequest)

    @PUT("api/tarjetas/{id}/estatus-seguro")
    suspend fun alternarEstatusTarjeta(@Path("id") id: Int, @Body body: AccionSeguraRequest)

    // DELETE con cuerpo: Retrofit lo exige declarar así.
    @HTTP(method = "DELETE", path = "api/tarjetas/{id}/borrar-seguro", hasBody = true)
    suspend fun borrarTarjeta(@Path("id") id: Int, @Body body: AccionSeguraRequest)

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

    @GET("api/usuarios_completos")
    suspend fun usuarios(): List<UsuarioDto>

    @DELETE("api/usuarios/{id}")
    suspend fun darDeBajaUsuario(@Path("id") id: Int)

    @PUT("api/usuarios/{id}/rol")
    suspend fun cambiarRol(@Path("id") id: Int, @Body body: CambioRolRequest)

    @GET("api/reportes/general")
    suspend fun resumenGeneral(): ResumenDto
}

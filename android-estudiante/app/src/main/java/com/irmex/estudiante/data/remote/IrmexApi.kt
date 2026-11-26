package com.irmex.estudiante.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

/** Contrato del backend FastAPI existente (no se modifica). */
interface IrmexApi {

    @POST("api/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("api/registro")
    suspend fun registrar(@Body body: RegistroRequest)

    @POST("api/recuperar-password")
    suspend fun recuperarPassword(@Body body: RecuperarRequest)

    @GET("api/mis-tarjetas")
    suspend fun misTarjetas(): List<TarjetaDto>

    @POST("api/recargar")
    suspend fun recargar(@Body body: RecargaRequest)

    @GET("api/historial/{tarjetaId}")
    suspend fun historial(@Path("tarjetaId") tarjetaId: Int): List<MovimientoDto>

    @GET("api/soporte/mis-tickets")
    suspend fun misReportes(): List<ReporteDto>

    @Multipart
    @POST("api/soporte/ticket")
    suspend fun crearReporte(
        @Part("asunto") asunto: RequestBody,
        @Part("mensaje") mensaje: RequestBody,
        @Part evidencia: MultipartBody.Part?,
    )

    @Multipart
    @POST("api/solicitar-renovacion")
    suspend fun solicitarRenovacion(@Part archivo: MultipartBody.Part)

    @GET("api/perfil/{correo}")
    suspend fun perfil(@Path("correo") correo: String): PerfilDto

    @PUT("api/perfil/{correo}")
    suspend fun actualizarPerfil(@Path("correo") correo: String, @Body body: PerfilUpdateRequest)
}

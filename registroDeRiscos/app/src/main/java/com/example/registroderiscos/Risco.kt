data class Risco(
    var id: String? = null,
    val descricao: String = "",
    val foto: String = "",
    val localizacao: String = "",
    val nivel_risco: String = "",
    val tipo_risco: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val data: String = "" // <-- Adicione este campo
)
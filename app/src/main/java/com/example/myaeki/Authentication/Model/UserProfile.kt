data class UserProfile(
    val user_id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val created_at: String,
    val address: String?,
    val postal_code: String?,
    val phone_number: String?,
    val wallet: Double 
)

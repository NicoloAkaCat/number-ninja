package dev.nicoloakacat.numberninja.db

data class UserData(
    var maxScore: Int? = null,
    val nationality: String? = null,
    val name: String? = null,
    var nBetterPlayers: Long? = null
)

package com.degradators.data.degradators.database.user.model.user

data class UserEntity(
    var id: String? = "",
    var mail: String? = "",
    var password: String? = "",
    var username: String? = "",
    var login: String? = "",
    var photo: String? = "",
    var markList: List<MarkListEntity>? = emptyList()
)
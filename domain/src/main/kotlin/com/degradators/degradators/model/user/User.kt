package com.degradators.degradators.model.user

data class User(
    var id: String = "",
    var mail: String = "",
    var password: String = "",
    var username: String = "",
    var login: String = "",
    var photo: String = "",
    var markList: List<MarkList> = emptyList()
)
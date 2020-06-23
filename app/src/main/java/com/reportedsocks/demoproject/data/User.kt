package com.reportedsocks.demoproject.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/** User model from github
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "login") val login: String,
    @ColumnInfo(name = "avatarUrl") @SerializedName("avatar_url") val avatarUrl: String,
    @ColumnInfo(name = "htmlUrl") @SerializedName("html_url") val htmlUrl: String,
    @ColumnInfo(name = "reposUrl") @SerializedName("repos_url") val reposUrl: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "siteAdmin") @SerializedName("site_admin") val siteAdmin: Boolean
) {
    override fun equals(other: Any?): Boolean {
        return other is User && other.id == id && other.login == login && other.avatarUrl == avatarUrl && other.htmlUrl == htmlUrl && other.reposUrl == reposUrl && other.type == type && other.siteAdmin == siteAdmin
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "User(id=$id, login=$login, type=$type)"
    }
}
package com.reportedsocks.demoproject.data.source

import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User

class FakeDataSource(private val initialUsers: List<User>) : DataSource {

    private val pageSize = 2

    private val savedUsers = mutableSetOf<User>()

    override suspend fun getUsers(id: Int): Result<List<User>> {
        val result = mutableListOf<User>()
        val users = if (initialUsers.isEmpty()) {
            savedUsers
        } else {
            initialUsers
        }
        for (user in users) {
            if (user.id > id && result.size < pageSize) {
                result.add(user)
            }
        }
        return Result.Success(result)
    }

    override suspend fun getUser(id: Int): Result<User> {
        for (user in savedUsers) {
            if (user.id == id) {
                return Result.Success(user)
            }
        }
        return Result.Error(Exception("No such user"))
    }

    override fun getUsersSync(id: Int): Result<List<User>> {
        val result = mutableListOf<User>()
        val users = if (initialUsers.isEmpty()) {
            savedUsers
        } else {
            initialUsers
        }
        for (user in users) {
            if (user.id > id && result.size < pageSize) {
                result.add(user)
            }
        }
        return Result.Success(result)
    }

    override fun getAllUsersSync(id: Int): Result<List<User>> {
        return Result.Success(savedUsers.toList())
    }

    override suspend fun getAllUsers(id: Int): Result<List<User>> {
        return Result.Success(savedUsers.toList())

    }

    override fun getAllUsersWithIdSmallerSync(id: Int): Result<List<User>> {
        val result = mutableListOf<User>()
        for (user in savedUsers) {
            if (user.id <= id) {
                result.add(user)
            }
        }
        return Result.Success(result)
    }

    override suspend fun saveUser(user: User) {
        savedUsers.add(user)
    }

    override fun saveUserSync(user: User) {
        savedUsers.add(user)
    }
}
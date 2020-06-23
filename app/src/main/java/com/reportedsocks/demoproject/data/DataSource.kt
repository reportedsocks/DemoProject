package com.reportedsocks.demoproject.data

/**
 * DataSource classes implement this interface
 */
interface DataSource {
    /**
     * Get next page of users starting with next user after specified id
     */
    suspend fun getUsers(id: Int): Result<List<User>>

    /**
     * Get one user by id
     */
    suspend fun getUser(id: Int): Result<User>

    /**
     * Synchronously get next page of users starting with next user after specified id
     */
    fun getUsersSync(id: Int): Result<List<User>>

    /**
     * Synchronously get all users available
     */
    fun getAllUsersSync(id: Int): Result<List<User>>

    /**
     * Synchronously get all users that stand before the specified id
     */
    fun getAllUsersWithIdSmallerSync(id: Int): Result<List<User>>

    /**
     * Save a given user in DataSource
     */
    suspend fun saveUser(user: User)

    /**
     * Synchronously save a given user in DataSource
     */
    fun saveUserSync(user: User)
}
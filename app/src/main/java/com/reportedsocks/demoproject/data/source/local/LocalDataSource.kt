package com.reportedsocks.demoproject.data.source.local

import com.reportedsocks.demoproject.data.DataSource
import com.reportedsocks.demoproject.data.Result
import com.reportedsocks.demoproject.data.User
import com.reportedsocks.demoproject.ui.util.PAGE_SIZE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Represent database and implements DataSource
 * @see DataSource
 */
@Singleton
class LocalDataSource @Inject constructor(
    private val usersDao: UsersDao
) : DataSource {

    /**
     * By default will use IO dispatcher to perform operations,
     * other dispatcher can be specified in constructor
     */
    constructor(usersDao: UsersDao, dispatcher: CoroutineDispatcher) : this(usersDao) {
        this.dispatcher = dispatcher
    }

    private var dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getUsers(id: Int): Result<List<User>> {
        return withContext(dispatcher) {
            try {
                Result.Success(usersDao.getUsers(id, PAGE_SIZE))
            } catch (e: Exception) {
                Result.Error(e, false)
            }
        }
    }

    override fun getUsersSync(id: Int): Result<List<User>> {
        return try {
            Result.Success(usersDao.getUsersSync(id, PAGE_SIZE))
        } catch (e: Exception) {
            Result.Error(e, false)
        }

    }

    override suspend fun getUser(id: Int): Result<User> {
        return withContext(dispatcher) {
            try {
                Result.Success(usersDao.getUser(id))
            } catch (e: java.lang.Exception) {
                Result.Error(e, false)
            }
        }
    }

    override fun getAllUsersSync(id: Int): Result<List<User>> {
        return try {
            Result.Success(usersDao.getAllUsersSync(id))
        } catch (e: Exception) {
            Result.Error(e, false)
        }

    }

    override fun getAllUsersWithIdSmallerSync(id: Int): Result<List<User>> {
        return try {
            Result.Success(usersDao.getAllUsersWithIdSmallerSync(id))
        } catch (e: Exception) {
            Result.Error(e, false)
        }

    }


    override suspend fun saveUser(user: User) {
        withContext(dispatcher) {
            usersDao.insertUser(user)
        }
    }

    override fun saveUserSync(user: User) {
        usersDao.insertUserSync(user)
    }

}
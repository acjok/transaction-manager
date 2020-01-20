package service

import dto.UserDto
import exceptions.UserAlreadyExistsException
import exceptions.UserNotFoundException
import model.User
import storage.Storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService {

    @Inject
    private lateinit var storage: Storage

    fun findById(id: Long): User =
        storage.findUserById(id) ?: throw UserNotFoundException("User with id: $id was not found.")

    private fun exists(email: String): Boolean = storage.existsByEmail(email)


    fun createUser(userDto: UserDto): User {
        if (!exists(userDto.email))
            return storage.createUser(User(userDto.email))
        else
            throw UserAlreadyExistsException("User with email: ${userDto.email} already exists.")
    }

    fun findAll(): List<User> = storage.findAllUsers()
}
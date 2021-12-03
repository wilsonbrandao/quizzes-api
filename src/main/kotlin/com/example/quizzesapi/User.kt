package com.example.quizzesapi

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "users")
data class User (
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val email: String,
    val active: Boolean
)

@Repository
interface UserRepository: JpaRepository<User, UUID>{
    fun existsByName(name: String): Boolean
    fun existsByNameAndIdNot(name: String, id: UUID): Boolean
}

data class UserReq(
    @field:[NotNull NotBlank Size(min = 3, max = 15)]
    val name: String,
    @field:[Email NotNull NotBlank Size(min = 3, max = 30)]
    val email: String,
    val active: Boolean = true
)

@RestController
@RequestMapping("user")
class UserController(val UserRepository: UserRepository){

    @GetMapping
    fun index() = ResponseEntity.ok(UserRepository.findAll())

    @PostMapping
    fun create (@Valid @RequestBody UserReq: UserReq): ResponseEntity<User>{
        if(UserRepository.existsByName(UserReq.name))
            throw ResponseStatusException(HttpStatus.CONFLICT, "User already exists")

        val user = User(
            name = UserReq.name,
            email = UserReq.email,
            active = UserReq.active
        )

        UserRepository.save(user)
        return ResponseEntity(user, HttpStatus.CREATED)
    }

    @GetMapping("{id}")
    fun show(@PathVariable id: UUID): ResponseEntity<User> {
        val user = getUser(id)

        return ResponseEntity.ok(user)
    }

    @PutMapping("{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody UserReq: UserReq): ResponseEntity<User>{
        if(UserRepository.existsByNameAndIdNot(UserReq.name, id))
            throw ResponseStatusException(HttpStatus.CONFLICT, "User already exists")

        val user = getUser(id)

        val updateUser = user.copy(
            name = UserReq.name,
            email = UserReq.email,
            active = UserReq.active
        )
        UserRepository.save(updateUser)
        return ResponseEntity.ok(updateUser)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Question>{
        val user = getUser(id)
        UserRepository.delete(user)
        return ResponseEntity.noContent().build()
    }

    private fun getUser(id: UUID): User{
        return UserRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
    }
}

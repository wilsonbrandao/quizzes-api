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
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
data class Quiz (
    @Id
    val id: UUID = UUID.randomUUID(),
    @ManyToMany
    val question: List<Question>,
    @ManyToOne
    val user: User
)
@Repository
interface QuizRepository: JpaRepository<Quiz, UUID>{
    //fun findAllById(id: List<UUID>): List<Question>
    fun findAllByUserId(userID: UUID): List<Quiz>
}


data class QuizReq(
   @field:[NotBlank NotNull Size(min = 1)]
    val QuestionIDs: List<UUID>,
    @field:[NotBlank NotNull]
    val userID: UUID
)

@RestController
class QuizzesController(
    val quizRepository: QuizRepository,
    val QuestionRepository: QuestionRepository,
    val UserRepository: UserRepository
){

    @GetMapping("{quizzes}")
    fun index() = ResponseEntity.ok(quizRepository.findAll())

    @PostMapping("{quizzes}")
    fun create (@Valid @RequestBody quizReq: QuizReq): ResponseEntity<Quiz>{
        val questions = QuestionRepository.findAllById(quizReq.QuestionIDs)
            //?: throw  ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found")

        val user = UserRepository.findByIdOrNull(quizReq.userID)
            ?: throw  ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        val quiz = Quiz (
            question = questions,
            user = user
        )
        quizRepository.save(quiz)
        return ResponseEntity(quiz, HttpStatus.CREATED)
    }

    @GetMapping("/quizzes/{id}")
    fun show(@PathVariable id: UUID) = ResponseEntity.ok(getQuiz(id))

    @GetMapping("/users/{id}/quizzes")
    fun getQuizByUser(@PathVariable userID: UUID): ResponseEntity<List<Quiz>>{
        val user = UserRepository.findByIdOrNull(userID)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        val quizzes = quizRepository.findAllByUserId(userID)
        return ResponseEntity.ok(quizzes)
    }

    private fun getQuiz(id: UUID): Quiz{
        return quizRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found")
    }
}



package com.example.quizzesapi

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
// import org.springframework.web.bind.annotation.RequestMethod
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

//cria categoria
data class Category(val id: UUID = UUID.randomUUID(), val name: String)

//cria lista de categorias
val categories = mutableListOf<Category>()

//cria obj categoria através do imput
data class CategoryReq(
    @field:[NotNull NotBlank Size(min = 3, max = 20)]
    val name: String
)

//cria rotas para categories
@RestController
@RequestMapping("categories") //mapeia /categories para CategoryController
class CategoryController{
    //
    //@RequestMapping(method = [RequestMethod.GET]) //se o método em categories for GET
    @GetMapping //RequestMapping para GET
    fun index() = ResponseEntity.ok(categories) //devolve uma lista mutavel de categorias

    @PostMapping
    fun create(@Valid @RequestBody categoryReq: CategoryReq): ResponseEntity<Category> {
        if(categories.any { it.name == categoryReq.name })
            throw ResponseStatusException(HttpStatus.CONFLICT, "Category already exists")

        val category = Category(name = categoryReq.name)
        categories.add(category)
        return ResponseEntity(category, HttpStatus.CREATED)
    }

    @GetMapping("{id}")
    fun show(@PathVariable id: UUID): ResponseEntity<Category> {
        val category = getCategory(id)

        return ResponseEntity.ok(category)
    }

    @PutMapping("{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody categoryReq: CategoryReq): ResponseEntity<Category> {
    //        val category = categories.first {it. id == id}
    //        category.name = categoryReq.name
    //        return category

        //acha o obj
        val category = getCategory(id)

        //recebe o obj passado
        val updateCategory = category.copy(name = categoryReq.name)
        categories.remove(category)
        categories.add(updateCategory)
        return ResponseEntity.ok(updateCategory)

    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Category>{
        val category = getCategory(id)

        categories.remove(category)
        return ResponseEntity.noContent().build()
    }

    private fun getCategory(id: UUID): Category{
        return categories.firstOrNull {it.id == id}
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "category not found")
    }

    }

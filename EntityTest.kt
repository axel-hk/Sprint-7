package com.example.demo

import com.example.demo.persistance.Entity
import com.example.demo.persistance.EntityRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
internal class EntityTest {

    @Autowired
    private lateinit var entityRepository: EntityRepository

    @Test
    fun `findById should find entity`() {
        // given
        val savedEntity = entityRepository.save(Entity(name = "Axel"))

        // when
        val foundEntity = entityRepository.findById(savedEntity.id!!)

        // then
        assertTrue { foundEntity.get() == savedEntity }
    }
}
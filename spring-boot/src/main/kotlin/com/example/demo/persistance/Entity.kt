package com.example.demo.persistance
import javax.persistence.*
import javax.persistence.Entity

@Entity
class Entity{
    @Id
    @GeneratedValue
    var id: Long? = null

    @Column
    var name: String? = null
}
package ru.sber.rdbms

class MyCustomException(val messege: String): Exception(messege) {
}
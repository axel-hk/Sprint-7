package ru.sber.rdbms

import java.sql.DriverManager
import java.sql.SQLException

class TransferOptimisticLock {
    val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/db",
        "postgres",
        "1111"
    )

    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {


        connection.use { conn ->
            val autoCommit = conn.autoCommit
            try {
                conn.autoCommit = false
                val prepareStatement1 = conn.prepareStatement("select amount from account1 where id = ${accountId1} for update")
                prepareStatement1.use { statement ->
                   val result =  statement.executeUpdate()
                    if(result<amount) throw MyCustomException("На счету не хватает средств")
                }
                val prepareStatement2 = conn.prepareStatement("select amount from account1 where id = ${accountId2} for update")
                prepareStatement2.use { statement ->
                    statement.executeUpdate()
                }
                val prepareStatement3 = conn.prepareStatement("update account1 set amount = amount - $amount where id = ${accountId1}")
                prepareStatement3.use { statement ->
                    statement.executeQuery()
                }
                val prepareStatement4 = conn.prepareStatement("update account1 set amount = amount + $amount where id = ${accountId2}")
                prepareStatement4.use { statement ->
                    statement.executeQuery()
                }
                conn.commit()
            } catch (exception: SQLException) {
                println(exception.message)
                conn.rollback()
            } finally {
                conn.autoCommit = autoCommit
            }
        }
    }
}

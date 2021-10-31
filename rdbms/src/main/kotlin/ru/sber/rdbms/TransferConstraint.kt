package ru.sber.rdbms

import java.sql.DriverManager
import java.sql.SQLException

class TransferConstraint {
    val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/db",
        "postgres",
        "1111"
    )
    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        connection.use{
            conn-> try {
            val prepareStatement3 =
                conn.prepareStatement("update account1 set amount = amount - $amount where id = $accountId1")
            prepareStatement3.use { statement ->
                statement.executeUpdate()
            }
            val prepareStatement4 =
                conn.prepareStatement("update account1 set amount = amount + $amount where id = $accountId2")
            prepareStatement4.use { statement ->
                statement.executeUpdate()
            }
        }catch (exception: SQLException){
            println("Не удалось выполнить операции")
        }
        }
    }
}

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


                var version1 = 0
                var version2 = 0
                var currentAmount = 0L

                val preparedStatement1 = conn.prepareStatement("select * from bank where id in (?, ?)")
                preparedStatement1.use { statement ->
                    statement.setLong(1, accountId1)
                    statement.setLong(2, accountId2)
                    statement.executeQuery().use { resultSet ->
                        resultSet.next()
                        currentAmount = resultSet.getLong("amount")
                        version1 = resultSet.getInt("version")
                        resultSet.next()
                        version2 = resultSet.getInt("version")
                    }
                }

                if (currentAmount - amount < 0) throw MyCustomException("Текущая сумма меньше суммы списания")

                val preparedStatement2 =
                    conn.prepareStatement("update bank set amount = amount - ?, version = version + 1 where id = ? and version = ?")
                preparedStatement2.use { statement ->
                    statement.setLong(1, amount)
                    statement.setLong(2, accountId1)
                    statement.setInt(3, version1)
                    val updateRows = statement.executeUpdate()
                    if (updateRows == 0) throw SQLException()
                }
                val preparedStatement3 =
                    conn.prepareStatement("update bank set amount = amount + ?, version = version + 1 where id = ? and version = ?")
                preparedStatement3.use { statement ->
                    statement.setLong(1, amount)
                    statement.setLong(2, accountId2)
                    statement.setInt(3, version2)
                    val updateRows = statement.executeUpdate()
                    if (updateRows == 0) throw SQLException()
                }
                conn.commit()

            } catch (exception: MyCustomException) {
                println(exception.message)
                conn.rollback()
            } catch (exception: SQLException) {
                println(exception.message )
                conn.rollback()
            } finally {
                conn.autoCommit = autoCommit
            }
        }
    }
}

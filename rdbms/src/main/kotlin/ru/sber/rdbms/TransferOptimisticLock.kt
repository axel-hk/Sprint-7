package ru.sber.rdbms

import java.sql.DriverManager
import java.sql.SQLException

class TransferOptimisticLock {
    val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/db",
        "postgres",
        "1111"
    )

    connection.use { connection ->
            connection.autoCommit = false

            try {
                connection.autoCommit = false

                connection.prepareStatement("SELECT amount, version FROM account1 WHERE id = ?").use { statement ->
                    statement.setLong(1, accountId1)
                    statement.executeQuery().use {
                        it.next()
                        versionId1 = it.getInt("version")
                        if (it.getInt("amount") - amount < 0)
                            throw NegativeAmountException()
                    }
                }
                connection.prepareStatement("SELECT version FROM account1 WHERE id = ?").use { statement ->
                    statement.setLong(1, accountId2)
                    statement.executeQuery().use {
                        it.next()
                        versionId2 = it.getInt("version")
                    }
                }

                connection.prepareStatement("UPDATE account1 SET amount = amount - ?, version = version + 1 WHERE id = ? AND version = ?")
                    .use { statement ->
                        statement.setLong(1, amount)
                        statement.setLong(2, accountId1)
                        statement.setInt(3, versionId1)
                        if (statement.executeUpdate() != 1)
                            throw CoupdateException()
                }
                connection.prepareStatement("UPDATE account1 SET amount = amount + ?, version = version + 1 WHERE id = ? AND version = ?")
                    .use { statement ->
                        statement.setLong(1, amount)
                        statement.setLong(2, accountId2)
                        statement.setInt(3, versionId2)
                        if (statement.executeUpdate() != 1)
                            throw CoupdateException()
                }

                connection.commit()
            } catch (e: SQLException) {
                e.printStackTrace()
                connection.rollback()
            } catch (e: NegativeAmountException) {
                e.printStackTrace()
                connection.rollback()
            } catch (e: CoupdateException) {
                e.printStackTrace()
                connection.rollback()
            } finally {
                connection.autoCommit = true
            }
        }
    }
}

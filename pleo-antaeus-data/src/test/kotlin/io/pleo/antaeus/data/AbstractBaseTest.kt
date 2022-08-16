package io.pleo.antaeus.data

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import setupInitialData
import java.io.File
import java.sql.Connection

abstract class AbstractBaseTest {
    lateinit var sut: AntaeusDal
    lateinit var db: Database

    val tables = arrayOf(InvoiceTable, CustomerTable)
    val dbFile: File = File.createTempFile("antaeus-db", ".sqlite")

    @BeforeEach
    fun setUp() {
        db = Database
            .connect(
                url = "jdbc:sqlite:${dbFile.absolutePath}",
                driver = "org.sqlite.JDBC",
                user = "root",
                password = ""
            )
            .also {
                TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
                transaction(it) {
                    // Drop all existing tables to ensure a clean slate on each run
                    SchemaUtils.drop(*tables)
                    // Create all tables
                    SchemaUtils.create(*tables)
                }
            }

        // Set up data access layer.
        this.sut = AntaeusDal(db = db)

        // Insert example data in the database.
        setupInitialData(dal = sut)

    }
}
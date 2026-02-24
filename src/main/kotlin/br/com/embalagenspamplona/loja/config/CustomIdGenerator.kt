
package br.com.embalagenspamplona.loja.config

import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import java.io.Serializable

/**
 * Gerador de ID customizado com prefixo.
 * Gera IDs no formato: {prefixo}-{numero_sequencial}
 * Exemplo: pp-1, pp-2, pp-3...
 */
class PrefixedIdGenerator : IdentifierGenerator {

    companion object {
        const val DEFAULT_PREFIX = "id"
    }

    override fun generate(session: SharedSessionContractImplementor, entity: Any): Serializable {
        val prefix = getPrefix(entity)
        val tableName = getTableName(entity)
        
        val sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(id FROM '[0-9]+\$') AS INTEGER)), 0) + 1 FROM $tableName WHERE id LIKE '$prefix-%'"
        
        val connection = session.jdbcCoordinator.logicalConnection.physicalConnection
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(sql)
        
        val nextId = if (resultSet.next()) {
            resultSet.getInt(1)
        } else {
            1
        }
        
        resultSet.close()
        statement.close()
        
        return "$prefix-$nextId"
    }

    private fun getPrefix(entity: Any): String {
        val annotation = entity::class.java.getAnnotation(IdPrefix::class.java)
        return annotation?.value ?: DEFAULT_PREFIX
    }

    private fun getTableName(entity: Any): String {
        val tableAnnotation = entity::class.java.getAnnotation(jakarta.persistence.Table::class.java)
        return tableAnnotation?.name ?: entity::class.java.simpleName.lowercase()
    }
}

/**
 * Anotação para definir o prefixo do ID
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class IdPrefix(val value: String)

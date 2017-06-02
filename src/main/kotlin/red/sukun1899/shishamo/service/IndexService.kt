package red.sukun1899.shishamo.service;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import red.sukun1899.shishamo.model.Index
import red.sukun1899.shishamo.repository.IndexRepository

/**
 * @author su-kun1899
 */
@Service
class IndexService(
        val dataSourceProperties: DataSourceProperties,
        val indexRepository: IndexRepository
) {
    @Transactional(readOnly = true)
    fun get(tableName: String): List<Index> = indexRepository.selectByTableName(dataSourceProperties.schema, tableName)
            .sortedWith(Comparator { (name1, _, category1), (name2, _, category2) ->
                if (category1.order != category2.order) category1.order - category2.order
                else name1.compareTo(name2)
            })
}

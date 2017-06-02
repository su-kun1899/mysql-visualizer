package red.sukun1899.shishamo.service

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import red.sukun1899.shishamo.model.Column
import red.sukun1899.shishamo.model.Index
import red.sukun1899.shishamo.model.ReferencedColumn
import red.sukun1899.shishamo.repository.IndexRepository
import spock.lang.Specification
import spock.lang.Unroll
/**
 * @author su-kun1899
 */
@Unroll
class IndexServiceSpec extends Specification {
    IndexService indexService
    DataSourceProperties dataSourceProperties
    IndexRepository indexRepository

    def setup() {
        dataSourceProperties = Mock()
        indexRepository = Mock()
        indexService = new IndexService(dataSourceProperties, indexRepository)
    }

    def 'Get indices by table name'() {
        given: 'Mocking repository'
        dataSourceProperties.getSchema() >> 'schema1'
        indexRepository.selectByTableName('schema1', 'table1') >> {
            [
                    new Index(
                            'index1',
                            [makeColumn('hoge')],
                            Index.Category.PRIMARY
                    ),
                    new Index(
                            'index2',
                            [
                                    makeColumn('fuga'),
                                    makeColumn('fuga')
                            ],
                            Index.Category.UNIQUE)
            ]
        }

        when:
        def actual = indexService.get('table1')

        then:
        actual.size() == 2
        actual.get(0).getName() == 'index1'
        actual.get(1).getName() == 'index2'
    }

    def 'Indices order by category and name'() {
        given: 'Shuffle indices'
        def indices = [
                new Index(
                        'a_pk',
                        [makeColumn('hoge')],
                        Index.Category.PRIMARY
                ),
                new Index(
                        'b_pk',
                        [makeColumn('fuga')],
                        Index.Category.PRIMARY
                ),
                new Index(
                        'a_uk',
                        [makeColumn('piyo')],
                        Index.Category.UNIQUE
                ),
                new Index(
                        'b_uk',
                        [makeColumn('hogehoge')],
                        Index.Category.UNIQUE
                ),
                new Index(
                        'a_k',
                        [makeColumn('fugafugafuga')],
                        Index.Category.PERFORMANCE
                ),
                new Index(
                        'b_k',
                        [makeColumn('piyopiyo')],
                        Index.Category.PERFORMANCE
                )
        ]
        Collections.shuffle(indices)

        and: 'Mocking repository'
        dataSourceProperties.getSchema() >> 'schema1'
        indexRepository.selectByTableName('schema1', 'table1') >> indices

        when:
        def actual = indexService.get('table1')

        then:
        actual.get(0).getName() == 'a_pk'
        actual.get(1).getName() == 'b_pk'
        actual.get(2).getName() == 'a_uk'
        actual.get(3).getName() == 'b_uk'
        actual.get(4).getName() == 'a_k'
        actual.get(5).getName() == 'b_k'
    }

    def makeColumn(String name) {
        return new Column(
                name,
                "",
                false,
                "",
                "",
                new ReferencedColumn("", ""),
                Collections.emptyList()
        )
    }
}

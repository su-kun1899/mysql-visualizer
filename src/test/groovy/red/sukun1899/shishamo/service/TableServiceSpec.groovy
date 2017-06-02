package red.sukun1899.shishamo.service

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import red.sukun1899.shishamo.model.Column
import red.sukun1899.shishamo.model.CreateTableStatement
import red.sukun1899.shishamo.model.ReferencedColumn
import red.sukun1899.shishamo.model.ReferencedTableCount
import red.sukun1899.shishamo.model.Table
import red.sukun1899.shishamo.repository.TableRepository
import spock.lang.Specification
import spock.lang.Unroll
/**
 * @author su-kun1899
 */
@Unroll
class TableServiceSpec extends Specification {
    TableService tableService
    DataSourceProperties dataSourceProperties
    TableRepository tableRepository

    def setup() {
        dataSourceProperties = new DataSourceProperties()
        dataSourceProperties.schema = 'schema'
        tableRepository = Mock()
        tableService = new TableService(dataSourceProperties, tableRepository)
    }

    def 'Get table list'() {
        given: 'Mocking repository'
        tableRepository.selectAll(_) >> {
            [
                    makeTable('table1'),
                    makeTable('table2'),
            ]
        }

        when:
        def tables = tableService.get()

        then:
        tables.size() == 2
        tables[0].getName() == 'table1'
        tables[1].getName() == 'table2'
    }

    def 'Get table detail'() {
        given: 'Mocking repository'
        def expected = new Table(
                'sample_table',
                '',
                0L,
                [
                        new Column('columnA', 'mysql', false, '', 'test1', new ReferencedColumn('', ''), Collections.emptyList()),
                        new Column('columnB', 'oracle', true, '', 'test2', new ReferencedColumn('', ''), Collections.emptyList())
                ]
        )
        tableRepository.select(*_) >> expected

        when:
        def table = tableService.get(expected.getName())

        then:
        assert table.getName() == expected.getName()
        assert table.getColumns().size() == expected.getColumns().size()
        table.getColumns().eachWithIndex { Column column, int i ->
            assert column.getName() == expected.getColumns().get(i).getName()
            assert column.getDefaultValue() == expected.getColumns().get(i).getDefaultValue()
            assert column.getNullable() == expected.getColumns().get(i).getNullable()
            assert column.getComment() == expected.getColumns().get(i).getComment()
        }
    }

    def 'Get parent table count'() {
        given:
        tableRepository.selectParentTableCountsByTableName(_) >> {
            [
                    'book'     : new ReferencedTableCount('book', 1L),
                    'publisher': new ReferencedTableCount('publisher', 0L),
            ]
        }

        when:
        def actual = tableService.getParentTableCountsByTableName()

        then:
        actual.size() == 1
        actual.get('book') == 1L
        actual.get('publisher') == null
    }

    def 'Get child table count'() {
        given:
        tableRepository.selectChildTableCountsByTableName(_) >> {
            [
                    'book'     : new ReferencedTableCount('book', 1L),
                    'publisher': new ReferencedTableCount('publisher', 0L),
            ]
        }

        when:
        def actual = tableService.getChildTableCountsByTableName()

        then:
        actual.size() == 1
        actual.get('book') == 1L
        actual.get('publisher') == null
    }

    def 'Get column count'() {
        given:
        tableRepository.selectColumnCountsByTableName(_) >> {
            [
                    'book'     : new ReferencedTableCount('book', 1L),
                    'publisher': new ReferencedTableCount('publisher', 0L),
            ]
        }

        when:
        def actual = tableService.getColumnCountsByTableName()

        then:
        actual.size() == 1
        actual.get('book') == 1L
        actual.get('publisher') == null
    }

    def 'Get create table statement'() {
        given:
        def createTableStatement = new CreateTableStatement('book', 'Create table book')
        tableRepository.showCreateTableStatement(_) >> createTableStatement

        when:
        def actual = tableService.getCreateTableStatement(makeTable('book'))

        then:
        actual.getTableName() == createTableStatement.getTableName()
        actual.getDdl() == createTableStatement.getDdl()
    }

    def makeTable(String name) {
        return new Table(name, "", 0L, Collections.emptyList())
    }
}

package red.sukun1899.shishamo.controller.page

import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import red.sukun1899.shishamo.embedded.mysql.EmbeddedMySqlUtil
import red.sukun1899.shishamo.model.*
import red.sukun1899.shishamo.service.IndexService
import red.sukun1899.shishamo.service.TableService
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author su-kun1899
 */
@Unroll
@SpringBootTest
@AutoConfigureMockMvc
class TableControllerSpec extends Specification {
    @Autowired
    MockMvc mockMvc

    @SpyBean
    TableService tableService

    @SpyBean
    IndexService indexService

    def setupSpec() {
        if (EmbeddedMySqlUtil.enable()) {
            EmbeddedMySqlUtil.ready()
        }
    }

    def 'Get all table list'() {
        given: 'Mocking get tables'
        def tables = [
                makeTable('table1'),
                makeTable('table2'),
        ]
        Mockito.doReturn(tables).when(tableService).get()

        and: 'Mocking get parent table count'
        def parentTableCounts = ['table1': 3, 'table2': 1]
        Mockito.doReturn(parentTableCounts)
                .when(tableService).getParentTableCountsByTableName()

        and: 'Mocking get child table count'
        def childTableCounts = ['table1': 4, 'table2': 2]
        Mockito.doReturn(childTableCounts)
                .when(tableService).getChildTableCountsByTableName()

        and: 'Mocking get makeColumn count'
        def columnCounts = ['table1': 5, 'table2': 10]
        Mockito.doReturn(columnCounts)
                .when(tableService).getColumnCountsByTableName()

        and: 'URL'
        def url = '/tables'

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get(url))

        then:
        result.andReturn().modelAndView.modelMap.get('tables') == tables
        result.andReturn().modelAndView.modelMap.get('parentTableCounts') == parentTableCounts
        result.andReturn().modelAndView.modelMap.get('childTableCounts') == childTableCounts
        result.andReturn().modelAndView.modelMap.get('columnCounts') == columnCounts
        result.andReturn().modelAndView.modelMap.get('schemaName') == 'sample'
    }

    def 'Get table detail'() {
        given: 'Mocking get table'
        def table = makeTable('table1')
        Mockito.doReturn(table).when(tableService).get(tableName)

        and: 'Mocking get indices'
        def indices = [
                makeIndex('index1', Index.Category.PRIMARY, [makeColumn('hoge')]),
                makeIndex('index1', Index.Category.PERFORMANCE, [makeColumn('fuga')])
        ]
        Mockito.doReturn(indices).when(indexService).get(tableName)

        and: 'Mocking get createTableStatement'
        def createTableStatement = makeCreateTableStatement('table1', 'dummy')
        Mockito.doReturn(createTableStatement).when(tableService).getCreateTableStatement(table)

        and: 'URL'
        def url = '/tables/' + tableName

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get(url))

        then:
        result.andReturn().modelAndView.modelMap.get('table') == table
        result.andReturn().modelAndView.modelMap.get('schemaName') == 'sample'
        result.andReturn().modelAndView.modelMap.get('indices') == indices
        result.andReturn().modelAndView.modelMap.get('createTableStatement') == createTableStatement

        where:
        tableName | _
        'table1'  | _
    }

    def makeTable(String name) {
        return new Table(name, '', 0L, [])
    }

    def makeColumn(String name) {
        return new Column(name, '', false, '', '', new ReferencedColumn('', ''), [])
    }

    def makeIndex(String name, Index.Category category, List<Column> columns) {
        return new Index(name, columns, category)
    }

    def makeCreateTableStatement(String tableName, String dd) {
        return new CreateTableStatement(tableName, dd)
    }
}

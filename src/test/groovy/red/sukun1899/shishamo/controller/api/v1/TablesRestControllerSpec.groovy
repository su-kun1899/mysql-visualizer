package red.sukun1899.shishamo.controller.api.v1

import org.hamcrest.Matchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import red.sukun1899.shishamo.embedded.mysql.EmbeddedMySqlUtil
import red.sukun1899.shishamo.model.Column
import red.sukun1899.shishamo.model.ReferencedColumn
import red.sukun1899.shishamo.model.Table
import red.sukun1899.shishamo.service.TableService
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

/**
 * @author su-kun1899
 */
@Unroll
@SpringBootTest
@AutoConfigureMockMvc
class TablesRestControllerSpec extends Specification {
    @Autowired
    MockMvc mockMvc

    @SpyBean
    TableService tableService

    def setupSpec() {
        if (EmbeddedMySqlUtil.enable()){
            EmbeddedMySqlUtil.ready()
        }
    }

    def 'Get table list'() {
        setup: 'Prepare expected value'
        def tables = [
                new Table('table1', "", 0L, Collections.emptyList()),
                new Table('table2', "", 0L, Collections.emptyList()),
        ]

        and: 'Mocking service'
        Mockito.doReturn(tables).when(tableService).get()

        and: 'URL'
        def url = '/v1/tables'

        expect:
        mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath('$').isNotEmpty())
                .andExpect(jsonPath('$').isArray())
                .andExpect(jsonPath('$', Matchers.hasSize(tables.size())))
                .andExpect(jsonPath('$[0].name').value(tables[0].getName()))
                .andExpect(jsonPath('$[1].name').value(tables[1].getName()))
    }

    def 'Get table detail'() {
        setup: 'Mock service'
        def table = new Table(
                tableName, "", 0L,
                [makeColumn(columnNames[0]), makeColumn(columnNames[1])]
        )
        Mockito.doReturn(table).when(tableService).get(Mockito.anyString())

        and: 'URL'
        def url = '/v1/tables/' + tableName

        expect:
        mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath('$').isNotEmpty())
                .andExpect(jsonPath('$.name').value(table.getName()))
                .andExpect(jsonPath('$.columns').isArray())
                .andExpect(jsonPath('$.columns', Matchers.hasSize(table.getColumns().size())))
                .andExpect(jsonPath('$.columns[0].name').value(table.getColumns().get(0).getName()))
                .andExpect(jsonPath('$.columns[1].name').value(table.getColumns().get(1).getName()))

        where:
        tableName      | columnNames
        'sample_table' | ['columnA', 'columnB']
    }

    def 'Get column detail'() {
        setup: 'Prepare expected value'
        def table = new Table(
                'sample_table', '', 0L,
                [new Column(
                        name,
                        defaultValue,
                        nullable,
                        type,
                        comment,
                        new ReferencedColumn('', ''),
                        Collections.emptyList()
                )]
        )

        and: 'URL'
        def url = '/v1/tables/' + table.getName()

        and: 'Mocking service'
        Mockito.doReturn(table).when(tableService).get(table.getName())

        expect:
        mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath('$').isNotEmpty())
                .andExpect(jsonPath('$.columns[0].name').value(table.getColumns().get(0).getName()))
                .andExpect(jsonPath('$.columns[0].defaultValue').value(table.getColumns().get(0).getDefaultValue()))
                .andExpect(jsonPath('$.columns[0].nullable').value(table.getColumns().get(0).getNullable()))
                .andExpect(jsonPath('$.columns[0].type').value(table.getColumns().get(0).getType()))
                .andExpect(jsonPath('$.columns[0].comment').value(table.getColumns().get(0).getComment()))

        where:
        name      | defaultValue     | nullable | type          | comment
        'columnA' | 'default_sample' | true     | 'varchar(40)' | 'comment_sample'
    }

    def makeColumn(String name) {
        return new Column(
                name, null, false, '', '',
                new ReferencedColumn("", ""),
                Collections.emptyList()
        )
    }
}

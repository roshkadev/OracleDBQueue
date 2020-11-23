import com.roshka.oracledbqueue.config.OracleDataSourceConfig;
import com.roshka.oracledbqueue.datasource.OracleDataSourceUtil;
import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.ROWID;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class TestOracleConnection {


    @Test
    public void testDatasourceConfiguration() throws IOException, SQLException {

        Properties properties = new Properties();
        properties.load(new FileReader("conf/ccp.properties"));

        final OracleDataSourceConfig oracleDataSourceConfig = OracleDataSourceConfig.loadFromProperties(properties);

        final DataSource pooledDataSource = OracleDataSourceUtil.createPooledDataSource(oracleDataSourceConfig);
        final Connection conn = pooledDataSource.getConnection();
        assert(conn.getMetaData().getDatabaseProductName().equalsIgnoreCase("Oracle"));

    }
}

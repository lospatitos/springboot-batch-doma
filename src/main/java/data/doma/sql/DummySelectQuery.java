package data.doma.sql;

import java.lang.reflect.Method;
import java.util.Collections;

import org.seasar.doma.FetchType;
import org.seasar.doma.internal.jdbc.sql.PreparedSql;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.SelectOptions;
import org.seasar.doma.jdbc.SqlKind;
import org.seasar.doma.jdbc.SqlLogType;
import org.seasar.doma.jdbc.query.SelectQuery;

import lombok.Data;

@Data
public class DummySelectQuery implements SelectQuery {

	private final Config config;

	@Override
	public SelectOptions getOptions() {
		return SelectOptions.get();
	}

	@Override
	public Config getConfig() {
		return config;
	}

	@Override
	public String getClassName() {
		return null;
	}

	@Override
	public String getMethodName() {
		return null;
	}

	@Override
	public PreparedSql getSql() {
		return new PreparedSql(SqlKind.SELECT, "dummy", "dummy", "dummy", Collections.emptyList(),
				SqlLogType.FORMATTED);
	}

	@Override
	public boolean isResultEnsured() {
		return false;
	}

	@Override
	public boolean isResultMappingEnsured() {
		return false;
	}

	@Override
	public FetchType getFetchType() {
		return FetchType.LAZY;
	}

	@Override
	public int getFetchSize() {
		return 0;
	}

	@Override
	public int getMaxRows() {
		return 0;
	}

	@Override
	public int getQueryTimeout() {
		return 0;
	}

	@Override
	public void prepare() {

	}

	@Override
	public void complete() {
	}

	@Override
	public Method getMethod() {
		return null;
	}

	@Override
	public SqlLogType getSqlLogType() {
		return null;
	}

	@Override
	public String comment(String sql) {
		return sql;
	}

}

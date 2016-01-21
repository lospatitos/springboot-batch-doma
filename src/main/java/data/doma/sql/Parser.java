package data.doma.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.seasar.doma.internal.expr.ExpressionEvaluator;
import org.seasar.doma.internal.expr.Value;
import org.seasar.doma.internal.jdbc.sql.NodePreparedSqlBuilder;
import org.seasar.doma.internal.jdbc.sql.PreparedSql;
import org.seasar.doma.internal.jdbc.sql.SqlParser;
import org.seasar.doma.internal.jdbc.sql.node.ExpandNode;
import org.seasar.doma.internal.jdbc.sql.node.SqlLocation;
import org.seasar.doma.jdbc.CommentContext;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.JdbcException;
import org.seasar.doma.jdbc.Naming;
import org.seasar.doma.jdbc.SelectOptions;
import org.seasar.doma.jdbc.SqlKind;
import org.seasar.doma.jdbc.SqlLogType;
import org.seasar.doma.jdbc.SqlNode;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.entity.EntityType;
import org.seasar.doma.message.Message;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author go.yokoyama
 *
 */
@Data
@AllArgsConstructor
public class Parser<ENTITY> {

	/**
	 * Doma設定
	 */
	private Config config;
	/**
	 * SQLテンプレート
	 */
	private String sqlFile;

	/**
	 * エンティティタイプ
	 */
	private EntityType<ENTITY> entityType;

	/**
	 * 検索オプション
	 */
	private SelectOptions options;

	/**
	 * 設定パラメータ
	 */
	private final Map<String, Value> variableValues = new HashMap<String, Value>();

	/**
	 * バインドパラメータの追加
	 *
	 * @param name
	 *            パラメータ名
	 * @param type
	 *            パラメータタイプ
	 * @param value
	 *            パラメータ値
	 */
	public <T> void addValue(String name, Class<T> type, T value) {
		variableValues.put(name, new Value(type, value));
	}

	/**
	 * Doma2のSQLクラス生成
	 *
	 * @return
	 */
	// public PreparedSql execute() {
	// SqlParser parser = new SqlParser(sql);
	// SqlNode node = parser.parse();
	// NodePreparedSqlBuilder builder = new NodePreparedSqlBuilder(config,
	// SqlKind.SCRIPT, null, new ExpressionEvaluator(variableValues,
	// config.getDialect().getExpressionFunctions(),
	// ConfigSupport.defaultClassHelper),
	// SqlLogType.FORMATTED);
	//
	// return builder.build(node, sql -> sql);
	// }

	public PreparedSql execute() {
		SqlParser parser = new SqlParser(sqlFile);
		SqlNode node = parser.parse();
		SqlNode transformedSqlNode = config.getDialect().transformSelectSqlNode(node, options);
		return buildSql((evaluator, expander) -> {
			NodePreparedSqlBuilder sqlBuilder = new NodePreparedSqlBuilder(config, SqlKind.SELECT, null, evaluator,
					SqlLogType.FORMATTED, expander);
			return sqlBuilder.build(transformedSqlNode, this::comment);
		});
	}

	protected PreparedSql buildSql(
			BiFunction<ExpressionEvaluator, Function<ExpandNode, List<String>>, PreparedSql> sqlBuilder) {
		ExpressionEvaluator evaluator = new ExpressionEvaluator(variableValues,
				config.getDialect().getExpressionFunctions(), config.getClassHelper());
		return sqlBuilder.apply(evaluator, this::expandColumns);
	}

	protected List<String> expandColumns(ExpandNode node) {
		if (entityType == null) {
			SqlLocation location = node.getLocation();
			throw new JdbcException(Message.DOMA2144, location.getSql(), location.getLineNumber(),
					location.getPosition());
		}
		Naming naming = config.getNaming();
		Dialect dialect = config.getDialect();
		return entityType.getEntityPropertyTypes().stream()
				.map(p -> p.getColumnName(naming::apply, dialect::applyQuote)).collect(Collectors.toList());
	}

	protected String comment(String sql) {
		// FIXME callerClassName callerMethodName methodの指定
		return config.getCommenter().comment(sql,
				new CommentContext("callerClassName", "callerMethodName", config, null));
	}
}

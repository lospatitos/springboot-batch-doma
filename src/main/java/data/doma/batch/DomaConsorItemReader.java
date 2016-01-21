package data.doma.batch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.doma.internal.jdbc.sql.InParameter;
import org.seasar.doma.internal.jdbc.sql.PreparedSql;
import org.seasar.doma.internal.jdbc.util.MetaTypeUtil;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.SelectOptions;
import org.seasar.doma.jdbc.entity.EntityType;
import org.springframework.batch.item.database.AbstractCursorItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import data.doma.result.Mapper;
import data.doma.sql.Parser;
import lombok.Data;

@Data
public class DomaConsorItemReader<ENTITY> extends AbstractCursorItemReader<ENTITY> {
	/**
	 * SQLテンプレート
	 */
	private final String sql;

	/**
	 * Doma設定
	 */
	private final Config config;

	/**
	 * resultSetとEntityのマッパー
	 */
	private final Mapper<ENTITY> rowMapper;

	/**
	 * ステートメント
	 */
	private PreparedStatement preparedStatement;

	/**
	 * SQLパーサ
	 */
	private final Parser<ENTITY> parser;

	public DomaConsorItemReader(String sql, Mapper<ENTITY> rowMapper) {
		super();
		setName(ClassUtils.getShortName(JdbcCursorItemReader.class));
		this.sql = sql;
		this.rowMapper = rowMapper;
		this.config = rowMapper.getConfig();
		String metaTypeName = MetaTypeUtil.getMetaTypeName(rowMapper.getEntityClass().getName());

		Class<?> entityTypeClass;
		try {
			entityTypeClass = Class.forName(metaTypeName);
		} catch (ClassNotFoundException e) {
			// TODO System例外
			throw new RuntimeException("TODO System例外投げる", e);
		}

		Method method;
		try {
			method = entityTypeClass.getMethod("getSingletonInternal", null);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO System例外
			throw new RuntimeException("TODO System例外投げる", e);
		}

		EntityType<ENTITY> entityType;
		try {
			entityType = (EntityType<ENTITY>) method.invoke(null, null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO System例外
			throw new RuntimeException("TODO System例外投げる", e);
		}
		SelectOptions options = SelectOptions.get();
		this.parser = new Parser<ENTITY>(config, sql, entityType, options);

		this.setDataSource(config.getDataSource());
		if (config.getFetchSize() != 0) {
			this.setFetchSize(config.getFetchSize());
		}
		if (config.getQueryTimeout() != 0) {
			this.setQueryTimeout(config.getQueryTimeout());
		}
	}

	/**
	 * 検索用のパラメータを追加する。
	 *
	 * @param name
	 *            パラメータ名
	 * @param type
	 *            パラメータタイプ
	 * @param value
	 *            値
	 */
	public void addParameter(String name, Class<Object> type, Object value) {
		this.parser.addValue(name, type, value);
	}

	/**
	 * 検索用のパラメータを追加する。
	 *
	 * @param name
	 *            パラメータ名
	 * @param type
	 *            パラメータタイプ
	 * @param value
	 *            値
	 */
	public void setSelectOption(SelectOptions options) {
		this.parser.setOptions(options);
	}

	@Override
	protected void cleanupOnClose() throws Exception {
		JdbcUtils.closeStatement(this.preparedStatement);

	}

	@Override
	protected void openCursor(Connection con) {
		try {
			PreparedSql preparedSql = parser.execute();
			String generateSql = preparedSql.getRawSql();

			if (isUseSharedExtendedConnection()) {
				preparedStatement = con.prepareStatement(generateSql, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
			} else {
				preparedStatement = con.prepareStatement(generateSql, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
			}
			applyStatementSettings(preparedStatement);
			int i = 0;

			for (InParameter<?> parameter : preparedSql.getParameters()) {
				preparedStatement.setObject(i + 1, parameter.getValue());
				i++;
			}

			this.rs = preparedStatement.executeQuery();
			handleWarnings(preparedStatement);
		} catch (SQLException se) {
			close();
			throw getExceptionTranslator().translate("Executing query", getSql(), se);
		}
	}

	@Override
	protected ENTITY readCursor(ResultSet rs, int currentRow) throws SQLException {

		return rowMapper.execute(rs);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		// TODO ライブラリの使用
		Assert.notNull(sql, "Sqlは必須です");
		Assert.notNull(config, "Configは必須です");
		Assert.notNull(rowMapper, "Configは必須です");

	}

}

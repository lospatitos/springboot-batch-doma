package data.doma.result;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.doma.Entity;
import org.seasar.doma.internal.jdbc.command.EntityProvider;
import org.seasar.doma.internal.jdbc.command.ObjectProvider;
import org.seasar.doma.internal.jdbc.util.MetaTypeUtil;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.entity.EntityType;

import data.doma.sql.DummySelectQuery;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * SQLの実行結果からエンティティにマッピングするクラス。
 *
 * @author go.yokoyama
 *
 */
@Data
@AllArgsConstructor
public class Mapper<ENTITY> {
	/**
	 * エンティティ
	 */
	private final Class<ENTITY> entityClass;

	/**
	 * Doma設定ファイル
	 */
	private final Config config;

	/**
	 * SQLの実行結果からエンティティにマッピングする。
	 *
	 * @param resultSet
	 *            結果クラス
	 * @return エンティティ
	 */
	public ENTITY execute(ResultSet resultSet) {
		// FIXME Doma依存を解消
		// エンティティのメタクラスの作成
		if (!entityClass.isAnnotationPresent(Entity.class)) {
			// TODO System例外
			throw new RuntimeException("TODO System例外投げる");
		}
		String metaTypeName = MetaTypeUtil.getMetaTypeName(entityClass.getName());
		Class<?> entityType;
		try {
			entityType = Class.forName(metaTypeName);
		} catch (ClassNotFoundException e) {
			// TODO System例外
			throw new RuntimeException("TODO System例外投げる", e);
		}

		Method method;
		try {
			method = entityType.getMethod("getSingletonInternal", null);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO System例外
			throw new RuntimeException("TODO System例外投げる", e);
		}
		ObjectProvider<ENTITY> provider = null;
		try {
			provider = new EntityProvider<ENTITY>((EntityType<ENTITY>) method.invoke(null, null),
					new DummySelectQuery(config), false);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO System例外
			throw new RuntimeException("TODO System例外投げる", e);
		}
		ENTITY entity;
		try {
			entity = provider.get(resultSet);
		} catch (SQLException e) {
			// TODO System例外
			throw new RuntimeException("TODO System例外投げる", e);
		}
		return entity;
	}

}

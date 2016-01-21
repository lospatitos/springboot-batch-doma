/**
 *
 */
package hello;

import java.util.List;

import org.seasar.doma.BatchInsert;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;

/**
 * @author go.yokoyama
 *
 */
@Dao
@ConfigAutowireable
public interface PersonDao {
	@BatchInsert
	int[] insertBatch(List<Person> persons);

	@Select
	List<Person> findAll();
}

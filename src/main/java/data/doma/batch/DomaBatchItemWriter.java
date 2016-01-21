package data.doma.batch;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
public class DomaBatchItemWriter<ENTITY> implements ItemWriter<ENTITY>, InitializingBean {

	private Function<List<? extends ENTITY>, int[]> function;

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public void write(List<? extends ENTITY> items) throws Exception {
		if (!items.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Executing batch with " + items.size() + " items.");
			}
			int[] results = function.apply(items);

			log.info("Inserted " + Arrays.stream(results).sum() + " items.");

		}
	}

}

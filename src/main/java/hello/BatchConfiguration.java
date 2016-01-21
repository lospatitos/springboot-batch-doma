package hello;

import java.util.List;

import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import data.common.sql.SqlFinder;
import data.doma.batch.DomaBatchItemWriter;
import data.doma.batch.DomaConsorItemReader;
import data.doma.result.Mapper;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	@Autowired
	private SqlFinder sqlFinder;
	@Autowired
	private Config config;
	@Autowired
	private PersonDao personDao;

	@Bean
	public Job importUserJob(JobBuilderFactory jobs, Step s1, JobExecutionListener listener) {
		return jobs.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener).flow(s1).end().build();
	}

	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<Account> reader, ItemWriter<Person> writer,
			ItemProcessor<Account, Person> processor) {
		return stepBuilderFactory.get("step1").<Account, Person> chunk(10).reader(reader).processor(processor)
				.writer(writer).build();
	}

	@Bean
	public ItemReader<Account> reader() {
		String sqlFile = "META-INF/hello/AccountDao/findAll.sql";
		String sql = sqlFinder.get(sqlFile);

		Mapper<Account> rowMapper = new Mapper<>(Account.class, config);

		DomaConsorItemReader<Account> itemReader = new DomaConsorItemReader<>(sql, rowMapper);
		itemReader.setSelectOption(SelectOptions.get().limit(3).offset(0));
		return itemReader;
	}

	@Bean
	public ItemProcessor<Account, Person> processor() {
		return new PersonItemProcessor();
	}

	@Bean
	public ItemWriter<Person> writer() {
		@SuppressWarnings("unchecked")
		DomaBatchItemWriter<Person> writer = new DomaBatchItemWriter<Person>(items -> {
			return personDao.insertBatch((List<Person>) items);
		});
		return writer;
	}

}

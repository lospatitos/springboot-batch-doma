# SpringBoot Batch [DOMA2](https://github.com/domaframework/doma) Sample(CursorBasedItemReader)

## 前提条件
RDBMSにOracleを選択した場合を想定
OracleのPaging処理で副問い合わせが発生するため、Paging ItemReaderではなくCursorBaseItemReaderを選択

## DomaConsorItemReader
`Mapper`に`Entity`を設定し、`ItemReader`にSQLテンプレートを指定することにより、`Item`を取得できるように実装。
`ResultSet`を`Entity`にマッピングする`Mapper`と内部でSqlテンプレートを解析する`Parser`が[DOMA2](https://github.com/domaframework/doma)の内部実装に依存しているため
[DOMA2](https://github.com/domaframework/doma)のバージョンアップで動作しなくなる恐れがある。
([DomaConsorItemReaderがDomaのinternalな実装に依存](https://github.com/domaframework/doma/issues/114))
```java
	@Bean
	public ItemReader<Account> reader() {
		String sqlFile = "META-INF/hello/AccountDao/findAll.sql";
		String sql = sqlFinder.get(sqlFile);

		Mapper<Account> rowMapper = new Mapper<>(Account.class, config);

		DomaConsorItemReader<Account> itemReader = new DomaConsorItemReader<>(sql, rowMapper);
		return itemReader;
	}
```

## DomaBatchItemWriter
ラムダ式を使用し、[DOMA2](https://github.com/domaframework/doma)のDaoの呼び出しを行う。
```java
	@Bean
	public ItemWriter<Person> writer() {
		@SuppressWarnings("unchecked")
		DomaBatchItemWriter<Person> writer = new DomaBatchItemWriter<Person>(items -> {
			return personDao.insertBatch((List<Person>) items);
		});
		return writer;
	}
```


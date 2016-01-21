package hello;

import org.springframework.batch.item.ItemProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersonItemProcessor implements ItemProcessor<Account, Person> {

	@Override
	public Person process(final Account account) throws Exception {
		String[] fullName = account.getName().split(" ");
		final String firstName = fullName[1].toUpperCase();
		final String lastName = fullName[0].toUpperCase();

		final Person person = new Person(firstName, lastName);

		log.info("Converting (" + account + ") into (" + person + ")");

		return person;
	}

}

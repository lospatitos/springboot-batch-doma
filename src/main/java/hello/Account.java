package hello;

import java.util.Date;

import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.jdbc.entity.NamingType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(naming = NamingType.SNAKE_LOWER_CASE)
public class Account {
	@Id
	private String email;
	private String password;
	private String name;
	private Date birthDay;
	private String zip;
	private String address;
}

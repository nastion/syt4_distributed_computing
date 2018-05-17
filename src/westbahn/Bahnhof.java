package westbahn;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Bahnhof {
	@Id
	@GeneratedValue
	private Long ID;
	
	@Size(min=2, max=150)
	@Pattern(regexp="^[a-z A-Z0-9äöüÄÖÜ-]*$", message="Ungültiger Name für den Bahnhof")
	private String name;
	
	private int absPreisEntfernung;
	
	private int absKmEntfernung;
	
	private int absZeitEntfernung;
	
	private boolean kopfBahnhof;
	
	public void setKopfBahnhof(boolean kopfBahnhof) {
		this.kopfBahnhof = kopfBahnhof;
	}
	
	public boolean getKopfBahnhof() {
		return this.kopfBahnhof;
	}
	
	public Bahnhof() {}
	
	public Bahnhof(String name) {
		this(name, 100, 100, 100, true);
	}
	
	public Bahnhof(String name, int absPreisEntfernung, int absKmEntfernung, int absZeitEntfernung, boolean kopfBahnhhof) {
		this.name = name;
		this.absPreisEntfernung = absPreisEntfernung;
		this.absKmEntfernung = absKmEntfernung;
		this.absZeitEntfernung = absZeitEntfernung;
		this.kopfBahnhof = kopfBahnhhof;
	}

	public String getName() {
		return name;
	}
	
}

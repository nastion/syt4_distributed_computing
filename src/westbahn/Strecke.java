package westbahn;

import javax.persistence.*;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;

@Entity
public class Strecke {
	@Id
	@GeneratedValue
	private Long ID;
	
	@NotNull
	@OneToOne
	private Bahnhof start;

	@OneToOne
	private Bahnhof bahnhof;

	@NotNull
	@OneToOne
	private Bahnhof ende;
	
	@AssertFalse(message="The starting station must not be the ending station")
	private boolean endeIsStart;

	public Strecke() {}
	public Strecke(Bahnhof start, Bahnhof ende) {
		this(start, ende, null);
	}
	public Strecke(Bahnhof start, Bahnhof ende, Bahnhof bahnhof) {
		this.start = start;
		this.ende = ende;
		this.bahnhof = bahnhof;
		this.endeIsStart = this.ende.getName().equals(this.start.getName());
	}
	public String getEndbahnhof() {
		return ende.getName();
	}
}

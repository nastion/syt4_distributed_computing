package westbahn;

import java.util.Date;

public class Reservierung {
	private Long ID;

	private Date datum;

	private int praemienMeilenBonus = 15;

	private int preis = 150;

	private StatusInfo status;

	private Zug zug;

	private Strecke strecke;

	private Benutzer benutzer;
	
	private Zahlung zahlung;

	public Reservierung() {}
	public Reservierung(Date datum, StatusInfo status, Zug zug, Benutzer benutzer, Zahlung zahlung) {
		this(datum, 15, 150, status, zug, null, benutzer, zahlung);
	}
	public Reservierung(Date datum, StatusInfo status, Strecke strecke, Benutzer benutzer, Zahlung zahlung) {
		this(datum, 15, 150, status, null, strecke, benutzer, zahlung);
	}

	public Reservierung(Date datum, int praemienMeilenBonus, int preis, StatusInfo status, Zug zug, Strecke strecke,
			Benutzer benutzer, Zahlung zahlung) {
		super();
		this.datum = datum;
		this.praemienMeilenBonus = praemienMeilenBonus;
		this.preis = preis;
		this.status = status;
		this.zug = zug;
		this.strecke = strecke;
		this.benutzer = benutzer;
		this.zahlung = zahlung;
	}
	public String showReservierung() {
		return benutzer.getName() + " hat am " + datum.toString() + " einen Zug nach " + strecke.getEndbahnhof() + " reserviert.";
	}
	
	public Benutzer getBenutzer() {
		return this.benutzer;
	}
}

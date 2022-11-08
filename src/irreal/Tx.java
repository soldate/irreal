package irreal;

import java.util.Date;

import org.json.JSONObject;

class Tx implements org.prevayler.Transaction<PersistentData> {

	private static final long serialVersionUID = 1L;

	private String from;
	private String to;
	private Long amount;

	public Tx(String from, String to, Long amount) {
		super();
		this.from = from;
		this.to = to;
		this.amount = amount;
	}

	@Override
	public void executeOn(PersistentData prevalentSystem, Date executionTime) {
		JSONObject d = prevalentSystem.data;

		Long fromBalance = 0L;
		Long toBalance = 0L;

		if (d.has(from)) fromBalance = d.getLong(from);
		if (d.has(to)) toBalance = d.getLong(to);

		if (fromBalance >= amount) {
			Long change = fromBalance - amount;
			Long update = toBalance + amount;

			d.put(to, update);
			d.put(from, change);
		}
	}
}

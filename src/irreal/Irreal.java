package irreal;

import java.security.PublicKey;

import org.json.JSONObject;

import webapp.BasicApp;

public class Irreal extends BasicApp {

	private static long supply = 0;

	public static void main(String[] args) throws Exception {
		// start http server and load database
		new Irreal().init();
	}

	private JSONObject data() {
		return prevayler.prevalentSystem().data;
	}

	private JSONObject getBalance(String user, JSONObject request) throws Exception {
		String pubkey = request.getString("pubkey");
		JSONObject response = new JSONObject();

		Long balance = getBalance(pubkey);
		response.put("balance", balance);

		if (debug()) response.put("debug", true);
		return response;
	}

	private synchronized JSONObject transfer(String user, JSONObject request) throws Exception {
		JSONObject response = new JSONObject();
		response.put("status", "error");

		String from = request.getString("from");
		String to = request.getString("to");
		Long amount = request.getLong("amount");
		String sign = request.getString("sign");

		if (from.length() != 87 || to.length() != 87) return response;
		if (from.equals(to)) return response;
		if (sign.length() < 10) return response;

		Long balance = getBalance(from);
		if (amount <= 0) return response;

		if (balance >= amount) {
			PublicKey fromPubKey = Crypto.getPublicKey(from);

			if (Crypto.verify(fromPubKey, from + to + amount, sign)) {
				prevayler.execute(new Tx(from, to, amount));
				response.put("status", "success");
			}
		}
		return response;
	}

	@Override
	protected boolean debug() {
		return false;
	}

	protected Long getBalance(String key) {
		JSONObject data = data();
		if (data.has(key)) return data.getLong(key);
		else return 0L;
	}

	@Override
	protected String getPasswordSSL() {
		return "123456";
	}

	@Override
	protected String getServletName() {
		return "irreal";
	}

	@Override
	protected JSONObject my_exec(String method, String user, JSONObject request) throws Exception {

		switch (method) {

		case "transfer":
			return transfer(user, request);

		case "getBalance":
			return getBalance(user, request);

		case "all":
			return data();

		default:
			throw new IllegalArgumentException("Unexpected value: " + method);
		}
	}

	@Override
	protected void prepareSnapshot() {
		JSONObject data = data();
		String[] keys = JSONObject.getNames(data);
		long balance = 0;
		for (String k : keys) {
			Object value = data.get(k);
			// if invalid remove, else recalculate supply
			if (k.length() != 87 || !(value instanceof Number)) {
				data.remove(k);
			} else if (k.length() == 87 && value instanceof Number) {
				balance = ((Number) value).longValue();
				if (balance == 0) data.remove(k);
				else supply = supply + balance;
			}
		}
	}
}
